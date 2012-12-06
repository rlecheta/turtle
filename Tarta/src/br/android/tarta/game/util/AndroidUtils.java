package br.android.tarta.game.util;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.animation.ScaleAnimation;

public class AndroidUtils {

	/**
	 * Retorna o IMEI para identificar o celular
	 * 
	 * @param context
	 * @return
	 */
	public static String getIMEI(Context context) {
		TelephonyManager mTelephonyMgr = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String imei = mTelephonyMgr.getDeviceId();
		return imei;
	}

	public static boolean isEmulador(Context context) {
		String imei = AndroidUtils.getIMEI(context);
		if(isEmpty(imei)) {
			return true;
		}
		if(isInteger(imei)) {
			long n = Long.parseLong(imei);
			boolean emulador = n == 0;
			return emulador;
		}
		return false;
	}

	public static boolean isEmuladororHTC(Context context) {
		String imei = AndroidUtils.getIMEI(context);
		if ("357988021314328".equals(imei)) {
			// HTC Hero
			return true;
		}
		if ("356698031201222".equals(imei)) {
			// Motorola Milestone
			return true;
		}
		if("355031040408409".equals(imei)) {
			// Nexus S
			return true;
		}
		if("355031040408409".equals(imei)) {
			// Xoom
			return true;
		}
		return isEmulador(context);
	}
	
	public static boolean isEmpty(String s) {
		return s == null || s.trim().length() == 0;
	}

	public static boolean isNotEmpty(String s) {
		return ! isEmpty(s);
	}
	
	public static boolean isInteger(String s) {
    	if(isEmpty(s)){
    		return false;
    	}
    	try {
    		Integer.parseInt(s);
    		return true;
    	} catch (NumberFormatException e) {
    		return false;
    	}
    }
	
	public static void animateAlpha(ViewGroup viewgroup, long time) {
		AnimationSet animationset = new AnimationSet(true);
		AlphaAnimation alphaanimation = new AlphaAnimation(0F, 1F);
		alphaanimation.setDuration(time);
		animationset.addAnimation(alphaanimation);
		LayoutAnimationController controller = new LayoutAnimationController(
				animationset, 0.25F);
		viewgroup.setLayoutAnimation(controller);
	}

	public static void animateScale(ViewGroup viewgroup, long time) {
		AnimationSet animationset = new AnimationSet(true);
		ScaleAnimation scaleanimation = new ScaleAnimation(0F, 1F, 0F, 1F);
		scaleanimation.setDuration(time);
		animationset.addAnimation(scaleanimation);
		LayoutAnimationController layoutanimationcontroller = new LayoutAnimationController(
				animationset, 0.25F);
		viewgroup.setLayoutAnimation(layoutanimationcontroller);
	}

	public static void animateView(Context context, View view, int resAnimId, long duration) {
		Animation a = AnimationUtils.loadAnimation(context, resAnimId);
		a.setDuration(duration);
		view.startAnimation(a);
	}
	
	/**
	 * Retorna se é Android 2.0 ou superior (API Level 11)
	 * 
	 * @return
	 */
	public static boolean isAndroid_3_x(){
		int apiLevel = getAPILevel();
		if(apiLevel >= 11){
			return true;
		}
		return false;
	}
	
	/**
	 * Retorna a API Level
	 * 
	 * 2 - Android 1.1
	 * 3 - Android 1.5
	 * 4 - Android 1.6
	 * 5 - Android 2.0
	 * 6 - Android 2.0.1
	 * 7 - Android 2.1
	 * 
	 * @return
	 */
	public static int getAPILevel(){
		int apiLevel = Integer.parseInt(Build.VERSION.SDK);
		return apiLevel;
	}
}
