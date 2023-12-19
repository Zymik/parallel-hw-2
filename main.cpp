#include <cilk/cilk.h>
#include <iostream>
#include <vector>
#include <algorithm>
#include <string>
#include <deque>
#include <atomic>
#include <assert.h>
#include <chrono>

using namespace std;

constexpr std::size_t BLOCK = 1024;

struct Graph {
    std::vector<std::vector<std::size_t>> ways;
    std::vector<std::size_t> bfs;
};

std::size_t get_pos(std::size_t i, std::size_t j, std::size_t k, std::size_t size) {
    return i + j * size + k * size * size;
}

Graph generate_cubic_graph(std::size_t size) {
    std::vector<std::vector<std::size_t>> graph = std::vector<std::vector<std::size_t>>(size * size * size);
    std::vector<std::size_t> bfs = std::vector<std::size_t>(size * size * size);


    #pragma cilk grainsize = BLOCK
    cilk_for (std::size_t i = 0; i < size; i++) {
        #pragma cilk grainsize = BLOCK
        cilk_for (std::size_t j = 0; j < size; j++) {
            #pragma cilk grainsize = BLOCK
            cilk_for (std::size_t k = 0; k < size; k++) {
                //std::cout << i << " " << j << " " << k << "\n";
                std::size_t pos = get_pos(i, j, k, size);
                bfs[pos] = i + j + k;

                if (i != 0) {
                    graph[pos].push_back(get_pos(i - 1, j, k, size));
                }
                if (j != 0) {
                    graph[pos].push_back(get_pos(i, j - 1, k, size));
                }
                if (k != 0) {
                    graph[pos].push_back(get_pos(i, j, k - 1, size));
                }

                if (i != size - 1) {
                    graph[pos].push_back(get_pos(i + 1, j, k, size));
                }

                if (j != size - 1) {
                    graph[pos].push_back(get_pos(i, j + 1, k, size));
                }

                if (k != size - 1) {
                    graph[pos].push_back(get_pos(i, j, k + 1, size));
                }
            }
        }
    }
    return Graph{graph, bfs};
}

std::vector<std::size_t> bfs_seq(vector<vector<std::size_t>>& ways) {
    std::vector<bool> marked = std::vector<bool>(ways.size());
    std::deque<std::size_t> queue = std::deque<std::size_t>{0};
    std::vector<std::size_t> result = std::vector<std::size_t>(ways.size());
    marked[0] = true;
    while (!queue.empty()) {
        std::size_t next = queue.front();
        queue.pop_front();

        for (std::size_t j : ways[next]) {
            if (!marked[j]) {
                marked[j] = true;
                queue.push_back(j);
                result[j] = result[next] + 1;
            }
        }
    }

    return result;
}


std::size_t serial_reduce(std::vector<std::size_t>& input, std::size_t l, std::size_t r) {
    std::size_t sum = 0;
    for (std::size_t i = l; i < r; i++) {
        sum += input[i];
    }
    return sum;
}

void serial_scan(std::vector<std::size_t>& input, std::vector<std::size_t>& result, std::size_t l, std::size_t r, std::size_t base) {
    std::size_t sum = base;
    for (std::size_t i = l; i < r; i++) {
        sum += input[i];
        result[i] = sum;
    }
    return;
}

void scan_with_result(std::vector<std::size_t>& input, std::vector<std::size_t>& result) {
    if (input.size() <= BLOCK) {
        serial_scan(input, result, 0, input.size(), 0);
        return;
    }
    std::size_t s = input.size() / BLOCK;
    if (input.size() % BLOCK != 0) {
        s++;
    }
    std::vector<std::size_t> sums = std::vector<std::size_t>(s);
    
    #pragma cilk grainsize = BLOCK
    cilk_for (std::size_t i = 0; i < s; i++ ) {
        std::size_t l = BLOCK * i;
        std::size_t r = std::min(BLOCK * (i + 1), input.size());
        sums[i] = serial_reduce(input, l, r);
    }
    std::vector<std::size_t> result1 = std::vector<std::size_t>(sums.size());
    scan_with_result(sums, result1);

    #pragma cilk grainsize = BLOCK
    cilk_for (std::size_t i = 0; i < s; i++ ) {
        int j = i + 1;
        int base = result1[i];

        if (i + 1 == result1.size()) {
            j = 0;
            base = 0;
        }

        int l = BLOCK * j;
        int r = std::min(BLOCK * (j + 1), input.size());
        serial_scan(input, result, l, r, base);
    }
}

std::vector<std::size_t> scan(std::vector<std::size_t>& input) {
    std::vector<std::size_t> result = std::vector<std::size_t>(input.size());
    scan_with_result(input, result);
    return result;
}


std::vector<std::size_t> bfs_par(std::vector<std::vector<std::size_t>>& ways) {
    std::vector<std::atomic<bool>> marked = std::vector<std::atomic<bool>>(ways.size());
    #pragma cilk grainsize = BLOCK
    cilk_for (std::size_t i = 0; i < ways.size(); i++) {
        marked[i].store(false);
    }
    
    
    marked[0].store(true);
    std::vector<std::size_t> result = std::vector<std::size_t>(ways.size());
    std::vector<std::size_t> frontier = std::vector<std::size_t>{1};
    std::size_t layer = 1;

    while (!frontier.empty()) {
        std::vector<std::size_t> f = std::vector<std::size_t>(frontier.size());
        
        #pragma cilk grainsize = BLOCK
        cilk_for (std::size_t i = 0; i < f.size(); i++) {
            if (frontier[i] > 0) {
                f[i] = ways[i].size();
            } else {
                f[i] = 0;
            }
        }
      
        std::vector<std::size_t> scanned = scan(f);
        std::vector<std::size_t> next_frontier = std::vector<std::size_t>(scanned.back());

        #pragma cilk grainsize = BLOCK
        cilk_for (std::size_t i = 0; i < frontier.size(); i++) {
            if (frontier[i] > 0) {
                std::size_t front = frontier[i] - 1;
                std::size_t start_pos = 0;

                if (i != 0) {
                    start_pos = scanned[i - 1];
                }
                
                for (std::size_t j = 0; j < ways[front].size(); j++) {
                    std::size_t v = ways[front][j];
                    if (v < front) {
                        continue;
                    }
                    bool orig = false;
                    if (marked[v].compare_exchange_strong(orig, true) ) {
                        next_frontier[start_pos + j] = v + 1;
                        result[v] = layer;
                    }
                }
            }
        }
        layer++;
        frontier = next_frontier;
    }
    return result;
}


int main(int argc, char** argv)
{

    std::cout << "Generating graph\n" << std::flush;
    auto test_graph = generate_cubic_graph(200);
    std::cout << "Test graph generated\n" << std::flush;

    auto seq_result = bfs_seq(test_graph.ways);
    if (test_graph.bfs == seq_result) {
        std::cout << "Seq bfs passed correnctness check\n" << std::flush;
    } else {
        std::cout << "Seq bfs not passed correnctness check\n" << std::flush;
        return 1;
    }

    auto par_result = bfs_par(test_graph.ways);
    if (test_graph.bfs == par_result) {
        std::cout << "Parallel bfs passed correnctness check\n" << std::flush;
    } else {
        std::cout << "Parallel bfs not passed correnctness check\n" << std::flush;
        return 1;
    }

    
    std::cout << "Generating perfomance graph, size: " << 500 << "\n" << std::flush;

    auto big_graph = generate_cubic_graph(500);
    long long total = 0;
    std::cout << "Test graph generated\n" << std::flush;
  
    std::cout << "Started seq test" << "\n";
    for (int i = 0; i < 5; i++) {
        auto start = std::chrono::high_resolution_clock::now();
        auto seq_result = bfs_seq(big_graph.ways);
        auto stop = std::chrono::high_resolution_clock::now();
        std::cout << seq_result.size() << "\n";
        total += std::chrono::duration_cast<std::chrono::microseconds>(stop - start).count();
        std::cout << i << ". Total: " << total << "\n" << std::flush;
    }

    total = 0;
    std::cout << "Started par test" << "\n";
    for (int i = 0; i < 5; i++) {
        auto start = std::chrono::high_resolution_clock::now();
        auto seq_result = bfs_seq(big_graph.ways);
        auto stop = std::chrono::high_resolution_clock::now();
        std::cout << seq_result.size() << "\n";
        total += std::chrono::duration_cast<std::chrono::microseconds>(stop - start).count();
        std::cout << i << ". Total: " << total << "\n" << std::flush;
    }

    return 0;
    
}
