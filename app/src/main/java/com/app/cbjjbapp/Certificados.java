package com.app.cbjjbapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class Certificados extends AppCompatActivity {
    // private Button bt_enviar, bt_voltar;
    private ImageView img_cert;
    StorageReference storageReference;
    FirebaseAuth fAuth;

    //floating menu
    FloatingActionButton bt_enviar, bt_voltar, floatingActionButton;
    Animation rotateOpen, rotateClose, fromBottom, toBottom;
    boolean isOpen = true;
    //floating menu


    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String usuarioID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certificados);

        getSupportActionBar().hide();
        IniciarComponentes();


        storageReference = FirebaseStorage.getInstance().getReference();
        fAuth = FirebaseAuth.getInstance();

        //setar clickListener ao botao float principal
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateFab();

            }
        });

        StorageReference profileRef = storageReference.child("certificados/" + fAuth.getCurrentUser().getUid() + "/certificado.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(img_cert);
            }
        });


        bt_voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                animateFab();

                Intent intent = new Intent(Certificados.this, TelaPrincipal.class);
                startActivity(intent);
                finish();
            }
        });


        //abrir galeria pra foto
        bt_enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateFab();

                Intent openGaleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGaleryIntent, 1000);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {
                Uri imageUri = data.getData();
                uploadImageToFirebase(imageUri);

            }
        }
    }

    private void animateFab() {
        if (isOpen) {
            floatingActionButton.startAnimation(rotateOpen);
            bt_enviar.startAnimation(fromBottom);
            bt_voltar.startAnimation(fromBottom);
            //  bt_deslogar.startAnimation(fromBottom);

            //bt_enviar, bt_voltar

            bt_enviar.setClickable(true);
            bt_voltar.setClickable(true);
            //  bt_deslogar.setClickable(true);
            isOpen = false;
        } else {
            floatingActionButton.startAnimation(rotateClose);
            bt_voltar.startAnimation(toBottom);
            bt_enviar.startAnimation(toBottom);
            // bt_deslogar.startAnimation(toBottom);

            bt_voltar.setClickable(false);
            bt_enviar.setClickable(false);
            //   bt_deslogar.setClickable(false);
            isOpen = true;
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {

        StorageReference fileRef = storageReference.child("certificados/" + fAuth.getCurrentUser().getUid() + "/certificado.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(img_cert);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Certificados.this, "Envio Falhou!", Toast.LENGTH_SHORT).show();

            }
        });
    }


/////doc



 /*   @Override
    protected void onStart() {
        super.onStart();

        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference documentReference = db.collection("Usuarios").document(usuarioID);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {

                if(documentSnapshot != null){
                    nomeUsuario.setText(documentSnapshot.getString("nome"));
                    faixaUsuario.setText(documentSnapshot.getString("faixa"));
                    gradUsuario.setText(documentSnapshot.getString("graduacao"));
                    pesoUsuario.setText(documentSnapshot.getString("peso"));
                    equipeUsuario.setText(documentSnapshot.getString("equipe"));
                    validacaoUsuario.setText(documentSnapshot.getString("validacao"));
                    generoUsuario.setText(documentSnapshot.getString("gênero"));
                }
            }
        });
    }*/


    private void IniciarComponentes() {
        img_cert = findViewById(R.id.img_cert);
        bt_voltar = findViewById(R.id.bt_voltar);
        bt_enviar = findViewById(R.id.bt_enviar);

        //floating menu
        floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        bt_voltar = (FloatingActionButton) findViewById(R.id.bt_voltar);
        bt_enviar = (FloatingActionButton) findViewById(R.id.bt_enviar);
        ///  mudDoc = (FloatingActionButton) findViewById(R.id.bt_doc);

        //animação
        rotateOpen = AnimationUtils.loadAnimation(this, R.anim.rotate_open_anim);
        rotateClose = AnimationUtils.loadAnimation(this, R.anim.rotate_close_anim);
        fromBottom = AnimationUtils.loadAnimation(this, R.anim.from_bottom_anim);
        toBottom = AnimationUtils.loadAnimation(this, R.anim.to_bottom_anim);
    }
}