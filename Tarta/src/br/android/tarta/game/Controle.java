package br.android.tarta.game;

import javax.microedition.lcdui.GraphicsUtils;
import javax.microedition.lcdui.game.GameCanvas;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.Log;
import android.view.MotionEvent;
import br.android.tarta.JogoTarta;
import br.android.tarta.R;
import br.android.tarta.game.controller.ControllerFactory;
import br.android.tarta.game.controller.TouchController;
import br.android.tarta.game.controller.TouchListener;
import br.android.tarta.game.sensor.AccelerometerListener;
import br.android.tarta.game.sensor.AccelerometerManager;
import br.android.ui.utils.ImageUtils;

/**
 * Controle com imagens para Touch
 * 
 * @author ricardo
 * 
 */
public class Controle implements AccelerometerListener, TouchListener{
	public static final String TAG = "controle";
	private static final boolean LOG_ON = false;
	public static boolean ON;
	private Seta imgSetaLeft;
	private Seta imgSetaRight;
	private Seta imgSetaUp;
	private Seta imgSetaDown;
	private Seta imgSetaFire;
	private boolean left;
	private boolean right;
	private boolean up;
	private boolean down;
	private boolean trackBallClick;
	
	private AccelerometerManager sensor;
	private float mSensorBuffer = 0;
//	private boolean sensorDireita;
//	private boolean sensorEsquerda;
//	private float acerX;
	private Paint mLinePaintCrossHairs = new Paint();
	private Paint paintVermelho = new Paint();
	private boolean fire;
	private TouchController touchController;
	
	public void init(Context context) {
		Bitmap img = ImageUtils.getBitmap(context, R.drawable.arrow_left3);
		int w = img.getWidth();
		int h = img.getHeight();

		int x = 0;
		int height = GameCanvas.height;

		int width = GameCanvas.width;

		Bitmap bitmap = ImageUtils.getBitmap(context, R.drawable.arrow_down3);

		int dy = height-h;
		int dy2 = dy - 2;
		
		imgSetaLeft = new Seta(context, img, x, dy2,Seta.LEFT);
//		imgSetaDown = new Seta(context, bitmap, w/2, dy,Seta.DOWN);
		imgSetaRight = new Seta(context, ImageUtils.getBitmap(context, R.drawable.arrow_right3), w,dy2,Seta.RIGHT);
		imgSetaUp = new Seta(context, ImageUtils.getBitmap(context, R.drawable.arrow_up3), width-w*2-20, dy2,Seta.UP);
		imgSetaFire = new Seta(context, ImageUtils.getBitmap(context, R.drawable.control_fire), width-w-10, dy2,Seta.UP);

		if(AccelerometerManager.ON){
			sensor = new AccelerometerManager(context,this);
		}
		mLinePaintCrossHairs.setColor(Color.BLUE);
		mLinePaintCrossHairs.setStrokeWidth(5);
		mLinePaintCrossHairs.setStyle(Style.STROKE);
		mLinePaintCrossHairs.setAntiAlias(true);

		paintVermelho.setColor(Color.RED);

		touchController = ControllerFactory.getController(this);
	}

	public void release() {
		left = false;
		right = false;
		up = false;
		down = false;
		fire = false;
	}

	public class Seta {
		public static final int LEFT 	= 1;
		public static final int RIGHT 	= 2;
		public static final int UP 		= 3;
		public static final int DOWN 	= 4;
		private int direcao;

		private Bitmap img;
		final int x;
		final int y;
		int width;
		int height;

		public Seta(Context context,Bitmap img, int x, int y,int direcao) {
			this.x = x;
			this.y = y;
			this.direcao = direcao;
			this.img = img;
			width = img.getWidth();
			height = img.getHeight();
		}

		private void draw(Canvas canvas) {
			if(img != null) {
				GraphicsUtils.drawBitmap(img, x, y, GraphicsUtils.TOP | GraphicsUtils.LEFT,canvas);
			}
		}

		private boolean checkInput(float x, float y) {
			// obtem a localização em pixel das sprites
			int s1x = Math.round(this.x);
			int s1y = Math.round(this.y);

			// verifica se as bordas das sprites se interceptam
			boolean xBateu = x >= s1x && x <= s1x + width;
			boolean yBateu = y >= s1y && y <= s1y + height;
			boolean bateu = xBateu && yBateu;

			return bateu;
		}

		public int getDirecao() {
			return direcao;
		}

		public String toString() {
			switch (direcao) {
			case RIGHT:
				return "right";
			case LEFT:
				return "left";
			case UP:
				return "up";
			case DOWN:
				return "down";
			default:
				break;
			}
			return "";
		}
	}
	
	public boolean isFire() {
		try {
			return fire;
		} finally {
//			down=false;
		}
	}
	
	public boolean isDown() {
		try {
			return down;
		} finally {
//			down=false;
		}
	}
	public boolean isLeft() {
		try {
			return left;
		} finally {
//			left=false;
		}
	}
	public boolean isRight() {
		try {
			return right;
		} finally {
//			right=false;
		}
	}
	public boolean isUp() {
		if(trackBallClick) {
			trackBallClick = false;
			return true;
		}
		try {
			return up;
		} finally {
//			up=false;
		}
	}
	
	public boolean isTrackBallClick() {
		return trackBallClick;
	}

	public Seta onTrackballEvent(MotionEvent event) {
		switch (event.getAction()) {
	        case MotionEvent.ACTION_DOWN:
	        	trackBallClick = true;
	            break;
	        case MotionEvent.ACTION_MOVE:
	            break;
	        case MotionEvent.ACTION_UP:
	        	trackBallClick = false;
	            break;
		}
		return null;
	}

	public void onTouchEvent(MotionEvent ev) {
		//touchController.dumpEvent(ev);
		touchController.onTouchEvent(ev);
	}

	public Seta onTouchEventSingleTouch(MotionEvent event) {
		int x = Math.round(event.getX());
		int y = Math.round(event.getY());

		int action = event.getAction();
		switch (action) {
	        case MotionEvent.ACTION_DOWN:
	        	if(JogoTarta.LOG_ON){
	        		JogoTarta.log(Controle.TAG, "down x:y " + event.getX() + ": " + event.getY());
	        	}

	        	boolean bateu = imgSetaLeft.checkInput(x, y);
	    		if(bateu) {
	    			left = true;
	    			return imgSetaLeft;
	    		}

	    		bateu = imgSetaRight.checkInput(x, y);
	    		if(bateu) {
	    			right = true;
	    			return imgSetaRight;
	    		}

	    		if(imgSetaUp != null) {
	    			bateu = imgSetaUp.checkInput(x, y);
		    		if(bateu) {
		    			up = true;
		    			return imgSetaUp;
		    		}
	    		}
	    		
	    		if(imgSetaFire != null) {
	    			bateu = imgSetaFire.checkInput(x, y);
		    		if(bateu) {
		    			fire = true;
		    			return imgSetaFire;
		    		}
	    		}

			if (imgSetaDown != null) {
				bateu = imgSetaDown.checkInput(x, y);
				if (bateu) {
					down = true;
					return imgSetaDown;
				}
			}
			break;
	        case MotionEvent.ACTION_MOVE:

	            break;
	        case MotionEvent.ACTION_UP:
	        	if(JogoTarta.LOG_ON){
	        		JogoTarta.log(Controle.TAG,  "UP release x:y " + event.getX() + ": " + event.getY());
	        	}
	            release();
	            break;
		}

		return null;
	}
	private void log(String string) {
		if(LOG_ON) {
			Log.d(TAG, string);
		}
	}

	@Override
	public void onAccelerationChanged(float x, float y, float z) {
		if (x > mSensorBuffer  || x < -mSensorBuffer) {
			if(x > 0.5) {
//				sensorDireita = true;
				right = true;
			}else if(x < -0.5) {
//				sensorEsquerda = true;
				left = true;
			}
//			this.acerX = x;
		}
	}

	/**
	 * Register the accelerometer sensor so we can use it in-game.
	 */
	public void registerListener() {
		if(sensor != null){
			sensor.registerListener();
		}
	}

	/**
	 * Unregister the accelerometer sensor otherwise it will continue to operate
	 * and report values.
	 */
	public void unregisterListener() {
		if(sensor != null){
			sensor.unregisterListener();
		}
	}

	public void draw(Canvas canvas) {
		if (imgSetaLeft != null && imgSetaRight != null /*&& imgSetaUp != null && imgSetaDown != null */) {
			imgSetaLeft.draw(canvas);
			imgSetaRight.draw(canvas);
			if(imgSetaUp != null) {
				imgSetaUp.draw(canvas);
			}
			if(imgSetaDown != null) {
				imgSetaDown.draw(canvas);
			}
			if(imgSetaFire != null) {
				imgSetaFire.draw(canvas);
			}
		}
	}
	
	 

	/**
	 * @param x
	 * @param y
	 * @param press indica se é down ou se está soltando
	 * @return
	 */
	@Override
	public boolean onTouch(int x, int y, boolean press) {
		boolean bateu = imgSetaLeft.checkInput(x, y);
		if(bateu) {
			if(press) {
				left = true;
				right = false;				
			} else {
				left = false;
			}
			return true;
		}

		bateu = imgSetaRight.checkInput(x, y);
		if(bateu || AccelerometerManager.ON) {
			if(press) {
				right = true;
				left = false;
			} else {
				right = false;
			}
			return true;
		}

		if(imgSetaUp != null) {
			bateu = imgSetaUp.checkInput(x, y);
			if(bateu) {
				if(press) {
					up = true;
					if(Controle.LOG_ON){
			    		log("up press!");
			    	}
				} else {
					up = false;
					if(Controle.LOG_ON){
			    		log("up off!");
			    	}
				}
				return true;
			}
		}

		if(imgSetaFire != null) {
			bateu = imgSetaFire.checkInput(x, y);
			if(bateu) {
				if(press) {
					fire = true;
					if(Controle.LOG_ON){
			    		log("up press!");
			    	}
				} else {
					fire = false;
					if(Controle.LOG_ON){
			    		log("up off!");
			    	}
				}
				return true;
			}
		}

		if(imgSetaDown != null) {
			bateu = imgSetaDown.checkInput(x, y);
			if(bateu) {
				if(press) {
					down = true;
				} else {
					down = false;
				}
				return true;
			}
		}

		return true;
	}
	public Seta getImgSetaFire() {
		return imgSetaFire;
	}
}
