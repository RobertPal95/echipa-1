package com.example.android.stiumate.Adaptors;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.stiumate.Classes.ScoreComparator;
import com.example.android.stiumate.Classes.Scoruri;
import com.example.android.stiumate.R;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.valueOf;

public class ListaScoruri extends ArrayAdapter {
    private List lista = new ArrayList();

    public ListaScoruri(Context context, int resource) {
        super(context, resource);
    }

    @SuppressWarnings("unchecked")
    public void add(Scoruri scor) {
        super.add(scor);
        lista.add(scor);
    }

    @Override
    public int getCount() {
        return lista.size();
    }

    @Override
    public Object getItem(int position) {
        return lista.get(position);
    }

    public void clearData() {
        lista.clear();
    }

    @NonNull
    @Override
    @SuppressWarnings("unused")
    public View getView(int position, View convertView, @NonNull ViewGroup parinte) {
        View linie = convertView;

        if (linie == null) {
            LayoutInflater design = (LayoutInflater) this.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (design != null) {
                linie = design.inflate(R.layout.listview_topscoruri, parinte, false);

                TextView nume_scor = linie.findViewById(R.id.topscoruri_nume);
                TextView utilizator_scor = linie.findViewById(R.id.topscoruri_scor);
                ImageView poza_utilizator = linie.findViewById(R.id.topscoruri_poza);
            }
        }
        Scoruri scor = (Scoruri) this.getItem(position);

        if (linie != null) {
            TextView nume_scor = linie.findViewById(R.id.topscoruri_nume);
            TextView utilizator_scor = linie.findViewById(R.id.topscoruri_scor);
            ImageView poza_utilizator = linie.findViewById(R.id.topscoruri_poza);

            if (scor != null) {
                nume_scor.setText(scor.getNume());
                utilizator_scor.setText(valueOf(scor.getScor()));
                Glide.with(getContext()).load(scor.getPoza()).into(poza_utilizator);
            }
            return linie;
        }
        return convertView.getRootView();
    }
}
