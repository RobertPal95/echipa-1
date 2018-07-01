package com.example.android.stiumate;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfilSetari extends AppCompatActivity {
    private EditText setari_schimba_nume, setari_schimba_parola, setari_schimba_email;
    private FirebaseAuth autentificare;
    private FirebaseUser utilizator;
    private ProgressDialog cercIncarca, cercIncarcaNume;
    private DatabaseReference ref_utilizator;
    private String email_nou, link_poza, id_utilizator, nume_utilizator;
    private boolean ok_nume = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profil_setari);

        setari_schimba_nume = findViewById(R.id.setari_schimba_nume);
        setari_schimba_parola = findViewById(R.id.setari_schimba_parola);
        setari_schimba_email = findViewById(R.id.setari_schimba_email);
        Button schimbaNumeUtilizator_bj = findViewById(R.id.setari_confirma_nume);
        Button schimbaParola_bj = findViewById(R.id.setari_confirma_parola);
        Button schimbaEmail_bj = findViewById(R.id.setari_confirma_email);
        Button stergeCont_bj = findViewById(R.id.setari_sterge_cont);

        autentificare = FirebaseAuth.getInstance();
        utilizator = autentificare.getCurrentUser();

        if (utilizator != null) {
            id_utilizator = utilizator.getUid();
        }

        if (id_utilizator != null) {
            ref_utilizator = FirebaseDatabase.getInstance()
                    .getReference().child("Utilizatori").child(id_utilizator);
        }

        schimbaNumeUtilizator_bj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cercIncarcaNume = new ProgressDialog(ProfilSetari.this);
                cercIncarcaNume.setMessage("Verificăm dacă noul nume este disponibil...");
                cercIncarcaNume.show();

                nume_utilizator = setari_schimba_nume.getText().toString().trim();

                if (nume_utilizator.equals("")) {
                    cercIncarca.dismiss();

                    setari_schimba_nume.setError("Introduceți noul nume de utilizator!");
                    return;
                }

                DatabaseReference ref_utilizatori = FirebaseDatabase
                        .getInstance().getReference().child("Utilizatori");

                ref_utilizatori.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot utilizatori) {
                        cercIncarcaNume.dismiss();

                        for (DataSnapshot utilizator : utilizatori.getChildren()) {
                            String nume_fb = utilizator
                                    .child("Nume utilizator").getValue(String.class);

                            if (nume_fb != null) {
                                if (nume_fb.equalsIgnoreCase(nume_utilizator)) {
                                    setari_schimba_nume.setError("Numele ales este deja luat!");
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

        schimbaParola_bj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cercIncarca = new ProgressDialog(ProfilSetari.this);
                cercIncarca.setMessage("Vă schimbăm parola...");
                cercIncarca.show();

                String parola_noua = setari_schimba_parola.getText().toString().trim();
                if (!parola_noua.equals("")) {
                    if (parola_noua.length() < 6) {
                        cercIncarca.dismiss();
                        setari_schimba_parola.setError("Introduceți minim 6 caractere!");
                    } else if (parola_noua.length() > 50) {
                        cercIncarca.dismiss();
                        setari_schimba_parola.setError("Introduceți maxim 50 caractere!");
                    } else {
                        utilizator.updatePassword(setari_schimba_parola.getText().toString()
                                .trim()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    cercIncarca.dismiss();

                                    Toast.makeText(ProfilSetari.this, "Parola a " +
                                            "fost modificată, vă puteți autentifica " +
                                            "cu cea nouă.", Toast.LENGTH_SHORT).show();

                                    autentificare.signOut();

                                    startActivity(new Intent(ProfilSetari.this,
                                            Autentificare.class));

                                    finish();
                                } else {
                                    cercIncarca.dismiss();
                                    Toast.makeText(ProfilSetari.this,
                                            "A intervenit o eroare în schimbarea parolei!",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                } else {
                    cercIncarca.dismiss();
                    setari_schimba_parola.setError("Introduceți noua parolă!");
                }
            }
        });

        schimbaEmail_bj.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                email_nou = setari_schimba_email.getText().toString().trim();

                if (!email_nou.equals("")) {
                    cercIncarca = new ProgressDialog(ProfilSetari.this);
                    cercIncarca.setMessage("Vă schimbăm adresa de email...");
                    cercIncarca.show();

                    utilizator.updateEmail(email_nou).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ProfilSetari.this,
                                        "Email-ul a fost modificat, vă puteți autentifica" +
                                                " cu cel nou.", Toast.LENGTH_LONG).show();

                                autentificare.signOut();

                                startActivity(new Intent(ProfilSetari.this,
                                        Autentificare.class));

                                finish();
                            } else {
                                cercIncarca.dismiss();

                                setari_schimba_email.setError("Email-ul ales există " +
                                        "deja în aplicație");
                            }
                        }
                    });
                } else {
                    setari_schimba_email.setError("Introduceți noul email!");
                }
            }
        });

        stergeCont_bj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder constructor =
                        new AlertDialog.Builder(ProfilSetari.this);

                constructor.setTitle("Confirmați ștergerea");
                constructor.setMessage("Sigur doriți să vă ștergeți contul?");

                constructor.setPositiveButton("Da", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        cercIncarca = new ProgressDialog(ProfilSetari.this);
                        cercIncarca.setMessage("Vă ștergem contul...");
                        cercIncarca.show();

                        utilizator.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    cercIncarca.dismiss();

                                    ref_utilizator.addListenerForSingleValueEvent(
                                            new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot profil) {
                                                    cercIncarca.dismiss();

                                                    link_poza = profil.child("Poza profil")
                                                            .getValue(String.class);

                                                    StorageReference poza_profil = FirebaseStorage
                                                            .getInstance()
                                                            .getReferenceFromUrl(link_poza);

                                                    poza_profil.delete();
                                                    ref_utilizator.removeValue();
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError eroare) {

                                                }
                                            });

                                    Toast.makeText(ProfilSetari.this,
                                            "Contul dumneavoastră a fost șters!",
                                            Toast.LENGTH_SHORT).show();

                                    startActivity(new Intent(ProfilSetari.this,
                                            Autentificare.class));

                                    finish();
                                } else {
                                    cercIncarca.dismiss();

                                    Toast.makeText(ProfilSetari.this,
                                            "A apărut o eroare în ștergerea contului" +
                                                    " dumneavoastră!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
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
    }

    private void actualizeaza_nume() {
        if (ok_nume) {
            ref_utilizator.child("Nume utilizator").setValue(nume_utilizator);
            FirebaseUser utilizator = FirebaseAuth.getInstance().getCurrentUser();

            UserProfileChangeRequest profileUpdates =
                    new UserProfileChangeRequest.Builder()
                            .setDisplayName(nume_utilizator).build();

            if (utilizator != null) {
                utilizator.updateProfile(profileUpdates);
            }

            Toast.makeText(ProfilSetari.this, "Numele de utilizator a " +
                    "fost actualizat.", Toast.LENGTH_SHORT).show();
        } else {
            ok_nume = true;
        }
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

                    if (imm != null) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profil_setari, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.profil_utilizator) {
            startActivity(new Intent(ProfilSetari.this, Autentificare.class));
        }

        if (id == R.id.pag_principala) {
            startActivity(new Intent(ProfilSetari.this, PagPrinc.class));
        }
        return true;
    }
}


