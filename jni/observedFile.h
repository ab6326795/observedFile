#include <jni.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <unistd.h>
#include <fcntl.h>
#include <sys/inotify.h>
#include <sys/stat.h>

#include<pthread.h>

#include <android/log.h>

#define MEM_ZERO(pDest, destSize) memset(pDest, 0, destSize)

#define LOG_TAG "N_filelistener.h"

#define LOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__))
#define LOGW(...) ((void)__android_log_print(ANDROID_LOG_WARN, LOG_TAG, __VA_ARGS__))
#define LOGE(...) ((void)__android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__))


#ifndef _Included_main_activity_UninstalledObserverActivity
#define _Included_main_activity_UninstalledObserverActivity
#ifdef __cplusplus
extern "C" {
#endif

/*
 * Class:     main_activity_UninstalledObserverActivity
 * Method:    init
 * Signature: (Ljava/lang/String;)Vcn.com.talker.util
 */
JNIEXPORT jint JNICALL Java_cn_com_talker_util_NativeFunction_startObserver(JNIEnv *, jobject,jstring ,jstring,jstring, jstring);


JNIEXPORT void JNICALL Java_cn_com_talker_util_NativeFunction_init(JNIEnv*,jobject,jstring,jint);

JNIEXPORT void JNICALL Java_cn_com_talker_util_NativeFunction_stopServiceObserver(JNIEnv*,jobject);

JNIEXPORT jboolean JNICALL Java_cn_com_talker_util_NativeFunction_destroyObserver(JNIEnv*,jobject,jstring);

#ifdef __cplusplus
}
#endif
#endif

