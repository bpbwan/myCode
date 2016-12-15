#include<stdlib.h>
#include<stdio.h>
#include<string.h>
#include<sys/stat.h>
#include<sys/types.h>
#include<fcntl.h>
#include<cutils/properties.h>

#include<android/log.h>
#ifndef __UNTILE__H___
#define __UNTILE__H___


#define LOG(X)  __android_log_print(ANDROID_LOG_INFO, "BCR", X)

#define WIDTH 1440
#define HEIGH 600
#define MaxWIDTH 1460

//#define  MY_H (480+20)
//#define  MY_W (800+20)
//#define  MY_H 500
//#define  MY_W 820
//#define YCIRPOINT 800*4
//#define YCIRPOINT (1024*4)
#define DOTSIZE 60000
#define RLSIZE	35000
#define PSIZE 	16
#define AMSIZE  50
#define MSIZE 	80	//16+32*2
#define DotLineSize 24




typedef unsigned char BYTE;
typedef  unsigned short UINT16;
typedef unsigned char UINT8;
typedef  int  UINT32;

#define PIX  4



//#define dotnum 2000
#define pointnum 2002

#define ShowParkLine 1
//class CBackTrack;

struct MatrixR
{
	double R1[3];
	double R2[3];
	double R3[3];
};
struct ParkLineParam
{
	//ParkLineParam():dotNum(1000),Smax( 3600.0), start(220),Lstart(80),Rstart( 220),atsafe(416),at1m(850),
	//	at2m(1600),at3m(3517),at4m(3700),at5m(4000),Fcar( 1000.0){}
	ParkLineParam():dotNum(1100),Smax( 3700.0), start(220),Lstart(80),Rstart( 220),atsafe(290),at1m(684),
		at2m(1382),at3m(2800),at4m(3700),at5m(4000),Fcar( 1000.0){}
			

	int dotNum;       //  画点的个数
	double Smax;     //   画线最远距离

	int start; //  左右开始画线的位置
	int Lstart; //  左右开始画线的位置
	int Rstart;

	int atsafe;
	int at1m;
	int at2m;
	int at3m;
	int at4m;
	int at5m;
	double Fcar;     // 
};
struct CarParam
{
	//CarParam():Lc(7300.0),Wc(1545.0),Lsa(0){}
	CarParam():Lc(4300.0),Wc(2595.0),Lsa(0){}
	double Lc;
	double Wc;
	int Lsa;
};

struct BcData
{
	int id;
	int atsafe;
	 int dotNum;
	 int high;          
	 int Vision;        
	 double Pu;
	 double Pv;
	 double Lcar;
	 double Wcar;
	 double x_angle;
	 double y_angle;
	 double Smax;	
	 int  mMaxY;	
	 int  mMinY;	
	 double  LcL;	
	 double  LcR;	
	 float LPu;
	 float RPu;
	 int  mNumF;
	 int trackMode;
	 int midLineY1;
	 int midLineY2;
	 int midLineY3;
	 int Reserve;
};

struct CameraParam
{

	CameraParam():dP(0),nhigh(500),nVision(180),dPu(0.08),\
		dPv(0.03),dx_angle(24),dy_angle(0.0),dz_angle(0.0){}
	
	double dP;
	int	   nhigh;           //  摄像机光学中心在世界水平地面的高度
	int    nVision;         //  摄像机视角  
	double dPu;             //  摄像机光心偏离CCD水平 使图像右移为+  0.01
	double dPv;             //  摄像机光心偏离CCD垂直 使图像上移为+  0.01
	double dx_angle;        //  摄像机X 方向水平角度
	double dy_angle;
	double dz_angle;
};
struct FuncParam
{
	ParkLineParam parkP;
	CarParam      carP;
	CameraParam   camP;
};


class CUntitled
{
public:
	CUntitled();
	~CUntitled();
public:
	
	 float *dotBuf;
	 float *RLBuf;
	 float *RLBuf2;
	 int *pBuf;
	  float*mBuf;
	
public:
	
	void ParkSevenLine();
public:
	void init_Param();//初始化所有参数
	void init_Car_Pm(double Lca=7300.0,double Wca=1545.0,int lsa=0);
	void init_Camera_InstallPm();
	void init_Camera_Module_insidePm();
	void init_Camera_Module_OutPm();
	void init_CalCurMatrixR();// 矩阵
	void DrawALine(int start_x, int start_y, int end_x, int end_y, int line_width);
	void MidpointLine(int x0,int y0,int x1,int y1,int line_width); 
	void FillFullLine(int x0,int y0,int x1,int y1,int line_width); 
	void FillDashedLine(int x0,int y0,int x1,int y1,int line_width, int offsetW, int fill, int space); 
	void FillTwoMidLine(int start_x, int start_y, int end_x, int end_y, int line_width, int length);

	///////////////// SAN ///////////////
 	void AllFunc();
// 	void TestDraw();
	/////////////////SAN ///////////////
	void DrawParkLine();//包含下面7条线
	void Linefl();
	void Linefr();
	void Lineff();
	void Linefm();
	void Linef1m();
	void Linef2m();
	void Linef3m();

	void InitSamplePm();
	void SwitchTrackUsingMode();
	void SampleLocusLine_R(double angle, bool flag);//预览轨迹
	void SampleLocusLine_L(double angle, bool flag);
	
	void (CUntitled::*LocusLine_R)(double angle, bool flag);
	void (CUntitled::*LocusLine_L)(double angle, bool flag);


	void SampleLocusLine_R1(double angle, bool flag);//预览轨迹
	void SampleLocusLine_L1(double angle, bool flag);
	
	void SampleLocusLine_R2(double angle, bool flag);//预览轨迹
	void SampleLocusLine_L2(double angle, bool flag);

	int Test_CountEachDrawTime(double angle);
	void Test_ChangeParam_ParkLine(FuncParam funPm/*ParkLineParam lineparam,CarParam carP,CameraParam camP*/);
	void Req_RealCarParamChange( BYTE *buff,int len, float* data ,FuncParam *m_pFuncParam);
	void saveTrackFile(int data);
	void Save();
	void Reset(int cartype);
	bool Read_conf(FuncParam *m_pFuncParam);
	void ResetTrackFileIsNotOk( );
	
public:
		
	int dotNum ;       //  画点的个数
	double Smax ;     //   画线最远距离
	double SmaxRL;    //  画线最远距离 预览轨迹  
	int start_point ; //  左右开始画线的位置
	int Lstart_point ; //  左右开始画线的位置
	int Rstart_point ;


	float *midDotLine;
	
	bool DEBUG_MODE;
	bool old_track;
	int maxY;
	int trailmode;
	int trackUsingMode;
	int mA;
	int mB;
	
	double Lcar ;
	double LcL;
	double LcR;

	
	//  Wcar = 2000.0;    //   车宽
	double Wcar;     //   停车位的宽
	int Lsafe;           //  车两边安全间隔 + —

	int carType;
	int mNumF;//16???? ???
	int mNum;	//16???? ???
	int mLine3;  //??? 
	int mMinY; //?? ?y ? 
	int maxP;
	int maxP2;
	int maxDot;
	int midLineY1;  //轨迹中线距离高度1
	int midLineY2;
	int midLineY3;

	
	int atsafe;
	int at1m;
	int at2m;
	int at3m;
	int at4m;
	int at5m;
	double Fcar ;     // 

	double P;
	int high;            //   摄像机光学中心在世界水平地面的高度
	int Vision;          //  摄像机视角  
	double Pu;             //  摄像机光心偏离CCD水平 使图像右移为+  0.01
	double Pv;             //  摄像机光心偏离CCD垂直 使图像上移为+  0.01
	double Pv2;             //  摄像机光心偏离CCD垂直 使图像上移为+  0.01
	double x_angle;        //  摄像机X 方向水平角度
	double y_angle;
	double z_angle;

	double Hccd ;       //  ccd阵列面积的水平长度  
	double Vccd ;       //  ccd阵列面积的垂直长度
	double Dccd ;
	//  fccd  = Dccd/(4.0*sin((2*pi*170/4)/360.0))      //   1.70; 摄像机的焦距 6.00
	double fhccd ;      //   水平视角*1.8 fhccd = Hccd/(4.0*sin( (2*pi*128/2)/360.0 ))
	//  fvccd = Vccd/(4.0*sin((2*pi*94/4)/360.0))      //   垂直视角*1.2 fvccd =Vccd/(4.0*sin( (2*pi*94/2)/360.0 ))
	double fccd ;
	double fvccd;
	//  Hlcd = 720.0;Vlcd = 480.0;
	double Hlcd;
	double Vlcd;
	double dx;
	double dy;
	double u0;
	double v0 ;
	double v02 ;
	float lpu;
	float rpu;

	int Zccd ;     //   摄像机光学中心在世界水平地面的高度
	double XLccd ;     //   780.0摄像机光学中心的水平位置980.0
	double XRccd;
	double theta_x;    //  30 绕 x轴 旋转的角度数
	double theta_y;           //  -3 绕 y轴 旋转的角度数
	double theta_z ;           //   绕 z轴 旋转的角度数

	double  R[3][3]; 
	double RT13;//T = [0; 0; Zccd./cos(pi-theta_x)];


	double x_dif;
	double y_dif;
	double yl_dif;
	double yr_dif;

	
	double VTan_theta ;

	///////
	double delta;
	double r_trk;		//圆弧半径
	double r_trk2;		//圆弧半径
	double alpha_max;	//一定弧长的弧角
	double alpha_max2;	//一定弧长的弧角
	double alpha0_L;
	double alpha0_R;
	double dalpha_L;//   画线角度分辨率
	double dalpha_R;

	//  其对应圆心坐标
	double a;
	double a2;
	double b;
	//   三维空间坐标系(Xw,Yw,Zw) 里的曲线
	int kL;
	int kR;

	double alphaL;
	double alphaR;
	//////

	/*unsigned*/ int yl_lcdMin,yl_lcdMax;
	/*unsigned*/ int yr_lcdMin,yr_lcdMax;

	double *xfl,*yfl;
	double *xfr,*yfr;


	/*unsigned*/ int *xl_lcd, *yl_lcd;
	/*unsigned*/ int *xr_lcd,*yr_lcd;

	/*unsigned*/ int *xf_lcd,*yf_lcd;
	/*unsigned*/ int *xm_lcd,*ym_lcd; 

	/*unsigned*/ int *x1m_lcd,*y1m_lcd;
	/*unsigned*/ int *x2m_lcd,*y2m_lcd; 
	/*unsigned*/ int *x3m_lcd,*y3m_lcd; 
	unsigned char  (*lcd_fig)[WIDTH+20][1];

	/*unsigned*/ int *xddl,*yddl;
	/*unsigned*/ int *xddr,*yddr;

	/*
	double xfl[pointnum],yfl[pointnum];
	double xfr[pointnum],yfr[pointnum];
	double xff[pointnum],yff[pointnum];
	double xfm[pointnum],yfm[pointnum];
	double xf1m[pointnum],yf1m[pointnum];
	double xf2m[pointnum],yf2m[pointnum];
	double xf3m[pointnum],yf3m[pointnum];
	*/
};

#endif


