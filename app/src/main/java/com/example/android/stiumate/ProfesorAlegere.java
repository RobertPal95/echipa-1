package com.example.android.stiumate;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ProfesorAlegere extends AppCompatActivity {

    private Spinner alege_lectie;
    private ArrayAdapter<String> adaptor_lista;
    private DatabaseReference ref_fb, ref_lectie;
    private ArrayList lista_cerinte, lista_teorie, lista_poze;
    private String titlu, teorie, cerinta, poza, lectie_aleasa, id_autor, id_utilizator;
    private int[] nr_intrebari;
    private boolean ok_publica = false, ok_sterge = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_profesor_alegere);

        alege_lectie = findViewById(R.id.alege_lectie);
        Button editeaza_lectie = findViewById(R.id.editeaza_lectie);
        Button lectii_existente = findViewById(R.id.lectii_existente);
        Button modifica_intrebari = findViewById(R.id.modifica_intrebari);
        Button publica_lectie = findViewById(R.id.publica_lectie);
        Button sterge_lectie = findViewById(R.id.sterge_lectie);
        Button adauga_lectie = findViewById(R.id.adauga_lectie);
        Button profAlegere_pagPrinc = findViewById(R.id.profAlegere_pagPrinc);

        adaptor_lista = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        lista_cerinte = new ArrayList();
        lista_teorie = new ArrayList();
        lista_poze = new ArrayList();

        FirebaseUser utilizator = FirebaseAuth.getInstance().getCurrentUser();
        if (utilizator != null) {
            id_utilizator = utilizator.getUid();
        }

        ref_fb = FirebaseDatabase.getInstance().getReference();
        ref_fb.addValueEventListener(new ValueEventListener() {
            @Override
            @SuppressWarnings("unchecked")
            public void onDataChange(DataSnapshot fb) {
                if (adaptor_lista.getCount() > 0) {
                    adaptor_lista.clear();
                    adaptor_lista.notifyDataSetChanged();

                    lista_teorie.clear();
                    lista_cerinte.clear();
                    lista_poze.clear();
                }
                DataSnapshot lectii = fb.child("Matematica");

                int nr_lectii = (int) lectii.getChildrenCount();
                nr_intrebari = new int[nr_lectii];
                int i = 0;

                for (DataSnapshot lectie : lectii.getChildren()) {
                    Boolean lectie_publica = lectie.child("Public").getValue(Boolean.class);
                    if (lectie_publica != null) {
                        if (!lectie_publica) {
                            id_autor = lectie.child("ID Autor").getValue(String.class);
                            if (id_autor != null) {
                                if (id_autor.equals(id_utilizator)) {

                                    titlu = lectie.getKey();
                                    adaptor_lista.add(titlu);

                                    teorie = lectie.child("Teorie").getValue(String.class);
                                    lista_teorie.add(teorie);

                                    cerinta = lectie.child("Cerinta").getValue(String.class);
                                    lista_cerinte.add(cerinta);

                                    poza = lectie.child("Imagine").getValue(String.class);
                                    lista_poze.add(poza);

                                    nr_intrebari[i] = (int) lectie
                                            .child("Intrebari").getChildrenCount();
                                    i++;
                                }
                            }
                        }
                    }
                }

                if (ok_publica) {
                    DataSnapshot utilizatori = fb.child("Utilizatori");

                    for (DataSnapshot utilizator : utilizatori.getChildren()) {
                        DatabaseReference adauga_lectie = ref_fb.child("Utilizatori");

                        adauga_lectie.child(utilizator.getKey()).child("Scoruri lectii")
                                .child(lectie_aleasa).setValue(0);
                    }
                    ok_publica = false;
                }

                if (ok_sterge) {
                    DataSnapshot utilizatori = fb.child("Utilizatori");

                    for (DataSnapshot utilizator : utilizatori.getChildren()) {
                        DatabaseReference adauga_lectie = ref_fb.child("Utilizatori");

                        adauga_lectie.child(utilizator.getKey()).child("Scoruri lectii")
                                .child(lectie_aleasa).removeValue();
                    }
                    ok_sterge = false;
                }

                if (adaptor_lista.getCount() > 0) {
                    alege_lectie.setAdapter(adaptor_lista);
                }
            }

            @Override
            public void onCancelled(DatabaseError eroare) {

            }
        });

        modifica_intrebari.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alege_lectie.getSelectedItem() != null) {
                    lectie_aleasa = alege_lectie.getSelectedItem().toString();

                    Intent i = new Intent(ProfesorAlegere.this,
                            ProfesorIntrebari.class);

                    i.putExtra("lectie", lectie_aleasa);
                    startActivity(i);
                } else {
                    Toast.makeText(ProfesorAlegere.this, "Nu există lecții" +
                            " disponibile, adăugați una mai întâi!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        editeaza_lectie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alege_lectie.getSelectedItem() != null) {
                    int poz_lectie = alege_lectie.getSelectedItemPosition();

                    Intent i = new Intent(ProfesorAlegere.this,
                            ProfesorAdauga.class);

                    i.putExtra("titlu", adaptor_lista.getItem(poz_lectie));
                    i.putExtra("teorie", lista_teorie.get(poz_lectie).toString());
                    i.putExtra("cerinta", lista_cerinte.get(poz_lectie).toString());
                    i.putExtra("poza", lista_poze.get(poz_lectie).toString());
                    startActivity(i);
                } else {
                    Toast.makeText(ProfesorAlegere.this, "Nu există lecții" +
                            " disponibile, adăugați una mai întâi!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        publica_lectie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alege_lectie.getSelectedItem() != null) {
                    int id_lectie = (int) alege_lectie.getSelectedItemId();

                    if (nr_intrebari[id_lectie] < 5) {
                        Toast.makeText(ProfesorAlegere.this, "Introduceți minim 5 " +
                                        "întrebări în lecție pentru a o publica!",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        lectie_aleasa = alege_lectie.getSelectedItem().toString();

                        DatabaseReference ref_lectie = FirebaseDatabase.getInstance()
                                .getReference().child("Matematica").child(lectie_aleasa);

                        ok_publica = true;
                        ref_lectie.child("Public").setValue(true);

                        Toast.makeText(ProfesorAlegere.this, "Lecția a fost " +
                                        "publicată și va apărea în aplicație!",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ProfesorAlegere.this, "Nu există lecții" +
                            " disponibile, adăugați una mai întâi!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        sterge_lectie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alege_lectie.getSelectedItem() != null) {
                    AlertDialog.Builder constructor =
                            new AlertDialog.Builder(ProfesorAlegere.this);

                    lectie_aleasa = alege_lectie.getSelectedItem().toString();

                    constructor.setTitle("Confirmați ștergerea");
                    constructor.setMessage("Sigur doriți să" +
                            " ștergeți lecția " + lectie_aleasa + "?");

                    constructor.setPositiveButton("Da", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                            ok_sterge = true;

                            ref_lectie = ref_fb.child("Matematica").child(lectie_aleasa);
                            int ind_lectie = (int) alege_lectie.getSelectedItemId();

                            StorageReference poza_profil = FirebaseStorage.getInstance()
                                    .getReferenceFromUrl(lista_poze.get(ind_lectie).toString());

                            poza_profil.delete();
                            ref_lectie.removeValue();

                            Toast.makeText(ProfesorAlegere.this, "Lecția a fost" +
                                    " ștearsă din aplicație!", Toast.LENGTH_SHORT).show();
                        }
                    });

                    constructor.setNegativeButton("Nu", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog alerta = constructor.create();
                    alerta.show();
                } else {
                    Toast.makeText(ProfesorAlegere.this, "Nu există lecții" +
                            " disponibile, adăugați una mai întâi!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        adauga_lectie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfesorAlegere.this,
                        ProfesorAdauga.class));
            }
        });

        lectii_existente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfesorAlegere.this,
                        ProfesorLectii.class));
            }
        });

        profAlegere_pagPrinc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfesorAlegere.this, PagPrinc.class));
            }
        });
    }

}
