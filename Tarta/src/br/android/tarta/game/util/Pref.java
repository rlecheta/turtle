package br.android.tarta.game.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Salva nas preferências
 * 
 * @author ricardo
 *
 */
public class Pref {
	private static final String ID = "tarta";

	private static final String FLAG_FASE 	= "fase";
	private static final String FLAG_VERSAO = "versao";

	/**
	 * Pega o valor das preferencias
	 * 
	 * @return
	 */
	private static int get(Context context,String flag) {
		SharedPreferences pref = context.getSharedPreferences(ID, 0);
		int fase = pref.getInt(flag,0);
		return fase;
	}

	/**
	 * Seta o valor nas preferencias
	 * 
	 * @param context
	 * @param flag
	 * @param valor
	 */
	private static void set(Context context,String flag,int valor) {
		SharedPreferences pref = context.getSharedPreferences(ID, 0);
		SharedPreferences.Editor editor = pref.edit();
		editor.putInt(flag, valor);
		editor.commit();
	}

	/**
	 * Fase salva nas prerencias
	 * 
	 * @param context
	 * @return
	 */
	public static int getFase(Context context) {
		return get(context, FLAG_FASE);
	}
	
	public static int getVersao(Context context) {
		return get(context, FLAG_VERSAO);
	}

	public static void setVersao(Context context, int versao) {
		set(context, FLAG_VERSAO, versao);
	}

	/**
	 * Salva a fase atual nas prerencias
	 * 
	 * @param context
	 * @param fase
	 */
	public static void setFase(Context context,int fase) {
		set(context, FLAG_FASE, fase);
	}

	public static boolean getSoundOn(Context context) {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		boolean check = sharedPref.getBoolean("pref_sound_on", true);
		return check;
	}

	public static String getControlMode(Context context) {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		String s = sharedPref.getString("pref_control", "touch");
		return s;
	}
}
