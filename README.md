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
0. Total: 63738ms
1. Total: 126867ms
2. Total: 194119ms
3. Total: 257402ms
4. Total: 324084ms
Avg sequential: 64816.8ms
Started par test
0. Total: 26370ms
1. Total: 48611ms
2. Total: 75921ms
3. Total: 98848ms
4. Total: 121200ms
Avg parallel: 24240ms
Speed up in 2.67396ms
```