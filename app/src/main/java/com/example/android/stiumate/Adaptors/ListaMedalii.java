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
import com.example.android.stiumate.Classes.Medalii;
import com.example.android.stiumate.R;

import java.util.ArrayList;
import java.util.List;

public class ListaMedalii extends ArrayAdapter {
    private List lista = new ArrayList();

    public ListaMedalii(Context context, int resource) {
        super(context, resource);
    }

    @SuppressWarnings("unchecked")
    public void add(Medalii medalie) {
        super.add(medalie);
        lista.add(medalie);
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
                linie = design.inflate(R.layout.listview_medalii, parinte, false);

                TextView nume_medalie = linie.findViewById(R.id.medalii_nume);
                TextView nume_reusita = linie.findViewById(R.id.medalii_reusita);
                ImageView poza_medalie = linie.findViewById(R.id.medalii_piatra);
            }
        }
        Medalii medalie = (Medalii) this.getItem(position);

        if (linie != null) {
            TextView nume_medalie = linie.findViewById(R.id.medalii_nume);
            TextView nume_reusita = linie.findViewById(R.id.medalii_reusita);
            ImageView poza_medalie = linie.findViewById(R.id.medalii_piatra);

            if (medalie != null) {
                nume_medalie.setText(medalie.getNume());
                nume_reusita.setText(medalie.getReusita());
                Glide.with(getContext()).load(medalie.getPiatra()).into(poza_medalie);
            }
            return linie;
        }
        return convertView.getRootView();
    }
}
