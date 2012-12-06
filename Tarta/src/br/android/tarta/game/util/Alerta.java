package br.android.tarta.game.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;
import br.android.tarta.R;

public class Alerta {

	public static void toast(Context context,String message) {
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}
	
	public static void alert(Context context,String message) {
		AlertDialog dialog = new AlertDialog.Builder(context)
		.setTitle(context.getString(R.string.app_name))
		.setMessage(message)
        .create();	
		dialog.setButton("OK", new DialogInterface.OnClickListener() {
		      public void onClick(DialogInterface dialog, int which) {
		        return;
		      } });
		dialog.show();
	}
}
