package com.example.android.stiumate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.stiumate.Adaptors.ListaFeedback;
import com.example.android.stiumate.Classes.DateComparator;
import com.example.android.stiumate.Classes.Feedbacks;
import com.example.android.stiumate.Classes.ScoreComparator;
import com.example.android.stiumate.Classes.Scoruri;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;

public class Feedback extends AppCompatActivity {

    private String nume, parere, poza;
    private ListView lista_feedback;
    private ListaFeedback adaptorFeedback;
    private EditText feedback_parere;
    private DatabaseReference ref_utilizatori, ref_feedbacks;
    private String id_utilizator;
    private FirebaseUser utilizator;
    private ProgressDialog cercIncarca;
    private long dataParere;
    private ArrayList<Feedbacks> array_feedbacks = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        cercIncarca = new ProgressDialog(Feedback.this);
        cercIncarca.setMessage("Încărcăm părerile introduse...");
        cercIncarca.show();

        lista_feedback = findViewById(R.id.feedback_lista);
        adaptorFeedback = new ListaFeedback(this, R.layout.listview_feedbacks);
        feedback_parere = findViewById(R.id.feedback_parere);
        Button feedback_trimite = findViewById(R.id.feedback_trimite);
        TextView inregistrare_feedback = findViewById(R.id.inregistrare_feedback);

        utilizator = FirebaseAuth.getInstance().getCurrentUser();
        if(utilizator == null) {
            inregistrare_feedback.setVisibility(View.VISIBLE);
            feedback_trimite.setVisibility(View.GONE);
            feedback_parere.setVisibility(View.GONE);
        } else {
            inregistrare_feedback.setVisibility(View.GONE);
        }

        ref_utilizatori = FirebaseDatabase.getInstance().getReference().child("Utilizatori");
        ref_utilizatori.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot utilizatori) {
                cercIncarca.dismiss();

                if (adaptorFeedback.getCount() > 0) {
                    adaptorFeedback.clearData();
                    adaptorFeedback.notifyDataSetChanged();
                }

                array_feedbacks.clear();

                for (DataSnapshot utilizator : utilizatori.getChildren()) {
                    nume = utilizator.child("Nume utilizator").getValue(String.class);
                    poza = utilizator.child("Poza profil").getValue(String.class);

                    for(DataSnapshot feedback: utilizator.child("Feedback").getChildren()) {
                        parere = feedback.child("Parere").getValue(String.class);
                        dataParere = feedback.child("Data").getValue(Long.class);
                        array_feedbacks.add(new Feedbacks(nume, parere, poza, dataParere));
                    }
                }

                Collections.sort(array_feedbacks, new DateComparator());

                for(int i = 0; i < array_feedbacks.size(); i++) {
                    adaptorFeedback.add(array_feedbacks.get(i));
                }

                lista_feedback.setAdapter(adaptorFeedback);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        feedback_trimite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (utilizator != null) {
                    id_utilizator = utilizator.getUid();
                }
                String parereIntrodusa = feedback_parere.getText().toString();

                if (TextUtils.isEmpty(parereIntrodusa)) {
                    feedback_parere.setError("Introduceți părerea mai întâi!");
                    return;
                }

                HashMap<String, Object> date_feedback = new HashMap<>();

                long dataCurenta = Calendar.getInstance().getTimeInMillis();

                date_feedback.put("Parere", parereIntrodusa);
                date_feedback.put("Data", dataCurenta);

                ref_feedbacks = ref_utilizatori.child(id_utilizator).child("Feedback");
                ref_feedbacks.push().setValue(date_feedback);

                feedback_parere.setText(null);
                Toast.makeText(Feedback.this, "Părerea dumneavoastră a fost " +
                        "publicată cu succes!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.feedback, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.pag_principala) {
            startActivity(new Intent(Feedback.this, PagPrinc.class));
        }
        return true;
    }

}
