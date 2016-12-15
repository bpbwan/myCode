package com.flyaudio.xml;
import java.util.ArrayList;

public class CarTypeEntity {
	ArrayList<Integer> carList = new ArrayList<Integer>();
	private int carTypeRes = 0;

	
	public void setCarTypeRes(String cartype){
		 carTypeRes = Integer.parseInt(cartype);
		}

	
	public void insert_(int cartype){
			this.carList.add(cartype);
		}

	
	public void insert_(String cartype){
		final int car = Integer.parseInt(cartype);
			this.carList.add(car);
		}


	public boolean searchAndMatch(int pcartype){
			boolean ret = false;
			for(Integer im: carList){
					if(im == pcartype){
						ret = true;
						break;
					}
				}
			return ret;
		}

   public int getcarTypeRes(){
   		return carTypeRes;
   	}
	
	public final static String CARTYPE = "CARTYPE";
	public final static String COMCARTYPE = "COMMONCARTYPE";
	public final static String CARTYPERES = "cartypeRes";
	public final static String CARTYPE_ENTITY = "cartype";
	

}

