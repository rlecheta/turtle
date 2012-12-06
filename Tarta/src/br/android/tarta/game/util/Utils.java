package br.android.tarta.game.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.DisplayMetrics;

public class Utils {

	/**
	 * Resize para a largura da tela mantendo o aspect ratio
	 * 
	 * @param context
	 * @param bmp
	 * @return
	 */
	public static Bitmap resizeFullWitdh(Bitmap bmp, int screenWidth) {
		int width = bmp.getWidth();
		int height = bmp.getHeight();

//		Log.i("img","w1 > " + width);
//		Log.i("img","h1 > " + height);
		
		int newWidth =  screenWidth;
		int newHeight =  height;
		
		if(width == newWidth) {
			return bmp;
		}
		
		int xwPercent = newWidth - width;
		int xhPercent = xwPercent*100/width;
		
//		Log.i("img","w2 > " + newWidth);
//		Log.i("img","h2 > " + newHeight);
		newHeight = xhPercent+height;
//		Log.i("img","h3 > " + newHeight);

		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;

		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);

		Bitmap resizedBitmap = Bitmap.createBitmap(bmp, 0, 0, width, height,
				matrix, true);
		return resizedBitmap;
	}
	
	/**
	 * Resize para a largura da tela mantendo o aspect ratio
	 * 
	 * @param context
	 * @param bmp
	 * @return
	 */
	public static Bitmap resizeFullHeight(Bitmap bmp, int screenHeight) {
		int width = bmp.getWidth();
		int height = bmp.getHeight();

//		Log.i("img","w1 > " + width);
//		Log.i("img","h1 > " + height);
		
		int newWidth = width;
		int newHeight =  screenHeight;
		
		if(height >= screenHeight) {
			return bmp;
		}

		int xwPercent = newHeight - height;
		int xhPercent = xwPercent*100/height;
		
//		Log.i("img","w2 > " + newWidth);
//		Log.i("img","h2 > " + newHeight);
		newWidth = xhPercent+width;
//		Log.i("img","h3 > " + newHeight);

		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;

		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);

		Bitmap resizedBitmap = Bitmap.createBitmap(bmp, 0, 0, width, height,
				matrix, true);
		return resizedBitmap;
	}
}
