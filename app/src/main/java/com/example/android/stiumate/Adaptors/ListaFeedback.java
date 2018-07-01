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
import com.example.android.stiumate.Classes.Feedbacks;
import com.example.android.stiumate.R;

import java.util.ArrayList;
import java.util.List;


public class ListaFeedback extends ArrayAdapter {
    private List lista = new ArrayList();

    public ListaFeedback(Context context, int resource) {
        super(context, resource);
    }

    @SuppressWarnings("unchecked")
    public void add(Feedbacks feedback) {
        super.add(feedback);
        lista.add(feedback);
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
                linie = design.inflate(R.layout.listview_feedbacks, parinte, false);

                TextView nume_feedback = linie.findViewById(R.id.feedbacks_nume);
                TextView parere_feedback = linie.findViewById(R.id.feedbacks_parere);
                ImageView poza_feedback = linie.findViewById(R.id.feedbacks_poza);
            }
        }
        Feedbacks feedback = (Feedbacks) this.getItem(position);

        if (linie != null) {
            TextView nume_feedback = linie.findViewById(R.id.feedbacks_nume);
            TextView parere_feedback = linie.findViewById(R.id.feedbacks_parere);
            ImageView poza_feedback = linie.findViewById(R.id.feedbacks_poza);

            if (feedback != null) {
                nume_feedback.setText(feedback.getNume());
                parere_feedback.setText(feedback.getParere());
                Glide.with(getContext()).load(feedback.getPoza()).into(poza_feedback);
            }
            return linie;
        }
        return convertView.getRootView();
    }
}
