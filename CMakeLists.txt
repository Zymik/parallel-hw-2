cmake_minimum_required(VERSION 3.24)

project(parallel_algo)

set(CMAKE_CXX_STANDARD 17)

add_executable(parallel_algo main.cpp)

include(FetchContent)
FetchContent_Declare(parlaylib
        GIT_REPOSITORY  https://github.com/cmuparlay/parlaylib.git
        GIT_TAG         master
        )
FetchContent_GetProperties(parlaylib)
if(NOT parlaylib_POPULATED)
    FetchContent_Populate(parlaylib)
    add_subdirectory(${parlaylib_SOURCE_DIR} EXCLUDE_FROM_ALL)
endif()

# Customize oneTBB configuration


# Connect oneTBB core target to our application (oneTBB will be built as dependency)
target_link_libraries(parallel_algo PRIVATE parlay)
