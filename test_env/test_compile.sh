source ./emsdk-portable/emsdk_env.sh
emcc -v
emcc module.cpp -std=c++11 -larmadillo --bind -s WASM=1 -o module.html -O0 -s LINKABLE=1 -s EXPORT_ALL=1 -s ALLOW_MEMORY_GROWTH=1