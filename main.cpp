#include <iostream>
#include <vector>
#include <algorithm>
#include <deque>
#include <atomic>
#include <parlay/parallel.h>
#include <parlay/primitives.h>

struct GraphGenerationResult {
    std::vector<std::vector<std::size_t>> ways;
    std::vector<std::size_t> bfs_result;
};

std::size_t get_pos(std::size_t i, std::size_t j, std::size_t k, std::size_t size) {
    return i + j * size + k * size * size;
}

GraphGenerationResult generate_cubic_graph(std::size_t size) {
    std::vector<std::vector<std::size_t>> graph = std::vector<std::vector<std::size_t>>(size * size * size);
    std::vector<std::size_t> bfs = std::vector<std::size_t>(size * size * size);

    for (std::size_t i = 0; i < size; i++) {
        for (std::size_t j = 0; j < size; j++) {
            for (std::size_t k = 0; k < size; k++) {
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
    return GraphGenerationResult{graph, bfs};
}

std::vector<std::size_t> bfs_seq(std::vector<std::vector<std::size_t>> &ways) {
    std::vector<bool> marked = std::vector<bool>(ways.size());
    std::deque<std::size_t> queue = std::deque<std::size_t>{0};
    std::vector<std::size_t> result = std::vector<std::size_t>(ways.size());
    marked[0] = true;
    while (!queue.empty()) {
        std::size_t next = queue.front();
        queue.pop_front();

        for (std::size_t j: ways[next]) {
            if (!marked[j]) {
                marked[j] = true;
                queue.push_back(j);
                result[j] = result[next] + 1;
            }
        }
    }

    return result;
}

std::vector<std::size_t> bfs_par(std::vector<std::vector<std::size_t>> &ways) {
    std::vector<std::atomic<bool>> marked = std::vector<std::atomic<bool>>(ways.size());

    marked[0] = true;
    std::vector<std::size_t> result = std::vector<std::size_t>(ways.size());
    parlay::sequence<std::size_t> frontier = parlay::sequence<std::size_t>(1);
    frontier[0] = 1;
    std::size_t layer = 1;

    while (!frontier.empty()) {
        parlay::sequence<std::size_t> scanned(frontier.size());

        parlay::parallel_for(0, scanned.size(),
                             [&](std::size_t i) {
                                 scanned[i] = ways[i].size();
                             }
        );

        std::size_t s = parlay::scan_inplace(scanned);
        parlay::sequence<std::size_t> next_frontier(s);

        parlay::parallel_for(0, frontier.size(),
                             [&](
                                     std::size_t i) {
                                 std::size_t front = frontier[i] - 1;
                                 std::size_t start_pos = 0;

                                 if (i != 0) {
                                     start_pos = scanned[i];
                                 }

                                 for (std::size_t j = 0; j < ways[front].size(); j++) {
                                     std::size_t v = ways[front][j];
                                     bool orig = false;
                                     if (marked[v].compare_exchange_strong(orig, true)) {
                                         next_frontier[start_pos + j] = v + 1;
                                         result[v] = layer;
                                     }
                                 }
                             }
        );
        layer++;
        frontier = parlay::filter(next_frontier, [](std::size_t u) { return u > 0; });
    }
    return result;
}

const int TEST_COUNT = 5;

int main() {
    std::cout << "Generating test graph\n" << std::flush;
    auto test_graph = generate_cubic_graph(200);
    std::cout << "Test graph generated\n" << std::flush;

    auto seq_result = bfs_seq(test_graph.ways);
    if (test_graph.bfs_result == seq_result) {
        std::cout << "Seq bfs passed correctness check\n" << std::flush;
    } else {
        std::cout << "Seq bfs not passed correctness check\n" << std::flush;
        return 1;
    }

    auto par_result = bfs_par(test_graph.ways);
    if (test_graph.bfs_result == par_result) {
        std::cout << "Parallel bfs passed correctness check\n" << std::flush;
    } else {
        std::cout << "Parallel bfs not passed correctness check\n" << std::flush;
        return 1;
    }

    std::cout << "Generating perfomance graph, size: " << 500 << "\n" << std::flush;

    auto big_graph = generate_cubic_graph(500);
    long long total = 0;
    std::cout << "Test graph generated\n" << std::flush;

    std::cout << "Started seq test" << "\n";
    for (int i = 0; i < TEST_COUNT; i++) {
        auto start = std::chrono::high_resolution_clock::now();
        bfs_seq(big_graph.ways);
        auto stop = std::chrono::high_resolution_clock::now();
        total += std::chrono::duration_cast<std::chrono::milliseconds>(stop - start).count();
        std::cout << i << ". Total: " << total << "ms" << "\n" << std::flush;
    }
    double seq_avg = (double) total / TEST_COUNT;
    std::cout << "Avg sequential: " << seq_avg << "ms" << "\n" << std::flush;

    total = 0;
    std::cout << "Started par test" << "\n";
    for (int i = 0; i < TEST_COUNT; i++) {
        auto start = std::chrono::high_resolution_clock::now();
        bfs_par(big_graph.ways);
        auto stop = std::chrono::high_resolution_clock::now();
        total += std::chrono::duration_cast<std::chrono::milliseconds>(stop - start).count();
        std::cout << i << ". Total: " << total  << "ms" << "\n" << std::flush;
    }
    double par_avg = (double) total / TEST_COUNT;
    std::cout << "Avg parallel: " << par_avg << "ms" << "\n";
    std::cout << "Speed up in " << seq_avg / par_avg << "ms" << "\n";
    return 0;
}