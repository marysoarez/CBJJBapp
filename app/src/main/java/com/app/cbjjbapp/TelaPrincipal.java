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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;


public class TelaPrincipal extends AppCompatActivity {
    private TextView nomeUsuario, faixaUsuario, gradUsuario, pesoUsuario, equipeUsuario, generoUsuario, validacaoUsuario;

    //floating menu
    FloatingActionButton bt_deslogar, mudFotoPerfil, mudDoc, floatingActionButton;
    Animation rotateOpen, rotateClose, fromBottom, toBottom;
    boolean isOpen = true;
    //floating menu


    private ImageView profileImage;
    StorageReference storageReference;
    FirebaseAuth fAuth;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String usuarioID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_principal);

        getSupportActionBar().hide();
        IniciarComponentes();


        storageReference = FirebaseStorage.getInstance().getReference();
        fAuth = FirebaseAuth.getInstance();

        StorageReference profileRef = storageReference.child("usuarios/" + fAuth.getCurrentUser().getUid() + "/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImage);
            }
        });


        //setar clickListener ao botao float principal
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateFab();

            }
        });

        //deslogar
        bt_deslogar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateFab();
                FirebaseAuth.getInstance().signOut();

                Intent intent = new Intent(TelaPrincipal.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });


        //mudar documento
        mudDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateFab();

                /* FirebaseAuth.getInstance().signOut();
                 */
                Intent intent = new Intent(TelaPrincipal.this, Certificados.class);
                startActivity(intent);
                finish();
            }
        });

        //abrir galeria pra foto
        mudFotoPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateFab();

                Intent openGaleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGaleryIntent, 1000);
            }
        });


    }

    private void animateFab(){
        if (isOpen){
            floatingActionButton.startAnimation(rotateOpen);
            mudFotoPerfil.startAnimation(fromBottom);
            mudDoc.startAnimation(fromBottom);
            bt_deslogar.startAnimation(fromBottom);

            mudFotoPerfil.setClickable(true);
            mudDoc.setClickable(true);
            bt_deslogar.setClickable(true);
            isOpen=false;
        }
        else{
            floatingActionButton.startAnimation(rotateClose);
            mudFotoPerfil.startAnimation(toBottom);
            mudDoc.startAnimation(toBottom);
            bt_deslogar.startAnimation(toBottom);

            mudFotoPerfil.setClickable(false);
            mudDoc.setClickable(false);
            bt_deslogar.setClickable(false);
            isOpen=true;
        }
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

    private void uploadImageToFirebase(Uri imageUri) {

        //upload image to storage

        StorageReference fileRef = storageReference.child("usuarios/" + fAuth.getCurrentUser().getUid() + "/profile.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(profileImage);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(TelaPrincipal.this, "Envio Falhou!", Toast.LENGTH_SHORT).show();

            }
        });
    }










    @Override
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
    }

    private void IniciarComponentes(){
        nomeUsuario = findViewById(R.id.textNomeUsuario);
        gradUsuario = findViewById(R.id.textgraduacao);
        faixaUsuario = findViewById(R.id.textFaixa);
        pesoUsuario = findViewById(R.id.text_categoria);
        equipeUsuario = findViewById(R.id.text_equipe);
        validacaoUsuario = findViewById(R.id.validacao);
        generoUsuario = findViewById(R.id.text_genero);
        profileImage = findViewById(R.id.profilepic);

        //floating menu
        floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        bt_deslogar = (FloatingActionButton) findViewById(R.id.bt_deslogar);
        mudFotoPerfil =(FloatingActionButton) findViewById(R.id.bt_ftPerfil);
        mudDoc = (FloatingActionButton) findViewById(R.id.bt_doc);

        //animação
        rotateOpen = AnimationUtils.loadAnimation(this, R.anim.rotate_open_anim);
        rotateClose = AnimationUtils.loadAnimation(this, R.anim.rotate_close_anim);
        fromBottom = AnimationUtils.loadAnimation(this, R.anim.from_bottom_anim);
        toBottom = AnimationUtils.loadAnimation(this, R.anim.to_bottom_anim);
    }

}