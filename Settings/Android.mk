LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_JAVA_LIBRARIES := bouncycastle telephony-common autochips
LOCAL_STATIC_JAVA_LIBRARIES := guava android-support-v4 jsr305
MTKSETTINGS_SRC_PATH := ../../../autochips/packages/apps/Settings/src

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES := $(call all-java-files-under, src)

LOCAL_SRC_FILES := $(filter-out \
                src/com/android/settings/SoundSettings.java \
                ,$(LOCAL_SRC_FILES))
LOCAL_SRC_FILES := $(filter-out \
                src/com/android/settings/wfd/WifiDisplaySettings.java \
                ,$(LOCAL_SRC_FILES))
LOCAL_SRC_FILES := $(filter-out \
                src/com/android/settings/DisplaySettings.java \
                ,$(LOCAL_SRC_FILES))
LOCAL_SRC_FILES := $(filter-out \
                src/com/android/settings/RingerVolumePreference.java \
                ,$(LOCAL_SRC_FILES))
LOCAL_SRC_FILES := $(filter-out \
                src/com/android/settings/deviceinfo/UsbSettings.java \
                ,$(LOCAL_SRC_FILES))
LOCAL_SRC_FILES := $(filter-out \
                src/com/android/settings/deviceinfo/StorageVolumePreferenceCategory.java \
                ,$(LOCAL_SRC_FILES))
LOCAL_SRC_FILES := $(filter-out \
                src/com/android/settings/SettingsPreferenceFragment.java \
                ,$(LOCAL_SRC_FILES))
LOCAL_SRC_FILES := $(filter-out \
                src/com/android/settings/applications/InstalledAppDetails.java \
                ,$(LOCAL_SRC_FILES))
LOCAL_SRC_FILES := $(filter-out \
                src/com/android/settings/SettingsLicenseActivity.java \
                ,$(LOCAL_SRC_FILES))
LOCAL_SRC_FILES := $(filter-out \
                src/com/android/settings/Settings.java \
                ,$(LOCAL_SRC_FILES))
LOCAL_SRC_FILES := $(filter-out \
                src/com/android/settings/MasterClearConfirm.java \
                ,$(LOCAL_SRC_FILES))
LOCAL_SRC_FILES := $(filter-out \
                src/com/android/settings/ProgressCategory.java \
                ,$(LOCAL_SRC_FILES))
LOCAL_SRC_FILES := $(filter-out \
                src/com/android/settings/DevelopmentSettings.java \
                ,$(LOCAL_SRC_FILES))
LOCAL_SRC_FILES := $(filter-out \
                src/com/android/settings/deviceinfo/Memory.java \
                ,$(LOCAL_SRC_FILES))
LOCAL_SRC_FILES := $(filter-out \
                src/com/android/settings/DataUsageSummary.java \
                ,$(LOCAL_SRC_FILES))
LOCAL_SRC_FILES := $(filter-out \
                src/com/android/settings/AccessibilitySettings.java \
                ,$(LOCAL_SRC_FILES))
LOCAL_SRC_FILES := $(filter-out \
                src/com/android/settings/wifi/AdvancedWifiSettings.java \
                ,$(LOCAL_SRC_FILES))
LOCAL_SRC_FILES := $(filter-out \
                src/com/android/settings/wifi/WifiEnabler.java \
                ,$(LOCAL_SRC_FILES))
LOCAL_SRC_FILES := $(filter-out \
                src/com/android/settings/bluetooth/% \
                ,$(LOCAL_SRC_FILES))
LOCAL_SRC_FILES := $(filter-out \
                src/com/android/settings/wifi/WifiApEnabler.java \
                ,$(LOCAL_SRC_FILES))
LOCAL_SRC_FILES := $(filter-out \
                src/com/android/settings/SecuritySettings.java \
                ,$(LOCAL_SRC_FILES))
LOCAL_SRC_FILES += $(call all-java-files-under, $(MTKSETTINGS_SRC_PATH))

LOCAL_PACKAGE_NAME := Settings
LOCAL_CERTIFICATE := platform

LOCAL_PROGUARD_FLAG_FILES := proguard.flags
#add flyaudio---
 include $(BUILD_PACKAGE)
#add flyaudio---
# Use the folloing include to make our test apk.
#include $(call all-makefiles-under,$(LOCAL_PATH))
