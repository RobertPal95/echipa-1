package com.example.android.stiumate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.stiumate.Adaptors.ListaLectii;
import com.example.android.stiumate.Classes.Lectii;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.valueOf;

public class Invata extends AppCompatActivity {

    private String id_utilizator;
    private ListView lista_lectii;
    private ListaLectii adaptorLectii;
    private ProgressDialog cercIncarca;
    private List<String> lista_poze = new ArrayList<>();
    private List<Integer> lista_intrebari = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invata);

        cercIncarca = new ProgressDialog(Invata.this);
        cercIncarca.setMessage("Încărcăm lista de lecții...");
        cercIncarca.show();

        lista_lectii = findViewById(R.id.invata_lista);
        adaptorLectii = new ListaLectii(this, R.layout.listview_lectii);

        DatabaseReference ref_fb = FirebaseDatabase
                .getInstance().getReference();

        ref_fb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot fb) {
                cercIncarca.dismiss();

                if (adaptorLectii.getCount() > 0) {
                    adaptorLectii.clearData();
                    adaptorLectii.notifyDataSetChanged();
                }

                FirebaseUser utilizator = FirebaseAuth.getInstance().getCurrentUser();
                if (utilizator != null) {
                    id_utilizator = utilizator.getUid();

                    if(lista_poze.size() > 0) {
                        lista_poze.clear();
                    }

                    if(lista_intrebari.size() > 0) {
                        lista_intrebari.clear();
                    }

                    DataSnapshot lectii = fb.child("Matematica");
                    for (DataSnapshot lectie : lectii.getChildren()) {
                        Boolean publica = lectie.child("Public").getValue(Boolean.class);
                        if (publica != null) {
                            if (publica) {
                                String poza = lectie.child("Imagine").getValue(String.class);
                                lista_poze.add(poza);

                                int nr_intrebari = (int) lectie
                                        .child("Intrebari").getChildrenCount();

                                lista_intrebari.add(nr_intrebari);
                            }
                        }
                    }

                    int i = 0;
                    DataSnapshot scoruri = fb.child("Utilizatori")
                            .child(id_utilizator).child("Scoruri lectii");

                    for (DataSnapshot scor : scoruri.getChildren()) {
                        String nume_lectie = scor.getKey();
                        Integer scor_actual = scor.getValue(Integer.class);
                        Integer scor_maxim = lista_intrebari.get(i) * 3;

                        Lectii scor_lista = new Lectii(nume_lectie,
                                lista_poze.get(i), scor_actual, scor_maxim);

                        adaptorLectii.add(scor_lista);

                        i++;
                    }
                    lista_lectii.setAdapter(adaptorLectii);
                } else {
                    DataSnapshot lectii = fb.child("Matematica");
                    for (DataSnapshot lectie : lectii.getChildren()) {
                        Boolean publica = lectie.child("Public").getValue(Boolean.class);
                        if (publica != null) {
                            if (publica) {
                                String nume_lectie = lectie.getKey();
                                String poza = lectie.child("Imagine").getValue(String.class);

                                Lectii lectie_noua = new Lectii(nume_lectie,
                                        poza, -1, -1);

                                adaptorLectii.add(lectie_noua);
                            }
                        }
                    }
                    lista_lectii.setAdapter(adaptorLectii);
                }
            }

            @Override
            public void onCancelled(DatabaseError eroare) {

            }
        });

        lista_lectii.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> viewParinte, View view, int pozitie, long l) {
                Intent i = new Intent(Invata.this, Lectie.class);
                Lectii lectie_aleasa = (Lectii) viewParinte.getItemAtPosition(pozitie);
                String titlu_lectie_aleasa = lectie_aleasa.getNume();
                int scor = lectie_aleasa.getScor_obtinut();
                i.putExtra("titlu_lectie", titlu_lectie_aleasa);
                i.putExtra("scor_invata", scor);
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.invata, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.pag_principala) {
            startActivity(new Intent(Invata.this, PagPrinc.class));
        }
        return true;
    }
}
