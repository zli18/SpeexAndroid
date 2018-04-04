# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

include_directories(src/main/cpp/include/)

if (${ANDROID_ABI} STREQUAL "x86_64")
    include_directories(${ANDROID_SYSROOT}/usr/include/x86_64-linux-android)
elseif (${ANDROID_ABI} STREQUAL "x86")
    include_directories(${ANDROID_SYSROOT}/usr/include/i686-linux-android)
elseif (${ANDROID_ABI} STREQUAL "arm64-v8a")
    include_directories(${ANDROID_SYSROOT}/usr/include/aarch64-linux-android)
elseif (${ANDROID_ABI} STREQUAL "armeabi-v7a")
    include_directories(${ANDROID_SYSROOT}/usr/include/arm-linux-androideabi)
endif()

add_library( # Sets the name of the library.
             native-lib

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             src/main/cpp/native-lib.cpp
             src/main/cpp/echo_canceller.cpp
             src/main/cpp/libspeex/bits.c
             src/main/cpp/libspeex/buffer.c
             src/main/cpp/libspeex/cb_search.c
             src/main/cpp/libspeex/exc_10_16_table.c
             src/main/cpp/libspeex/exc_10_32_table.c
             src/main/cpp/libspeex/exc_20_32_table.c
             src/main/cpp/libspeex/exc_5_256_table.c
             src/main/cpp/libspeex/exc_5_64_table.c
             src/main/cpp/libspeex/exc_8_128_table.c
             src/main/cpp/libspeex/fftwrap.c
             src/main/cpp/libspeex/filterbank.c
             src/main/cpp/libspeex/filters.c
             src/main/cpp/libspeex/gain_table.c
             src/main/cpp/libspeex/gain_table_lbr.c
             src/main/cpp/libspeex/hexc_10_32_table.c
             src/main/cpp/libspeex/hexc_table.c
             src/main/cpp/libspeex/high_lsp_tables.c
             src/main/cpp/libspeex/jitter.c
             src/main/cpp/libspeex/kiss_fft.c
             src/main/cpp/libspeex/kiss_fftr.c
             src/main/cpp/libspeex/lpc.c
             src/main/cpp/libspeex/lsp.c
             src/main/cpp/libspeex/lsp_tables_nb.c
             src/main/cpp/libspeex/ltp.c
             src/main/cpp/libspeex/mdf.c
             src/main/cpp/libspeex/modes.c
             src/main/cpp/libspeex/modes_wb.c
             src/main/cpp/libspeex/nb_celp.c
             src/main/cpp/libspeex/preprocess.c
             src/main/cpp/libspeex/quant_lsp.c
             src/main/cpp/libspeex/resample.c
             src/main/cpp/libspeex/sb_celp.c
             src/main/cpp/libspeex/scal.c
             src/main/cpp/libspeex/smallft.c
             src/main/cpp/libspeex/speex.c
             src/main/cpp/libspeex/speex_callbacks.c
             src/main/cpp/libspeex/speex_header.c
             src/main/cpp/libspeex/stereo.c
             src/main/cpp/libspeex/vbr.c
             src/main/cpp/libspeex/vorbis_psy.c
             src/main/cpp/libspeex/vq.c
             src/main/cpp/libspeex/window.c
             src/main/cpp/libogg/bitwise.c
             src/main/cpp/libogg/framing.c)

set(CMAKE_C_FLAGS "-DFIXED_POINT -DUSE_KISS_FFT -DEXPORT=\"\" -UHAVE_CONFIG_H -DANDROID_ABI=armeabi-v7a")

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
                       native-lib

                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib} )