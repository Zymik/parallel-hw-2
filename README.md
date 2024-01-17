Сборка:
```
cmake -S . -B build
cmake --build build
```

Запуск:
```
PARLAY_NUM_THREADS=4 ./build/parallel_algo
```

Результаты тестирования:
```
Generating test graph
Test graph generated
Seq bfs passed correctness check
Parallel bfs passed correctness check
Generating perfomance graph, size: 500
Test graph generated
Started seq test
0. Total: 60140ms
1. Total: 118358ms
2. Total: 176351ms
3. Total: 234578ms
4. Total: 293273ms
Avg sequential: 58654.6ms
Started par test
0. Total: 20929ms
1. Total: 41925ms
2. Total: 63130ms
3. Total: 84749ms
4. Total: 105910ms
Avg parallel: 21182ms
Speed up in 2.76908ms
```