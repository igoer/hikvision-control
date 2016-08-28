package com.hxh.hikvision;

import com.hxh.hikvision.api.CloudCode;
import com.hxh.hikvision.api.Control;
import com.hxh.hikvision.api.LoginPlay;


public class Main {
	
	public static void main(String[] args) throws Exception{
		LoginPlay lp = new LoginPlay();
		lp.doLogin("218.206.13.27", (short)8000, "admin", "12345");
		Control.getImgSavePath("218.206.13.27", "C://img/2.jpg");
		
		System.out.println(Control.cloudControl("218.206.13.27", CloudCode.PAN_LEFT, CloudCode.SPEED_LV6, CloudCode.START));
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Control.cloudControl("218.206.13.27", CloudCode.PAN_LEFT, CloudCode.SPEED_LV6, CloudCode.END);
		
	}
	
}
