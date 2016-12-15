#include "untile.h"
#include<math.h>

#define  pi 3.14159

int width =1024;
int height =600;
int   my_h=600+20;
int  my_w= 1024+20;


int mm3[16] = {325,301,279,260,243,227,213,201,191,182,175,169,164,159,155,150};
//24,22,19,17,16,15,14,12,10,8,7,6,5,4,3


#define WIDTHBYTES(bits) (((bits) + 31) / 32 * 4)

#define TESTFORCHANGEPARAM

double simple_str(char *buf)
{
	double ret=0;
	int i =0;
	float p =10;
	float m=1;
	while (buf[i]!='\0')
		{
			if (buf[i] == '-')
				{
					i++;
					continue;
				}
			if (buf[i]=='.') {
					m=0.1;
					p=1;
					i++;
					continue;
				}
			ret = ret*p+(buf[i]-'0')*m;
			if (m<1)
				m = m/10;
			i++;
		}
	if (buf[0]=='-')
		ret=-ret;
	return ret;
}


//mbuf[0~3]  顶点坐标mbuf[4~7] 中点 坐标   //mbuf[8~11] 底部坐标
//float mm[15] = {297,273,248,230,213,198,183,171,161,150,142,135,126,119,115};
//float mm3[16] = {324,293,269,246,229,211,195,182,169,157,147,137,129,121,115,107};

int doti[7]={147,180,218,309,436,530,650};


float getRealY(float startY, float srcY, float newH,float dstMaxY, float dstMinY)
{
	float tmp =0;
	tmp = startY+(srcY-dstMinY)*newH/(dstMaxY-dstMinY);
	return tmp;
}


CUntitled::CUntitled()
{
	
	  xl_lcd = new /*unsigned*/ int[pointnum];yl_lcd = new /*unsigned*/ int[pointnum];
	  xr_lcd = new /*unsigned*/ int[pointnum];yr_lcd = new /*unsigned*/ int[pointnum];
	  xf_lcd = new /*unsigned*/ int[pointnum];yf_lcd = new /*unsigned*/ int[pointnum];
	  xm_lcd = new /*unsigned*/ int[pointnum];ym_lcd = new /*unsigned*/ int[pointnum];
	  
	  x1m_lcd = new /*unsigned*/ int[pointnum];y1m_lcd = new /*unsigned*/ int[pointnum];
	  x2m_lcd = new /*unsigned*/ int[pointnum];y2m_lcd = new /*unsigned*/ int[pointnum];
	  x3m_lcd = new /*unsigned*/ int[pointnum];y3m_lcd = new /*unsigned*/ int[pointnum];

	  xfl  = new double[pointnum], yfl	= new double[pointnum];
	  xfr  = new double[pointnum], yfr	= new double[pointnum];

	  xddl = new /*unsigned*/ int[pointnum],yddl = new /*unsigned*/ int[pointnum];
	  xddr = new /*unsigned*/ int[pointnum],yddr = new /*unsigned*/ int[pointnum];

	  RLBuf = new float[RLSIZE];
	  dotBuf = new float[DOTSIZE];
	  pBuf = new int[PSIZE];
	  mBuf = new float[MSIZE];
	  midDotLine = new float[DotLineSize];
	  
	  yl_lcdMin	 = 0;
	  yl_lcdMax	 = 0;
	  yr_lcdMin	 = 0;
	  yr_lcdMax = 0;
	
	  lcd_fig  = new UINT8[HEIGH+20][WIDTH+20][1];

	  dotNum =800;       //  画点的个数
	  Smax = 3000.0;     //   画线最远距离

	  start_point =220; //  左右开始画线的位置
	  Lstart_point = 80; //  左右开始画线的位置
	  Rstart_point = 220;

	  atsafe=547;
	  at1m=684;
	  at2m=1382;
	  at3m=3311;
	  at4m=3700;
	  at5m=4000;
	  Fcar = 1000.0;     // 
	  mLine3 = 4; //奔驰中间线条比例值 
	  mNum = 116;//76
	  mNumF = 300;  //54
	  mMinY = 108;//68
	  LcR = 13457;
	  LcL = 14521;
	  trailmode =0;
	  maxY = 334;
	  carType = 100;
	  trackUsingMode = 1;
	  
	  init_Car_Pm();
	  init_Camera_InstallPm();
	  
	  LocusLine_R  = &CUntitled::SampleLocusLine_R1;
	  LocusLine_L = &CUntitled::SampleLocusLine_L1;
	  DEBUG_MODE = false;
	  old_track = false;

	
//LOG("XX2");
}
CUntitled::~CUntitled()
{
	if ( RLBuf != NULL)
		{
			free(RLBuf);
			RLBuf = NULL;
		}
	if ( dotBuf != NULL)
		{
			free(dotBuf);
			dotBuf = NULL;
		}
	if ( pBuf != NULL)
		{
			free(pBuf);
			pBuf = NULL;
		}
	free(mBuf);
	mBuf = NULL;


	delete []xddl;
	delete []yddl;
	delete []xddr;
	delete []yddr;

}
void CUntitled::init_Car_Pm(double Lca,double Wca,int lsa)
{
	 // Lcar = 2603.0;     //   车的轴距
	  Lcar = Lca;
	//  Wcar = 2000.0;    //   车宽
	 Wcar = Wca;     //   停车位的宽
	 Lsafe= lsa;           //  车两边安全间隔 + —
}

void CUntitled::init_Camera_InstallPm()
{	//  P=-40;               //  摄像头安装偏移水平中心  汽车左边负数 
	 P=0;
	 //high=850;            //   摄像机光学中心在世界水平地面的高度
	 //Vision=115;          //  摄像机视角  
	 //Pu=0.05;             //  摄像机光心偏离CCD水平 使图像右移为+  0.01
	 //Pv=0.00;             //  摄像机光心偏离CCD垂直 使图像上移为+  0.01
	 //x_angle=33.5;        //  摄像机X 方向水平角度
	 //y_angle=4.0;
	 //z_angle=2.0;

	 high=500;            //   摄像机光学中心在世界水平地面的高度
	 Vision=180;          //  摄像机视角  
	 Pu=0.08;             //  摄像机光心偏离CCD水平 使图像右移为+  0.01
	 Pv=0.03;             //  摄像机光心偏离CCD垂直 使图像上移为+  0.01
	 x_angle=24;        //  摄像机X 方向水平角度
	 y_angle=1.0;
	 z_angle=0.0;
}
void CUntitled::init_Camera_Module_insidePm()
{
	//   摄像机模型 内部参数
	 Hccd = 4.080;       //  ccd阵列面积的水平长度  
	 Vccd = 3.102;       //  ccd阵列面积的垂直长度
	 Dccd = sqrt(Hccd*Hccd + Vccd*Vccd);
	//  fccd  = Dccd/(4.0*sin((2*pi*170/4)/360.0))      //   1.70; 摄像机的焦距 6.00
	 fhccd = Hccd/(4.0*sin((2*pi*Vision/4)/360.0));      //   水平视角*1.8 fhccd = Hccd/(4.0*sin( (2*pi*128/2)/360.0 ))
	//  fvccd = Vccd/(4.0*sin((2*pi*94/4)/360.0))      //   垂直视角*1.2 fvccd =Vccd/(4.0*sin( (2*pi*94/2)/360.0 ))
	 fccd  = fhccd;
	 fvccd = fhccd;
	//  Hlcd = 720.0;Vlcd = 480.0;
	 //Hlcd = 800.0;
	 //Vlcd = 480.0;
	 Hlcd = width;
	 Vlcd = height;
	 dx = Hlcd/Hccd;
	 dy = Vlcd/Vccd;
	 u0 = Hccd/2.0+Pu;
	 v0 = Vccd/2.0+Pv;
}
void CUntitled::init_Camera_Module_OutPm()
{
	// Fcar = 1000.0;     //   
	//   摄像机模型 外部参数
	 Zccd  =high;     //   摄像机光学中心在世界水平地面的高度
	 XLccd = -(Wcar*0.5 + P)- Lsafe  ;     //   780.0摄像机光学中心的水平位置980.0
	 XRccd = +(Wcar*0.5 - P)+ Lsafe  ;
	 theta_x = (2*pi*(90 + x_angle))/360.0;    //  30 绕 x轴 旋转的角度数
	 theta_y = (2*pi*(y_angle))/360.0;           //  -3 绕 y轴 旋转的角度数
	 theta_z = (2*pi*(z_angle))/360.0;           //   绕 z轴 旋转的角度数

	 RT13 =  Zccd/cos(pi-theta_x);
}

void CUntitled::init_CalCurMatrixR()
{
	R[0][0] = cos(theta_z)*cos(theta_y);
	R[0][1] = cos(theta_z)*sin(theta_y)*sin(theta_x) - sin(theta_z);
	R[0][2] = cos(theta_z)*sin(theta_y)*cos(theta_x) - sin(theta_z)*sin(theta_x);

	R[1][0] = sin(theta_z)*cos(theta_y);
	R[1][1] = sin(theta_z)*sin(theta_y)*sin(theta_x) + cos(theta_z)*cos(theta_x);
	R[1][2] = sin(theta_z)*sin(theta_y)*cos(theta_x) + cos(theta_z)*sin(theta_x);

	R[2][0] = -sin(theta_y);
	R[2][1] = cos(theta_y)*sin(theta_x);
	R[2][2] = cos(theta_y)*cos(theta_x);

}

void CUntitled::init_Param()
{

	 SmaxRL = Smax ;
	 init_Camera_Module_insidePm();
	 init_Camera_Module_OutPm();
	 init_CalCurMatrixR();

	 x_dif = (Wcar +Lsafe)/(dotNum-1);
	 y_dif = (Smax -start_point)/(dotNum-1);
	 yl_dif = (Smax -Lstart_point)/(dotNum-1);
	 yr_dif = (Smax -Rstart_point)/(dotNum-1);

	 VTan_theta = tan(pi-theta_x);
}

void CUntitled::Linefl()
 {
	 double xwl = 0,ywl = 0,zwl = 0;
	 double xcl = 0,ycl = 0,zcl = 0;
	 double xul = 0,yul = 0;
	 double lambda = 0;

	 int j = 0;

	 memset(xl_lcd,0,pointnum);
	 memset(yl_lcd,0,pointnum);

	 memset(xfl,0,pointnum);
	 memset(yfl,0,pointnum);

	for(int i=1;i<=dotNum;i++)
	{
		xwl = XLccd;
		ywl = -Zccd *  VTan_theta + (i-1)*yl_dif + Lstart_point;   //  -800   Zccd*tan(theta_x)+211.8
		zwl = 0;
	
		xul = fhccd*(R[0][0] * xwl + R[0][1] * ywl + R[0][2] * zwl)/(R[2][0] * xwl + R[2][1] * ywl + R[2][2] * zwl + RT13);
		yul = fvccd*(R[1][0] * xwl + R[1][1] * ywl + R[1][2] * zwl)/(R[2][0] * xwl + R[2][1] * ywl + R[2][2] * zwl + RT13);

		lambda = sqrt(xul*xul+yul*yul)/fccd;
		xfl[i] = 2.0*xul*sin((atan(lambda))/2)/lambda;
		yfl[i] = 2.0*yul*sin((atan(lambda))/2)/lambda;

		if( ((xfl[i]>=-(Hccd/2.0)) && (xfl[i]<=(Hccd/2.0))) && ((yfl[i]>=-(Vccd/2.0)) && (yfl[i]<=(Vccd/2.0))) )
		{
			j = j + 1;
 			xl_lcd[j] = UINT16((-xfl[i] + u0)*dx);
			yl_lcd[j] = UINT16((yfl[i] + v0)*dy);
			if(j==1)
			{
				yl_lcdMin = yl_lcd[j];
				yl_lcdMax = yl_lcd[j];
			}
			else if( yl_lcd[j] > yl_lcdMax)
				yl_lcdMax = yl_lcd[j];
			else if( yl_lcd[j] < yl_lcdMin)
				yl_lcdMin = yl_lcd[j];
		}
	}
 }

void CUntitled::Linefr()	
{
	double xwr = 0,ywr = 0,zwr = 0;
	double xcr = 0,ycr = 0,zcr = 0;
	double xur = 0,yur = 0;
	double lambda = 0;
	int j = 0;
	
	memset(xfr,0,pointnum);
	memset(yfr,0,pointnum);
	memset(xr_lcd,0,pointnum);
	memset(yr_lcd,0,pointnum);

	 for(int i=1;i<=dotNum;i++)
	 {
		 xwr = XRccd;
		 ywr = -Zccd *  VTan_theta + (i-1)*yr_dif + Rstart_point;   //  800 Zccd*tan(theta_x)983+211.8
		 zwr = 0;
	
		xur = fhccd*(R[0][0] * xwr + R[0][1] * ywr + R[0][2] * zwr + 0)/(R[2][0] * xwr + R[2][1] * ywr + R[2][2] * zwr + RT13);
		yur = fvccd*(R[1][0] * xwr + R[1][1] * ywr + R[1][2] * zwr + 0)/(R[2][0] * xwr + R[2][1] * ywr + R[2][2] * zwr + RT13);

		lambda = sqrt(xur*xur+yur*yur)/fccd;
		xfr[i] = 2.0*xur*sin((atan(lambda))/2)/lambda;
		yfr[i] = 2.0*yur*sin((atan(lambda))/2)/lambda;
	
		 if( ((xfr[i]>=-(Hccd/2.0)) && (xfr[i]<=(Hccd/2.0))) && ((yfr[i]>=-(Vccd/2.0)) && (yfr[i]<=(Vccd/2.0))) )
		 {
			 j = j + 1;
			 xr_lcd[i] = UINT16((-xfr[i] + u0)*dx);
			 yr_lcd[i] = UINT16((yfr[i] + v0)*dy);

			 if(j==1)
			 {
				 yr_lcdMin = yr_lcd[j];
				 yr_lcdMax = yr_lcd[j];
			 }
			 else if( yr_lcd[j] > yr_lcdMax)
				 yr_lcdMax = yr_lcd[j];
			 else if( yr_lcd[j] < yr_lcdMin)
				 yr_lcdMin = yr_lcd[j];
		 }
		 
	 }

}


void CUntitled::Lineff()
{
	double xwf = 0,ywf = 0,zwf = 0;
	double xcf = 0,ycf = 0,zcf = 0;
	double xuf = 0,yuf = 0;
	double lambda = 0;
	double xff,yff;

	memset(xf_lcd,0,pointnum);
	memset(yf_lcd,0,pointnum);
	int j = 0;

	for(int i=1;i<=dotNum;i++)
	{
		xwf = XLccd + (i-1)*x_dif  ;
		ywf = -Zccd *  VTan_theta  +atsafe;                  //  adjust 390保险杆区域 800   +191.8
		zwf = 0;

		xcf = R[0][0] * xwf + R[0][1] * ywf + R[0][2] * zwf + 0;
		ycf = R[1][0] * xwf + R[1][1] * ywf + R[1][2] * zwf + 0;
		zcf = R[2][0] * xwf + R[2][1] * ywf + R[2][2] * zwf + RT13;
		
		xuf = fhccd*(R[0][0] * xwf + R[0][1] * ywf + R[0][2] * zwf + 0)/(R[2][0] * xwf + R[2][1] * ywf + R[2][2] * zwf + RT13);
		yuf = fvccd*(R[1][0] * xwf + R[1][1] * ywf + R[1][2] * zwf + 0)/(R[2][0] * xwf + R[2][1] * ywf + R[2][2] * zwf + RT13);

		lambda = sqrt(xuf*xuf+yuf*yuf)/fccd;
		xff/*[i]*/ = 2.0*xuf*sin((atan(lambda))/2)/lambda;
		yff/*[i]*/ = 2.0*yuf*sin((atan(lambda))/2)/lambda;
	
		if( ((xff/*[i]*/>=-(Hccd/2)) && (xff/*[i]*/<=(Hccd/2))) && ((yff/*[i]*/>=-(Vccd/2)) && (yff/*[i]*/<=(Vccd/2))) )
		{
			j = j + 1;

			xf_lcd[i] = UINT16((-xff + u0)*dx);
			yf_lcd[i] = UINT16((yff + v0)*dy);
		}
		

	}

}

void CUntitled::Linefm()
{
	double xwm = 0,ywm = 0,zwm = 0;
	double xcm = 0,ycm = 0,zcm = 0;
	double xum = 0,yum = 0;
	double lambda = 0;
	int j = 0;
	double xfm,yfm;

	memset(xm_lcd,0,pointnum);
	memset(ym_lcd,0,pointnum);
	 for(int i=1;i<=dotNum;i++)
	 {
		 xwm = XLccd + (i-1)*x_dif  ;
		 ywm = -Zccd *  VTan_theta +at1m;                 //   0+191.8
		 zwm = 0;

		xum = fhccd*(R[0][0] * xwm + R[0][1] * ywm + R[0][2] * zwm + 0)/(R[2][0] * xwm + R[2][1] * ywm + R[2][2] * zwm + RT13);
		yum = fvccd*(R[1][0] * xwm + R[1][1] * ywm + R[1][2] * zwm + 0)/(R[2][0] * xwm + R[2][1] * ywm + R[2][2] * zwm + RT13);

		lambda = sqrt(xum*xum+yum*yum)/fccd;
		xfm/*[i]*/ = 2.0*xum*sin((atan(lambda))/2)/lambda;
		yfm/*[i]*/ = 2.0*yum*sin((atan(lambda))/2)/lambda;
	
		 if( ((xfm/*[i]*/>=-(Hccd/2)) && (xfm/*[i]*/<=(Hccd/2))) && ((yfm/*[i]*/>=-(Vccd/2)) && (yfm/*[i]*/<=(Vccd/2))) )
		 { 
			 j = j + 1;
			 xm_lcd[i] = UINT16((-xfm/*[i]*/ + u0)*dx);
			 ym_lcd[i] = UINT16((yfm/*[i]*/ + v0)*dy);
		 }

	 }

}

void CUntitled::Linef1m()
{
	double xw1m = 0,yw1m = 0,zw1m = 0;
	double xc1m = 0,yc1m = 0,zc1m = 0;
	double xu1m = 0,yu1m = 0;
	double lambda = 0;
	int j = 0;
	double xf1m,yf1m;

	memset(x1m_lcd,0,pointnum);
	memset(y1m_lcd,0,pointnum);

	for(int i=1;i<=dotNum;i++)
	{
		xw1m = XLccd + (i-1)*x_dif  ;
		yw1m = -Zccd *  VTan_theta +at2m;     //  600*tan(theta_x) + 
		zw1m = 0;

		xu1m = fhccd*(R[0][0] * xw1m + R[0][1] * yw1m + R[0][2] * zw1m + 0)/(R[2][0] * xw1m + R[2][1] * yw1m + R[2][2] * zw1m + RT13);
		yu1m = fvccd*(R[1][0] * xw1m + R[1][1] * yw1m + R[1][2] * zw1m + 0)/(R[2][0] * xw1m + R[2][1] * yw1m + R[2][2] * zw1m + RT13);

		lambda = sqrt(xu1m*xu1m+yu1m*yu1m)/fccd;
		xf1m/*[i]*/ = 2.0*xu1m*sin((atan(lambda))/2)/lambda;
		yf1m/*[i]*/ = 2.0*yu1m*sin((atan(lambda))/2)/lambda;
	
			if( ((xf1m/*[i]*/>=-(Hccd/2)) && (xf1m/*[i]*/<=(Hccd/2))) && ((yf1m/*[i]*/>=-(Vccd/2)) && (yf1m/*[i]*/<=(Vccd/2))) )
			{
				j = j + 1;

				x1m_lcd[i] = UINT16((-xf1m/*[i]*/ + u0)*dx);
				y1m_lcd[i] = UINT16((yf1m/*[i]*/ + v0)*dy);
			}

	}

}
void CUntitled::Linef2m()
{
	double xw2m = 0,yw2m = 0,zw2m = 0;
	double xc2m = 0,yc2m = 0,zc2m = 0;
	double xu2m = 0,yu2m = 0;
	double lambda = 0;
	int j = 0;
	double xf2m,yf2m;

	memset(x2m_lcd,0,pointnum);
	memset(y2m_lcd,0,pointnum);

	for(int i=1;i<=dotNum;i++)
	{
		xw2m = XLccd + (i-1)*x_dif;
		yw2m = -Zccd * VTan_theta +at3m;     //  600*tan(theta_x) + 
		zw2m = 0;

		xu2m = fhccd*(R[0][0] * xw2m + R[0][1] * yw2m + R[0][2] * zw2m + 0)/(R[2][0] * xw2m + R[2][1] * yw2m + R[2][2] * zw2m + RT13);
		yu2m = fvccd*(R[1][0] * xw2m + R[1][1] * yw2m + R[1][2] * zw2m + 0)/(R[2][0] * xw2m + R[2][1] * yw2m + R[2][2] * zw2m + RT13);

		lambda = sqrt(xu2m*xu2m+yu2m*yu2m)/fccd;
		xf2m/*[i]*/ = 2.0*xu2m*sin((atan(lambda))/2)/lambda;
		yf2m/*[i]*/ = 2.0*yu2m*sin((atan(lambda))/2)/lambda;

		if( ((xf2m/*[i]*/>=-(Hccd/2)) && (xf2m/*[i]*/<=(Hccd/2))) && ((yf2m/*[i]*/>=-(Vccd/2)) && (yf2m/*[i]*/<=(Vccd/2))) )
		{
			j = j + 1;
			x2m_lcd[i] = UINT16((-xf2m/*[i]*/ + u0)*dx);
			y2m_lcd[i] = UINT16((yf2m/*[i]*/ + v0)*dy);
		}

	}

}

void CUntitled::Linef3m()
{
	double xw3m=0,yw3m=0,zw3m=0;
	double xc3m=0,yc3m=0,zc3m = 0;
	double xu3m=0,yu3m=0;
	double lambda=0.0;
	int j = 0; 
	double xf3m,yf3m;


	memset(x3m_lcd,0,pointnum);
	memset(y3m_lcd,0,pointnum);

	for(int i=1;i<=dotNum;i++)
	{
		xw3m = XLccd + (i-1)*x_dif  ;
		yw3m = -Zccd * VTan_theta +at4m;     //  600*tan(theta_x) + 
		zw3m = 0;

		xu3m = fhccd*(R[0][0] * xw3m + R[0][1] * yw3m + R[0][2] * zw3m + 0)/(R[2][0] * xw3m + R[2][1] * yw3m + R[2][2] * zw3m + RT13);
		yu3m = fvccd*(R[1][0] * xw3m + R[1][1] * yw3m + R[1][2] * zw3m + 0)/(R[2][0] * xw3m + R[2][1] * yw3m + R[2][2] * zw3m + RT13);

		lambda = sqrt(xu3m*xu3m+yu3m*yu3m)/fccd;
		xf3m/*[i]*/ = 2.0*xu3m*sin((atan(lambda))/2)/lambda;
		yf3m/*[i]*/ = 2.0*yu3m*sin((atan(lambda))/2)/lambda;

		if( ((xf3m/*[i]*/>=-(Hccd/2)) && (xf3m/*[i]*/<=(Hccd/2))) && ((yf3m/*[i]*/>=-(Vccd/2)) && (yf3m/*[i]*/<=(Vccd/2))) )
		{
			j = j + 1;
			x3m_lcd[i] = UINT16((-xf3m/*[i]*/ + u0)*dx);
			y3m_lcd[i] = UINT16((yf3m/*[i]*/ + v0)*dy);
		}

	}
}
void CUntitled::ParkSevenLine()
{

	unsigned int vddl=0,vddh=0,vddlh=0;

	if (yl_lcdMax>yr_lcdMax)
		vddl = yl_lcdMax+1;
	else
		vddl = yr_lcdMax+1;

	if(yl_lcdMin<yr_lcdMin)
		vddh = yl_lcdMin-1;
	else
		vddh = yr_lcdMin-1;

	vddlh=vddl-vddh+1;

	memset(lcd_fig,0,my_h*my_w*1);

	int x;
	int y ;
	int i ;

	int pp = 0;
	int fp = 0;
	for(x=-2;x<=2;x++)
	{
		for(y = -1;y<=1;y++)
		{
			for(i=1;i<=dotNum;i++)
			{
				if( yl_lcd[i]+y < 0 || xl_lcd[i]+x < 0 || yr_lcd[i]+y < 0 || xr_lcd[i]+x < 0 || \
				  	yl_lcd[i]+y > my_h || xl_lcd[i]+x > my_w || yr_lcd[i]+y > my_h || xr_lcd[i]+x >my_w )
					return;
				lcd_fig[yl_lcd[i]+y][xl_lcd[i]+x][0]=255;
				
				lcd_fig[yr_lcd[i]+y][xr_lcd[i]+x][0]=255;
			}
		}
	}


	for(int y=-1;y<=3;y++)
	{
		for(int x=1;x<=dotNum;x++)
		{
			if( yf_lcd[x]+y <0 || xf_lcd[x] < 0 )
				return;

			lcd_fig[yf_lcd[x]+y][xf_lcd[x]][0]=255; //一
			if(pp == 0&&x>dotNum/2) {
					pBuf[0] =xf_lcd[x];
					pBuf[1] = yf_lcd[x]+y;
					pp+=2;
		//	__android_log_print(4,"BCR", "1. x = %d  y = %d", pBuf[0], pBuf[1]);
			}

			if( ym_lcd[x]+y <0 || xm_lcd[x] < 0 )
				return;

			lcd_fig[ym_lcd[x]+y][xm_lcd[x]][0]=255;  //二           at1m
			if(fp == 0&& x==1)
				{
						pBuf[8] =xm_lcd[x];
						pBuf[9] = ym_lcd[x]+y;
						fp+=2;
				}

			if( y1m_lcd[x]+y <0 || x1m_lcd[x] < 0 )
				return;

			lcd_fig[y1m_lcd[x]+y][x1m_lcd[x]][0]=255;  //三        at2m
			if(fp == 2&& x==dotNum)
				{
						pBuf[10] =x1m_lcd[x];
						pBuf[11] = y1m_lcd[x]+y;
						fp+=2;
				}

			if( y2m_lcd[x]+y <0 || x2m_lcd[x] < 0 )
				return;

			lcd_fig[y2m_lcd[x]+y][x2m_lcd[x]][0]=255;  //四        at3m
			if(fp == 4&& x==1)
				{
						pBuf[12] =x2m_lcd[x];
						pBuf[13] = y2m_lcd[x]+y;
						fp+=2;
				}


			lcd_fig[y3m_lcd[x]+y][x3m_lcd[x]][0]=255;  //五            at4m
			if(pp == 2&&x>dotNum/2) {				
						pBuf[2] =x3m_lcd[x];
						pBuf[3] =y3m_lcd[x]+y;
						pp+=2;
			//			__android_log_print(4,"BCR", "2. x = %d  y = %d", pBuf[2], pBuf[3]);
					}
			if(fp == 6&& x== dotNum)
				{
						pBuf[14] =x3m_lcd[x];
						pBuf[15] = y3m_lcd[x]+y;
						fp+=2;
				}

		}  
	}


	int ndotLstart = dotNum*(at1m-Lstart_point)/(at3m-Lstart_point);
	int ndotRstart = dotNum*(at1m-Rstart_point)/(at3m-Rstart_point);
	for(int x=-2;x<=2;x++)
	{
		for(int y=-1;y<=1;y++)
		{
			for(int i=1;i<= ndotLstart/*100 dotNum*(at1m-Lstart_point)/(at3m-Lstart_point)*/;i++)
			{
				if(yl_lcd[i]+y < 0 || xl_lcd[i]+x < 0  )
					return;
				lcd_fig[yl_lcd[i]+y][xl_lcd[i]+x][0]=255;    // 左 底下
				if(pp == 6) {
						pBuf[6] =xl_lcd[i]+x;
						pBuf[7] =yl_lcd[i]+y;
						//__android_log_print(4,"BCR", "3. x = %d  y = %d", pBuf[4], pBuf[5]);
						pp+=2;
					}
			}
			for(int i=1;i<=ndotRstart/*100 dotNum*(at1m-Rstart_point)/(at3m-Rstart_point)*/;i++)
					{
						if( yr_lcd[i]+y < 0 || xr_lcd[i]+x < 0 )
							return;
						lcd_fig[yr_lcd[i]+y][xr_lcd[i]+x][0]=255;  //右底层
						if(pp == 4) {
								pBuf[4] =xr_lcd[i]+x;
								pBuf[5] =yr_lcd[i]+y;
							//	__android_log_print(4,"BCR", "4. x = %d  y = %d", pBuf[6], pBuf[7]);
								pp+=2;
							}
					}

		}
	}

int p = 0;
	for(int j=1/*50*/;j<=height;j++)
	{
		for(int i=1;i<=width;i++)
		{
			if(lcd_fig[height-(j-1)][i][0] != 0 )
				{
				dotBuf[p+1] =height-j-1;
				dotBuf[p] =i-1;
				p+=2;
			}
		}
	} 
	maxDot = p;

}

void CUntitled::DrawParkLine()
{
   LOG("DrawParkLine");
   Linefl();	// 右	 线
   Linefr();	// 左	 线
   Lineff();	// 中 1 号线
   Linefm();	// 中 2 号线
   Linef1m();	// 中 3 号线
   Linef2m();	// 中 4 号线
   Linef3m();	// 中 5 号线
   ParkSevenLine();// 转换成视觉“正八字图”

}

void CUntitled:: FillTwoMidLine (int start_x, int start_y, int end_x, int end_y,int line_width, int length)
{
	int x1, y1, x2, y2;
	int all_x = abs(end_x-start_x);
	float k = (float)(end_y-start_y)/(float)(end_x - start_x);
	float ab = cos(atan(k))*length;
	x1 = (int)(start_x+ab);
	y1= (int)(k*ab+start_y);
	DrawALine(start_x, start_y,x1, y1, line_width);
    x2 = (int)(end_x-ab);
	y2= (int)(end_y-k*ab);
	DrawALine(x2, y2,end_x, end_y, line_width);

}

void CUntitled::FillDashedLine(int start_x, int start_y, int end_x, int end_y, int line_width, int offsetW, int fill, int space)
{
		int all_x = abs(end_x-start_x);
		float k = (float)(end_y-start_y)/(float)(end_x - start_x);
		int dest_y = 0;
		float b = start_y-start_x*k;
		start_x +=offsetW;
		end_x-=offsetW;
		int overx = start_x+fill;
		for(int i = start_x; i <= end_x; i++)
			{
				dest_y = (int)(k*i+b);
				
			//	__android_log_print(4, "BCR", "k  = %f	dest_y %d ",k, dest_y);
				
				for(int y=-line_width; y<=line_width;y++)
					lcd_fig[dest_y+y][i][0]=255;
				if(i > overx){
					i+=space;
					overx = i+fill;
				}
			}


}

void CUntitled::DrawALine(int start_x, int start_y, int end_x, int end_y, int line_width)
{
	int all_x = abs(end_x-start_x);
	float k = (float)(end_y-start_y)/(float)(end_x - start_x);
	int dest_y = 0;
	float b = start_y-start_x*k;
	
	for(int i = start_x; i <= end_x; i++)
		{
			dest_y = (int)(k*i+b);
			
		//	__android_log_print(4, "BCR", "k  = %f	dest_y %d ",k, dest_y);

			for(int y=-line_width; y<=line_width;y++)
				lcd_fig[dest_y+y][i][0]=255;
		}
	//__android_log_print(4, "BCR", "start_x = %d	start_y %d end_x %d end_y %d b %f",
	//		start_x, start_y, end_x, end_y, b);

}

void CUntitled::FillFullLine(int x0,int y0,int x1,int y1,int line_width)  
{  
    int a,b,d1,d2,d,x,y;
	float m;
	if (x1<x0){d=x0,x0=x1,x1=d;d=y0,y0=y1,y1=d;}  
    a=y0-y1,b=x1-x0;if (b==0) m=-1*a*100;  
    else m=(float)a/(x0-x1);x=x0,y=y0;  
	
   for(int my=-line_width; my<=line_width;my++)
	lcd_fig[y+my][x][0]=255;

   
    if (m>=0 && m<=1)  
    {d=2*a+b;d1=2*a,d2=2*(a+b);  
     while (x<x1)  
     {  if (d<=0)    {   x++,y++,d+=d2;}  
        else    {   x++,d+=d1;  }  

		    for(int my=-line_width; my<=line_width;my++)
				lcd_fig[y+my][x][0]=255;
     }}  
    else if (m<=0 && m>=-1)  
    {d=2*a-b;d1=2*a-2*b,d2=2*a;  
     while (x<x1)  
     {  if (d>0) {   x++,y--,d+=d1;}  
        else    {   x++,d+=d2;  }  

		    for(int my=-line_width; my<=line_width;my++)
				lcd_fig[y+my][x][0]=255;
     }  }  
    else if (m>1)  
    {d=a+2*b;d1=2*(a+b),d2=2*b;  
     while (y<y1)  
     {  if (d>0) {   x++,y++,d+=d1;}  
        else    {   y++,d+=d2;  }  

		 	for(int my=-line_width; my<=line_width;my++)
				lcd_fig[y+my][x][0]=255;
     }  }  
    else  
    {d=a-2*b;d1=-2*b,d2=2*(a-b);      
     while (y>y1)  
     {  if (d<=0)    {   x++,y--,d+=d2;}  
        else    {   y--,d+=d1;  }  

	   	   for(int my=-line_width; my<=line_width;my++)
				lcd_fig[y+my][x][0]=255;
     }}   
}  

void CUntitled::SwitchTrackUsingMode(){
	__android_log_print(4, "BCR", "SwitchTrackUsingMode  %d ",trackUsingMode);
	switch(trackUsingMode){
		case 1: 
				LocusLine_R  = &CUntitled::SampleLocusLine_R1;
				LocusLine_L = &CUntitled::SampleLocusLine_L1;
				break;
		case 2:
				LocusLine_R  = &CUntitled::SampleLocusLine_R2;
				LocusLine_L = &CUntitled::SampleLocusLine_L2;
				break;
		}

}





void CUntitled::SampleLocusLine_L(double angle, bool isSaveFile)
{
	(this->*LocusLine_L)(angle, isSaveFile);

}	

void CUntitled::SampleLocusLine_R(double angle, bool isSaveFile)
{

	(this->*LocusLine_R)(angle, isSaveFile);
}


void CUntitled::SampleLocusLine_L1(double angle, bool isSaveFile)
{
	int al =91-angle;
	al = -al;
	int N = int(al);

	double xwl,ywl,zwl;
	double xur,yur;

	double xwr,ywr,zwr;
	double xul,yul;
	double lambda;

	double dxfr,dyfr;
	double dxfl,dyfl;
	double mPu = Pu;
	
	int m1 = dotNum-1;
	int nn = 0, mmr =16, mi = 0, mml = 18;
	int mm5=0;
	int fl = 0;
	

	dx = Hccd/Hlcd;  
	dy = Vccd/Vlcd;    //   象素间距离

	int  lWidth=width,lHeight=height;
	memset(lcd_fig,0,my_h*MaxWIDTH*1);

	memset(xddl,0,pointnum*4);
	memset(yddl,0,pointnum*4);
	memset(xddr,0,pointnum*4);
	memset(yddr,0,pointnum*4);
	memset(mBuf,0,MSIZE);
		if(N!=-1)
		mPu += (N)*lpu/50;				//0.1 most left
		u0 = Hccd/2.0+mPu;
		//__android_log_print(4, "BCR", "mPu	 %f\n",mPu);
		Pv2 = Pv;
		//if(N!=-1)
			//Pv2 -= 0.15*N/50; ///0.2
		v02 = Vccd/2.0+Pv2;
		
	//if( !DEBUG_MODE)
	//	Lcar = LcL;  //for guiji measure
	if( DEBUG_MODE)
		LcL = Lcar;  //for guiji measure
		else Lcar = LcL;
	delta = (2.0*pi*N)/360.0;
	r_trk = Lcar/tan(delta);   //   圆弧半径
		//LcL = Lcar;
	r_trk2 = LcL/tan(delta);   //   圆弧半径
	alpha_max = SmaxRL/(r_trk+Wcar/2) +asin(Fcar/(r_trk+Wcar/2));    //   一定弧长的弧角
	alpha_max2 = SmaxRL/(r_trk2+Wcar/2) +asin(Fcar/(r_trk2+Wcar/2));	 //   一定弧长的弧角


		alpha0_L  = asin((Fcar + Lstart_point)/(r_trk-Wcar/2));
		alpha0_R  = asin((Fcar + Rstart_point)/(r_trk2+Wcar/2));
		dalpha_L = (alpha_max -alpha0_L)/(dotNum-1);    //   画线角度分辨率
		dalpha_R = (alpha_max2 -alpha0_R)/(dotNum-1);

		//  其对应圆心坐标
		a = -r_trk + (XLccd+XRccd)/2;
		a2 = -r_trk2 + (XLccd+XRccd)/2;
		b = Zccd*tan(theta_x) - Fcar;

		//   三维空间坐标系(Xw,Yw,Zw) 里的曲线
		kL=0; 
		kR=0;

		//  *********************************************
		//   三维空间坐标系 到 摄像机坐标系 的变换
		//      (Xw,Yw,Zw) => (Xc,Yc,Zc)
		//   摄像机坐标 和 像平面坐标 的针孔透视变换
		//   (Xc,Yc,Zc) => (Xu,Yu)
		//  *********************************************
		//for i = 1:1:dotNum
		//__android_log_print(4, "BCR", "1 ");

		for(int i=1;i<=dotNum;i++)   
		{
			alphaL = dalpha_L*(i -kL*2) +alpha0_L;
			xwl = a + (r_trk-Wcar/2  - Lsafe )*cos(alphaL);
			ywl = b + (r_trk-Wcar/2  - Lsafe )*sin(alphaL);
			zwl = 0;

			xur = fhccd * (R[0][0]*xwl + R[0][1]*ywl + R[0][2]*zwl) / (R[2][0]*xwl + R[2][1]*ywl + R[2][2]*zwl + RT13);
			yur = fvccd * (R[1][0]*xwl + R[1][1]*ywl + R[1][2]*zwl) / (R[2][0]*xwl + R[2][1]*ywl + R[2][2]*zwl + RT13);
			 
			lambda = sqrt(xur*xur+yur*yur)/fccd;
			dxfr = (2.0*xur*sin((atan(lambda))/2))/lambda;
			dyfr = (2.0*yur*sin((atan(lambda))/2))/lambda;

			xddr[i] = UINT16(abs((-dxfr+u0)/dx));
			yddr[i] = UINT16((dyfr+v0)/dy);

			//  *********************************************
			//  *********************************************

			alphaR = dalpha_R*(i -kR*2) +alpha0_R;
			xwr  = a2 + (r_trk2+Wcar/2 + Lsafe  )*cos(alphaR);
			ywr = b + (r_trk2+Wcar/2 + Lsafe  )*sin(alphaR);
			zwr = 0;

			xul = fhccd*(R[0][0]*xwr + R[0][1]*ywr + R[0][2]*zwr)/(R[2][0]*xwr + R[2][1]*ywr + R[2][2]*zwr + RT13);
			yul = fvccd*(R[1][0]*xwr + R[1][1]*ywr + R[1][2]*zwr)/(R[2][0]*xwr + R[2][1]*ywr + R[2][2]*zwr + RT13);
 
			lambda = sqrt(xul*xul+yul*yul)/fccd;
			dxfl = 2.0*xul*sin((atan(lambda))/2)/lambda;
			dyfl = 2.0*yul*sin((atan(lambda))/2)/lambda;

			xddl[i] = UINT16(abs((-dxfl+u0)/dx));
			yddl[i] = UINT16((dyfl+v02)/dy);

			//	if(i==m4||i==m3||i==m1||i==m2)
			if(i==m1) // two line
			{
				mBuf[2] = xddl[i];
				mBuf[3] = yddl[i];
			}
			if(i == 1) {
				mBuf[8] = xddr[i];
				mBuf[9] = yddr[i];

				mBuf[10] = xddl[i];
				mBuf[11] = yddl[i];
			}

		}


		int p = 0;
		int mA = 2;
		int mB =-2;
		int mflag = 0;
		int dotN =0;
		float maxl,maxr;
		float mtmp;

	
		float Na= maxY+N/5+20;
		float Nb= maxY-N/10+20;
		float numF =2*N/50;									//left
		float mN = (float)(6*N/50);
		int MaxH = 0;
		
		if(maxY == 0 || maxY == 600)
			MaxH = (int)mBuf[9];
		else
			MaxH = maxY;
			
		//	mtmp = mNumF+numF;
		if(mBuf[11] < maxY) {
			mtmp =  mBuf[11];
			}
		else mtmp =  maxY;


			maxl=MaxH-mBuf[3];
			
			mi=1;
			mtmp = mtmp+numF; //dd
			mm5 = (int)mtmp;
				for(int i=1; i<=dotNum;i++)
				{
					
					for(int x=mB;x<=mA;x++)
					{
						for(int y=-1;y<=0;y++)
						{
						
						if(yddr[i]>(mMinY-7*N/50)&&(yddr[i]+y)<Nb)   
						{	lcd_fig[yddr[i]+y][xddr[i]+x][0]=255;
							if(mflag==0) 
								{
								mBuf[0]= xddr[i];
								mBuf[1] = yddr[i];
								mflag=1;
							}
							if(yddr[i] <mBuf[1] )
							{
								mBuf[0]= xddr[i];
								mBuf[1] = yddr[i];
								dotN = i;
							}
	
						}
						if((xddl[i]+x)>0&&(yddl[i]+y)<Na) 
							{ 
							lcd_fig[yddl[i]+y][(xddl[i]+x)][0]=255;
							}
						}	
					}
					if(yddr[i] == 235)
					{
						mBuf[4] = xddr[i];
						mBuf[5] = yddr[i];
					}

					if(yddl[i] == 235)
					{	
						mBuf[6] = xddl[i];
						mBuf[7] = yddl[i];
					}
					
				if(yddl[i] == mm5&&mi <16)
				{
					mBuf[mml++] = xddl[i];
					//mBuf[mml++] = yddl[i];
					mBuf[mml++] = mtmp;
				//the last-1 point
							mml+=2; 
							//mtmp=maxl/(mm3[0]-mN-mm3[15]);
							//mtmp = mBuf[3]+(mm3[mi++]-mm3[15])*mtmp;

							//mtmp =mBuf[3]+(float)(mm3[mi++]-mm3[15])*maxl/(mNumF+numF-mN-mm3[15]);// getRealY(mBuf[3],mm3[mi++],maxl,mm3[0]-mN,mm3[15]);
							mtmp = getRealY(mBuf[3],mm3[mi++],maxl,mNumF+numF-mN,mm3[15]);
							mm5  = (int)mtmp;
						//	__android_log_print(4, "BCR", "mi %d  mm5 %d",mi, mm5);
					 	
				}
				if(yddr[i]==maxY)
					{
							mBuf[8] = xddr[i]+mB;
							mBuf[9] = yddr[i];
					}
					if(yddl[i]==maxY)
					{
							mBuf[10] = xddl[i]+mA;
							mBuf[11] = yddl[i];
					}
			}
			
			maxr = MaxH-mBuf[1];
			
			numF = 3*N/50;
			//mN = 10*N/50;
			//mtmp = mNumF-numF;//getRealY(mBuf[1],mm3[mi++],maxr,mm3[0]+mN,mm3[15]);
			if(mBuf[9] < maxY) {
			mtmp =  mBuf[9];
			}
			else mtmp =  maxY;
			

			mtmp =  mBuf[9]-numF; //ddd
			
			mm5 = (int)mtmp;
			if(mm5 > mBuf[9]) 
				mm5 = mBuf[9];
		//	__android_log_print(4, "BCR", "mi %d  mm5 %f",mi, mtmp);

			mi = 1;
			for(int i=1; i<=dotN;i++)
			{
				if(yddr[i]==mm5&&mi<16)			//16xian
				{
						mBuf[mmr++] = xddr[i];
						//mBuf[mmr++] = yddr[i];
						mBuf[mmr++] =mtmp;
						
							//mtmp=maxr/(mm3[0]+mN-mm3[15]);
							//mtmp=mBuf[1]+(mm3[mi++]-mm3[15])*mtmp;

							//mtmp = mBuf[1]+(float)(mm3[mi++]-mm3[15])*maxr/(mNumF-numF+mN-mm3[15]);// getRealY(mBuf[1],mm3[mi++],maxr,mm3[0]+mN,mm3[15]);
							mtmp = getRealY(mBuf[1],mm3[mi++],maxr,mNumF-numF+mN,mm3[15]);

							mm5=(int)mtmp;
							mmr+=2;	
						//	__android_log_print(4, "BCR", "mi %d  mm5 %d",mi, mm5);
						
					
				}	
				//if(yddl[i]==mBuf[19])
				//		mBuf[18] = xddl[i];

					
			}
		//__android_log_print(4, "BCR", "mBuf[3]=%d mBuf[1]%d\n",(int)mBuf[3],(int)mBuf[1]);
				for(int j=1;j<=height;j++)
				{
					for(int i=1;i<=width;i++)
					{
						if(lcd_fig[(j-1)][i][0] != 0){
							if(isSaveFile)
							{
								RLBuf2[p] =i-1;
								RLBuf2[p+1]=j-1;	
							}
							else {
								RLBuf[p] =i-1;
								RLBuf[p+1]=j-1;	
							}
						p+=2;
						}	
					}
					
				} 
				if(isSaveFile)
					maxP2 = p;
					else
					maxP = p;
				//__android_log_print(4, "BCR", "P_R	 %d\n",p);

		return ;
}
				
			

void CUntitled::SampleLocusLine_R1(double angle, bool isSaveFile)
{
		int al = angle-90;
		al = -al;
		int N = al;


		//  *********************************************LC ,WC, Fcar, SmaxRL,dotNum
		double xwl,ywl,zwl;
		double xur,yur;

		double xwr,ywr,zwr;
		double xul,yul;
		double lambdal;
		double lambdar;
		double mPu = Pu;
	
		dx = Hccd/Hlcd;  
		dy = Vccd/Vlcd;    //   象素间距离

		int  lWidth=width,lHeight=height;

		memset(lcd_fig,0,my_h*MaxWIDTH*1);

		memset(xddl,0,pointnum*4);
		memset(yddl,0,pointnum*4);
		memset(xddr,0,pointnum*4);
		memset(yddr,0,pointnum*4);
		memset(mBuf,0,MSIZE);
		mPu += (-N)*rpu/50;				//0.1 most right
		 u0 = Hccd/2.0+mPu;
		Pv2 = Pv;
			//Pv2 -= 0.15*N/50;
		         v02 = Vccd/2.0+Pv2;
		//	Lcar = LcR; // measure test

		if( DEBUG_MODE)
			LcR=Lcar;
		else 
			Lcar=LcR;

			delta = (2.0*pi*N)/360.0;
			r_trk = Lcar/tan(delta);   //   圆弧半径
			//LcR=Lcar;
			r_trk2 = LcR/tan(delta);   //   圆弧半径
			alpha_max = SmaxRL/(r_trk+Wcar/2) +asin(Fcar/(r_trk+Wcar/2));      //   一定弧长的弧角
			alpha_max2 = SmaxRL/(r_trk2+Wcar/2) +asin(Fcar/(r_trk2+Wcar/2));      //   一定弧长的弧角
	
			alpha0_L  = asin((Fcar +Lstart_point)/(r_trk2+Wcar/2));
			alpha0_R  = asin((Fcar +Rstart_point)/(r_trk-Wcar/2));
			dalpha_L = (alpha_max2 -alpha0_L)/(dotNum-1);    //   画线角度分辨率
			dalpha_R = (alpha_max -alpha0_R)/(dotNum-1);


			a = r_trk + (XLccd+XRccd)/2;
			a2 = r_trk2 + (XLccd+XRccd)/2;
			b = Zccd*tan(theta_x) - Fcar;

			//   三维空间坐标系(Xw,Yw,Zw) 里的曲线
			kL=0; 
			kR=0;

			//  *********************************************
			//   三维空间坐标系 到 摄像机坐标系 的变换
			//      (Xw,Yw,Zw) => (Xc,Yc,Zc)
			//   摄像机坐标 和 像平面坐标 的针孔透视变换
			//   (Xc,Yc,Zc) => (Xu,Yu)
			//  *********************************************

			double dxfr,dyfr;
			double dxfl,dyfl;
			double dxcr,dycr,dzcr;
			double dxcl,dycl,dzcl;
			int m1 = dotNum;
			float maxl,maxr;
			int mmr =16, mi = 0, mml = 18;
			int mm5 = 0;
			int cutnum;
			for(int i=1;i<=dotNum;i++)
			{
				alphaL = pi -(dalpha_L*(i -kL*2) +alpha0_L);
				xwl = a2 + (r_trk2+Wcar/2 +Lsafe)*cos(alphaL);
				ywl = b + (r_trk2+Wcar/2 +Lsafe)*sin(alphaL);
				zwl = 0;

				alphaR = pi -(dalpha_R*(i -kR*2) +alpha0_R);
				xwr = a + (r_trk-Wcar/2 -Lsafe  )*cos(alphaR);
				ywr = b + (r_trk-Wcar/2 -Lsafe  )*sin(alphaR);
				zwr = 0;

				//B()
				dxcr = R[0][0]*xwl + R[0][1]*ywl + R[0][2]*zwl;
				dycr = R[1][0]*xwl + R[1][1]*ywl + R[1][2]*zwl;
				dzcr = R[2][0]*xwl + R[2][1]*ywl + R[2][2]*zwl + RT13;

				//C()
				dxcl = R[0][0]*xwr + R[0][1]*ywr + R[0][2]*zwr;
				dycl = R[1][0]*xwr + R[1][1]*ywr + R[1][2]*zwr;
				dzcl = R[2][0]*xwr + R[2][1]*ywr + R[2][2]*zwr + RT13;

				xul = fhccd*(dxcl)/(dzcl);
				yul = fvccd*(dycl)/(dzcl);

				lambdal = sqrt(xul*xul+yul*yul)/fccd;
				dxfl = 2.0*xul*sin((atan(lambdal))/2)/lambdal;
				dyfl = 2.0*yul*sin((atan(lambdal))/2)/lambdal;


				xur = fhccd*(dxcr)/(dzcr);
				yur = fvccd*(dycr)/(dzcr);
				
				cutnum = ceil((sqrt(xur*xur+yur*yur)/fccd)*10000);
				lambdar = (cutnum+1e-8)/10000;
				
				 dxfr  = 2.0*xur*sin((atan(lambdar))/2)/lambdar;
				 dyfr  = 2.0*yur*sin((atan(lambdar))/2)/lambdar;

				xddl[i] = UINT16(abs((-dxfl+u0)/dx));			// 		xdl = uint16( (-xfl + u0)./dx );
				if ((-dxfl+u0)<0)
				{
					xddl[i] =-4;
				}
				
				yddl[i] = UINT16((dyfl+v0)/dy);		// 		ydl = uint16( (yfl + v0)./dy );			
				xddr[i] = UINT16(abs((-dxfr+u0)/dx)); // 		xdr = uint16( (-xfr + u0)./dx );
				yddr[i] = UINT16((dyfr+v02)/dy); // 		ydr = uint16( (yfr + v0)./dy );
					
			//	if(i==(int)dotNum/mLine3)
		//		{
			//		mBuf[6] = xddl[i];
			//		mBuf[7] = yddl[i];
			//	}
				if(i==m1) // two line
				{
					mBuf[0] = xddr[i];
					mBuf[1] = yddr[i];
				}
				if(i == 1) {
					mBuf[8] = xddr[i];
					mBuf[9] = yddr[i];

					mBuf[10] = xddl[i];
					mBuf[11] = yddl[i];
				}
			}
			int mA = 2;
			int mB=-2;
			int mflag =0;
			int mrflag = 0;
			int dotN =0;
			float mtmp =0;
			float mN = 5*N/50;	

			float Na= maxY+N/5+20;
			float Nb= maxY-N/10+20;
			mBuf[1] +=1;
			int MaxH = 0;
			if(maxY == 0 || maxY == 600)
					MaxH = (int)mBuf[9];
				else
					MaxH = maxY;
			if(mBuf[9] < maxY)
					mtmp =	mBuf[9];
				else mtmp =  maxY;

			maxr=MaxH-mBuf[1];
			mi = 1;
			float numF = 2*N/50;				//right

			mtmp =  mtmp+numF;//getRealY(mBuf[1],mm3[mi++],maxr,mm3[0]-mN,mm3[15]);
			mm5=(int)mtmp;
		//	__android_log_print(4, "BCR", "mi %d  mtmp %f,	MaxH %d",mi, mtmp, MaxH);

			for(int i=1; i<=dotNum;i++)
			{

				for(int x=mB;x<=mA;x++)
				{
					for(int y=-1;y<=0;y++)
					{
						if(/*yddr[i]>((mMinY-N)-(int)0.4*N)&&*/yddr[i]+y < Na)
						{
						//	lcd_fig[yddr[i]+y][xddr[i]+x][1]=255;  //右线
						lcd_fig[yddr[i]+y][xddr[i]+x][0]=255;  //右线
						}

 						if(yddl[i]>(mMinY-7*N/50)&&yddl[i]+y<Nb) 
 						{ 
						//	lcd_fig[yddl[i]+y][(xddl[i]+x)][1]=255;  //左线
							lcd_fig[yddl[i]+y][(xddl[i]+x)][0]=255;  //左线
							if(mflag == 0)
							{	mBuf[2] = xddl[i];
								mBuf[3] = yddl[i];
								mflag=1;
							}
							if(mBuf[3]>yddl[i])
							{	mBuf[2] = xddl[i];
								mBuf[3] = yddl[i];
								dotN = i;
							}
						}
					}
				}
				if(yddr[i] == 235)
				{
						mBuf[4] = xddr[i];
						mBuf[5] = yddr[i];
				}
				
				if(yddl[i] == 235)
				{	
						mBuf[6] = xddl[i];
						mBuf[7] = yddl[i];
				}

				if(yddr[i] == mm5&&mi <16)
				{
					mBuf[mmr++] = xddr[i];
					mBuf[mmr++] =mtmp;
					
							mmr+=2; 
						//	mtmp =  mBuf[1]+(float)(mm3[mi++]-mm3[15])*maxr/(mNumF+numF-mN-mm3[15]);
							mtmp = getRealY(mBuf[1],mm3[mi++],maxr,mNumF+numF-mN,mm3[15]);
							mm5=(int)mtmp;
						//	__android_log_print(4, "BCR", "mi %d  mm5 %d",mi, mm5);
					
					
				}
					if(yddr[i]==maxY)
					{
							mBuf[8] = xddr[i]+=mB;
							mBuf[9] = yddr[i];
					}
					if(yddl[i]==maxY)
					{
							mBuf[10] = xddl[i]+=mA;
							mBuf[11] = yddl[i];
					}
			}
			mBuf[3]-=2;
			mi = 1;
			//mN = 4*N/50;
			numF = 6*N/50;
			maxl= MaxH-mBuf[3];
			//mtmp =  mNumF-numF;///getRealY(mBuf[3],mm3[mi++],maxl,mm3[0]+mN,mm3[15]);


			mtmp =  mBuf[11]-numF; //ddd
			
			mm5  = (int)mtmp;
			if(mm5 > mBuf[11]) 
				mm5 = mBuf[11];
		
				for(int i=1; i<=dotN;i++)
					{
					if(yddl[i]==mm5&&mi<16)			//16xian
						{
							mBuf[mml++] = xddl[i];
							mBuf[mml++] = mtmp;
							mml+=2;	
							
								// mtmp =  mBuf[3]+(float)(mm3[mi++]-mm3[15])*maxl/(mm3[0]+mN-mm3[15]);
								mtmp =  getRealY(mBuf[3],mm3[mi++],maxl,mm3[0]+mN,mm3[15]);
								mm5  = (int)mtmp;
							//	__android_log_print(4, "BCR", "mi %d  mm5 %d",mi, mm5);
						
						}
					}

			int p =0;
			for(int j=1;j<=height;j++)
			{
				for(int i=1;i<=width;i++)
				{
					if(lcd_fig[j-1][i][0] !=0)
						{
							if(isSaveFile)
							{
								RLBuf2[p] =i-1;
								RLBuf2[p+1]=j-1;	
							}
							else {
								RLBuf[p] =i-1;
								RLBuf[p+1]=j-1;	
							}
						p+=2;
						}
				}
			} 			
			if(isSaveFile)
				maxP2 = p;
				else
				maxP = p;
			//__android_log_print(4, "BCR", "p_L :   %d\n",p);
	return ;

}
									
							

void CUntitled::SampleLocusLine_L2(double angle, bool isSaveFile)
{
	int al =91-angle;
	al = -al;
	int N = int(al);

	double xwl,ywl,zwl;
	double xur,yur;

	double xwr,ywr,zwr;
	double xul,yul;
	double lambda;

	double dxfr,dyfr;
	double dxfl,dyfl;
	double mPu = Pu;
	
	int m1 = dotNum-1;
	int nn = 0, mmr =16, mi = 0, mml = 18;
	int mm5=0;
	int fl = 0;
	

	dx = Hccd/Hlcd;  
	dy = Vccd/Vlcd;    //   象素间距离

	int  lWidth=width,lHeight=height;
	memset(lcd_fig,0,my_h*MaxWIDTH*1);

	memset(xddl,0,pointnum*4);
	memset(yddl,0,pointnum*4);
	memset(xddr,0,pointnum*4);
	memset(yddr,0,pointnum*4);
	memset(mBuf,0,MSIZE);
		if(N!=-1)
		mPu += (N)*lpu/50;				//0.1 most left
		u0 = Hccd/2.0+mPu;
		//__android_log_print(4, "BCR", "mPu	 %f\n",mPu);
		Pv2 = Pv;
		//if(N!=-1)
			//Pv2 -= 0.15*N/50; ///0.2
		v02 = Vccd/2.0+Pv2;
		
	//if( !DEBUG_MODE)
	//	Lcar = LcL;  //for guiji measure
	if( DEBUG_MODE)
		LcL = Lcar;  //for guiji measure
		else Lcar = LcL;
	delta = (2.0*pi*N)/360.0;
	r_trk = Lcar/tan(delta);   //   圆弧半径
		//LcL = Lcar;
	r_trk2 = LcL/tan(delta);   //   圆弧半径
	alpha_max = SmaxRL/(r_trk+Wcar/2) +asin(Fcar/(r_trk+Wcar/2));    //   一定弧长的弧角
	alpha_max2 = SmaxRL/(r_trk2+Wcar/2) +asin(Fcar/(r_trk2+Wcar/2));	 //   一定弧长的弧角


		alpha0_L  = asin((Fcar + Lstart_point)/(r_trk-Wcar/2));
		alpha0_R  = asin((Fcar + Rstart_point)/(r_trk2+Wcar/2));
		dalpha_L = (alpha_max -alpha0_L)/(dotNum-1);    //   画线角度分辨率
		dalpha_R = (alpha_max2 -alpha0_R)/(dotNum-1);

		//  其对应圆心坐标
		a = -r_trk + (XLccd+XRccd)/2;
		a2 = -r_trk2 + (XLccd+XRccd)/2;
		b = Zccd*tan(theta_x) - Fcar;

		//   三维空间坐标系(Xw,Yw,Zw) 里的曲线
		kL=0; 
		kR=0;

		for(int i=1;i<=dotNum;i++)   
		{
			alphaL = dalpha_L*(i -kL*2) +alpha0_L;
			xwl = a + (r_trk-Wcar/2  - Lsafe )*cos(alphaL);
			ywl = b + (r_trk-Wcar/2  - Lsafe )*sin(alphaL);
			zwl = 0;

			xur = fhccd * (R[0][0]*xwl + R[0][1]*ywl + R[0][2]*zwl) / (R[2][0]*xwl + R[2][1]*ywl + R[2][2]*zwl + RT13);
			yur = fvccd * (R[1][0]*xwl + R[1][1]*ywl + R[1][2]*zwl) / (R[2][0]*xwl + R[2][1]*ywl + R[2][2]*zwl + RT13);
			 
			lambda = sqrt(xur*xur+yur*yur)/fccd;
			dxfr = (2.0*xur*sin((atan(lambda))/2))/lambda;
			dyfr = (2.0*yur*sin((atan(lambda))/2))/lambda;

			xddr[i] = UINT16(abs((-dxfr+u0)/dx));
			yddr[i] = UINT16((dyfr+v0)/dy);

			//  *********************************************
			//  *********************************************

			alphaR = dalpha_R*(i -kR*2) +alpha0_R;
			xwr  = a2 + (r_trk2+Wcar/2 + Lsafe  )*cos(alphaR);
			ywr = b + (r_trk2+Wcar/2 + Lsafe  )*sin(alphaR);
			zwr = 0;

			xul = fhccd*(R[0][0]*xwr + R[0][1]*ywr + R[0][2]*zwr)/(R[2][0]*xwr + R[2][1]*ywr + R[2][2]*zwr + RT13);
			yul = fvccd*(R[1][0]*xwr + R[1][1]*ywr + R[1][2]*zwr)/(R[2][0]*xwr + R[2][1]*ywr + R[2][2]*zwr + RT13);
 
			lambda = sqrt(xul*xul+yul*yul)/fccd;
			dxfl = 2.0*xul*sin((atan(lambda))/2)/lambda;
			dyfl = 2.0*yul*sin((atan(lambda))/2)/lambda;

			xddl[i] = UINT16(abs((-dxfl+u0)/dx));
			yddl[i] = UINT16((dyfl+v02)/dy);

			//	if(i==m4||i==m3||i==m1||i==m2)
			if(i==m1) // two line
			{
				mBuf[2] = xddl[i];
				mBuf[3] = yddl[i];			

				mBuf[0] = xddr[i];
				mBuf[1] = yddr[i];	
			}
			else if(midLineY1 == i){
				midDotLine[2] = xddl[i];
				midDotLine[3] = yddl[i];
				
				midDotLine[0] = xddr[i];
				midDotLine[1] = yddr[i];

				}
			else if(midLineY2 == i){
				midDotLine[6] = xddl[i];
				midDotLine[7] = yddl[i];
				
				midDotLine[4] = xddr[i];
				midDotLine[5] = yddr[i];

				}
			else if(midLineY3 == i){
				midDotLine[10] = xddl[i];
				midDotLine[11] = yddl[i];
				
				midDotLine[8] = xddr[i];
				midDotLine[9] = yddr[i];

				}

		}

		DrawALine((int)mBuf[2], (int)mBuf[3], (int)mBuf[0], (int)mBuf[1], 2);
		if(angle != 90) {
			FillTwoMidLine((int)midDotLine[2], (int)midDotLine[3], (int)midDotLine[0], (int)midDotLine[1], 2, 50);
			FillTwoMidLine((int)midDotLine[6], (int)midDotLine[7], (int)midDotLine[4], (int)midDotLine[5], 2, 50);
		}else {
			DrawALine((int)midDotLine[2], (int)midDotLine[3], (int)midDotLine[0], (int)midDotLine[1], 2);
			DrawALine((int)midDotLine[6], (int)midDotLine[7], (int)midDotLine[4], (int)midDotLine[5], 2);
			}
		FillDashedLine((int)midDotLine[10], (int)midDotLine[11], (int)midDotLine[8], (int)midDotLine[9], 2, 50, 4, 6);

		int p = 0;
		int mA = 2;
		int mB =-2;
		int mflag = 0;
		int dotN =0;
		float maxl,maxr;
		float mtmp;

				for(int i=1; i<=dotNum;i++)
				{
					
					for(int x=mB;x<=mA;x++)
					{
						for(int y=-1;y<=0;y++)
						{
						
						if(yddr[i]>(mMinY-7*N/50)&&(yddr[i]+y)<maxY)   
						{	lcd_fig[yddr[i]+y][xddr[i]+x][0]=255;
						}
						if(yddl[i]>(mMinY-7*N/50)&&(yddl[i]+y)<maxY) 
							{ 
							lcd_fig[yddl[i]+y][(xddl[i]+x)][0]=255;
							}
						}	
					}
	
			}	
	
				for(int j=1;j<=height;j++)
				{
					for(int i=1;i<=width;i++)
					{
						if(lcd_fig[(j-1)][i][0] != 0){
							if(isSaveFile)
							{
								RLBuf2[p] =i-1;
								RLBuf2[p+1]=j-1;	
							}
							else {
								RLBuf[p] =i-1;
								RLBuf[p+1]=j-1;	
							}
						p+=2;
						}	
					}
					
				} 
				if(isSaveFile)
					maxP2 = p;
					else
					maxP = p;
				//__android_log_print(4, "BCR", "P_R	 %d\n",p);

		return ;
}
				
			

void CUntitled::SampleLocusLine_R2(double angle, bool isSaveFile)
{
		int al = angle-90;
		al = -al;
		int N = al;


		//  *********************************************LC ,WC, Fcar, SmaxRL,dotNum
		double xwl,ywl,zwl;
		double xur,yur;

		double xwr,ywr,zwr;
		double xul,yul;
		double lambdal;
		double lambdar;
		double mPu = Pu;
	
		dx = Hccd/Hlcd;  
		dy = Vccd/Vlcd;    //   象素间距离

		int  lWidth=width,lHeight=height;

		memset(lcd_fig,0,my_h*MaxWIDTH*1);

		memset(xddl,0,pointnum*4);
		memset(yddl,0,pointnum*4);
		memset(xddr,0,pointnum*4);
		memset(yddr,0,pointnum*4);
		memset(mBuf,0,MSIZE);
		mPu += (-N)*rpu/50;				//0.1 most right
		 u0 = Hccd/2.0+mPu;
		Pv2 = Pv;
			//Pv2 -= 0.15*N/50;
		         v02 = Vccd/2.0+Pv2;
		//	Lcar = LcR; // measure test

		if( DEBUG_MODE)
			LcR=Lcar;
		else 
			Lcar=LcR;

			delta = (2.0*pi*N)/360.0;
			r_trk = Lcar/tan(delta);   //   圆弧半径
			//LcR=Lcar;
			r_trk2 = LcR/tan(delta);   //   圆弧半径
			alpha_max = SmaxRL/(r_trk+Wcar/2) +asin(Fcar/(r_trk+Wcar/2));      //   一定弧长的弧角
			alpha_max2 = SmaxRL/(r_trk2+Wcar/2) +asin(Fcar/(r_trk2+Wcar/2));      //   一定弧长的弧角
	
			alpha0_L  = asin((Fcar +Lstart_point)/(r_trk2+Wcar/2));
			alpha0_R  = asin((Fcar +Rstart_point)/(r_trk-Wcar/2));
			dalpha_L = (alpha_max2 -alpha0_L)/(dotNum-1);    //   画线角度分辨率
			dalpha_R = (alpha_max -alpha0_R)/(dotNum-1);


			a = r_trk + (XLccd+XRccd)/2;
			a2 = r_trk2 + (XLccd+XRccd)/2;
			b = Zccd*tan(theta_x) - Fcar;

			//   三维空间坐标系(Xw,Yw,Zw) 里的曲线
			kL=0; 
			kR=0;

			//  *********************************************
			//   三维空间坐标系 到 摄像机坐标系 的变换
			//      (Xw,Yw,Zw) => (Xc,Yc,Zc)
			//   摄像机坐标 和 像平面坐标 的针孔透视变换
			//   (Xc,Yc,Zc) => (Xu,Yu)
			//  *********************************************

			double dxfr,dyfr;
			double dxfl,dyfl;
			double dxcr,dycr,dzcr;
			double dxcl,dycl,dzcl;
			int m1 = dotNum;
			float maxl,maxr;
			int mmr =16, mi = 0, mml = 18;
			int mm5 = 0;
			int cutnum;
			for(int i=1;i<=dotNum;i++)
			{
				alphaL = pi -(dalpha_L*(i -kL*2) +alpha0_L);
				xwl = a2 + (r_trk2+Wcar/2 +Lsafe)*cos(alphaL);
				ywl = b + (r_trk2+Wcar/2 +Lsafe)*sin(alphaL);
				zwl = 0;

				alphaR = pi -(dalpha_R*(i -kR*2) +alpha0_R);
				xwr = a + (r_trk-Wcar/2 -Lsafe  )*cos(alphaR);
				ywr = b + (r_trk-Wcar/2 -Lsafe  )*sin(alphaR);
				zwr = 0;

				//B()
				dxcr = R[0][0]*xwl + R[0][1]*ywl + R[0][2]*zwl;
				dycr = R[1][0]*xwl + R[1][1]*ywl + R[1][2]*zwl;
				dzcr = R[2][0]*xwl + R[2][1]*ywl + R[2][2]*zwl + RT13;

				//C()
				dxcl = R[0][0]*xwr + R[0][1]*ywr + R[0][2]*zwr;
				dycl = R[1][0]*xwr + R[1][1]*ywr + R[1][2]*zwr;
				dzcl = R[2][0]*xwr + R[2][1]*ywr + R[2][2]*zwr + RT13;

				xul = fhccd*(dxcl)/(dzcl);
				yul = fvccd*(dycl)/(dzcl);

				lambdal = sqrt(xul*xul+yul*yul)/fccd;
				dxfl = 2.0*xul*sin((atan(lambdal))/2)/lambdal;
				dyfl = 2.0*yul*sin((atan(lambdal))/2)/lambdal;


				xur = fhccd*(dxcr)/(dzcr);
				yur = fvccd*(dycr)/(dzcr);
				
				cutnum = ceil((sqrt(xur*xur+yur*yur)/fccd)*10000);
				lambdar = (cutnum+1e-8)/10000;
				
				 dxfr  = 2.0*xur*sin((atan(lambdar))/2)/lambdar;
				 dyfr  = 2.0*yur*sin((atan(lambdar))/2)/lambdar;

				xddl[i] = UINT16(abs((-dxfl+u0)/dx));			// 		xdl = uint16( (-xfl + u0)./dx );
				if ((-dxfl+u0)<0)
				{
					xddl[i] =-4;
				}
				
				yddl[i] = UINT16((dyfl+v0)/dy);		// 		ydl = uint16( (yfl + v0)./dy );			
				xddr[i] = UINT16(abs((-dxfr+u0)/dx)); // 		xdr = uint16( (-xfr + u0)./dx );
				yddr[i] = UINT16((dyfr+v02)/dy); // 		ydr = uint16( (yfr + v0)./dy );
					
			//	if(i==(int)dotNum/mLine3)
		//		{
			//		mBuf[6] = xddl[i];
			//		mBuf[7] = yddl[i];
			//	}
				
				if(i==m1) // two line
						{
							mBuf[2] = xddl[i];
							mBuf[3] = yddl[i];			
			
							mBuf[0] = xddr[i];
							mBuf[1] = yddr[i];	
						}
						else if(midLineY1 == i){
							midDotLine[2] = xddl[i];
							midDotLine[3] = yddl[i];
							
							midDotLine[0] = xddr[i];
							midDotLine[1] = yddr[i];
			
							}
						else if(midLineY2 == i){
							midDotLine[6] = xddl[i];
							midDotLine[7] = yddl[i];
							
							midDotLine[4] = xddr[i];
							midDotLine[5] = yddr[i];
			
							}
						else if(midLineY3 == i){
							midDotLine[10] = xddl[i];
							midDotLine[11] = yddl[i];
							
							midDotLine[8] = xddr[i];
							midDotLine[9] = yddr[i];
			
							}
			
			}
			
			DrawALine((int)mBuf[2], (int)mBuf[3], (int)mBuf[0], (int)mBuf[1], 2);
			FillTwoMidLine((int)midDotLine[2], (int)midDotLine[3], (int)midDotLine[0], (int)midDotLine[1], 2, 50);
			FillTwoMidLine((int)midDotLine[6], (int)midDotLine[7], (int)midDotLine[4], (int)midDotLine[5], 2, 50);	
			FillDashedLine((int)midDotLine[10], (int)midDotLine[11], (int)midDotLine[8], (int)midDotLine[9], 2, 50,4, 6);
				
			int mA = 2;
			int mB=-2;
			for(int i=1; i<=dotNum;i++)
			{

				for(int x=mB;x<=mA;x++)
				{
					for(int y=-1;y<=0;y++)
					{
						if(yddr[i]>(mMinY-7*N/50)&&yddr[i]+y < maxY)
						{
						lcd_fig[yddr[i]+y][xddr[i]+x][0]=255;  //右线
						}

 						if(yddl[i]>(mMinY-7*N/50)&&yddl[i]+y<maxY) 
 						{ 
							lcd_fig[yddl[i]+y][(xddl[i]+x)][0]=255;  //左线
							
						}
					}
				}
				
			}
			
			int p =0;
			for(int j=1;j<=height;j++)
			{
				for(int i=1;i<=width;i++)
				{
					if(lcd_fig[j-1][i][0] !=0)
						{
							if(isSaveFile)
							{
								RLBuf2[p] =i-1;
								RLBuf2[p+1]=j-1;	
							}
							else {
								RLBuf[p] =i-1;
								RLBuf[p+1]=j-1;	
							}
						p+=2;
						}
				}
			} 			
			if(isSaveFile)
				maxP2 = p;
				else
				maxP = p;
			//__android_log_print(4, "BCR", "p_L :   %d\n",p);
	return ;

}
							

		
							

///////////////////////////////////////////////////////
void CUntitled::AllFunc()
{
	//LOG("allfunc");
	init_Param();
	   LOG("AllFunc");
   	//Linefl();	// 右	 线
   	//Linefr();	// 左	 线
	DrawParkLine();	
}


void  CUntitled::Save( )
{
	int fd = open("/ext_sdcard1/bc_save_conf",  O_RDWR|O_TRUNC|O_CREAT, 0777);
	if(fd<0){
		LOG("can not open sd1 bc_save_conf");
		fd = open("/ext_sdcard2/bc_save_conf",  O_RDWR|O_TRUNC|O_CREAT, 0777);
		if(fd<0){
			LOG("can not open sd2 bc_save_conf");  
		fd = open("/data/bc_save_conf",  O_RDWR|O_TRUNC|O_CREAT, 0777);
		if(fd<0)  {  LOG("can not open data");
		return;  }
		} }
	LOG("save OK");
	char buf[300]={0};
	 sprintf(buf, "\t%d\t%d\t%d\t%0.2f\t%0.2f\t%0.3f\t%0.2f\t%0.4f\t%0.1f\t%0.1f\t%0.4f\t%d\t%d\t%d\t%d\t%d",
		atsafe,high,Vision,Lcar,Wcar,Pu,Pv,x_angle,Smax,Fcar,y_angle,at1m,at2m,at3m,at4m,at5m);
	  __android_log_print(4, "BCR", " save:%s\n", buf);
	  __android_log_print(4, "BCR", " mNumF : %d  mNum :%d   mLine3: %d  maxY: %d\n", mNumF,mNum, mLine3, maxY);
	write(fd, buf,strlen(buf));
	close(fd);
	int mfd = open("/data/backcar_conf",  O_RDWR|O_TRUNC|O_CREAT, 0777);
	if(mfd <0)   {  LOG("can not open backcar_conf");
		return;  }
	char mbuf[500]={0};
		 sprintf(mbuf, "atsafe=%d\thigh=%d\tVision=%d\tLcar=%0.2f\tWcar=%0.2f\tPu=%0.3f\n\tPv=%0.2f\tx_angle=%0.4f\tSmax=%0.1f\tFcar=%0.1f\ty_angle=%0.4f\tat1m=%d\tat2m=%d\tat3m=%d\tat4m=%d\tat5m=%d",
		atsafe,high,Vision,Lcar,Wcar,Pu,Pv,x_angle,Smax,Fcar,y_angle,at1m,at2m,at3m,at4m,at5m);
		 write(mfd, mbuf,strlen(mbuf));
	close(mfd);
}

bool  CUntitled::Read_conf(FuncParam *m_pFuncParam)
{
	int fd = open("/ext_sdcard1/bc_save_conf",  O_RDONLY);
	if (fd<0){
			LOG("can not open sd1 bc_save_conf");
			fd = open("/ext_sdcard2/bc_save_conf",  O_RDONLY);
		}
	if (fd <0) {
			LOG("can not open sd2 bc_save_conf..");
			fd = open("/data/bc_save_conf",  O_RDONLY);
			if (fd<0)	{
					LOG("can not open data");
					return false;
				}
			LOG("open data");
		}
	LOG("OK");
	
	char buf[300]={0};
	int m = read(fd,buf,200);
	//  __android_log_print(4, "BCR","m = %d  read buf = %s",m, buf);

	char *poi;
	poi = strtok(buf,"/\t");
	m_pFuncParam->parkP.atsafe=atoi(poi);
	poi = strtok(NULL,"/\t");
	m_pFuncParam->camP.nhigh=atoi(poi);
	poi = strtok(NULL,"/\t");
	m_pFuncParam->camP.nVision=atoi(poi);
	poi = strtok(NULL,"/\t");
	m_pFuncParam->carP.Lc=simple_str(poi);
	poi = strtok(NULL,"/\t");
	m_pFuncParam->carP.Wc=simple_str(poi);
	poi = strtok(NULL,"/\t");
	m_pFuncParam->camP.dPu=simple_str(poi);
	poi = strtok(NULL,"/\t");
	m_pFuncParam->camP.dPv=simple_str(poi);
	poi = strtok(NULL,"/\t");
	m_pFuncParam->camP.dx_angle=simple_str(poi);
	poi = strtok(NULL,"/\t");
	m_pFuncParam->parkP.Smax=simple_str(poi);
	poi = strtok(NULL,"/\t");
	Fcar=simple_str(poi);
	poi = strtok(NULL,"/\t");
	m_pFuncParam->camP.dy_angle=simple_str(poi);
	poi = strtok(NULL,"/\t");
	m_pFuncParam->parkP.at1m = atoi(poi);
	poi = strtok(NULL,"/\t");
	m_pFuncParam->parkP.at2m = atoi(poi);
	poi = strtok(NULL,"/\t");
	m_pFuncParam->parkP.at3m = atoi(poi);
	poi = strtok(NULL,"/\t");
	m_pFuncParam->parkP.at4m = atoi(poi);
	poi = strtok(NULL,"/\t");
	m_pFuncParam->parkP.at5m = atoi(poi);
	  __android_log_print(4,"BCR", " atsafe=%d  high=%d   Vision=%d   Lcar=%0.2f   Wcar=%0.2f   Pu=%0.3f \n   Pv=%0.2f   x_angle=%0.4f   Smax=%0.1f  Fcar=%0.1f  y_angle=%0.4f",
			m_pFuncParam->parkP.atsafe,m_pFuncParam->camP.nhigh,m_pFuncParam->camP.nVision,m_pFuncParam->carP.Lc,m_pFuncParam->carP.Wc,
			m_pFuncParam->camP.dPu,m_pFuncParam->camP.dPv,m_pFuncParam->camP.dx_angle,m_pFuncParam->parkP.Smax,Fcar,m_pFuncParam->camP.dy_angle);
	  __android_log_print(4,"BCR", "at1m = %d  at2m = %d  at3m = %d  at4m = %d  at5m = %d ",
	  	m_pFuncParam->parkP.at1m,m_pFuncParam->parkP.at2m,m_pFuncParam->parkP.at3m,
	  	m_pFuncParam->parkP.at4m,m_pFuncParam->parkP.at5m);

	close(fd);
	return true;
}

void  CUntitled::Req_RealCarParamChange( BYTE *buff,int arg, float* data, FuncParam *m_pFuncParam)
{
	switch (buff[0])
		{
		case 0x01:
			if (data[0]<0 && pBuf[3]>0)
				m_pFuncParam->camP.dx_angle+=0.5;
			else if (data[0]>0 &&pBuf[1] <height)
				m_pFuncParam->camP.dx_angle-=0.5;
			break;
		case 0x02:
			if (data[0]>0)
				m_pFuncParam->camP.nhigh+=10;
			else
				m_pFuncParam->camP.nhigh-=10;
			break;
		case 0x03:
			if (data[0]<1)
				m_pFuncParam->camP.nVision+=2;
			else  if (pBuf[4]>=0 && data[0]>1&&pBuf[6] <= width)
				m_pFuncParam->camP.nVision-=2;
			break;
		case 0x04:
			if (data[0]<0 && pBuf[3]> 0)
				m_pFuncParam->camP.dPv-=0.01;
			else if (data[0]>0 && pBuf[1]<(height-10)) 
				m_pFuncParam->camP.dPv+=0.01;
			break;
		case 0x05:
			
			if (pBuf[4]>=0 && data[0]<0)
				{
					m_pFuncParam->camP.dPu-=0.01;
				}
			else  if (pBuf[6] <= (width-20) && data[0]>0)
				{
					m_pFuncParam->camP.dPu+=0.01;
				}
			break;
		case 0x06:
			__android_log_print(4, "BCR", "Lc = %f", data[0]);
			if(data[0] != 0)
			m_pFuncParam->carP.Lc = data[0];
			break;
		case 0x07:
			if (data[0]<0&& pBuf[3]> 0 )
				m_pFuncParam->parkP.atsafe+=2;
			else if (data[0]>0&& pBuf[1]<(height-10)) 
				m_pFuncParam->parkP.atsafe-=2;
			if(data[0]>0)
				mMinY+=2;
			else mMinY-=2;
			__android_log_print(4, "BCR", "mMinY = %d", mMinY);
			break;
		case 0x08:
			if (data[0]>0)
				m_pFuncParam->carP.Wc-=20;
			else
				m_pFuncParam->carP.Wc+=20;
			break;
		case 0x09:
			if (data[0]>0)
				m_pFuncParam->carP.Wc+=20;
			else
				m_pFuncParam->carP.Wc-=20;
			break;
		case 0x0a:
			m_pFuncParam->parkP.Smax+=50;
			break;
		case 0x0b:
			m_pFuncParam->parkP.Smax-=50;
			break;
		case 0x0d:
			if (data[0]<0 || arg>0)
				m_pFuncParam->camP.dy_angle -=0.2;
			else if (data[0]>0||arg<0)
				m_pFuncParam->camP.dy_angle+=0.2;
			break;
		case 0x0e:
			if (data[0]<0 ||arg <0)
				m_pFuncParam->camP.dy_angle+=0.2;
			else if (data[0]>0 ||arg>0)
				m_pFuncParam->camP.dy_angle-=0.2;
			break;
		case 0x0f:  if(data[0]<0 && (at1m+AMSIZE) < at2m)  m_pFuncParam->parkP.at1m +=20; 
					else if(data[0]>0 && at1m>400)  m_pFuncParam->parkP.at1m -=20;  
				if(data[0]<0) mNum+=4;
				else mNum-=4;

			if(data[0]>0)
				mMinY+=2;
			else mMinY-=2;
				break;
		case 0x10: if(data[0]<0 &&  (at2m+AMSIZE)<at3m)  m_pFuncParam->parkP.at2m +=20;
					else if(data[0]>0 &&  (at1m+AMSIZE)<at2m)  m_pFuncParam->parkP.at2m -=20;
					if(data[0]>0)
				maxY+=2;
			else maxY-=2;

				break;
		case 0x11: if(data[0]<0 && (at3m+AMSIZE) < at4m) m_pFuncParam->parkP.at3m +=20;
					else if(data[0]>0 && (at2m+AMSIZE)<at3m)  m_pFuncParam->parkP.at3m -=20;
					if(data[0]<0) mNumF+=1;
					else mNumF-=1;
				break;
		case 0x12: if(data[0]<0) {  m_pFuncParam->parkP.at4m +=40;
					m_pFuncParam->parkP.Smax+=40;
					}
					else if(data[0]>0 && at3m <=at4m) {  m_pFuncParam->parkP.at4m -=40;
					m_pFuncParam->parkP.Smax-=40;
						}
				break;
		case 0x13:
		//	if(data[1] != 0)
		//    m_pFuncParam->carP.Lc = data[1];
			Reset(data[0]);
			break;
		case 0x14:
			Save();
			break;
		case 0x15:
			if(width != data[1] || height != data[2]){
				width = data[1];
				height= data[2];

				if(height<540){
					my_h=600+20;
					my_w=1024+20;
					}else{
					my_h=height+20;
					my_w=width+20;
				}	
				__android_log_print(4, "BCR", "screenConfig %d * %d \n myw %d * myh %d", width, 
					height,my_w, my_h);
				Reset(data[0]);
			}else 
				return;
			break;
		case 0x16:
			ResetTrackFileIsNotOk();
			break;
		case 0x17:
			width = 720;
			height= 480;
			my_h=height+20;
			my_w=width+20;
		
		/*	{
			__android_log_print(4, "BCR", "data =%f\n",data );
			int m_data = data;
			__android_log_print(4, "BCR", "m_data =%d\n",m_data );
			Reset(m_data%1000);
			__android_log_print(4, "BCR", "m_data =%d\n",m_data );
			m_data = m_data/1000;
			height = m_data%1000;
			__android_log_print(4, "BCR", "m_data =%d\n",m_data );
			m_data = m_data/1000;
			width = m_data%1000;
			__android_log_print(4, "BCR", "h =%d  w %d\n",height, width);
			if(height<600){
				my_h=600+20;
				my_w=1024+20;
				}else
				{
				my_h=height+20;
				my_w=width+20;
			}			
			}
			*/
			break;
		case 0x18:
			// backcar busy or not
			saveTrackFile(data[0]);
			return;
			break;
		case 0x19:
	  		trailmode =0;
			DEBUG_MODE = true;
		case 0x20:
			property_set("ctl.stop", "bootanim");
			break;
		default:
			break;
		}
	Test_ChangeParam_ParkLine(*m_pFuncParam);
}


void CUntitled::Test_ChangeParam_ParkLine(FuncParam funPm/*ParkLineParam lineparam,CarParam carP,CameraParam camP*/)
{
	ParkLineParam lineparam = funPm.parkP;
	dotNum =lineparam.dotNum;       //  画点的个数
	Smax = lineparam.Smax;     //   画线最远距离
	SmaxRL = Smax + 1000;
	start_point =80;//lineparam.start; //  左右开始画线的位置
	Lstart_point = 220;//lineparam.Lstart; //  左右开始画线的位置
	Rstart_point = 220;//lineparam.Rstart;

	atsafe=lineparam.atsafe;
	at1m=lineparam.at1m;
	at2m=lineparam.at2m;
	at3m=lineparam.at3m;
	at4m=lineparam.at4m;
	at5m=lineparam.at5m;
	
	Fcar = lineparam.Fcar;     // 
	


	CarParam carP = funPm.carP;
	Lcar = carP.Lc;
	//  Wcar = 2000.0;    //   车宽
	Wcar = carP.Wc;     //   停车位的宽
	//Lsafe= carP.Lsa;           //  车两边安全间隔 + —

	CameraParam camP = funPm.camP;
		// P  = camP.dP;
	  high = camP.nhigh;            //   摄像机光学中心在世界水平地面的高度
	 Vision = camP.nVision;          //  摄像机视角  
	Pu = camP.dPu;             //  摄像机光心偏离CCD水平 使图像右移为+  0.01
	Pv = camP.dPv;             //  摄像机光心偏离CCD垂直 使图像上移为+  0.01
	x_angle = camP.dx_angle;        //  摄像机X 方向水平角度
	y_angle = camP.dy_angle;
	//z_angle = camP.dz_angle;
       LOG("ChangeParam");
	//__android_log_print(4, "BCR", " %d  high  =%d  Vision=%d  Lc=%0.1f  Wc=%0.1f   Pu=%0.2f  Pv=%0.2f   x_angle=%0.1f   Smax=%0.1f  %0.1f   y_angle=%0.2fstart_point=%d  Lstart_point=%d  Rstart_point =%d  atsafe =%d  at1m=%d  at2m=%d at3m=%d   at4m=%d   at5m=%d  dotNum=%d\n",
	//	RL,high,Vision,Lcar,Wcar,Pu,Pv,x_angle,Smax,Fcar,y_angle,start_point,Lstart_point,Rstart_point,atsafe,at1m,at2m,at3m,at4m,at5m,dotNum);
	AllFunc();
}

