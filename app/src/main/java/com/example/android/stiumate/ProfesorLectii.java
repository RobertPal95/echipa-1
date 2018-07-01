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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfesorLectii extends AppCompatActivity {

    private ListView lista_lectii;
    private ArrayAdapter<String> adaptor_lista;
    private DatabaseReference ref_fb;
    private TextView existent_lectie;
    private String nume_element, id_utilizator;
    private boolean ok = false, ok_sterge = false;
    private int nr_lectii = 0;
    private ProgressDialog cercIncarca;
    private Button existent_confirma;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profesor_lectii);

        cercIncarca = new ProgressDialog(ProfesorLectii.this);
        cercIncarca.setMessage("Încărcăm lecțiile disponibile...");
        cercIncarca.show();

        lista_lectii = findViewById(R.id.existent_lista);
        adaptor_lista = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);

        lista_lectii.addFooterView(new View(ProfesorLectii.this),
                null, true);

        lista_lectii.addHeaderView(new View(ProfesorLectii.this),
                null, true);

        existent_lectie = findViewById(R.id.existent_lectie);
        existent_confirma = findViewById(R.id.existent_confirma);

        FirebaseUser utilizator = FirebaseAuth.getInstance().getCurrentUser();
        if(utilizator != null) {
            id_utilizator = utilizator.getUid();
        }

        ref_fb = FirebaseDatabase.getInstance().getReference();
        ref_fb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot fb) {
                cercIncarca.dismiss();

                if (adaptor_lista.getCount() > 0) {
                    adaptor_lista.clear();
                    adaptor_lista.notifyDataSetChanged();
                }
                DataSnapshot lectii = fb.child("Matematica");

                for (DataSnapshot lectie : lectii.getChildren()) {
                    Boolean lectie_publica = lectie.child("Public").getValue(Boolean.class);

                    if (lectie_publica != null) {
                        if (lectie_publica) {
                            String id_autor = lectie.child("ID Autor").getValue(String.class);

                            if (id_autor != null) {
                                if (id_autor.equals(id_utilizator)) {
                                    String titlu = lectie.getKey();
                                    adaptor_lista.add(titlu);
                                    nr_lectii++;
                                }
                            }
                        }
                    }
                }

                if(nr_lectii == 0) {
                    String prop = "Nu există nicio lecție disponibilă!";
                    existent_lectie.setText(prop);
                    existent_confirma.setVisibility(View.GONE);
                }

                lista_lectii.setAdapter(adaptor_lista);

                if (ok_sterge) {
                    DataSnapshot utilizatori = fb.child("Utilizatori");

                    for (DataSnapshot utilizator : utilizatori.getChildren()) {
                        DatabaseReference adauga_lectie = ref_fb.child("Utilizatori");

                        adauga_lectie.child(utilizator.getKey()).child("Scoruri lectii")
                                .child(nume_element).removeValue();
                    }
                    ok_sterge = false;
                }
            }

            @Override
            public void onCancelled(DatabaseError eroare) {

            }
        });

        lista_lectii.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> viewParinte, View view, int pozitie, long l) {
                nume_element = (String) viewParinte.getItemAtPosition(pozitie);

                String prop = getResources().getString(R.string.lectie1)
                        + nume_element + getResources().getString(R.string.lectie2);

                existent_lectie.setText(prop);
                ok = true;

                for (int i = 0; i < viewParinte.getChildCount(); i++) {
                    viewParinte.getChildAt(i).setBackgroundColor(getResources()
                            .getColor(R.color.colorPrimary));
                }

                view.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            }
        });

        existent_confirma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ok) {
                    ok_sterge = true;
                    ref_fb.child("Matematica").child(nume_element)
                            .child("Public").setValue(false);

                    existent_lectie.setText("");

                    Toast.makeText(ProfesorLectii.this,
                            "Lecția a fost scoasă din aplicație și acum se poate modifica!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProfesorLectii.this, "Vă rugăm să selectați o " +
                            "lecție mai întâi!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profesor_lectii, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id == R.id.profil_utilizator){
            startActivity(new Intent(ProfesorLectii.this, ProfilUtilizator.class));
        }

        if(id == R.id.profesor_alegere){
            startActivity(new Intent(ProfesorLectii.this, ProfesorAlegere.class));
        }

        if(id == R.id.pag_principala){
            startActivity(new Intent(ProfesorLectii.this, PagPrinc.class));
        }
        return true;
    }
}
