package br.android.tarta;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import br.android.tarta.game.GameManager;
import br.android.tarta.game.util.Pref;

/**
 * Activity que demonstra o cadastro de carros:
 * 
 * - ListActivity: para listar os carros - RepositorioCarro para salvar os dados
 * no banco - Carro
 * 
 * @author rlecheta
 * 
 */
public class TelaListaFase extends Activity implements OnItemClickListener {

	public static String FASE_STR;
	private GridView gridview;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		
		setContentView(R.layout.list_fase);

		gridview = (GridView) findViewById(R.id.grid);
		gridview.setOnItemClickListener(this);

		//listView = (ListView) findViewById(R.id.listview);
		//listView.setBackgroundResource(R.drawable.background_escuro);
		//listView.setOnItemClickListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();

		int total = GameManager.QUANTIDADE_FASES;

		int fase = Pref.getFase(this);

		// Adaptador de lista customizado para cada linha de um carro
		gridview.setAdapter(new FaseListAdapter(this,total,fase, R.layout.grid_fase_item));
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, final int idx,long id) {

		int fase = Pref.getFase(this);
		boolean podeJogar = id <= fase;
		if(JogoTarta.DEV_MODE) {
			podeJogar = true;
		}
		if(podeJogar) {
			Pref.setFase(this, (int) id);

			startActivity(new Intent(this,JogoTarta.class));	
		} else {
			Toast.makeText(this, getString(R.string.play_other_level_first), Toast.LENGTH_SHORT).show();
		}
	}
}