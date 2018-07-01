package com.example.android.stiumate.Adaptors;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.android.stiumate.Classes.Meniu;
import com.example.android.stiumate.R;

import java.util.ArrayList;
import java.util.List;

public class ListaMeniu extends ArrayAdapter {
    private List lista = new ArrayList();

    public ListaMeniu(Context context, int resource) {
        super(context, resource);
    }

    @SuppressWarnings("unchecked")
    public void add(Meniu formular) {
        super.add(formular);
        lista.add(formular);
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
                linie = design.inflate(R.layout.listview_modifica_intrebari,
                        parinte, false);

                TextView intrebare = linie.findViewById(R.id.intrebari_text);
                TextView indiciu1 = linie.findViewById(R.id.intrebari_indiciu1);
                TextView indiciu2 = linie.findViewById(R.id.intrebari_indiciu2);
                TextView raspuns = linie.findViewById(R.id.intrebari_raspuns);
            }
        }
        Meniu meniu = (Meniu) this.getItem(position);

        if (linie != null) {
            TextView intrebare = linie.findViewById(R.id.intrebari_text);
            TextView indiciu1 = linie.findViewById(R.id.intrebari_indiciu1);
            TextView indiciu2 = linie.findViewById(R.id.intrebari_indiciu2);
            TextView raspuns = linie.findViewById(R.id.intrebari_raspuns);

            if (meniu != null) {
                intrebare.setText(meniu.getIntrebare());
                indiciu1.setText(meniu.getIndiciu1());
                indiciu2.setText(meniu.getIndiciu2());
                raspuns.setText(meniu.getRaspuns());
            }
            return linie;
        }
        return convertView.getRootView();
    }
}
