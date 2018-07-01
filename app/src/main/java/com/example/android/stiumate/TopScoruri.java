package com.example.android.stiumate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import com.example.android.stiumate.Adaptors.ListaScoruri;
import com.example.android.stiumate.Classes.ScoreComparator;
import com.example.android.stiumate.Classes.Scoruri;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class TopScoruri extends AppCompatActivity {

    private String nume;
    private String poza;
    private Integer scorUtilizator;
    private ListView lista_scoruri;
    private ListaScoruri adaptorScoruri;
    private ProgressDialog cercIncarca;
    private ArrayList<Scoruri> array_scoruri = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_scoruri);

        cercIncarca = new ProgressDialog(TopScoruri.this);
        cercIncarca.setMessage("Încărcăm lista de scoruri...");
        cercIncarca.show();

        lista_scoruri = findViewById(R.id.topscoruri_lista);
        adaptorScoruri = new ListaScoruri(this, R.layout.listview_topscoruri);

        DatabaseReference referintaPersonalitate = FirebaseDatabase
                .getInstance().getReference().child("Utilizatori");

        referintaPersonalitate.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot utilizatori) {
                cercIncarca.dismiss();

                if (adaptorScoruri.getCount() > 0) {
                    adaptorScoruri.clearData();
                    adaptorScoruri.notifyDataSetChanged();
                }

                array_scoruri.clear();

                for (DataSnapshot utilizator : utilizatori.getChildren()) {
                    nume = utilizator.child("Nume utilizator").getValue(String.class);
                    scorUtilizator = utilizator.child("Scor").getValue(Integer.class);
                    poza = utilizator.child("Poza profil").getValue(String.class);
                    array_scoruri.add(new Scoruri(nume, poza, scorUtilizator));
                }

                Collections.sort(array_scoruri, new ScoreComparator());

                for(int i = 0; i < array_scoruri.size(); i++) {
                    adaptorScoruri.add(array_scoruri.get(i));
                }

                lista_scoruri.setAdapter(adaptorScoruri);
            }

            @Override
            public void onCancelled(DatabaseError eroare) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.scor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id == R.id.pag_principala){
            startActivity(new Intent(TopScoruri.this, PagPrinc.class));
        }
        return true;
    }
}
