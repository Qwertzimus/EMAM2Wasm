docker exec -it emscripten emcc ./test_env/module.cpp -std=c++11 --bind -s WASM=1 -o module.html -O0 -s LINKABLE=1 -s EXPORT_ALL=1 -I./armadillo-8.300.3/include