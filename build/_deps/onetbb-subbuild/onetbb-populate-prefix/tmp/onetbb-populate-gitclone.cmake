# Distributed under the OSI-approved BSD 3-Clause License.  See accompanying
# file Copyright.txt or https://cmake.org/licensing for details.

cmake_minimum_required(VERSION 3.5)

if(EXISTS "/Users/ivankosolapov/CLionProjects/parallel_algo/build/_deps/onetbb-subbuild/onetbb-populate-prefix/src/onetbb-populate-stamp/onetbb-populate-gitclone-lastrun.txt" AND EXISTS "/Users/ivankosolapov/CLionProjects/parallel_algo/build/_deps/onetbb-subbuild/onetbb-populate-prefix/src/onetbb-populate-stamp/onetbb-populate-gitinfo.txt" AND
  "/Users/ivankosolapov/CLionProjects/parallel_algo/build/_deps/onetbb-subbuild/onetbb-populate-prefix/src/onetbb-populate-stamp/onetbb-populate-gitclone-lastrun.txt" IS_NEWER_THAN "/Users/ivankosolapov/CLionProjects/parallel_algo/build/_deps/onetbb-subbuild/onetbb-populate-prefix/src/onetbb-populate-stamp/onetbb-populate-gitinfo.txt")
  message(STATUS
    "Avoiding repeated git clone, stamp file is up to date: "
    "'/Users/ivankosolapov/CLionProjects/parallel_algo/build/_deps/onetbb-subbuild/onetbb-populate-prefix/src/onetbb-populate-stamp/onetbb-populate-gitclone-lastrun.txt'"
  )
  return()
endif()

execute_process(
  COMMAND ${CMAKE_COMMAND} -E rm -rf "/Users/ivankosolapov/CLionProjects/parallel_algo/build/_deps/onetbb-src"
  RESULT_VARIABLE error_code
)
if(error_code)
  message(FATAL_ERROR "Failed to remove directory: '/Users/ivankosolapov/CLionProjects/parallel_algo/build/_deps/onetbb-src'")
endif()

# try the clone 3 times in case there is an odd git clone issue
set(error_code 1)
set(number_of_tries 0)
while(error_code AND number_of_tries LESS 3)
  execute_process(
    COMMAND "/usr/bin/git"
            clone --no-checkout --config "advice.detachedHead=false" "https://github.com/oneapi-src/oneTBB.git" "onetbb-src"
    WORKING_DIRECTORY "/Users/ivankosolapov/CLionProjects/parallel_algo/build/_deps"
    RESULT_VARIABLE error_code
  )
  math(EXPR number_of_tries "${number_of_tries} + 1")
endwhile()
if(number_of_tries GREATER 1)
  message(STATUS "Had to git clone more than once: ${number_of_tries} times.")
endif()
if(error_code)
  message(FATAL_ERROR "Failed to clone repository: 'https://github.com/oneapi-src/oneTBB.git'")
endif()

execute_process(
  COMMAND "/usr/bin/git"
          checkout "v2021.11.0" --
  WORKING_DIRECTORY "/Users/ivankosolapov/CLionProjects/parallel_algo/build/_deps/onetbb-src"
  RESULT_VARIABLE error_code
)
if(error_code)
  message(FATAL_ERROR "Failed to checkout tag: 'v2021.11.0'")
endif()

set(init_submodules TRUE)
if(init_submodules)
  execute_process(
    COMMAND "/usr/bin/git" 
            submodule update --recursive --init 
    WORKING_DIRECTORY "/Users/ivankosolapov/CLionProjects/parallel_algo/build/_deps/onetbb-src"
    RESULT_VARIABLE error_code
  )
endif()
if(error_code)
  message(FATAL_ERROR "Failed to update submodules in: '/Users/ivankosolapov/CLionProjects/parallel_algo/build/_deps/onetbb-src'")
endif()

# Complete success, update the script-last-run stamp file:
#
execute_process(
  COMMAND ${CMAKE_COMMAND} -E copy "/Users/ivankosolapov/CLionProjects/parallel_algo/build/_deps/onetbb-subbuild/onetbb-populate-prefix/src/onetbb-populate-stamp/onetbb-populate-gitinfo.txt" "/Users/ivankosolapov/CLionProjects/parallel_algo/build/_deps/onetbb-subbuild/onetbb-populate-prefix/src/onetbb-populate-stamp/onetbb-populate-gitclone-lastrun.txt"
  RESULT_VARIABLE error_code
)
if(error_code)
  message(FATAL_ERROR "Failed to copy script-last-run stamp file: '/Users/ivankosolapov/CLionProjects/parallel_algo/build/_deps/onetbb-subbuild/onetbb-populate-prefix/src/onetbb-populate-stamp/onetbb-populate-gitclone-lastrun.txt'")
endif()
