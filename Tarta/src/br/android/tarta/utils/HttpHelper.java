package br.android.tarta.utils;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import android.content.Context;
import android.util.Log;

public class HttpHelper {

	private static final String TAG = "Http";
	public static boolean LOG_ON = false;
	private static final int TIMEOUT = 30000;
	private static final String CHARSET = "UTF-8";

	private HttpURLConnection conn;
	private InputStream in;
	private DataInputStream dataIn;
	private final Context context;
	
	public HttpHelper(Context context) {
		this.context = context;
	}

	public String getString() throws IOException {
		try {
			String s = FileUtils.readString(in);
			return s;
		} finally {
			close();
		}
	}
	
	public byte[] getBytes() throws IOException {
		byte[] bytes = FileUtils.readBytes(in);
		return bytes;
	}
	
	public InputStream getInputStream() {
		return in;
	}
	
	public DataInputStream getDataInputStream() {
		dataIn = new DataInputStream(in);
		return dataIn;
	}

	public void doPost(String url, Map<String,String> params) throws IOException {
		String queryString = getQueryString(params);
		doPost(url, queryString,CHARSET);
	}

	public void doPost(String url, Map<String,String> params, String charset) throws IOException {
		String queryString = getQueryString(params);
		doPost(url, queryString,charset);
	}

	public void doPost(String url, String params, String charset) throws IOException {
		byte[] bytes = params != null ? params.getBytes(charset) : null;
		doPost(url, bytes);
	}
	
	public void doPost(String url, byte[] params) throws IOException {
		if(LOG_ON){
			Log.d(TAG, "Http.doPost: " + url + "?" + params);
		}
		URL u = new URL(url);

		conn = (HttpURLConnection) u.openConnection();
		conn.setReadTimeout(TIMEOUT);

		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setDoInput(true);

		conn.connect();

		if(params != null) {
			OutputStream out = conn.getOutputStream();
			out.write(params);
			out.flush();
			out.close();
		}

		this.in = conn.getInputStream();
	}

	public void doGet(String url) throws IOException {
		doGet(url, null);
	}

	public void doGet(String url, Map<String,String> params) throws IOException {
		doGet(url, params, CHARSET);
	}

	public void doGet(String url, Map<String,String> params, String charset) throws IOException {
		String queryString = getQueryString(params);

		if(queryString != null){
			url += "?" + queryString;
		}

		if(LOG_ON) {
			Log.d(TAG, "Http.doGet: " + url);
		}

		if(LOG_ON){
			Log.d(TAG, "Http.doGet " + url + "?" + params);
		}
		URL u = new URL(url);

		conn = (HttpURLConnection) u.openConnection();
		conn.setReadTimeout(TIMEOUT);

		conn.setRequestMethod("GET");
		conn.setDoOutput(true);
		conn.setDoInput(true);

		conn.connect();
		
		this.in = conn.getInputStream();
	}
	
	public void close() {
		try {
			if(in != null) {
				in.close();
				in = null;
			}
			if(dataIn != null) {
				dataIn.close();
				dataIn = null;
			}
			if(conn != null) {
				conn.disconnect();
				conn = null;
			}
		} catch (Exception e) {
			Log.e(TAG, "HttpConnectionImpl conn.disconnect(): " + e.getMessage());
		}
	}

	public byte[] doGetBytes(String url, Map<String, String> params) throws IOException {
		doGet(url, params);
		byte[] bytes = FileUtils.readBytes(in); 
		return bytes;
	}
	
	/**
	 * Retorna a QueryString para 'GET' 
	 * 
	 * @param params
	 * @return
	 * @throws IOException
	 */
	public static String getQueryString(Map<String,String> params) throws IOException {
		if (params == null || params.size() == 0) {
			return null;
		}
		String urlParams = null;
		for (String chave : params.keySet()) {
			Object objValor = params.get(chave);
			if (objValor != null) {
				String valor = objValor.toString();
				urlParams = urlParams == null ? "" : urlParams + "&";
				urlParams += chave + "=" + valor;
			}
		}
		return urlParams;
	
	}
}
