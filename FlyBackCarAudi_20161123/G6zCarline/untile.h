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
			

	int dotNum;       //  ����ĸ���
	double Smax;     //   ������Զ����

	int start; //  ���ҿ�ʼ���ߵ�λ��
	int Lstart; //  ���ҿ�ʼ���ߵ�λ��
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
	int	   nhigh;           //  ������ѧ����������ˮƽ����ĸ߶�
	int    nVision;         //  ������ӽ�  
	double dPu;             //  ��������ƫ��CCDˮƽ ʹͼ������Ϊ+  0.01
	double dPv;             //  ��������ƫ��CCD��ֱ ʹͼ������Ϊ+  0.01
	double dx_angle;        //  �����X ����ˮƽ�Ƕ�
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
	void init_Param();//��ʼ�����в���
	void init_Car_Pm(double Lca=7300.0,double Wca=1545.0,int lsa=0);
	void init_Camera_InstallPm();
	void init_Camera_Module_insidePm();
	void init_Camera_Module_OutPm();
	void init_CalCurMatrixR();// ����
	void DrawALine(int start_x, int start_y, int end_x, int end_y, int line_width);
	void MidpointLine(int x0,int y0,int x1,int y1,int line_width); 
	void FillFullLine(int x0,int y0,int x1,int y1,int line_width); 
	void FillDashedLine(int x0,int y0,int x1,int y1,int line_width, int offsetW, int fill, int space); 
	void FillTwoMidLine(int start_x, int start_y, int end_x, int end_y, int line_width, int length);

	///////////////// SAN ///////////////
 	void AllFunc();
// 	void TestDraw();
	/////////////////SAN ///////////////
	void DrawParkLine();//������7����
	void Linefl();
	void Linefr();
	void Lineff();
	void Linefm();
	void Linef1m();
	void Linef2m();
	void Linef3m();

	void InitSamplePm();
	void SwitchTrackUsingMode();
	void SampleLocusLine_R(double angle, bool flag);//Ԥ���켣
	void SampleLocusLine_L(double angle, bool flag);
	
	void (CUntitled::*LocusLine_R)(double angle, bool flag);
	void (CUntitled::*LocusLine_L)(double angle, bool flag);


	void SampleLocusLine_R1(double angle, bool flag);//Ԥ���켣
	void SampleLocusLine_L1(double angle, bool flag);
	
	void SampleLocusLine_R2(double angle, bool flag);//Ԥ���켣
	void SampleLocusLine_L2(double angle, bool flag);

	void SampleLocusLine_R3(double angle, bool flag);//Ԥ���켣
	void SampleLocusLine_L3(double angle, bool flag);

	int Test_CountEachDrawTime(double angle);
	void Test_ChangeParam_ParkLine(FuncParam funPm/*ParkLineParam lineparam,CarParam carP,CameraParam camP*/);
	void Req_RealCarParamChange( BYTE *buff,int len, float* data ,FuncParam *m_pFuncParam);
	void saveTrackFile(int data);
	void Save();
	void Reset(int cartype);
	bool Read_conf(FuncParam *m_pFuncParam);
	void ResetTrackFileIsNotOk( );
	
public:
		
	int dotNum ;       //  ����ĸ���
	double Smax ;     //   ������Զ����
	double SmaxRL;    //  ������Զ���� Ԥ���켣  
	int start_point ; //  ���ҿ�ʼ���ߵ�λ��
	int Lstart_point ; //  ���ҿ�ʼ���ߵ�λ��
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

	
	//  Wcar = 2000.0;    //   ����
	double Wcar;     //   ͣ��λ�Ŀ�
	int Lsafe;           //  �����߰�ȫ��� + ��

	int carType;
	int mNumF;//16???? ???
	int mNum;	//16???? ???
	int mLine3;  //??? 
	int mMinY; //?? ?y ? 
	int maxP;
	int maxP2;
	int maxDot;
	int midLineY1;  //�켣���߾���߶�1
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
	int high;            //   ������ѧ����������ˮƽ����ĸ߶�
	int Vision;          //  ������ӽ�  
	double Pu;             //  ��������ƫ��CCDˮƽ ʹͼ������Ϊ+  0.01
	double Pv;             //  ��������ƫ��CCD��ֱ ʹͼ������Ϊ+  0.01
	double Pv2;             //  ��������ƫ��CCD��ֱ ʹͼ������Ϊ+  0.01
	double x_angle;        //  �����X ����ˮƽ�Ƕ�
	double y_angle;
	double z_angle;

	double Hccd ;       //  ccd��������ˮƽ����  
	double Vccd ;       //  ccd�������Ĵ�ֱ����
	double Dccd ;
	//  fccd  = Dccd/(4.0*sin((2*pi*170/4)/360.0))      //   1.70; �����Ľ��� 6.00
	double fhccd ;      //   ˮƽ�ӽ�*1.8 fhccd = Hccd/(4.0*sin( (2*pi*128/2)/360.0 ))
	//  fvccd = Vccd/(4.0*sin((2*pi*94/4)/360.0))      //   ��ֱ�ӽ�*1.2 fvccd =Vccd/(4.0*sin( (2*pi*94/2)/360.0 ))
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

	int Zccd ;     //   ������ѧ����������ˮƽ����ĸ߶�
	double XLccd ;     //   780.0������ѧ���ĵ�ˮƽλ��980.0
	double XRccd;
	double theta_x;    //  30 �� x�� ��ת�ĽǶ���
	double theta_y;           //  -3 �� y�� ��ת�ĽǶ���
	double theta_z ;           //   �� z�� ��ת�ĽǶ���

	double  R[3][3]; 
	double RT13;//T = [0; 0; Zccd./cos(pi-theta_x)];


	double x_dif;
	double y_dif;
	double yl_dif;
	double yr_dif;

	
	double VTan_theta ;

	///////
	double delta;
	double r_trk;		//Բ���뾶
	double r_trk2;		//Բ���뾶
	double alpha_max;	//һ�������Ļ���
	double alpha_max2;	//һ�������Ļ���
	double alpha0_L;
	double alpha0_R;
	double dalpha_L;//   ���߽Ƕȷֱ���
	double dalpha_R;

	//  ���ӦԲ�����
	double a;
	double a2;
	double b;
	//   ��ά�ռ����ϵ(Xw,Yw,Zw) �������
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


