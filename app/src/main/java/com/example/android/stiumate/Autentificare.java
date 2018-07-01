package com.example.android.stiumate;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.support.annotation.NonNull;
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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Autentificare extends AppCompatActivity {

    private FirebaseAuth fb_aut;
    private EditText camp_email, camp_parola;
    private String parola;
    private ProgressDialog cercIncarca;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autentificare);

        Button login = findViewById(R.id.autentificare_login);
        Button catre_inregistrare = findViewById(R.id.autentificare_catre_inregistrare);
        Button resetare_parola = findViewById(R.id.autentificare_resetare_parola);
        camp_email = findViewById(R.id.autentificare_email);
        camp_parola = findViewById(R.id.autentificare_parola);

        fb_aut = FirebaseAuth.getInstance();
        if (fb_aut.getCurrentUser() != null) {
            startActivity(new Intent(Autentificare.this, ProfilUtilizator.class));
            finish();
        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cercIncarca = new ProgressDialog(Autentificare.this);
                cercIncarca.setMessage("Verificăm datele introduse...");
                cercIncarca.show();

                String email = camp_email.getText().toString();
                parola = camp_parola.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    camp_email.setError("Nu ați introdus adresa de email!");
                    return;
                }

                if (TextUtils.isEmpty(parola)) {
                    camp_parola.setError("Nu ați introdus parola!");
                    return;
                }

                fb_aut.signInWithEmailAndPassword(email, parola)
                        .addOnCompleteListener(Autentificare.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (!task.isSuccessful()) {
                                    cercIncarca.dismiss();

                                    if (parola.length() < 6) {
                                        camp_parola.setError("Introduceți minim 6 caractere!");
                                    } else {
                                        Toast.makeText(Autentificare.this,
                                                "A intervenit o eroare!",
                                                Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    cercIncarca.dismiss();

                                    Intent intent = new Intent(Autentificare.this,
                                            ProfilUtilizator.class);

                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
            }
        });

        catre_inregistrare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Autentificare.this, Inregistrare.class));
            }
        });

        resetare_parola.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Autentificare.this, ResetareParola.class));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.autentificare, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id == R.id.pag_principala){
            startActivity(new Intent(Autentificare.this, PagPrinc.class));
        }
        return true;
    }
}
