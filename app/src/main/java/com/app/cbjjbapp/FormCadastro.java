package com.app.cbjjbapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FormCadastro extends AppCompatActivity {
    private EditText edit_nome, edit_email, edit_senha, edit_doc, edit_equipe, edit_tecnico, edit_nascimento, edit_genero, edit_peso, edit_telefone;
    private Spinner edit_faixa, edit_grad;
    private Button bt_cadastrar;
    private Button select_photo;
    private ImageView imgPhoto;
    private Uri selectedUri;

    String[] faixa = {"BRANCA", "CINZA", "AMARELA", "LARANJA","VERDE", "AZUL", "ROXA", "MARROM", "PRETA", "CORAL", "VERMELHA"};
    String[] graduacao = {"ALUNO", "ATLETA", "PRATICANTE", "MONITOR", "INSTRUTOR", "PROFESSOR", "MESTRE", "GRANDE MESTRE"};

    String[] mensagens = {"PREENCHA TODOS OS CAMPOS", "CADASTRO REALIZADO COM SUCESSO"};
    String usuarioID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_cadastro);

        getSupportActionBar().hide();
        IniciarComponentes();
        bt_cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nome = edit_nome.getText().toString();
                String email = edit_email.getText().toString();
                String senha = edit_senha.getText().toString();
                String doc =  edit_doc.getText().toString();
                String equipe = edit_equipe.getText().toString();
                String tecnico = edit_tecnico.getText().toString();
                String nascimento = edit_nascimento.getText().toString();
                String genero = edit_genero.getText().toString();
                String peso = edit_peso.getText().toString();
                String telefone = edit_telefone.getText().toString();
                String faixa = edit_faixa.getSelectedItem().toString();
                String graduacao = edit_grad.getSelectedItem().toString();




                if(nome.isEmpty()|| email.isEmpty()|| senha.isEmpty()||doc.isEmpty()||equipe.isEmpty()||tecnico.isEmpty()||nascimento.isEmpty()||genero.isEmpty()||peso.isEmpty()||graduacao.isEmpty()||telefone.isEmpty()||faixa.isEmpty()){
                    Snackbar snackbar= Snackbar.make(view,mensagens[0], Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                }else{
                    CadastrarUsuario(view);
                }
            }
        });
    }

    private void CadastrarUsuario(View view){

        String email = edit_email.getText().toString();
        String senha = edit_senha.getText().toString();

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    SalvarDadosUsuario();

                    Snackbar snackbar= Snackbar.make(view,mensagens[1], Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                }else{
                    String erro;
                    try {
                        throw task.getException();

                    }catch (FirebaseAuthWeakPasswordException e){
                        erro = "DIGITE UMA SENHA COM NO MINIMO 6 CARACTERES";

                    }catch(FirebaseAuthUserCollisionException e ){
                        erro = "ESTE E-MAIL JÁ FOI CADASTRADO";

                    }catch(FirebaseAuthInvalidCredentialsException e){
                        erro = "EMAIL INVALIDO";

                    }catch(Exception e){
                        erro = "ERRO AO CADASTRAR USUARIO";
                    }
                    Snackbar snackbar= Snackbar.make(view,erro, Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();

                }
            }
        });

    }


    private void SalvarDadosUsuario(){
        String nome = edit_nome.getText().toString();
        String email = edit_email.getText().toString();
        String senha = edit_senha.getText().toString();
        String doc = edit_doc.getText().toString();
        String equipe = edit_equipe.getText().toString();
        String tecnico = edit_tecnico.getText().toString();
        String nascimento = edit_nascimento.getText().toString();
        String genero = edit_genero.getText().toString();
        String peso = edit_peso.getText().toString();
        String telefone = edit_telefone.getText().toString();
        String faixa = edit_faixa.getSelectedItem().toString();
        String graduacao = edit_grad.getSelectedItem().toString();



        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> usuarios = new HashMap<>();
        usuarios.put("nome", nome);
        usuarios.put("email", email);
        usuarios.put("senha", senha);
        usuarios.put("doc", doc);
        usuarios.put("equipe", equipe);
        usuarios.put("tecnico", tecnico);
        usuarios.put("nascimento", nascimento);
        usuarios.put("gênero", genero);
        usuarios.put("peso", peso);
        usuarios.put("telefone", telefone);
        usuarios.put("faixa", faixa);
        usuarios.put("graduacao", graduacao);


        usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference documentReference = db.collection("Usuarios").document(usuarioID);
        documentReference.set(usuarios).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("db", "Sucesso ao salvar os dados" );
                    }
                })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.d("db_error", "Erro ao salvar os dados" + e.toString());
                    }
                });


    }


    private void IniciarComponentes(){
        edit_nome = findViewById(R.id.edit_nome);
        edit_email = findViewById(R.id.edit_email);
        edit_senha = findViewById(R.id.edit_senha);
        edit_doc = findViewById(R.id.edit_doc);
        edit_equipe = findViewById(R.id.edit_equipe);
        edit_tecnico = findViewById(R.id.edit_tecnico);
        edit_nascimento = findViewById(R.id.edit_nascimento);
        edit_genero = findViewById(R.id.edit_genero);
        edit_peso = findViewById(R.id.edit_peso);
        edit_telefone = findViewById(R.id.edit_telefone);
        edit_faixa = findViewById(R.id.edit_faixa);
        edit_grad = findViewById(R.id.edit_grad);
        bt_cadastrar = findViewById(R.id.bt_cadastrar);

        ArrayAdapter<String> adapterFaixa = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item, faixa);
        edit_faixa.setAdapter(adapterFaixa);


        ArrayAdapter<String> adapterGrad = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item, graduacao);
        edit_grad.setAdapter(adapterGrad);

    };
}