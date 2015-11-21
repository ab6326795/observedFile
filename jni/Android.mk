LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_LDLIBS    := -L$(SYSROOT)/usr/lib -llog
LOCAL_MODULE    := talker_native
LOCAL_SRC_FILES := observedFile.cpp

include $(BUILD_SHARED_LIBRARY)
