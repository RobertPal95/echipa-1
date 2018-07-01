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
import com.example.android.stiumate.Classes.Lectii;
import com.example.android.stiumate.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.valueOf;

public class ListaLectii extends ArrayAdapter {
    private List lista = new ArrayList();

    public ListaLectii(Context context, int resource) {
        super(context, resource);
    }

    @SuppressWarnings("unchecked")
    public void add(Lectii lectie) {
        super.add(lectie);
        lista.add(lectie);
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
                linie = design.inflate(R.layout.listview_lectii, parinte, false);

                TextView nume_lectie = linie.findViewById(R.id.lectii_nume);
                ImageView poza_lectie = linie.findViewById(R.id.lectii_poza);
                TextView scor_lectie = linie.findViewById(R.id.lectii_scor_actual);
            }
        }
        Lectii lectie = (Lectii) this.getItem(position);

        if (linie != null) {
            TextView nume_lectie = linie.findViewById(R.id.lectii_nume);
            ImageView poza_lectie = linie.findViewById(R.id.lectii_poza);
            TextView scor_lectie = linie.findViewById(R.id.lectii_scor_actual);

            if (lectie != null) {
                Integer scor_auth = lectie.getScor_obtinut();
                if(scor_auth == -1) {
                    scor_lectie.setVisibility(View.GONE);
                } else {
                    String scor = lectie.getScor_obtinut() + " / " + lectie.getScor_maxim();
                    scor_lectie.setText(scor);
                }

                nume_lectie.setText(lectie.getNume());
                Glide.with(getContext()).load(lectie.getPoza()).into(poza_lectie);
            }
            return linie;
        }
        return convertView.getRootView();
    }
}
