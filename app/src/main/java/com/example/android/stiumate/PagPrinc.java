package com.example.android.stiumate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PagPrinc extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pag_princ);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        ImageView logo = findViewById(R.id.pag_princ_logo);

        String link_logo = "https://firebasestorage.googleapis.com/v0/b/stiumate-3dda4.appspot.com/o/Logo%2FlogoNout9.png?alt=media&token=19c745cc-3d4c-4875-ad2a-5233f7be4260";

        Glide.with(PagPrinc.this).load(link_logo).into(logo);

        Button invata = findViewById(R.id.butonInvata);
        Button ghid = findViewById(R.id.butonGhid);
        Button autentificare = findViewById(R.id.butonAutentificare);

        FirebaseUser utilizator = FirebaseAuth.getInstance().getCurrentUser();

        if (utilizator != null) {
            autentificare.setText(R.string.profil);
        } else {
            autentificare.setText(R.string.autentificare);
        }

        Button feedback = findViewById(R.id.butonFeedBack);
        Button topscoruri = findViewById(R.id.butonScoruri);
        Button iesire = findViewById(R.id.butonIesire);

        invata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PagPrinc.this, Invata.class));
            }
        });

        ghid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PagPrinc.this, Ghid.class));
            }
        });

        autentificare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PagPrinc.this, Autentificare.class));
            }
        });

        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PagPrinc.this, Feedback.class));
            }
        });

        topscoruri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PagPrinc.this, TopScoruri.class));
            }
        });

        iesire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveTaskToBack(true);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pag_princ, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.catre_autentificare) {
            startActivity(new Intent(PagPrinc.this, Autentificare.class));
        }
        if (id == R.id.catre_feedback) {
            startActivity(new Intent(PagPrinc.this, Feedback.class));
        }
        if (id == R.id.catre_topscoruri) {
            startActivity(new Intent(PagPrinc.this, TopScoruri.class));
        }
        return true;
    }
}
