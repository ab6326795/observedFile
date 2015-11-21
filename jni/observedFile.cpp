/*
 * filelistener.cpp
 *
 *  Created on: 2014年11月21日
 *      Author: Administrator
 */

/* 头文件begin */
#include <observedFile.h>

/* 头文件end */

#ifdef __cplusplus
extern "C"
{
#endif

static jboolean isCopy = JNI_TRUE;

#pragma
/*
//Java字符串的类和获取方法ID
jclass    gStringClass;
jmethodID gmidStringInit;
jmethodID gmidStringGetBytes;

void init(JNIEnv* env){
	gStringClass= env->FindClass("java/lang/String");
	gmidStringGetBytes= env->GetMethodID(gStringClass, "getBytes", "(Ljava/lang/String;)[B");
    if (env->ExceptionCheck()) {
    	LOGE(env->GetStringUTFChars(env->NewStringUTF( "B method not found"), &isCopy));
    }
	gmidStringInit= env->GetMethodID(gStringClass, "", "(Ljava/lang/String;)V");
}

jstring formatToJstring(JNIEnv *env,const char* pchar,const char* encoding){
    jstring jencoding;
    jbyteArray bytes = env->NewByteArray(strlen(pchar));
    env->SetByteArrayRegion(bytes, 0, strlen(pchar), (jbyte*)pchar);
    jencoding = env->NewStringUTF(encoding);
    return (jstring)env->NewObject(gStringClass, gmidStringInit, bytes, jencoding);
}
*/

static jstring g_userSerial;
static JavaVM* g_jvm=NULL;
static pthread_t p_thread;
static bool bRunObserver;
static jstring g_serverName;
static int g_listener_interval;

JNIEXPORT void JNICALL Java_cn_com_talker_util_NativeFunction_init(JNIEnv* env,jobject obj,jstring observerName,jint interval){

	//保存全局JVM，以便在子线程中使用
	env->GetJavaVM(&g_jvm);
	g_listener_interval=interval<5? 5:interval;
	g_serverName=observerName;
}

void *thread_fun(void* arg){
	JNIEnv *env;
	LOGE("run");
	//Attach主线程
	if(g_jvm->AttachCurrentThread(&env,NULL)!=JNI_OK){
		LOGE("%s:AttachCurrentThread() failed",__FUNCTION__);
		return NULL;
	}
	while(bRunObserver){

		LOGE("runObserver");
		 // fork子进程，以执行轮询任务
		    pid_t pid = fork();
		    if (pid < 0)
		    {

		    	LOGE( "fork failed !!!");

		        exit(1);
		    }
		    else if (pid == 0){
		    	if (g_userSerial == NULL)
				{
					execlp("am", "am", "startservice", "-n",env->GetStringUTFChars(g_serverName,&isCopy), (char *)NULL);

				}
				else
				{
					execlp("am", "am", "startservice","--user", env->GetStringUTFChars( g_userSerial, &isCopy), "-n",env->GetStringUTFChars(g_serverName,&isCopy), (char *)NULL);
				}
		    }

		//一分钟检查一次
		sleep(g_listener_interval);
	}
	pthread_exit(0);
}

/*JNIEXPORT jint JNICALL Java_cn_com_talker_util_NativeFunction_runObserver(JNIEnv* env,jobject jthizz,jstring className,jstring method){
	jstring className=(jstring)arg;
			jclass jclazz= env->FindClass(env->GetStringUTFChars(className,&isCopy));
			if (env->ExceptionCheck()) {
				LOGI(env->GetStringUTFChars(env->NewStringUTF( "class not found"), &isCopy));
			}
			jmethodID methodID=env->GetStaticMethodID(jclazz,"listener","()Z");//"(Ljava/lang/String;)V");//
			if (env->ExceptionCheck()) {
				LOGI(env->GetStringUTFChars(env->NewStringUTF( "method not found"), &isCopy));
			}
		   // init(env);

			jboolean result=env->CallStaticBooleanMethod(jclazz,methodID);//formatToJstring(env,"萨达s","gbk"));//env->NewStringUTF("\u4e2d\u6587\u5b57\u8bfb\u6309\u65f6\u0061\u0073")
			LOGE("result:%d",result);

}*/
void startServiceObserver(){

	//启动一个线程
	bRunObserver=true;
	if(p_thread==0)
	{
		int result=pthread_create(&p_thread,NULL,&thread_fun,NULL);
		if(result!=JNI_OK){
			LOGE("create thread failed");
		}else{
			LOGE("create thread success");
		}
	}else{
		LOGE("线程已经存在");
	}

}

/**
 * 停止服务监听
 */
JNIEXPORT void JNICALL Java_cn_com_talker_util_NativeFunction_stopServiceObserver(JNIEnv* env,jobject obj)
{
	bRunObserver=false;
}

JNIEXPORT jboolean JNICALL Java_cn_com_talker_util_NativeFunction_destroyObserver(JNIEnv* env,jobject obj,jstring packName){

	char app_dir[128]={0};
	char app_files_dir[128]={0};
	char app_observed_file[128]={0};
	char app_look_file[128]={0};

	sprintf(app_dir,"/data/data/%s",env->GetStringUTFChars(packName,&isCopy));
	sprintf(app_files_dir,"%s/files",app_dir);
	sprintf(app_observed_file,"%s/observedFile",app_files_dir);
	sprintf(app_look_file,"%s/lookFile",app_files_dir);

	LOGE("destroyObserver");
	//停止服务监听
	bRunObserver=false;

	// 若监听文件所在文件夹不存在
	FILE *p_filesDir = fopen(app_files_dir, "r");
	if (p_filesDir == NULL)
	{
		LOGE( "destroyObserver failed 1");
		return false;
	}

	// 若被监听文件不存在，创建文件
	FILE *p_observedFile = fopen(app_observed_file, "r");
	if (p_observedFile == NULL)
	{
		LOGE( "destroyObserver failed 2");
		return false;
	}

	// 创建锁文件，通过检测加锁状态来保证只有一个卸载监听进程 只读打开
	int lockFileDescriptor = open(app_look_file, O_RDONLY);
	if (lockFileDescriptor == -1)
	{
		LOGE( "destroyObserver failed 3");
		return false;
	}
	int lockRet = flock(lockFileDescriptor, LOCK_UN | LOCK_NB);
	if (lockRet == -1)
	{
		LOGE( "lock_un failed!");
		return false;
	}
	LOGE( "lock_un success!");
	return true;
}

/*
 * Class:     main_activity_UninstalledObserverActivity
 * Method:    init
 * Signature: ()V
 * return: 子进程pid
 */
JNIEXPORT jint JNICALL Java_cn_com_talker_util_NativeFunction_startObserver(JNIEnv *env, jobject obj,jstring packName,jstring userSerial,
		jstring component,jstring url)
{

	char app_dir[128]={0};
	char app_files_dir[128]={0};
	char app_observed_file[128]={0};
	char app_look_file[128]={0};

	sprintf(app_dir,"/data/data/%s",env->GetStringUTFChars(packName,&isCopy));
	sprintf(app_files_dir,"%s/files",app_dir);
	sprintf(app_observed_file,"%s/observedFile",app_files_dir);
	sprintf(app_look_file,"%s/lookFile",app_files_dir);


    // fork子进程，以执行轮询任务
    pid_t pid = fork();
    if (pid < 0)
    {

    	LOGE( "fork failed !!!");

        exit(1);
    }
    else if (pid == 0)
    {
    	LOGE("fork success,i is child");

        // 若监听文件所在文件夹不存在，创建
        FILE *p_filesDir = fopen(app_files_dir, "r");
        if (p_filesDir == NULL)
        {
            int filesDirRet = mkdir(app_files_dir, S_IRWXU | S_IRWXG | S_IXOTH);
            if (filesDirRet == -1)
            {
            	LOGE( "mkdir failed !!!");

                exit(1);
            }
        }

        // 若被监听文件不存在，创建文件
        FILE *p_observedFile = fopen(app_observed_file, "r");
        if (p_observedFile == NULL)
        {
            p_observedFile = fopen(app_observed_file, "w");
        }
        fclose(p_observedFile);

        // 创建锁文件，通过检测加锁状态来保证只有一个卸载监听进程 只读打开
        int lockFileDescriptor = open(app_look_file, O_RDONLY);
        if (lockFileDescriptor == -1)
        {
            lockFileDescriptor = open(app_look_file, O_CREAT);
        }
        int lockRet = flock(lockFileDescriptor, LOCK_EX | LOCK_NB);
        if (lockRet == -1)
        {
        	LOGE( "observed by another process");

            exit(0);
        }
        LOGE( "observed by child process");

        // 分配空间，以便读取event
        void *p_buf = malloc(sizeof(struct inotify_event));
        if (p_buf == NULL)
        {
            LOGE( "malloc failed !!!");

            exit(1);
        }
        // 分配空间，以便打印mask
        int maskStrLength = 7 + 10 + 1;// mask=0x占7字节，32位整形数最大为10位，转换为字符串占10字节，'\0'占1字节
        char *p_maskStr = (char*)malloc(maskStrLength);
        if (p_maskStr == NULL)
        {
            free(p_buf);

            LOGE("malloc failed !!!");

            exit(1);
        }

        // 开始监听
        LOGE("start observe");

        g_userSerial=userSerial;
        startServiceObserver();

        // 初始化
        int fileDescriptor = inotify_init();
        if (fileDescriptor < 0)
        {
            free(p_buf);
            free(p_maskStr);

            LOGE( "inotify_init failed !!!");

            exit(1);
        }

        // 添加被监听文件到监听列表
        int watchDescriptor = inotify_add_watch(fileDescriptor, app_observed_file, IN_ALL_EVENTS);

        if (watchDescriptor < 0)
        {
            free(p_buf);
            free(p_maskStr);

            LOGE("inotify_add_watch failed !!!");

            exit(1);
        }

        while(1)
        {
            // read会阻塞进程
            size_t readBytes = read(fileDescriptor, p_buf, sizeof(struct inotify_event));


            LOGE("mask=0x%x",((struct inotify_event *) p_buf)->mask);

            // 若文件被删除，可能是已卸载，还需进一步判断app文件夹是否存在
            if (IN_DELETE_SELF == ((struct inotify_event *) p_buf)->mask)
            {
            	jboolean isDel=false;
            	/**
            	 * 用10秒的时间来轮询，而不应该直接就跑去重新创建，这可能会导致文件重复创建删除都无法删除。
            	 * 场景：监控文件被删除，系统还在删除其他文件，此时APP又去重新创建文件夹，那么会导致系统在删除目录时无法删除，
            	 * 导致程序以为APP没有被卸载
            	 */
            	for(int i=0;i<10;i++){
                    FILE *p_appDir = fopen(app_dir, "r");
                    // 确认已卸载
                    if (p_appDir == NULL)
                    {
                    	LOGE("file delete");
                        inotify_rm_watch(fileDescriptor, watchDescriptor);
                        isDel=true;
                        break;
                    }
                    fclose(p_appDir);
                    sleep(1);
            	}
            	if(isDel){
            		break;
            	}else {
            		// 10秒钟后发现文件夹都存在，说明未卸载，可能用户执行了"清除数据"

                    LOGE("delele app cache");

                    // 重新创建被监听文件，并重新监听
                        // 若监听文件所在文件夹不存在，创建
				   FILE *p_filesDir = fopen(app_files_dir, "r");
				   if (p_filesDir == NULL)
				   {
					   int filesDirRet = mkdir(app_files_dir, S_IRWXU | S_IRWXG | S_IXOTH);
					   if (filesDirRet == -1)
					   {
						   LOGE("mkdir failed !!!");

						   exit(1);
					   }
				   }

                    FILE *p_observedFile = fopen(app_observed_file, "w");
                    fclose(p_observedFile);

                    int watchDescriptor = inotify_add_watch(fileDescriptor, app_observed_file, IN_ALL_EVENTS);
                    if (watchDescriptor < 0)
                    {
                        free(p_buf);
                        free(p_maskStr);

                        LOGE("inotify_add_watch failed !!!");

                        exit(1);
                    }
            	}

            }
        }

        // 释放资源
        free(p_buf);
        free(p_maskStr);

        // 停止监听
        LOGE(  "stop observe");


        if (userSerial == NULL)
        {
        	 //LOGE( env->GetStringUTFChars(env->NewStringUTF( "a"), &isCopy));
            // 执行命令am start -a android.intent.action.VIEW -d $(url)
        	 if(component!=NULL){
        		 execlp("am", "am", "start","-n",env->GetStringUTFChars(component,&isCopy), "-a", "android.intent.action.VIEW", "-d", env->GetStringUTFChars(url,&isCopy), (char *)NULL);
        	 }else{
                 execlp("am", "am", "start", "-a", "android.intent.action.VIEW", "-d", env->GetStringUTFChars(url,&isCopy), (char *)NULL);
        	 }

        }
        else
        {
        	//LOGE( env->GetStringUTFChars(env->NewStringUTF( "b"), &isCopy));
/*        	sprintf(debug_buffer,"--user %s  url: %s",env->GetStringUTFChars( userSerial, &isCopy),env->GetStringUTFChars( url, &isCopy));
        	 LOGE(debug_buffer);*/
            // 执行命令am start --user userSerial -a android.intent.action.VIEW -d $(url)
        	if(component!=NULL){
        		execlp("am", "am", "start", "--user", env->GetStringUTFChars( userSerial, &isCopy), "-n",env->GetStringUTFChars(component,&isCopy),
        				"-a", "android.intent.action.VIEW", "-d", env->GetStringUTFChars(url,&isCopy), (char *)NULL);
        	}else{
                execlp("am", "am", "start", "--user", env->GetStringUTFChars( userSerial, &isCopy), "-a", "android.intent.action.VIEW", "-d", env->GetStringUTFChars(url,&isCopy), (char *)NULL);
        	}
        }

        // 执行命令失败log
        LOGE("exec AM command failed !!!");
    }
    else
    {
    	LOGE("super exit");
        // 父进程直接退出，使子进程被init进程领养，以避免子进程僵死，同时返回子进程pid
        return pid;
    }
}

#ifdef __cplusplus
}
#endif
