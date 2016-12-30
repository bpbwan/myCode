//author baipeibin
#include<jni.h>
#include<android/log.h>
#include<stdio.h>
#include<stdlib.h>
#include<string.h>
#include<sys/types.h>
#include<sys/stat.h>
#include<fcntl.h>
#include<utils/misc.h>
#include<JNIHelp.h>
#include<android_runtime/AndroidRuntime.h>
#include<string.h>
#include<sys/stat.h>
#include"untile.h"
#include"cartrack.h"

CUntitled *m_pCUntitled;
FuncParam *m_pFuncParam;
extern int   width;
extern int height;
extern int   my_h;
extern int  my_w;

bool isFileOK = false;
bool isFileOK2 = false;

bool isBusy = false;
unsigned char haveSave[101];
unsigned char haveSave2[101];
struct BcData bcdata[4];

jfloat  buf [DOTSIZE];
jfloat  buf2[RLSIZE];

int pIn =0;
int pAngle = 0;
unsigned char isfileok[24]={0};
int tmode = 0;
bool judgeFlag = false;

char *filepath1 = "/data/.obdata";
char *filepath2 = "/data/.obdata2";
char *filepath3 = "/data/.obdata3";
char *filepath4 = "/data/.obdata4";

static pthread_t create_thread(const char* name, void (*start)(void *), void* arg)  
{  
    return (pthread_t)android::AndroidRuntime::createJavaThread(name, start, arg);  
}  


void CUntitled::Reset(int cartype)
{
	carType = cartype;
	if(carType==214 || carType == 215)  //track ģʽ�ɵ�
		old_track = true;


	__android_log_print(4, "BCR", "cartype =%d\n",cartype);

}

void CUntitled::saveTrackFile(int data)
{

		if(data == 1)
		{		
			isBusy = true;
			judgeFlag = true;
		LOG("backcar is busy");
			}
		else if(data == 0)
		{
					isBusy = false;
		LOG("backcar is not busy");
					isFileOkAndSaveTrack(m_pCUntitled->trailmode);
					m_pCUntitled->trailmode = 0;
		}
}

void CUntitled::ResetTrackFileIsNotOk(){
	system("rm /data/.obdata*");
		
}

void isFileOkAndSaveTrack(int mode){
		if(mode == 0)
			return;
		struct stat sta_file;
		char tmpfilename[30];
		memset(tmpfilename, 0, 30);
		switch(mode)
			{
			case 1:  memcpy(tmpfilename, filepath1, strlen(filepath1));
					break;
			case 2:	memcpy(tmpfilename, filepath2, strlen(filepath2));
					break;
			case 3: memcpy(tmpfilename, filepath3, strlen(filepath3));
					break;
			case 4: memcpy(tmpfilename, filepath4, strlen(filepath4));
					break;
			}
		__android_log_print(4, "BCR ", " tmpfilename %s  ",tmpfilename);
		stat(tmpfilename,&sta_file);
		if(sta_file.st_size !=(24+101*(RLSIZE*4+4*(MSIZE+1))))
			{	
				isFileOK = false;		
				struct argSaveFile *arg = (struct argSaveFile*)malloc(sizeof(struct argSaveFile));
				memset(arg, 0, sizeof(struct argSaveFile));
				arg->mode = mode;
				memcpy(arg->filename, tmpfilename, strlen(tmpfilename));
				__android_log_print(4, "BCR ", " arg mode %d  %s",arg->mode, arg->filename);
				create_thread("BackCar", &SaveAsFile, (void *)arg);
			}	

}


void JudgeIsFileOk()
{
	if(!judgeFlag)
		return;
		struct stat sta_file;
		char tmpfilename[30];
		memset(tmpfilename, 0, 30);
		switch(m_pCUntitled->trailmode)
			{
			case 1:  memcpy(tmpfilename, filepath1, strlen(filepath1));
					break;
			case 2:	memcpy(tmpfilename, filepath2, strlen(filepath2));
					break;
			case 3: memcpy(tmpfilename, filepath3, strlen(filepath3));
					break;
			case 4: memcpy(tmpfilename, filepath4, strlen(filepath4));
					break;
			}
		stat(tmpfilename,&sta_file);
		if(sta_file.st_size !=(24+101*(RLSIZE*4+4*(MSIZE+1))))
			{	
				isFileOK = false;		
				LOG("isFileOK  false");
			}else {
			isFileOK = true;
				LOG("isFileOK  true");
				}
			
		judgeFlag = false;
}

void SaveAsFile(void *argv)
{
	int mAngle = 40;
	int pn = 0 ;
	struct argSaveFile *mArg = (struct argSaveFile*)argv;
	//__android_log_print(4, "BCR ", " mArg mode %d  %s",mArg->mode, mArg->filename);
	if(transferMode(mArg->mode)<0) 
		return;

	memset(haveSave, 0, 101);
	m_pCUntitled->RLBuf2 = new float[RLSIZE];
	m_pCUntitled->maxP2 = 0;
	for(mAngle = 40; mAngle<=140; mAngle++)
	{	
		if(isBusy) goto OUT;
		//if(mAngle == 90) continue;
		memset((unsigned char*)m_pCUntitled->RLBuf2, 0 , RLSIZE*4);
		if(mAngle >  90)
	 		m_pCUntitled->SampleLocusLine_R(mAngle,true);
		else if(mAngle <=90 && mAngle !=0)
      			m_pCUntitled->SampleLocusLine_L(mAngle,true);
		saveData(mAngle-40, ++pn, mArg->filename, &isFileOK);
	}
	OUT:
	free(m_pCUntitled->RLBuf2);
}

void saveData(int a, int b, char *filename, bool *flag)
{
	if(haveSave[a] == 0)
		{		
			haveSave[a] = 1;
			int	fd = open(filename, O_WRONLY|O_CREAT, 0777);
			lseek(fd, 24+a*(RLSIZE*4+4*(MSIZE+1)), SEEK_SET); 
			if(write(fd, &(m_pCUntitled->maxP2), 4)<0)
				goto ERRO;
			if(write(fd, (unsigned char*)m_pCUntitled->mBuf, MSIZE*4)<0)
				goto ERRO;
			if(write(fd, (unsigned char*)m_pCUntitled->RLBuf2, RLSIZE*4)<0)
				goto ERRO;

			if(b == 100)
				{
					lseek(fd,0, SEEK_SET);
					if(write(fd, "FlyAudioBackCar", sizeof("FlyAudioBackCar"))<0)
						goto ERRO;
					*flag = true;
					__android_log_print(4, "BCR ", " backcar [%s] file  save ok",filename);
				}
			
			close(fd);
		}
		return;
	ERRO:
		*flag = false;
		isBusy = true;
			

}



int transferMode(int mode)
{
	__android_log_print(4, "BCR ", " transferMode  %d in",mode );
	int m = mode-1;
	if(m>0&&bcdata[m].id==0)
		return -1;
		m_pFuncParam->carP.Lc =bcdata[m].Lcar;
		m_pFuncParam->carP.Wc= bcdata[m].Wcar ;
		m_pFuncParam->camP.nhigh =  bcdata[m].high;
		m_pFuncParam->camP.nVision =  bcdata[m].Vision;
		m_pFuncParam->camP.dPu=  bcdata[m].Pu;
		m_pFuncParam->camP.dPv =	bcdata[m].Pv;
		m_pFuncParam->camP.dx_angle = bcdata[m].x_angle;
		m_pFuncParam->parkP.Smax = bcdata[m].Smax;
		m_pFuncParam->camP.dy_angle =bcdata[m].y_angle;
		m_pFuncParam->parkP.atsafe = bcdata[m].atsafe;
		m_pCUntitled->maxY = bcdata[m].mMaxY;
		m_pCUntitled->mMinY = bcdata[m].mMinY;
		m_pCUntitled->LcR = bcdata[m].LcR;
	 	m_pCUntitled->LcL = bcdata[m].LcL;
		m_pCUntitled->lpu = bcdata[m].LPu;
		m_pCUntitled->rpu = bcdata[m].RPu;
		m_pCUntitled->mNumF=bcdata[m].mNumF;
		m_pFuncParam->parkP.dotNum = bcdata[m].dotNum;
		m_pCUntitled->midLineY1 = bcdata[m].midLineY1;
		m_pCUntitled->midLineY2 = bcdata[m].midLineY2;
		m_pCUntitled->midLineY3 = bcdata[m].midLineY3;

		
		if(bcdata[m].trackMode != 0 && m_pCUntitled->trackUsingMode != bcdata[m].trackMode) {
			m_pCUntitled->trackUsingMode = bcdata[m].trackMode;
			m_pCUntitled->SwitchTrackUsingMode();
		}
		
	m_pCUntitled->Test_ChangeParam_ParkLine(*m_pFuncParam);

	
	__android_log_print(4, "BCR ", " transferMode out");
	return bcdata[m].id;
}


void  CTestRealCar( )
{
	m_pFuncParam = new FuncParam();
	m_pCUntitled = new CUntitled();
	m_pCUntitled->ResetTrackFileIsNotOk();
}
void com_flyaudio_backcar_SendParam(JNIEnv *env, jobject obj, jbyte cmd, jint len, jfloatArray mdata)
{
	BYTE buff[2] ={0};
	buff[0]  = (BYTE)cmd;
	jboolean isCopy = false;
	jfloat* data = env->GetFloatArrayElements(mdata, &isCopy); 
	
	__android_log_print(4, "BCR", " cmd    %d\n",cmd);
	m_pCUntitled->Req_RealCarParamChange( buff,  len, data, m_pFuncParam);
	env->ReleaseFloatArrayElements(mdata, data, 0);  
}

void setTrailmode(JNIEnv  *env,  jobject  thiz,  jint mode)
{
	if(m_pCUntitled->trailmode != mode)
	{
		m_pCUntitled->trailmode = mode;
		judgeFlag = true;
		if(transferMode(mode)<0)
		{
			jmethodID Method = env->GetMethodID(env->GetObjectClass(thiz),"ReXmlDataInit","()V"); 
			tmode = 0;
			env->CallVoidMethod(thiz,Method);
			transferMode(mode);
		}
	}
}


void com_flyaudio_backcar_Change_L(JNIEnv  *env,  jobject  thiz, jdouble angle,  jint trimode )
{
	//__android_log_print(4, "BCR", " R     %0.1f\n", angle);
	JudgeIsFileOk();
	pAngle = angle;
	if(isFileOK)
		return;
	m_pCUntitled->SampleLocusLine_L(angle, false);
	 
}

void com_flyaudio_backcar_Change_R(JNIEnv  *env,  jobject  thiz,jdouble angle, jint trimode )
{
	//__android_log_print(4, "BCR", "L	   %0.1f\n",angle);
	JudgeIsFileOk();
	pAngle = angle;
	if(isFileOK)
		return;
	m_pCUntitled->SampleLocusLine_R(angle,false);
}
void setFileIsOk(int mode, bool  flag)
{
	isFileOK = flag; 
}






jintArray com_flyaudio_backcar_Get_Map(JNIEnv  *env,  jobject  obj, jint x, jint y)
{
	m_pCUntitled->RLBuf[0] = 0;
	jfieldID fid_arrays = env->GetFieldID(env->GetObjectClass(obj),"fbuf","[F");
	jfieldID  pbuf_id = env->GetFieldID(env->GetObjectClass(obj),"pbuf","[I");
	
	if (fid_arrays == NULL)
		LOG("get fbuf fail");
	
	jfloatArray jint_arr = (jfloatArray)(env)->GetObjectField(obj, fid_arrays);
	jintArray jint_pbuf = (jintArray)(env)->GetObjectField(obj, pbuf_id);
	if (jint_arr == NULL)
		LOG("jni_arr null");
	jfieldID maxid = env->GetFieldID(env->GetObjectClass(obj),"maxDot","I");
	env->SetIntField(obj, maxid, m_pCUntitled->maxDot); 
	memcpy((unsigned char *)buf, (unsigned char *)m_pCUntitled->dotBuf, DOTSIZE*4);
	env->SetFloatArrayRegion( jint_arr, 0, DOTSIZE, buf);

	jint pb[PSIZE];
	memcpy((unsigned char *)pb, (unsigned char *)m_pCUntitled->pBuf, 4*PSIZE);
	env->SetIntArrayRegion( jint_pbuf, 0, PSIZE, pb);
	return NULL;
}


jintArray com_flyaudio_backcar_Get_RL(JNIEnv  *env,  jobject  obj, jint mode, jint y)
{
//	jfloat mpb[PSIZE];
	jfieldID lid_arrays = env->GetFieldID(env->GetObjectClass(obj),"lbuf","[F");
	if (lid_arrays == NULL)
		LOG("get lbuf fail");
	jfloatArray jint_larr = (jfloatArray)(env)->GetObjectField(obj, lid_arrays);
	if (jint_larr == NULL)
		LOG("jni_larr null");
	//memset(buf2, 0, RLSIZE*4);
	jfieldID maxid = env->GetFieldID(env->GetObjectClass(obj),"maxPoint","I");
	jfieldID  mbuf_id = env->GetFieldID(env->GetObjectClass(obj),"mbuf","[F");
	jfloatArray jint_mbuf = (jfloatArray)(env)->GetObjectField(obj, mbuf_id);

	if(!isFileOK) {
		//memcpy((unsigned char *)buf2, (unsigned char *)m_pCUntitled->RLBuf, RLSIZE*4);
		//env->SetFloatArrayRegion( jint_larr, 0, RLSIZE, buf2);	
		env->SetFloatArrayRegion( jint_larr, 0, RLSIZE, m_pCUntitled->RLBuf);	
	

	}else
		{
		int fd2=-1;
		int map = 0;
			char tmpfilename[30];
			memset(tmpfilename, 0, 30);
			switch(m_pCUntitled->trailmode)
			{
			case 1:  memcpy(tmpfilename, filepath1, strlen(filepath1));
					break;
			case 2:	memcpy(tmpfilename, filepath2, strlen(filepath2));
					break;
			case 3: memcpy(tmpfilename, filepath3, strlen(filepath3));
					break;
			case 4: memcpy(tmpfilename, filepath4, strlen(filepath4));
					break;
			}
			fd2 = open(tmpfilename, O_RDONLY);
		if(fd2 >0){
			lseek(fd2, (pAngle-40)*(RLSIZE*4+4*(MSIZE+1))+24, SEEK_SET);
			read(fd2, &map, 4);
			read(fd2, m_pCUntitled->mBuf, MSIZE*4);
			if(read(fd2, buf2, RLSIZE*4)<0)
				isFileOK = false; 
			env->SetFloatArrayRegion( jint_larr, 0, RLSIZE, buf2);
			m_pCUntitled->maxP = map;	
		//	__android_log_print(4, "BCR", " get pangle %d  map = %d ", pAngle, map);
		}else {
			LOG("isFileOK false");
			isFileOK = false; 
			}
		close(fd2);
		}
	env->SetIntField(obj, maxid, m_pCUntitled->maxP); 
	env->SetFloatArrayRegion( jint_mbuf, 0, MSIZE, m_pCUntitled->mBuf);

	return NULL;
}


  void com_flyaudio_backcar_Set_param(JNIEnv  *env,  jobject  obj, jobject obj_param)
{
	
	int mode = tmode;
//	__android_log_print(4, "BCR", " mode = %d", mode);
	if(mode>=4) return;
	jclass param_obj = env->GetObjectClass(obj_param);
		jfieldID atsafe = env->GetFieldID(param_obj,"atsafe","I");
		jfieldID dotNum = env->GetFieldID(param_obj,"dotNum","I");
		jfieldID high = env->GetFieldID(param_obj,"high","I");
		jfieldID vision = env->GetFieldID(param_obj,"Vision","I");
		jfieldID Pu = env->GetFieldID(param_obj,"Pu","D");
		jfieldID Pv = env->GetFieldID(param_obj,"Pv","D");
		jfieldID lcar = env->GetFieldID(param_obj,"Lcar","D");
		jfieldID wcar = env->GetFieldID(param_obj,"Wcar","D");
		jfieldID xangle = env->GetFieldID(param_obj,"x_angle","D");
		jfieldID yangle = env->GetFieldID(param_obj,"y_angle","D");
		jfieldID smax = env->GetFieldID(param_obj,"Smax","D");
		jfieldID maxy = env->GetFieldID(param_obj,"mMaxY","I");
		jfieldID miny = env->GetFieldID(param_obj,"mMinY","I");
		jfieldID lcl = env->GetFieldID(param_obj,"LcL","D");
		jfieldID lcr = env->GetFieldID(param_obj,"LcR","D");
		jfieldID mnumf = env->GetFieldID(param_obj,"mNumF","I");
		jfieldID lpu = env->GetFieldID(param_obj,"LPu","F");
		jfieldID rpu = env->GetFieldID(param_obj,"RPu","F");
		jfieldID midLiney1 = env->GetFieldID(param_obj,"midLineY1","I");
		jfieldID midLiney2 = env->GetFieldID(param_obj,"midLineY2","I");
		jfieldID midLiney3 = env->GetFieldID(param_obj,"midLineY3","I");
		jfieldID trackMode = env->GetFieldID(param_obj,"TrackMode","I");
		


		bcdata[mode].atsafe = env->GetIntField(obj_param , atsafe);
		bcdata[mode].dotNum = env->GetIntField(obj_param , dotNum);
		bcdata[mode].high = env->GetIntField(obj_param , high);
		bcdata[mode].Vision = env->GetIntField(obj_param , vision);
		bcdata[mode].mNumF = env->GetIntField(obj_param , mnumf);
		bcdata[mode].mMinY = env->GetIntField(obj_param , miny);
		bcdata[mode].mMaxY = env->GetIntField(obj_param , maxy);
		bcdata[mode].Pu = env->GetDoubleField(obj_param,Pu);
		bcdata[mode].Pv = env->GetDoubleField(obj_param,Pv);
		bcdata[mode].Lcar = env->GetDoubleField(obj_param,lcar);
		bcdata[mode].Wcar = env->GetDoubleField(obj_param,wcar);
		bcdata[mode].x_angle = env->GetDoubleField(obj_param,xangle);
		bcdata[mode].y_angle = env->GetDoubleField(obj_param,yangle);
		bcdata[mode].Smax = env->GetDoubleField(obj_param,smax);
		bcdata[mode].LcL = env->GetDoubleField(obj_param,lcl);
		bcdata[mode].LcR = env->GetDoubleField(obj_param,lcr);
		bcdata[mode].LPu = env->GetFloatField(obj_param,lpu);
		bcdata[mode].RPu = env->GetFloatField(obj_param,rpu);

		bcdata[mode].midLineY1 = env->GetIntField(obj_param,midLiney1);
		bcdata[mode].midLineY2 = env->GetIntField(obj_param,midLiney2);
		bcdata[mode].midLineY3 = env->GetIntField(obj_param,midLiney3);
		bcdata[mode].trackMode = env->GetIntField(obj_param,trackMode);
		bcdata[mode].id = mode+1;
		tmode++;
		//__android_log_print(4, "BCR", " atsafe %d Pu %0.3f", bcdata[mode].atsafe, bcdata[mode].Pu);
		__android_log_print(4, "BCR", " com_flyaudio_backcar_Set_param mode = %d", mode);
		PrintBcParam(mode);
}

void PrintBcParam(int m)
{
	if(bcdata[m].id<=0)
		return;
LOG("=====================================");
	__android_log_print(4, "BCR", "atsafe %d dotnum %d high %d", bcdata[m].atsafe,bcdata[m].dotNum,bcdata[m].high);
__android_log_print(4, "BCR", "vision %d  numf  %d  minY  %d", bcdata[m].Vision,bcdata[m].mNumF,bcdata[m].mMinY);
__android_log_print(4, "BCR", "maxY  %d", bcdata[m].mMaxY);
__android_log_print(4, "BCR", "pu %0.4f  pv %0.4f  lcar %0.4f", bcdata[m].Pu,bcdata[m].Pv,bcdata[m].Lcar);
__android_log_print(4, "BCR", "wc %0.4f xangle %0.4f  yangle %0.4f", bcdata[m].Wcar,bcdata[m].x_angle,bcdata[m].y_angle);
__android_log_print(4, "BCR", "samx %0.4f  lcl %0.4f  lcr  %0.4f", bcdata[m].Smax,bcdata[m].LcL,bcdata[m].LcR);
__android_log_print(4, "BCR", "lpu %0.2f   rpu  %0.2f  trackMode %d ", bcdata[m].LPu,bcdata[m].RPu, bcdata[m].trackMode);
		
}

static int com_flyaudio_backcar_bc_preinit(JNIEnv  *env,  jobject  obj)
{
	CTestRealCar( );
//	m_pCUntitled->Read_conf(m_pFuncParam);
	memset(&bcdata[0], 0, sizeof(struct BcData));
	memset(&bcdata[1], 0, sizeof(struct BcData));
	memset(&bcdata[2], 0, sizeof(struct BcData));
	memset(&bcdata[3], 0, sizeof(struct BcData));
	
	tmode = 0;
	pIn =0;
	pAngle = 0;
	memset(isfileok, 0, sizeof(isfileok));
	judgeFlag = false;
	isBusy = false;
	LOG("bc_preinit");
	return 0;
}


static int com_flyaudio_backcar_Carline_init(JNIEnv  *env,  jobject  obj)
{
	m_pCUntitled->AllFunc();
	jfieldID lcid = env->GetFieldID(env->GetObjectClass(obj),"Lcar","I");
	env->SetIntField(obj, lcid, m_pFuncParam->carP.Lc); 

	LOG("init OK");
	return 0;
}
static const char *classPathName = "com/flyaudio/backcar/trackbitmap/BackCarTrack";
static JNINativeMethod methods[] = {
	{"Carline_init",	"()I", (void*)com_flyaudio_backcar_Carline_init},
	{"bc_preinit",	"()I", (void*)com_flyaudio_backcar_bc_preinit},
	{"Change_R", "(DI)V", (void*)com_flyaudio_backcar_Change_R},
	{"SendParam",	"(BI[F)V",	(void*)com_flyaudio_backcar_SendParam},
	{"Change_L",	"(DI)V",	(void*)com_flyaudio_backcar_Change_L},
	{"Get_Map", "(II)[I", (void*)com_flyaudio_backcar_Get_Map},
	{"Get_RL", "(II)[I", (void*)com_flyaudio_backcar_Get_RL},
	{"Set_Param", "(Lcom/flyaudio/data/BcData;)V", (void*)com_flyaudio_backcar_Set_param},
	{"setTrailmode","(I)V",(void*)setTrailmode}
};
static int registerFuncs(JNIEnv* _env)
{
	return android::AndroidRuntime::registerNativeMethods(_env, classPathName, methods, NELEM(methods));
}
jint JNI_OnLoad(JavaVM* vm, void * reserved)
{
	JNIEnv* env = NULL;
	jint result = -1;
	if (vm->GetEnv((void**) &env, JNI_VERSION_1_4) != JNI_OK) {
			LOG("EEROR: Getenv failed\n");
			goto fail;
		}
	assert(env != NULL);
	if ( registerFuncs(env) < 0) {
			LOG("ERROR: native registeration failed\n");
			goto fail;
		}
	result = JNI_VERSION_1_4;
fail:
	return result;
}

