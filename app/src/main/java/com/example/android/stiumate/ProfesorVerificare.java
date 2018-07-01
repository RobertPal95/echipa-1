package com.example.android.stiumate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfesorVerificare extends AppCompatActivity {

    private String parola_fb, id_utilizator;
    private EditText introdu_parola;
    private Boolean profesor;
    private ProgressDialog cercIncarca;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profesor_verificare);

        cercIncarca = new ProgressDialog(ProfesorVerificare.this);
        cercIncarca.setMessage("Încărcăm pagina de verificare...");
        cercIncarca.show();

        introdu_parola = findViewById(R.id.introdu_parola);
        Button confirma_parola = findViewById(R.id.confirma_parola);

        FirebaseUser utilizator = FirebaseAuth.getInstance().getCurrentUser();
        if (utilizator != null) {
            id_utilizator = utilizator.getUid();
        }

        DatabaseReference ref_fb = FirebaseDatabase.getInstance().getReference();
        ref_fb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot fb) {
                cercIncarca.dismiss();

                parola_fb = fb.child("Profesor").child("Parola").getValue(String.class);

                profesor = fb.child("Utilizatori").child(id_utilizator)
                        .child("Profesor").getValue(Boolean.class);
                System.out.println("CEVA" + profesor);
            }

            @Override
            public void onCancelled(DatabaseError eroare) {

            }
        });

        confirma_parola.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String parola_introdusa = introdu_parola.getText().toString();

                if (TextUtils.isEmpty(parola_introdusa)) {
                    introdu_parola.setError("Introduceți parola!");
                    return;
                }

                System.out.println("CEVA" + profesor);

                if(!profesor) {
                    introdu_parola.setError("Acces interzis!");
                    return;
                }

                if (parola_introdusa.equals(parola_fb)) {
                    Intent i = new Intent(ProfesorVerificare.this,
                            ProfesorAlegere.class);

                    startActivity(i);
                } else {
                    introdu_parola.setError("Parolă incorectă!");
                }
            }
        });
    }
}
