# Distributed under the OSI-approved BSD 3-Clause License.  See accompanying
# file Copyright.txt or https://cmake.org/licensing for details.

cmake_minimum_required(VERSION 3.5)

file(MAKE_DIRECTORY
  "/Users/ivankosolapov/CLionProjects/parallel_algo/cmake-build-debug/_deps/parlaylib-src"
  "/Users/ivankosolapov/CLionProjects/parallel_algo/cmake-build-debug/_deps/parlaylib-build"
  "/Users/ivankosolapov/CLionProjects/parallel_algo/cmake-build-debug/_deps/parlaylib-subbuild/parlaylib-populate-prefix"
  "/Users/ivankosolapov/CLionProjects/parallel_algo/cmake-build-debug/_deps/parlaylib-subbuild/parlaylib-populate-prefix/tmp"
  "/Users/ivankosolapov/CLionProjects/parallel_algo/cmake-build-debug/_deps/parlaylib-subbuild/parlaylib-populate-prefix/src/parlaylib-populate-stamp"
  "/Users/ivankosolapov/CLionProjects/parallel_algo/cmake-build-debug/_deps/parlaylib-subbuild/parlaylib-populate-prefix/src"
  "/Users/ivankosolapov/CLionProjects/parallel_algo/cmake-build-debug/_deps/parlaylib-subbuild/parlaylib-populate-prefix/src/parlaylib-populate-stamp"
)

set(configSubDirs )
foreach(subDir IN LISTS configSubDirs)
    file(MAKE_DIRECTORY "/Users/ivankosolapov/CLionProjects/parallel_algo/cmake-build-debug/_deps/parlaylib-subbuild/parlaylib-populate-prefix/src/parlaylib-populate-stamp/${subDir}")
endforeach()
if(cfgdir)
  file(MAKE_DIRECTORY "/Users/ivankosolapov/CLionProjects/parallel_algo/cmake-build-debug/_deps/parlaylib-subbuild/parlaylib-populate-prefix/src/parlaylib-populate-stamp${cfgdir}") # cfgdir has leading slash
endif()
