package com.example.android.stiumate;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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
import com.google.firebase.auth.FirebaseAuth;

public class ResetareParola extends AppCompatActivity {

    private EditText resetareParola_email;
    private FirebaseAuth fb_aut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetare_parola);

        resetareParola_email = findViewById(R.id.resetareParola_email);
        Button resetareParola_reseteaza = findViewById(R.id.resetareParola_reseteaza);
        Button resetareParola_inapoi = findViewById(R.id.resetareParola_inapoi);
        fb_aut = FirebaseAuth.getInstance();

        resetareParola_reseteaza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = resetareParola_email.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplication(),
                            "Introduceți email-ul!", Toast.LENGTH_SHORT).show();
                    return;
                }

                fb_aut.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ResetareParola.this,
                                    "V-am trimis instrucțiunile necesare pe email.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ResetareParola.this,
                                    "Acest email nu este înregistrat în aplicație!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        resetareParola_inapoi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ResetareParola.this, Autentificare.class));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.resetare_parola, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if(id == R.id.autentificare){
            startActivity(new Intent(ResetareParola.this, Autentificare.class));
        }

        if(id == R.id.pag_principala){
            startActivity(new Intent(ResetareParola.this, PagPrinc.class));
        }
        return true;
    }
}
