package com.runningman.util;

public class ThreadUtil {
	
	 /**
     * �߳�ͣ��������
     * @return
     */
    public static void sleep(int seconds){
    	try{
			Thread.sleep(seconds);
		}catch(InterruptedException e){}
    }
	
}
