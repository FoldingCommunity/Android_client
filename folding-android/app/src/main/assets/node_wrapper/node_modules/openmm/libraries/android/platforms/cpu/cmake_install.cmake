# Install script for directory: /Users/adimaggio2021/Downloads/test222/openmm-master/platforms/cpu

# Set the install prefix
if(NOT DEFINED CMAKE_INSTALL_PREFIX)
  set(CMAKE_INSTALL_PREFIX "/usr/local/openmm")
endif()
string(REGEX REPLACE "/$" "" CMAKE_INSTALL_PREFIX "${CMAKE_INSTALL_PREFIX}")

# Set the install configuration name.
if(NOT DEFINED CMAKE_INSTALL_CONFIG_NAME)
  if(BUILD_TYPE)
    string(REGEX REPLACE "^[^A-Za-z0-9_]+" ""
           CMAKE_INSTALL_CONFIG_NAME "${BUILD_TYPE}")
  else()
    set(CMAKE_INSTALL_CONFIG_NAME "Release")
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

# Install shared libraries without execute permission?
if(NOT DEFINED CMAKE_INSTALL_SO_NO_EXE)
  set(CMAKE_INSTALL_SO_NO_EXE "0")
endif()

# Is this installation the result of a crosscompile?
if(NOT DEFINED CMAKE_CROSSCOMPILING)
  set(CMAKE_CROSSCOMPILING "TRUE")
endif()

if("x${CMAKE_INSTALL_COMPONENT}x" STREQUAL "xUnspecifiedx" OR NOT CMAKE_INSTALL_COMPONENT)
  file(INSTALL DESTINATION "${CMAKE_INSTALL_PREFIX}/include/openmm/cpu" TYPE FILE FILES
    "/Users/adimaggio2021/Downloads/test222/openmm-master/platforms/cpu/include/AlignedArray.h"
    "/Users/adimaggio2021/Downloads/test222/openmm-master/platforms/cpu/include/CpuBondForce.h"
    "/Users/adimaggio2021/Downloads/test222/openmm-master/platforms/cpu/include/CpuCustomGBForce.h"
    "/Users/adimaggio2021/Downloads/test222/openmm-master/platforms/cpu/include/CpuCustomManyParticleForce.h"
    "/Users/adimaggio2021/Downloads/test222/openmm-master/platforms/cpu/include/CpuCustomNonbondedForce.h"
    "/Users/adimaggio2021/Downloads/test222/openmm-master/platforms/cpu/include/CpuGBSAOBCForce.h"
    "/Users/adimaggio2021/Downloads/test222/openmm-master/platforms/cpu/include/CpuGayBerneForce.h"
    "/Users/adimaggio2021/Downloads/test222/openmm-master/platforms/cpu/include/CpuKernelFactory.h"
    "/Users/adimaggio2021/Downloads/test222/openmm-master/platforms/cpu/include/CpuKernels.h"
    "/Users/adimaggio2021/Downloads/test222/openmm-master/platforms/cpu/include/CpuLangevinDynamics.h"
    "/Users/adimaggio2021/Downloads/test222/openmm-master/platforms/cpu/include/CpuLangevinMiddleDynamics.h"
    "/Users/adimaggio2021/Downloads/test222/openmm-master/platforms/cpu/include/CpuNeighborList.h"
    "/Users/adimaggio2021/Downloads/test222/openmm-master/platforms/cpu/include/CpuNonbondedForce.h"
    "/Users/adimaggio2021/Downloads/test222/openmm-master/platforms/cpu/include/CpuNonbondedForceFvec.h"
    "/Users/adimaggio2021/Downloads/test222/openmm-master/platforms/cpu/include/CpuPlatform.h"
    "/Users/adimaggio2021/Downloads/test222/openmm-master/platforms/cpu/include/CpuRandom.h"
    "/Users/adimaggio2021/Downloads/test222/openmm-master/platforms/cpu/include/CpuSETTLE.h"
    "/Users/adimaggio2021/Downloads/test222/openmm-master/platforms/cpu/include/windowsExportCpu.h"
    )
endif()

if(NOT CMAKE_INSTALL_LOCAL_ONLY)
  # Include the install script for each subdirectory.
  include("/Users/adimaggio2021/Downloads/test222/openmm-master/build/platforms/cpu/sharedTarget/cmake_install.cmake")

endif()

