package br.android.tarta;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Adapter para a lista de fases. Marca o flag de fase completa.
 * 
 * Monta a View com "R.layout.list_linha_fase"
 * 
 * @author ricardo
 *
 */
public class FaseListAdapter extends BaseAdapter {

	private Context context;
	private final int faseAtual;
	private final int total;
	private final int layout;

	public FaseListAdapter(Context context, int total, int faseAtual, int layout) {
		super();
		this.context = context;
		this.total = total;
		this.faseAtual = faseAtual;
		this.layout = layout;
	}

	@Override
	public int getCount() {
		return total;
	}

	@Override
	public Object getItem(int position) {
		return context.getString(R.string.level) + " " + (position+1);
	}

	@Override
	public long getItemId(int position) {
		int fase = position+1;
		return fase;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(layout, null);

		// Atualiza o valor do TextView
		TextView nome = (TextView) view.findViewById(R.id.lListNomeFase);
		String nomeFase = getItem(position).toString();
		nome.setText(nomeFase);
		
		ImageView img = (ImageView) view.findViewById(R.id.imgFaseComplete);
		img.setImageResource(R.drawable.unlock);	

		boolean faseConcluida = position+1 < faseAtual;
		if(faseConcluida) {
			nome.setTextAppearance(context, R.style.itemMenuFaseCompleta);

			img.setImageResource(R.drawable.fase_complete);	
		} else {

			boolean cinza = position+1 <= faseAtual;
			if(cinza) {
				nome.setTextAppearance(context, R.style.itemMenuFaseCompleta);
			} else {
				img.setImageResource(R.drawable.lock);
			}
		}

		return view;
	}
}
