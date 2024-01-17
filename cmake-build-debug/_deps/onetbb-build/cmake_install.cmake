# Install script for directory: /Users/ivankosolapov/CLionProjects/parallel_algo/cmake-build-debug/_deps/onetbb-src

# Set the install prefix
if(NOT DEFINED CMAKE_INSTALL_PREFIX)
  set(CMAKE_INSTALL_PREFIX "/usr/local")
endif()
string(REGEX REPLACE "/$" "" CMAKE_INSTALL_PREFIX "${CMAKE_INSTALL_PREFIX}")

# Set the install configuration name.
if(NOT DEFINED CMAKE_INSTALL_CONFIG_NAME)
  if(BUILD_TYPE)
    string(REGEX REPLACE "^[^A-Za-z0-9_]+" ""
           CMAKE_INSTALL_CONFIG_NAME "${BUILD_TYPE}")
  else()
    set(CMAKE_INSTALL_CONFIG_NAME "Debug")
  endif()
  message(STATUS "Install configuration: \"${CMAKE_INSTALL_CONFIG_NAME}\"")
endif()

# Set the component getting installed.
if(NOT CMAKE_INSTALL_COMPONENT)
  if(COMPONENT)
    message(STATUS "Install component: \"${COMPONENT}\"")
    set(CMAKE_INSTALL_COMPONENT "${COMPONENT}")
  else()
    set(CMAKE_INSTALL_COMPONENT)
  endif()
endif()

# Is this installation the result of a crosscompile?
if(NOT DEFINED CMAKE_CROSSCOMPILING)
  set(CMAKE_CROSSCOMPILING "FALSE")
endif()

# Set default install directory permissions.
if(NOT DEFINED CMAKE_OBJDUMP)
  set(CMAKE_OBJDUMP "/Library/Developer/CommandLineTools/usr/bin/objdump")
endif()

if(CMAKE_INSTALL_COMPONENT STREQUAL "devel" OR NOT CMAKE_INSTALL_COMPONENT)
  file(INSTALL DESTINATION "${CMAKE_INSTALL_PREFIX}/include" TYPE DIRECTORY FILES "/Users/ivankosolapov/CLionProjects/parallel_algo/cmake-build-debug/_deps/onetbb-src/include/")
endif()

if(CMAKE_INSTALL_COMPONENT STREQUAL "devel" OR NOT CMAKE_INSTALL_COMPONENT)
  if(EXISTS "$ENV{DESTDIR}${CMAKE_INSTALL_PREFIX}/lib/cmake/TBB/TBBTargets.cmake")
    file(DIFFERENT _cmake_export_file_changed FILES
         "$ENV{DESTDIR}${CMAKE_INSTALL_PREFIX}/lib/cmake/TBB/TBBTargets.cmake"
         "/Users/ivankosolapov/CLionProjects/parallel_algo/cmake-build-debug/_deps/onetbb-build/CMakeFiles/Export/50bf70e196cec95d4d513cf01ed33e20/TBBTargets.cmake")
    if(_cmake_export_file_changed)
      file(GLOB _cmake_old_config_files "$ENV{DESTDIR}${CMAKE_INSTALL_PREFIX}/lib/cmake/TBB/TBBTargets-*.cmake")
      if(_cmake_old_config_files)
        string(REPLACE ";" ", " _cmake_old_config_files_text "${_cmake_old_config_files}")
        message(STATUS "Old export file \"$ENV{DESTDIR}${CMAKE_INSTALL_PREFIX}/lib/cmake/TBB/TBBTargets.cmake\" will be replaced.  Removing files [${_cmake_old_config_files_text}].")
        unset(_cmake_old_config_files_text)
        file(REMOVE ${_cmake_old_config_files})
      endif()
      unset(_cmake_old_config_files)
    endif()
    unset(_cmake_export_file_changed)
  endif()
  file(INSTALL DESTINATION "${CMAKE_INSTALL_PREFIX}/lib/cmake/TBB" TYPE FILE FILES "/Users/ivankosolapov/CLionProjects/parallel_algo/cmake-build-debug/_deps/onetbb-build/CMakeFiles/Export/50bf70e196cec95d4d513cf01ed33e20/TBBTargets.cmake")
  if(CMAKE_INSTALL_CONFIG_NAME MATCHES "^([Dd][Ee][Bb][Uu][Gg])$")
    file(INSTALL DESTINATION "${CMAKE_INSTALL_PREFIX}/lib/cmake/TBB" TYPE FILE FILES "/Users/ivankosolapov/CLionProjects/parallel_algo/cmake-build-debug/_deps/onetbb-build/CMakeFiles/Export/50bf70e196cec95d4d513cf01ed33e20/TBBTargets-debug.cmake")
  endif()
endif()

if(CMAKE_INSTALL_COMPONENT STREQUAL "devel" OR NOT CMAKE_INSTALL_COMPONENT)
  file(INSTALL DESTINATION "${CMAKE_INSTALL_PREFIX}/lib/cmake/TBB" TYPE FILE FILES
    "/Users/ivankosolapov/CLionProjects/parallel_algo/cmake-build-debug/_deps/onetbb-build/TBBConfig.cmake"
    "/Users/ivankosolapov/CLionProjects/parallel_algo/cmake-build-debug/_deps/onetbb-build/TBBConfigVersion.cmake"
    )
endif()

if(CMAKE_INSTALL_COMPONENT STREQUAL "devel" OR NOT CMAKE_INSTALL_COMPONENT)
  file(INSTALL DESTINATION "${CMAKE_INSTALL_PREFIX}/share/doc/TBB" TYPE FILE FILES "/Users/ivankosolapov/CLionProjects/parallel_algo/cmake-build-debug/_deps/onetbb-src/README.md")
endif()

if(NOT CMAKE_INSTALL_LOCAL_ONLY)
  # Include the install script for each subdirectory.
  include("/Users/ivankosolapov/CLionProjects/parallel_algo/cmake-build-debug/_deps/onetbb-build/src/tbb/cmake_install.cmake")
  include("/Users/ivankosolapov/CLionProjects/parallel_algo/cmake-build-debug/_deps/onetbb-build/src/tbbmalloc/cmake_install.cmake")
  include("/Users/ivankosolapov/CLionProjects/parallel_algo/cmake-build-debug/_deps/onetbb-build/src/tbbmalloc_proxy/cmake_install.cmake")
  include("/Users/ivankosolapov/CLionProjects/parallel_algo/cmake-build-debug/_deps/onetbb-build/src/tbbbind/cmake_install.cmake")
  include("/Users/ivankosolapov/CLionProjects/parallel_algo/cmake-build-debug/_deps/onetbb-build/cmake/post_install/cmake_install.cmake")

endif()
