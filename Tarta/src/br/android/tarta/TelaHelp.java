package br.android.tarta;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class TelaHelp extends Activity {

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		WebView web = new WebView(this);

		WebSettings webSettings = web.getSettings();
		webSettings.setSavePassword(false);
		webSettings.setSaveFormData(false);
		webSettings.setJavaScriptEnabled(true);
		webSettings.setSupportZoom(false);

		web.setWebChromeClient(new MyWebChromeClient());

		String htmldata = "";//loadResToString(R.raw.help, this);
		
		web.loadData(htmldata, "text/html", "UTF-8");
		
		//web.loadUrl("http://www.google.com.br");

		setContentView(web);
	}

	public static String loadResToString(int resId, Activity ctx) {

		try {
			InputStream is = ctx.getResources().openRawResource(resId);

			byte[] buffer = new byte[4096];
			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			while (true) {
				int read = is.read(buffer);

				if (read == -1) {
					break;
				}

				baos.write(buffer, 0, read);
			}

			baos.close();
			is.close();

			String data = baos.toString();

			return data;
		} catch (Exception e) {
			JogoTarta.logError("ResourceUtils failed to load resource to string", e);
			return null;
		}
	}

	/**
	 * Provides a hook for calling "alert" from javascript. Useful for debugging
	 * your javascript.
	 */
	final class MyWebChromeClient extends WebChromeClient {
		@Override
		public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
			result.confirm();
			return true;
		}
		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			super.onProgressChanged(view, newProgress);
		}
	}

}