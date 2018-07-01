package com.example.android.stiumate;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.android.stiumate.Classes.Intrebari;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Exerseaza extends AppCompatActivity {

    private String text_intrebare, indiciu1, indiciu2, raspuns, nume_lectie,
            raspuns_corect, raspuns_gresit;
    private TextView textV_intrebare, textV_hint, nr_greseli;
    private EditText editT_raspuns;
    private Button butonTrimite, butonUrmatoareaIntrebare;
    private int ok = 0;
    private int greseli =0;
    private List<Intrebari> listaIntrebari = new ArrayList<>();
    private Intrebari intrebareNoua;
    private Integer scor = 0;
    private Integer scor_invata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exerseaza);

        textV_intrebare = findViewById(R.id.exerseaza_intrebare);
        editT_raspuns = findViewById(R.id.exerseaza_raspuns);
        textV_hint = findViewById(R.id.exerseaza_hint);
        nr_greseli = findViewById(R.id.exerseaza_greseli);
        butonTrimite = findViewById(R.id.exerseaza_butonRaspuns);
        butonUrmatoareaIntrebare = findViewById(R.id.exerseaza_urmatoareIntrebare);
        raspuns_corect = getResources().getString(R.string.raspuns_corect);
        raspuns_gresit = getResources().getString(R.string.raspuns_gresit);

        Bundle primit = getIntent().getExtras();
        if (primit != null) {
            nume_lectie = primit.getString("nume_lectie", "Nume lecție");
            scor_invata = primit.getInt("scor_lectie", 0);

        }

        DatabaseReference ref_intrebare = FirebaseDatabase.getInstance()
                .getReference().child("Matematica").child(nume_lectie).child("Intrebari");

        ref_intrebare.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot intrebari) {
                for (DataSnapshot intrebare : intrebari.getChildren()) {
                    text_intrebare = intrebare.child("Text").getValue(String.class);
                    raspuns = intrebare.child("Raspuns").getValue(String.class);
                    indiciu1 = intrebare.child("Indiciu 1").getValue(String.class);
                    indiciu2 = intrebare.child("Indiciu 2").getValue(String.class);

                    intrebareNoua = new Intrebari(text_intrebare, indiciu1, indiciu2, raspuns);
                    listaIntrebari.add(intrebareNoua);
                }
                urmatoareaIntrebare(0);
            }

            @Override
            public void onCancelled(DatabaseError eroare) {

            }
        });
    }

    private void raspunsuri(final int id) {
        ok = 0;
        greseli = 0;

        butonTrimite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String raspuns_lista = listaIntrebari.get(id).getRaspuns();
                nr_greseli.setVisibility(View.VISIBLE);
                if (ok == 0) {
                    if (editT_raspuns.getText().toString()
                            .equals(raspuns_lista)) {

                        textV_hint.setText(raspuns_corect);
                        scor += 3;
                        butonTrimite.setVisibility(View.GONE);
                    } else {
                        String text_ind1 = raspuns_gresit + " " +
                                listaIntrebari.get(id).getIndiciu1();

                        textV_hint.setText(text_ind1);
                        greseli++;
                        String afisare_greseli = greseli + " " +
                                getResources().getString(R.string.greseli);

                        nr_greseli.setText(afisare_greseli);
                        ok++;
                    }
                }
                else if (ok == 1) {
                    if (editT_raspuns.getText().toString()
                            .equals(raspuns_lista)) {

                        textV_hint.setText(raspuns_corect);
                        scor += 2;
                        butonTrimite.setVisibility(View.GONE);
                    } else {
                        String text_ind2 = raspuns_gresit + " " +
                                listaIntrebari.get(id).getIndiciu2();

                        textV_hint.setText(text_ind2);
                        greseli ++;
                        String afisare_greseli = greseli + " " +
                                getResources().getString(R.string.greseli);

                        nr_greseli.setText(afisare_greseli);
                        ok++;
                    }
                } else if (ok == 2) {
                    if (editT_raspuns.getText().toString()
                            .equals(raspuns_lista)) {

                        textV_hint.setText(raspuns_corect);
                        scor += 1;
                        butonTrimite.setVisibility(View.GONE);
                    } else {
                        String era_corect = getResources().getString(R.string.era_corect) + " "
                                + raspuns_lista;

                        textV_hint.setText(era_corect);
                        greseli ++;
                        String afisare_greseli = greseli + " " +  getResources().getString(R.string.greseli);
                        nr_greseli.setText(afisare_greseli);

                        if(greseli ==3 )
                            butonTrimite.setVisibility(View.GONE);
                    }

                }
            }
        });

        butonUrmatoareaIntrebare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (id + 1 < listaIntrebari.size()) {
                    urmatoareaIntrebare(id + 1);
                    nr_greseli.setVisibility(View.GONE);
                    butonTrimite.setVisibility(View.VISIBLE);
                }
                else {
                    Intent i = new Intent(Exerseaza.this, AfisareScor.class);
                    i.putExtra("nrIntrebari", listaIntrebari.size());
                    i.putExtra("scorFinal", scor);
                    i.putExtra("id_lectie", nume_lectie);
                    i.putExtra("scor_afisareScor", scor_invata);
                    startActivity(i);
                    finish();
                }
            }
        });
    }

    private void urmatoareaIntrebare(int id) {
        textV_intrebare.setText(listaIntrebari.get(id).getText_intrebare());
        editT_raspuns.setText("");
        textV_hint.setText("");
        raspunsuri(id);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.exerseaza, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id == R.id.lectii){
            startActivity(new Intent(Exerseaza.this, Invata.class));
        }

        if(id == R.id.pag_principala){
            startActivity(new Intent(Exerseaza.this, PagPrinc.class));
        }
        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();

            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);

                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();

                    InputMethodManager imm =
                            (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

                    if(imm != null) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }
}
