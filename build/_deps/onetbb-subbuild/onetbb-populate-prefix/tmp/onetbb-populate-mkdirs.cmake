# Distributed under the OSI-approved BSD 3-Clause License.  See accompanying
# file Copyright.txt or https://cmake.org/licensing for details.

cmake_minimum_required(VERSION 3.5)

file(MAKE_DIRECTORY
  "/Users/ivankosolapov/CLionProjects/parallel_algo/build/_deps/onetbb-src"
  "/Users/ivankosolapov/CLionProjects/parallel_algo/build/_deps/onetbb-build"
  "/Users/ivankosolapov/CLionProjects/parallel_algo/build/_deps/onetbb-subbuild/onetbb-populate-prefix"
  "/Users/ivankosolapov/CLionProjects/parallel_algo/build/_deps/onetbb-subbuild/onetbb-populate-prefix/tmp"
  "/Users/ivankosolapov/CLionProjects/parallel_algo/build/_deps/onetbb-subbuild/onetbb-populate-prefix/src/onetbb-populate-stamp"
  "/Users/ivankosolapov/CLionProjects/parallel_algo/build/_deps/onetbb-subbuild/onetbb-populate-prefix/src"
  "/Users/ivankosolapov/CLionProjects/parallel_algo/build/_deps/onetbb-subbuild/onetbb-populate-prefix/src/onetbb-populate-stamp"
)

set(configSubDirs )
foreach(subDir IN LISTS configSubDirs)
    file(MAKE_DIRECTORY "/Users/ivankosolapov/CLionProjects/parallel_algo/build/_deps/onetbb-subbuild/onetbb-populate-prefix/src/onetbb-populate-stamp/${subDir}")
endforeach()
if(cfgdir)
  file(MAKE_DIRECTORY "/Users/ivankosolapov/CLionProjects/parallel_algo/build/_deps/onetbb-subbuild/onetbb-populate-prefix/src/onetbb-populate-stamp${cfgdir}") # cfgdir has leading slash
endif()
