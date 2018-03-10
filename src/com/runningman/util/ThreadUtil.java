package com.runningman.util;

public class ThreadUtil {
	
	 /**
     * œﬂ≥ÃÕ£∂Ÿ»Ù∏…√Î
     * @return
     */
    public static void sleep(int seconds){
    	try{
			Thread.sleep(seconds);
		}catch(InterruptedException e){}
    }
	
}
