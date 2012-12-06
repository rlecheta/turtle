package br.android.tarta.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.util.Log;

public class FileUtils {
	private static final String TAG = "FileUtils";

	/**
	 * Retora este arquivo, mas com o caminho correto para salvar no /mnt/sdcard
	 * 
	 * @param context
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static File getSdCardFile(Context context, File file) throws IOException {
		String pasta = file.getParentFile().getName();
		String arquivo = file.getName();
		File cacheDir = null;
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			File sdcardDir = android.os.Environment.getExternalStorageDirectory();
			cacheDir = new File(sdcardDir, pasta);
		} else {
			cacheDir = context.getCacheDir();
		}
		if (!cacheDir.exists()) {
			boolean b = cacheDir.mkdirs();
			if(!b) {
				throw new IOException("Não foi possivel criar o diretório: " + cacheDir);
			}
		}

		File fileFinal = new File(cacheDir,arquivo);
		return fileFinal;
	}
	
	public static File getFile(Uri uri) {
		try {
			String uriString = uri.toString();
			File f = new File(new URI(uriString));
			return f;
		} catch (URISyntaxException e) {
			return null;
		}
	}
	
	public static byte[] readBytes(File file)  {
		try {
			return readBytes(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			Log.e(TAG, e.getMessage(), e);
		}
		return null;
	}

	public static byte[] readBytes(InputStream in)  {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			byte[] buffer = new byte[1024];
			int len;
			while ((len = in.read(buffer)) > 0) {
				bos.write(buffer, 0, len);
			}
			byte[] bytes = bos.toByteArray();
			return bytes;
		}catch(Exception e) {
			Log.e(TAG, e.getMessage(), e);
			return null;
		}finally {
			try {
				bos.close();
				in.close();
			} catch (IOException e) {
				Log.e(TAG, e.getMessage(), e);
			}
		}
	}

	public static String readString(InputStream in) throws IOException {
		byte[] bytes = readBytes(in);
		String texto = new String(bytes);
		return texto;
	}
	
	public static String readString(InputStream in, String charset) throws IOException {
		byte[] bytes = readBytes(in);
		String texto = new String(bytes, charset);
		return texto;
	}
	
	/**
	 * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
	 * 
	 * @param context
	 * @param bytes
	 * @param file
	 * @return 
	 * @throws IOException 
	 */
	public static File writeToSdcard(Context context, byte[] bytes, File file) throws IOException {
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(bytes);
		fos.close();
		return file;
	}

	/**
	 * Retorna um xml fixo da pasta /res/raw
	 * 
	 * @param resources
	 * @param raw
	 * @return
	 * @throws IOException
	 */
	public static String getXmlFake(Resources resources, int raw, String charset) throws IOException {
		try {
			InputStream in = resources.openRawResource(raw);
			String s = readString(in, charset);
			return s;
		} catch (RuntimeException e) {
			Log.e(TAG, e.getMessage(), e);
		}
		return null;
	}
}
