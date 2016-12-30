#LOCAL_PATH:= $(call my-dir)
#APKBUILDPAH := $(LOCAL_PATH)
#include $(call all-makefiles-under,$(LOCAL_PATH))

#$(shell cd $(ANDROID_SRC)/$(APKBUILDPAH))

LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE_TAGS := optional
LOCAL_SRC_FILES := $(call all-java-files-under, src)  $(call all-renderscript-files-under, src)
LOCAL_PACKAGE_NAME := FlyaudioBackCar
LOCAL_JAVA_LIBRARIES := autochips
LOCAL_MODULE_PATH:=$(LOCAL_PATH)
LOCAL_AAPT_FLAGS += -C ldpi -c mdpi -c hdpi
include $(BUILD_PACKAGE)


#$(warning $(ANDROID_SRC)/$(APKBUILDPAH)  $(LOCAL_PATH))
#include $(call all-makefiles-under,$(LOCAL_PATH))