package com.example.android.stiumate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class Inregistrare extends AppCompatActivity {

    private EditText inregistrare_email, inregistrare_nume, inregistrare_parola;
    private FirebaseAuth autentificare;
    private ProgressDialog cercIncarca;
    private String email, nume, parola, poza_implicita, id_utilizator;
    private DatabaseReference ref_fb;
    private boolean ok_nume = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inregistrare);

        Button catre_login = findViewById(R.id.inregistrare_catre_login);
        autentificare = FirebaseAuth.getInstance();
        inregistrare_email = findViewById(R.id.inregistrare_email);
        inregistrare_nume = findViewById(R.id.inregistrare_nume);
        inregistrare_parola = findViewById(R.id.inregistrare_parola);
        Button inregistrare = findViewById(R.id.inregistrare_inregistreaza);

        poza_implicita = "https://firebasestorage.googleapis.com/v0/b/stiumate-3dda4" +
                ".appspot.com/o/Poze%20profil%2FFara_imagine" +
                ".jpg?alt=media&token=dd4a1b0a-7460-4132-91ac-619d6e9c0df7";

        inregistrare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = inregistrare_email.getText().toString().trim();
                nume = inregistrare_nume.getText().toString().trim();
                parola = inregistrare_parola.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    inregistrare_email.setError("Introduceți email-ul!");
                    return;
                }

                if (TextUtils.isEmpty(nume)) {
                    inregistrare_nume.setError("Introduceți numele de utilizator!");
                    return;
                }

                if (TextUtils.isEmpty(parola)) {
                    inregistrare_parola.setError("Introduceți parola!");
                    return;
                }

                if (parola.length() < 6) {
                    inregistrare_parola.setError("Parola trebuie să conțină minim 6 caractere!");
                    return;
                }

                if (nume.length() < 2) {
                    inregistrare_nume.setError("Numele de utilizator " +
                            "trebuie să conțină minim 2 caractere!");
                    return;
                }

                if (nume.length() > 20) {
                    inregistrare_nume.setError("Numele de utilizator " +
                            "trebuie să conțină maxim 20 de caractere!");
                    return;
                }

                ref_fb = FirebaseDatabase.getInstance().getReference();
                DatabaseReference ref_utilizatori = ref_fb.child("Utilizatori");

                ref_utilizatori.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot utilizatori) {
                        for (DataSnapshot utilizator : utilizatori.getChildren()) {

                            String nume_fb = utilizator
                                    .child("Nume utilizator").getValue(String.class);

                            if (nume_fb != null) {
                                if (nume_fb.equalsIgnoreCase(nume)) {
                                    inregistrare_nume.setError("Numele ales este deja luat!");
                                    ok_nume = false;
                                }
                            }
                        }
                        actualizeaza_nume();
                    }

                    @Override
                    public void onCancelled(DatabaseError eroare) {

                    }
                });
            }
        });

        catre_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Inregistrare.this, Autentificare.class));
            }
        });
    }

    private void actualizeaza_nume() {
        if (ok_nume) {
            cercIncarca = new ProgressDialog(Inregistrare.this);
            cercIncarca.setMessage("Verificăm datele introduse...");
            cercIncarca.show();

            autentificare.createUserWithEmailAndPassword(email, parola).addOnCompleteListener(Inregistrare.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        cercIncarca.dismiss();

                        inregistrare_email.setError("Email-ul este " +
                                "invalid sau este deja înregistrat!");
                    } else {
                        FirebaseUser utilizator = FirebaseAuth
                                .getInstance().getCurrentUser();

                        if (utilizator != null) {
                            id_utilizator = utilizator.getUid();
                        }

                        DatabaseReference ref_utilizatori = FirebaseDatabase
                                .getInstance().getReference();

                        if (id_utilizator != null) {
                            ref_utilizatori = FirebaseDatabase
                                    .getInstance().getReference()
                                    .child("Utilizatori").child(id_utilizator);
                        }

                        HashMap<String, Object> date_utilizator = new HashMap<>();

                        date_utilizator.put("Poza profil", poza_implicita);
                        date_utilizator.put("Scor", 0);
                        date_utilizator.put("Nume utilizator", nume);
                        date_utilizator.put("Profesor", false);
                        ref_utilizatori.setValue(date_utilizator);

                        DatabaseReference ref_medalii = ref_utilizatori.child("Medalii");

                        ref_medalii.child("101").setValue(false);
                        ref_medalii.child("102").setValue(false);
                        ref_medalii.child("103").setValue(false);
                        ref_medalii.child("104").setValue(false);
                        ref_medalii.child("105").setValue(false);
                        ref_medalii.child("106").setValue(false);
                        ref_medalii.child("107").setValue(false);
                        ref_medalii.child("108").setValue(false);
                        ref_medalii.child("109").setValue(false);
                        ref_medalii.child("110").setValue(false);
                        ref_medalii.child("111").setValue(false);
                        ref_medalii.child("112").setValue(false);

                        ref_fb.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot fb) {
                                cercIncarca.dismiss();

                                DatabaseReference ref_scoruri = ref_fb
                                        .child("Utilizatori")
                                        .child(id_utilizator).child("Scoruri lectii");

                                HashMap<String, Integer> scoruri = new HashMap<>();

                                DataSnapshot lectii = fb.child("Matematica");
                                for (DataSnapshot lc : lectii.getChildren()) {
                                    Boolean publica = lc.child("Public")
                                            .getValue(Boolean.class);

                                    if (publica != null) {
                                        if (publica) {
                                            scoruri.put(lc.getKey(), 0);
                                        }
                                    }
                                }
                                ref_scoruri.setValue(scoruri);
                            }

                            @Override
                            public void onCancelled(DatabaseError eroare) {

                            }
                        });

                        UserProfileChangeRequest profileUpdates =
                                new UserProfileChangeRequest.Builder()
                                        .setDisplayName(nume)
                                        .setPhotoUri(Uri.parse(poza_implicita)).build();

                        if (utilizator != null) {
                            utilizator.updateProfile(profileUpdates);
                        }

                        Toast.makeText(Inregistrare.this,
                                "Contul a fost creeat!", Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(
                                Inregistrare.this, Autentificare.class));
                    }
                }
            });
        } else {
            ok_nume = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.resetare_parola, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.autentificare) {
            startActivity(new Intent(Inregistrare.this, Autentificare.class));
        }

        if (id == R.id.pag_principala) {
            startActivity(new Intent(Inregistrare.this, PagPrinc.class));
        }
        return true;
    }
}
