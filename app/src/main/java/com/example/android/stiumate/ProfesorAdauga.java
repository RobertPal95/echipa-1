package com.example.android.stiumate;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.android.stiumate.Classes.Intrebari;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProfesorAdauga extends AppCompatActivity {

    private EditText adauga_titlu, adauga_teorie, adauga_cerinta;
    private DatabaseReference ref_mate;
    private Uri link_poza;
    private static final int cod_poza_lectie = 123;
    private int pozitie_cursor_teorie, pozitie_cursor_cerinta;
    private boolean ok = false, ok_lectie, ok_adauga = false;
    private String titlu_nou, teorie_noua, cerinta_noua, titlu,
            text_introdus_teorie, text_introdus_cerinta, poza, id_utilizator;
    private List<Intrebari> intrebari = new ArrayList<>();
    private HashMap<String, Object> lectie_noua;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_profesor_adauga);

        adauga_titlu = findViewById(R.id.adauga_lectie);
        adauga_teorie = findViewById(R.id.adauga_teorie);
        adauga_cerinta = findViewById(R.id.adauga_cerinta);

        ImageButton adauga_poza = findViewById(R.id.adauga_poza);
        Button confirma_lectie = findViewById(R.id.confirma_lectie);
        Button goleste_lectie = findViewById(R.id.goleste_lectie);

        Bundle primit = getIntent().getExtras();
        if (primit != null) {
            titlu = primit.getString("titlu", "Titlu");
            String teorie = primit.getString("teorie", "Teorie");
            String cerinta = primit.getString("cerinta", "Cerință");
            poza = primit.getString("poza", "Imagine");

            adauga_titlu.setText(titlu);
            adauga_teorie.setText(teorie);
            adauga_cerinta.setText(cerinta);

            ok = true;
        } else {
            ok_adauga = true;
        }

        pozitie_cursor_teorie = 0;
        text_introdus_teorie = "";
        adauga_teorie.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                pozitie_cursor_teorie = adauga_teorie.getSelectionStart();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                adauga_teorie.removeTextChangedListener(this);

                if (adauga_teorie.getLineCount() > 10) {
                    adauga_teorie.setText(text_introdus_teorie);
                    adauga_teorie.setSelection(pozitie_cursor_teorie);
                } else {
                    text_introdus_teorie = adauga_teorie.getText().toString();
                }
                adauga_teorie.addTextChangedListener(this);
            }
        });

        pozitie_cursor_cerinta = 0;
        text_introdus_cerinta = "";
        adauga_cerinta.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                pozitie_cursor_cerinta = adauga_cerinta.getSelectionStart();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                adauga_cerinta.removeTextChangedListener(this);

                if (adauga_cerinta.getLineCount() > 5) {
                    adauga_cerinta.setText(text_introdus_cerinta);
                    adauga_cerinta.setSelection(pozitie_cursor_cerinta);
                } else {
                    text_introdus_cerinta = adauga_cerinta.getText().toString();
                }
                adauga_cerinta.addTextChangedListener(this);
            }
        });

        FirebaseUser utilizator = FirebaseAuth.getInstance().getCurrentUser();
        if (utilizator != null) {
            id_utilizator = utilizator.getUid();
        }

        adauga_poza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent,
                        "Completează acțiunea folosind"), cod_poza_lectie);
            }
        });

        confirma_lectie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ok) {
                    titlu_nou = adauga_titlu.getText().toString();
                    teorie_noua = adauga_teorie.getText().toString();
                    cerinta_noua = adauga_cerinta.getText().toString();

                    if (TextUtils.isEmpty(titlu_nou)) {
                        adauga_titlu.setError("Introduceți titlul lecției!");
                        return;
                    }

                    if (TextUtils.isEmpty(teorie_noua)) {
                        adauga_teorie.setError("Introduceți teoria!");
                        return;
                    }

                    if (TextUtils.isEmpty(cerinta_noua)) {
                        adauga_cerinta.setError("Introduceți cerința!");
                        return;
                    }

                    ok_lectie = false;

                    ref_mate = FirebaseDatabase.getInstance()
                            .getReference().child("Matematica");

                    ref_mate.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot lectii) {
                            for (DataSnapshot lectie : lectii.getChildren()) {
                                if (lectie.getKey().equals(titlu_nou)) {
                                    ok_lectie = true;
                                }

                                if (titlu != null) {
                                    if (titlu.equals(titlu_nou)) {
                                        ok_lectie = false;
                                    }
                                }
                            }

                            if (ok_lectie) {
                                adauga_titlu.setError("Lecția există deja în aplicație!");
                                ok_lectie = false;
                            } else {
                                lectie_noua = new HashMap<>();

                                if(ok_adauga) {
                                    if (link_poza != null) {
                                        lectie_noua.put("Imagine", link_poza.toString());
                                    } else {
                                        lectie_noua.put("Imagine", poza);
                                    }

                                    lectie_noua.put("Cerinta", cerinta_noua);
                                    lectie_noua.put("Public", false);
                                    lectie_noua.put("Teorie", teorie_noua);
                                    lectie_noua.put("ID Autor", id_utilizator);

                                    ref_mate.child(titlu_nou).setValue(lectie_noua);

                                    Toast.makeText(ProfesorAdauga.this,
                                            "Lecția a fost adaugată cu succes!",
                                            Toast.LENGTH_SHORT).show();

                                    startActivity(new Intent(ProfesorAdauga.this,
                                            ProfesorAlegere.class));

                                    return;
                                }

                                if (titlu != null) {
                                    if (!titlu.equals(titlu_nou)) {
                                        DataSnapshot intrebari_lectie = lectii
                                                .child(titlu).child("Intrebari");

                                        for (DataSnapshot intr : intrebari_lectie.getChildren()) {
                                            String text = intr.child("Text")
                                                    .getValue(String.class);

                                            String ind1 = intr.child("Indiciu 1")
                                                    .getValue(String.class);

                                            String ind2 = intr.child("Indiciu 2")
                                                    .getValue(String.class);

                                            String rasp = intr.child("Raspuns")
                                                    .getValue(String.class);

                                            intrebari.add(new Intrebari(text, ind1, ind2, rasp));
                                        }

                                        ref_mate.child(titlu).removeValue();

                                        if (link_poza != null) {
                                            lectie_noua.put("Imagine", link_poza.toString());
                                        } else {
                                            lectie_noua.put("Imagine", poza);
                                        }

                                        lectie_noua.put("Cerinta", cerinta_noua);
                                        lectie_noua.put("Public", false);
                                        lectie_noua.put("Teorie", teorie_noua);
                                        lectie_noua.put("ID Autor", id_utilizator);

                                        ref_mate.child(titlu_nou).setValue(lectie_noua);

                                        for (int i = 0; i < intrebari.size(); i++) {
                                            DatabaseReference ref_intreb = ref_mate.child(titlu_nou)
                                                    .child("Intrebari").child(i + 101 + "");

                                            ref_intreb.child("Text")
                                                    .setValue(intrebari.get(i).getText_intrebare());

                                            ref_intreb.child("Indiciu 1")
                                                    .setValue(intrebari.get(i).getIndiciu1());

                                            ref_intreb.child("Indiciu 2")
                                                    .setValue(intrebari.get(i).getIndiciu2());

                                            ref_intreb.child("Raspuns")
                                                    .setValue(intrebari.get(i).getRaspuns());
                                        }

                                        Toast.makeText(ProfesorAdauga.this,
                                                "Lecția a fost modificată cu succes!",
                                                Toast.LENGTH_SHORT).show();

                                        startActivity(new Intent(ProfesorAdauga.this,
                                                ProfesorAlegere.class));
                                    } else {
                                        DatabaseReference ref_lectie = ref_mate.child(titlu);

                                        if (link_poza != null) {
                                            ref_lectie.child("Imagine").setValue(link_poza.toString());
                                        } else {
                                            ref_lectie.child("Imagine").setValue(poza);
                                        }

                                        ref_lectie.child("Public").setValue(false);
                                        ref_lectie.child("Teorie").setValue(teorie_noua);
                                        ref_lectie.child("Cerinta").setValue(teorie_noua);
                                        ref_lectie.child("ID Autor").setValue(id_utilizator);

                                        Toast.makeText(ProfesorAdauga.this,
                                                "Lecția a fost modificată cu succes!",
                                                Toast.LENGTH_SHORT).show();

                                        startActivity(new Intent(ProfesorAdauga.this,
                                                ProfesorAlegere.class));
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError eroare) {

                        }
                    });
                } else {
                    Toast.makeText(ProfesorAdauga.this,
                            "Vă rugăm să alegeți o poză reprezentativă pentru lecție!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        goleste_lectie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adauga_titlu.getText().clear();
                adauga_teorie.getText().clear();
                adauga_cerinta.getText().clear();
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == cod_poza_lectie && resultCode == RESULT_OK) {
            Uri imagine_selectata = data.getData();

            StorageReference ref_poze_lectii = FirebaseStorage
                    .getInstance().getReference().child("Lectii");

            if (imagine_selectata != null) {
                StorageReference adresa_poza = ref_poze_lectii
                        .child(imagine_selectata.getLastPathSegment());

                Toast.makeText(ProfesorAdauga.this,
                        "Imaginea se încarcă, vă rugăm să așteptați...",
                        Toast.LENGTH_SHORT).show();

                adresa_poza.putFile(imagine_selectata).addOnSuccessListener(ProfesorAdauga.
                        this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(ProfesorAdauga.this,
                                "Imaginea s-a încărcat cu succes!",
                                Toast.LENGTH_SHORT).show();

                        link_poza = taskSnapshot.getDownloadUrl();
                        ok = true;
                    }
                });
            }
        }
    }
}
