package com.example.android.stiumate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Lectie extends AppCompatActivity {

    private TextView lectie_teorie, lectie_cerinta;
    private String nume_lectie;
    private int scor_depeInvata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lectie);

        lectie_teorie = findViewById(R.id.lectie_teorie);
        lectie_cerinta = findViewById(R.id.lectie_cerinta);
        Button exerseaza = findViewById(R.id.lectie_but_exerseaza);

        Bundle primit = getIntent().getExtras();
        if (primit != null) {
            nume_lectie = primit.getString("titlu_lectie", "Nume lecție");
            scor_depeInvata = primit.getInt("scor_invata", 0);
        }

        DatabaseReference ref_mate = FirebaseDatabase.getInstance()
                .getReference().child("Matematica").child(nume_lectie);

        ref_mate.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot lectii) {
                String teorie = lectii.child("Teorie").getValue(String.class);
                lectie_teorie.setText(teorie);

                String cerinta = lectii.child("Cerinta").getValue(String.class);
                lectie_cerinta.setText(cerinta);
            }

            @Override
            public void onCancelled(DatabaseError eroare) {

            }
        });

        exerseaza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Lectie.this, Exerseaza.class);
                i.putExtra("nume_lectie", nume_lectie);
                i.putExtra("scor_lectie", scor_depeInvata);
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.lectie, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id == R.id.lectii){
            startActivity(new Intent(Lectie.this, Invata.class));
        }

        if(id == R.id.pag_principala){
            startActivity(new Intent(Lectie.this, PagPrinc.class));
        }
        return true;
    }
}
