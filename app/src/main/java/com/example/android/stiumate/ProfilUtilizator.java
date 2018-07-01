package com.example.android.stiumate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.stiumate.Adaptors.ListaMedalii;
import com.example.android.stiumate.Classes.Medalii;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.storage.UploadTask;

import static java.lang.String.valueOf;

public class ProfilUtilizator extends AppCompatActivity {
    private FirebaseAuth fb_aut;
    private String nume, poza, email, id_utilizator, text_nume, text_email, text_scor,
            nume_medalie, text_medalie, imagine_medalie, poza_semnul_intrebarii, poza_implicita;
    private ImageView pozaProfil;
    private Integer scor;
    private TextView numeBD, emailBD, scorBD;
    private int numara = 0, contor, nr_negative;
    private long timp_inceput = 0;
    private static final int cod_alegere_poza_profil = 123;
    private FirebaseUser utilizator;
    private ProgressDialog cercIncarca;
    private ListView lista_medalii;
    private ListaMedalii adaptorMedaliiPozitive, adaptorMedaliiNegative;
    private ArrayAdapter<Boolean> adaptor_obtinut;
    private DatabaseReference ref_fb;
    private String[] texte_medalii;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil_utilizator);

        cercIncarca = new ProgressDialog(ProfilUtilizator.this);
        cercIncarca.setMessage("Încărcăm profilul dumneavoastră...");
        cercIncarca.show();

        numeBD = findViewById(R.id.profil_nume);
        emailBD = findViewById(R.id.profil_email);
        scorBD = findViewById(R.id.profil_scor);
        pozaProfil = findViewById(R.id.profil_poza);

        fb_aut = FirebaseAuth.getInstance();
        utilizator = fb_aut.getCurrentUser();

        if(utilizator != null) {
            id_utilizator = utilizator.getUid();
        }

        lista_medalii = findViewById(R.id.profil_lista);

        lista_medalii.addFooterView(new View(ProfilUtilizator.this),
                null, true);

        lista_medalii.addHeaderView(new View(ProfilUtilizator.this),
                null, true);

        adaptorMedaliiPozitive = new ListaMedalii(this, R.layout.listview_medalii);
        adaptorMedaliiNegative = new ListaMedalii(this, R.layout.listview_medalii);

        poza_semnul_intrebarii = "https://firebasestorage.googleapis.com/v0/b/stiumate-3dda4." +
                "appspot.com/o/Medalii%2FSemnulIntrebarii" +
                ".png?alt=media&token=c2a437e3-0e16-4046-8966-1ff108ad8f45" ;

        poza_implicita = "https://firebasestorage.googleapis.com/v0/b/stiumate-3dda4" +
                ".appspot.com/o/Poze%20profil%2FFara_imagine" +
                ".jpg?alt=media&token=dd4a1b0a-7460-4132-91ac-619d6e9c0df7";

        text_nume = getResources().getString(R.string.nume_profil);
        text_email = getResources().getString(R.string.email_profil);
        text_scor = getResources().getString(R.string.scor_profil);

        if (id_utilizator != null) {
            ref_fb = FirebaseDatabase.getInstance().getReference();
            ref_fb.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot fb) {
                    if (adaptorMedaliiPozitive.getCount() > 0) {
                        adaptorMedaliiPozitive.clearData();
                        adaptorMedaliiPozitive.notifyDataSetChanged();
                    }

                    if (adaptorMedaliiNegative.getCount() > 0) {
                        adaptorMedaliiNegative.clearData();
                        adaptorMedaliiNegative.notifyDataSetChanged();
                    }

                    adaptor_obtinut = new ArrayAdapter<>(getApplicationContext(),
                            android.R.layout.simple_list_item_1);

                    if (adaptor_obtinut.getCount() > 0) {
                        adaptor_obtinut.clear();
                        adaptor_obtinut.notifyDataSetChanged();
                    }

                    DataSnapshot profil = fb.child("Utilizatori").child(id_utilizator);
                    nume = profil.child("Nume utilizator").getValue(String.class);
                    email = utilizator.getEmail();
                    scor = profil.child("Scor").getValue(Integer.class);
                    poza = profil.child("Poza profil").getValue(String.class);

                    DataSnapshot realizari = profil.child("Medalii");
                    for (DataSnapshot realizare : realizari.getChildren()) {
                        Boolean obtinut = realizare.getValue(Boolean.class);
                        adaptor_obtinut.add(obtinut);
                    }
                    texte_medalii = new String[adaptor_obtinut.getCount()];

                    String final_nume = text_nume + " " + nume;
                    numeBD.setText(final_nume);

                    String final_email = text_email + " " + email;
                    emailBD.setText(final_email);

                    String final_scor = text_scor + " " + valueOf(scor);
                    scorBD.setText(final_scor);

                    Glide.with(getApplicationContext()).load(poza).into(pozaProfil);

                    contor = 0;
                    nr_negative = 0;
                    DataSnapshot medalii = fb.child("Medalii");

                    for (DataSnapshot medalie : medalii.getChildren()) {
                        nume_medalie = medalie.child("Nume").getValue(String.class);
                        text_medalie = medalie.child("Text").getValue(String.class);
                        imagine_medalie = medalie.child("Imagine").getValue(String.class);

                        Boolean med = adaptor_obtinut.getItem(contor);
                        if (med != null) {
                            if (med) {
                                adaptorMedaliiPozitive.add(new Medalii(
                                        nume_medalie, text_medalie, imagine_medalie));
                            } else {
                                adaptorMedaliiNegative.add(new Medalii(
                                        nume_medalie, text_medalie, imagine_medalie));

                                texte_medalii[nr_negative] = text_medalie;
                                nr_negative++;
                            }
                        }
                        contor++;
                    }

                    int nr_negative = adaptorMedaliiNegative.getCount();
                    for (int i = 0; i < nr_negative; i++) {
                        adaptorMedaliiPozitive.add(new Medalii("???",
                                texte_medalii[i], poza_semnul_intrebarii));
                    }
                    lista_medalii.setAdapter(adaptorMedaliiPozitive);
                    cercIncarca.dismiss();
                }

                @Override
                public void onCancelled(DatabaseError eroare) {

                }
            });
        }

        pozaProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent,
                        "Completează acțiunea folosind "), cod_alegere_poza_profil);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == cod_alegere_poza_profil && resultCode == RESULT_OK) {
            Uri poza_selectata = data.getData();
            pozaProfil.setImageURI(poza_selectata);

            StorageReference adresa_poza = FirebaseStorage
                    .getInstance().getReference().child("Poze profil");

            if (poza_selectata != null) {
                adresa_poza = adresa_poza.child(poza_selectata.getLastPathSegment());
            }

            Toast.makeText(ProfilUtilizator.this,
                    "Vă încărcăm noua poză de profil...", Toast.LENGTH_SHORT).show();

            if (poza_selectata != null) {
                adresa_poza.putFile(poza_selectata).addOnSuccessListener(this,
                        new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Toast.makeText(ProfilUtilizator.this,
                                        "V-am încărcat poza de profil cu succes!",
                                        Toast.LENGTH_SHORT).show();

                                if(!poza_implicita.equals(poza)) {
                                    StorageReference poza_profil = FirebaseStorage
                                            .getInstance().getReferenceFromUrl(poza);

                                    poza_profil.delete();
                                }

                                Uri link_poza = taskSnapshot.getDownloadUrl();
                                String poza_setata = "";
                                if (link_poza != null) {
                                    poza_setata = link_poza.toString();
                                }

                                UserProfileChangeRequest profileUpdates =
                                        new UserProfileChangeRequest.Builder()
                                                .setPhotoUri(Uri.parse(poza_setata)).build();

                                utilizator.updateProfile(profileUpdates);
                                ref_fb.child("Utilizatori").child(id_utilizator)
                                        .child("Poza profil").setValue(poza_setata);
                            }
                        });
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profil_utilizator, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.setari) {
            startActivity(new Intent(ProfilUtilizator.this, ProfilSetari.class));
        }
        if (id == R.id.pag_principala){
            startActivity(new Intent(ProfilUtilizator.this, PagPrinc.class));
        }
        if (id == R.id.delogare) {
            fb_aut.signOut();
            startActivity(new Intent(ProfilUtilizator.this, PagPrinc.class));

            Toast.makeText(ProfilUtilizator.this,
                    "Ați ieșit din cont!", Toast.LENGTH_SHORT).show();

            finish();
        }
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent eveniment) {
        if (eveniment.getAction() == MotionEvent.ACTION_UP) {
            long timp_curent = System.currentTimeMillis();
            if (timp_inceput == 0 || (timp_curent - timp_inceput > 3000)) {
                timp_inceput = timp_curent;
                numara = 1;
            } else {
                numara++;
            }
            if (numara == 5) {
                Intent i = new Intent(ProfilUtilizator.this,
                        ProfesorVerificare.class);

                startActivity(i);
            }
            return true;
        }
        return false;
    }
}
