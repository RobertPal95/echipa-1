package com.example.android.stiumate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import static java.lang.String.valueOf;

public class AfisareScor extends AppCompatActivity {

    private DatabaseReference ref_medalii_profil, ref_utilizator;
    private Boolean[] realizari;
    private String[] nume_medalie;
    private int contor, nr_intrebari;
    private String id_utilizator, id_lectie;
    private TextView afisare_medalii;
    private ProgressDialog cercIncarca;
    private Boolean premiul_intai = false, premiul_doi = false, premiul_trei = false;
    private Integer scor, scor_total, scor_utilizator, scor_afisareScor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_afisare_scor);

        cercIncarca = new ProgressDialog(AfisareScor.this);
        cercIncarca.setMessage("Vă verificăm punctajul obținut...");
        cercIncarca.show();

        Bundle primit = getIntent().getExtras();
        if (primit != null) {
            scor = primit.getInt("scorFinal", 0);
            nr_intrebari = primit.getInt("nrIntrebari", 0);
            id_lectie = primit.getString("id_lectie", "nume");
            scor_afisareScor = primit.getInt("scor_afisareScor", 0);

        }

        TextView afisare_scorText = findViewById(R.id.afisare_scor_text);
        Button inapoiLaText = findViewById(R.id.afisare_scor_inapoiLaLectii);
        Button inapoilaPagPrinc = findViewById(R.id.pag_principala);
        afisare_medalii = findViewById(R.id.afisare_scor_medalii);

        String text_scor = getResources().getString(R.string.scorul_dvs);
        String text_final = text_scor + " " + valueOf(scor) + " / " + nr_intrebari * 3;
        afisare_scorText.setText(text_final);

        FirebaseUser utilizator = FirebaseAuth.getInstance().getCurrentUser();

        if (utilizator != null) {
            id_utilizator = utilizator.getUid();
            DatabaseReference ref_fb = FirebaseDatabase.getInstance().getReference();

            realizari = new Boolean[12];
            nume_medalie = new String[12];

            ref_medalii_profil = ref_fb.child("Utilizatori")
                    .child(id_utilizator).child("Medalii");

            if (scor > scor_afisareScor) {
                ref_fb.child("Utilizatori").child(id_utilizator)
                        .child("Scoruri lectii").child(id_lectie).setValue(scor);
            }

            ref_utilizator = ref_fb.child("Utilizatori").child(id_utilizator);

            ref_fb.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot fb) {
                    cercIncarca.dismiss();

                    DataSnapshot medalii = fb.child("Medalii");
                    DataSnapshot utilizatori = fb.child("Utilizatori");

                    DataSnapshot medalii_profil = utilizatori
                            .child(id_utilizator).child("Medalii");

                    DataSnapshot scoruri = fb.child("Utilizatori")
                            .child(id_utilizator).child("Scoruri lectii");

                    scor_total = 0;
                    for (DataSnapshot scor : scoruri.getChildren()) {
                        scor_total += scor.getValue(Integer.class);
                    }
                    ref_utilizator.child("Scor").setValue(scor_total);

                    contor = 0;
                    for (DataSnapshot medalie_profil : medalii_profil.getChildren()) {
                        realizari[contor] = medalie_profil.getValue(Boolean.class);
                        contor++;
                    }

                    contor = 0;
                    for (DataSnapshot medalie : medalii.getChildren()) {
                        nume_medalie[contor] = medalie.child("Nume").getValue(String.class);
                        contor++;
                    }

                    if (!realizari[9] || !realizari[10] || !realizari[11]) {
                        ArrayList<Integer> lista_scoruri = new ArrayList<>();
                        for (DataSnapshot utilizator : utilizatori.getChildren()) {
                            scor_utilizator = utilizator.child("Scor").getValue(Integer.class);

                            int exista = 0;
                            if (lista_scoruri.size() > 0) {
                                for (int i = 0; i < lista_scoruri.size(); i++) {
                                    if (scor_utilizator.equals(lista_scoruri.get(i))) {
                                        exista = 1;
                                    }
                                }
                            }

                            if (exista == 0) {
                                lista_scoruri.add(scor_utilizator);
                            }
                        }
                        Collections.sort(lista_scoruri);
                        int size = lista_scoruri.size() - 1;

                        if (lista_scoruri.size() >= 1 && scor_total >= lista_scoruri.get(size)) {
                            premiul_intai = true;
                            premiul_doi = true;
                            premiul_trei = true;
                        } else if (lista_scoruri.size() >= 2 &&
                                scor_total >= lista_scoruri.get(size - 1)) {
                            premiul_doi = true;
                            premiul_trei = true;
                        } else if (lista_scoruri.size() >= 3 &&
                                scor_total >= lista_scoruri.get(size - 2)) {
                            premiul_trei = true;
                        }
                    }

                    verifica_medalii();
                }

                @Override
                public void onCancelled(DatabaseError eroare) {

                }
            });
        } else {
            cercIncarca.dismiss();
        }

        inapoiLaText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AfisareScor.this, Invata.class);
                startActivity(i);
            }
        });

        inapoilaPagPrinc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AfisareScor.this, PagPrinc.class);
                startActivity(i);
            }
        });
    }

    private void verifica_medalii() {
        String premiu = "";
        int conditii = 0;

        if (!realizari[1] && scor == nr_intrebari * 3) {
            ref_medalii_profil.child("102").setValue(true);
            premiu += "\n" + nume_medalie[1];
            conditii++;
        }

        if (!realizari[8] && scor_total >= 150) {
            ref_medalii_profil.child("109").setValue(true);
            premiu += "\n" + nume_medalie[8];
            conditii++;
        }

        if (!realizari[7] && scor_total >= 120) {
            ref_medalii_profil.child("108").setValue(true);
            premiu += "\n" + nume_medalie[7];
            conditii++;
        }

        if (!realizari[6] && scor_total >= 90) {
            ref_medalii_profil.child("107").setValue(true);
            premiu += "\n" + nume_medalie[6];
            conditii++;
        }

        if (!realizari[5] && scor_total >= 60) {
            ref_medalii_profil.child("106").setValue(true);
            premiu += "\n" + nume_medalie[5];
            conditii++;
        }

        if (!realizari[4] && scor_total >= 30) {
            ref_medalii_profil.child("105").setValue(true);
            premiu += "\n" + nume_medalie[4];
            conditii++;
        }

        if (!realizari[0]) {
            ref_medalii_profil.child("101").setValue(true);
            premiu += "\n" + nume_medalie[0];
            conditii++;
        }


        if (!realizari[9] && premiul_trei) {
            ref_medalii_profil.child("110").setValue(true);
            premiu += "\n" + nume_medalie[9];
            conditii++;
        }

        if (!realizari[10] && premiul_doi) {
            ref_medalii_profil.child("111").setValue(true);
            premiu += "\n" + nume_medalie[10];
            conditii++;
        }

        if (!realizari[11] && premiul_intai) {
            ref_medalii_profil.child("112").setValue(true);
            premiu += "\n" + nume_medalie[11];
            conditii++;
        }

        if (conditii != 0) {
            String premiu_afisat;

            if (conditii == 1) {
                premiu_afisat = getResources().getString(R.string.o_medalie);
            } else {
                premiu_afisat = getResources().getString(R.string.nr_medalii, conditii);
            }

            premiu_afisat += "\n" + premiu + "\n\n" +
                    getResources().getString(R.string.felicitari) + "\n\n" +
                    getResources().getString(R.string.vezi_medalii);

            afisare_medalii.setText(premiu_afisat);
        }
    }
}
