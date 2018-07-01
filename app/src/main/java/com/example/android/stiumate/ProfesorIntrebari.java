package com.example.android.stiumate;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.stiumate.Adaptors.ListaMeniu;
import com.example.android.stiumate.Classes.Meniu;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import static java.lang.Integer.valueOf;

public class ProfesorIntrebari extends AppCompatActivity {

    private String text, indiciu1, indiciu2, raspuns, lectie;
    private int id_intrebare, intrebare_curenta;
    private boolean ok_lista = false, ok_bara = false, ok_sterge = false;
    private ListView lista_intrebari;
    private ListaMeniu adaptorIntrebari;
    private EditText et_intrebare, et_indiciu1, et_indiciu2, et_raspuns;
    private DatabaseReference ref_intrebari;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_profesor_intrebari);

        Bundle primit = getIntent().getExtras();
        if (primit != null) {
            lectie = primit.getString("lectie", "Nume lecție");
        }

        et_intrebare = findViewById(R.id.editare_intrebare);
        et_indiciu1 = findViewById(R.id.editare_indiciu1);
        et_indiciu2 = findViewById(R.id.editare_indiciu2);
        et_raspuns = findViewById(R.id.editare_raspuns);

        Button adauga = findViewById(R.id.editare_adauga);
        Button modifica = findViewById(R.id.editare_modifica);
        Button sterge = findViewById(R.id.editare_sterge);
        Button goleste = findViewById(R.id.editare_goleste);

        lista_intrebari = findViewById(R.id.intrebari_lista);

        adaptorIntrebari = new ListaMeniu(this, R.layout.listview_modifica_intrebari);

        ref_intrebari = FirebaseDatabase.getInstance()
                .getReference().child("Matematica").child(lectie).child("Intrebari");

        ref_intrebari.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot intrebari) {
                if (adaptorIntrebari.getCount() > 0) {
                    adaptorIntrebari.clearData();
                    adaptorIntrebari.notifyDataSetChanged();
                } else {
                    id_intrebare = 100;
                }

                for (DataSnapshot intrebare : intrebari.getChildren()) {
                    id_intrebare = valueOf(intrebare.getKey());
                    text = intrebare.child("Text").getValue(String.class);
                    indiciu1 = intrebare.child("Indiciu 1").getValue(String.class);
                    indiciu2 = intrebare.child("Indiciu 2").getValue(String.class);
                    raspuns = intrebare.child("Raspuns").getValue(String.class);

                    adaptorIntrebari.add(new Meniu(text, indiciu1, indiciu2, raspuns));
                }
                lista_intrebari.setAdapter(adaptorIntrebari);

                if(!ok_bara && lista_intrebari.getCount() > 0) {
                    lista_intrebari.addFooterView(new View(ProfesorIntrebari.this),
                            null, true);

                    lista_intrebari.addHeaderView(new View(ProfesorIntrebari.this),
                            null, true);

                    ok_bara = true;
                }

                if(ok_sterge) {
                    for (DataSnapshot intrebare : intrebari.getChildren()) {
                        id_intrebare = valueOf(intrebare.getKey());

                        if(id_intrebare > intrebare_curenta + 100) {
                            text = intrebare.child("Text").getValue(String.class);
                            indiciu1 = intrebare.child("Indiciu 1").getValue(String.class);
                            indiciu2 = intrebare.child("Indiciu 2").getValue(String.class);
                            raspuns = intrebare.child("Raspuns").getValue(String.class);

                            HashMap<String, String> muta_intrebare = new HashMap<>();
                            muta_intrebare.put("Text", text);
                            muta_intrebare.put("Indiciu 1", indiciu1);
                            muta_intrebare.put("Indiciu 2", indiciu2);
                            muta_intrebare.put("Raspuns", raspuns);
                            ref_intrebari.child(id_intrebare - 1 + "").setValue(muta_intrebare);
                        }
                    }
                    ref_intrebari.child(id_intrebare + "").removeValue();
                    ok_sterge = false;
                }
            }

            @Override
            public void onCancelled(DatabaseError eroare) {

            }
        });

        lista_intrebari.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> viewParinte, View view, int pozitie, long l) {
                Meniu intrebare = (Meniu) viewParinte.getItemAtPosition(pozitie);

                String text = intrebare.getIntrebare();
                et_intrebare.setText(text);

                String indiciu1 = intrebare.getIndiciu1();
                et_indiciu1.setText(indiciu1);

                String indiciu2 = intrebare.getIndiciu2();
                et_indiciu2.setText(indiciu2);

                String raspuns = intrebare.getRaspuns();
                et_raspuns.setText(raspuns);

                intrebare_curenta = pozitie;
                ok_lista = true;
            }
        });

        adauga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = et_intrebare.getText().toString();
                String indiciu1 = et_indiciu1.getText().toString();
                String indiciu2 = et_indiciu2.getText().toString();
                String raspuns = et_raspuns.getText().toString();

                if (TextUtils.isEmpty(text)) {
                    et_intrebare.setError("Introduceți întrebarea!");
                    return;
                }

                if (TextUtils.isEmpty(indiciu1)) {
                    et_indiciu1.setError("Introduceți primul indiciu!");
                    return;
                }

                if (TextUtils.isEmpty(indiciu2)) {
                    et_indiciu2.setError("Introduceți al doilea indiciu!");
                    return;
                }

                if (TextUtils.isEmpty(raspuns)) {
                    et_raspuns.setError("Introduceți răspunsul!");
                    return;
                }

                HashMap<String, String> intrebare = new HashMap<>();
                intrebare.put("Text", text);
                intrebare.put("Indiciu 1", indiciu1);
                intrebare.put("Indiciu 2", indiciu2);
                intrebare.put("Raspuns", raspuns);
                ref_intrebari.child(id_intrebare + 1 + "").setValue(intrebare);

                et_intrebare.getText().clear();
                et_indiciu1.getText().clear();
                et_indiciu2.getText().clear();
                et_raspuns.getText().clear();
            }
        });

        modifica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lista_intrebari.getCount() == 0) {
                    Toast.makeText(ProfesorIntrebari.this,
                            "Nu există încă întrebări adăugate!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (ok_lista) {
                    String text = et_intrebare.getText().toString();
                    String indiciu1 = et_indiciu1.getText().toString();
                    String indiciu2 = et_indiciu2.getText().toString();
                    String raspuns = et_raspuns.getText().toString();

                    if (TextUtils.isEmpty(text)) {
                        et_intrebare.setError("Nu ați ales o întrebare pentru a o modifica!");
                        return;
                    }

                    if (TextUtils.isEmpty(indiciu1)) {
                        et_indiciu1.setError("Nu ați ales indiciul 1 pentru a-l modifica!");
                        return;
                    }

                    if (TextUtils.isEmpty(indiciu2)) {
                        et_indiciu2.setError("Nu ați ales indiciul 2 pentru a-l modifica!");
                        return;
                    }

                    if (TextUtils.isEmpty(raspuns)) {
                        et_raspuns.setError("Nu ați ales răspunsul pentru a-l modifica!");
                        return;
                    }

                    HashMap<String, String> intrebare = new HashMap<>();
                    intrebare.put("Text", text);
                    intrebare.put("Indiciu 1", indiciu1);
                    intrebare.put("Indiciu 2", indiciu2);
                    intrebare.put("Raspuns", raspuns);
                    ref_intrebari.child(intrebare_curenta + 100 + "").setValue(intrebare);

                    et_intrebare.getText().clear();
                    et_indiciu1.getText().clear();
                    et_indiciu2.getText().clear();
                    et_raspuns.getText().clear();

                    ok_lista = false;
                } else {
                    Toast.makeText(ProfesorIntrebari.this,
                            "Selectați o întrebare mai întâi!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        sterge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lista_intrebari.getCount() == 0) {
                    Toast.makeText(ProfesorIntrebari.this,
                            "Nu există încă întrebări adăugate!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!ok_lista) {
                    Toast.makeText(ProfesorIntrebari.this,
                            "Selectați o întrebare mai întâi!", Toast.LENGTH_SHORT).show();
                    return;
                }

                AlertDialog.Builder constructor =
                        new AlertDialog.Builder(ProfesorIntrebari.this);

                constructor.setTitle("Confirmați ștergerea");

                constructor.setMessage("Sigur doriți să ștergeți întrebarea '" +
                        intrebare_curenta + "'?");

                constructor.setPositiveButton("Da", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        ref_intrebari.child(intrebare_curenta + 100 + "").removeValue();

                        et_intrebare.getText().clear();
                        et_indiciu1.getText().clear();
                        et_indiciu2.getText().clear();
                        et_raspuns.getText().clear();

                        ok_sterge = true;

                        Toast.makeText(ProfesorIntrebari.this, "Întrebarea a fost" +
                                " ștearsă din lecție!", Toast.LENGTH_SHORT).show();
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
            }
        });

        goleste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text_intrebare = et_intrebare.getText().toString();
                String text_indiciu1 = et_indiciu1.getText().toString();
                String text_indiciu2 = et_indiciu2.getText().toString();
                String text_raspuns = et_raspuns.getText().toString();

                if(text_intrebare.equals("") && text_indiciu1.equals("") &&
                        text_indiciu2.equals("") && text_raspuns.equals("")) {

                    Toast.makeText(ProfesorIntrebari.this,
                            "Niciun câmp nu este completat pentru a putea fi golit!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    et_intrebare.getText().clear();
                    et_indiciu1.getText().clear();
                    et_indiciu2.getText().clear();
                    et_raspuns.getText().clear();

                    Toast.makeText(ProfesorIntrebari.this,
                            "Câmpurile au fost golite!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
