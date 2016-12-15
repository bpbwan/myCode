LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)
LOCAL_SRC_FILES:=  \
				untile.cpp \
				Jni_car.cpp
LOCAL_MODULE_PATH:= $(LOCAL_PATH)
LOCAL_MODULE := libcarline
LOCAL_CPPFLAGS := -g 
LOCAL_STATIC_LIBRARIES:=
LOCAL_SHARED_LIBRARIES:= \
				liblog\
				libandroid_runtime \
				libcutils 
			
include $(BUILD_SHARED_LIBRARY)
