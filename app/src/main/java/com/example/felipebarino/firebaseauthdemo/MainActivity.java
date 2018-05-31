package com.example.felipebarino.firebaseauthdemo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonRegister;

    private EditText editTextName;
    private EditText editTextLastname;
    private EditText editTextEmail;
    private EditText editTextPassword;

    private TextView textViewSignin;
    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private FirebaseUser user;

    public UserInformation userInformation;
    public Device userDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();

        // Se já houver usuário logado, pular parte de Registro ou Logar
        if(firebaseAuth.getCurrentUser() != null){
            finish();
            startActivity(new Intent(getApplicationContext(), PrimaryActivity.class));
        }

        databaseReference = FirebaseDatabase.getInstance().getReference();

        progressDialog = new ProgressDialog(this);

        buttonRegister = (Button) findViewById(R.id.buttonRegister);

        editTextName = (EditText) findViewById(R.id.editTextName);
        editTextLastname = (EditText) findViewById(R.id.editTextLastname);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);

        textViewSignin = (TextView) findViewById(R.id.textViewSignin);

        buttonRegister.setOnClickListener(this);
        textViewSignin.setOnClickListener(this);

        userDevice = new Device("init");
    }

    private void saveUserInformation(String email, String password){
        // Pegar o nome e sobrenome
        String name = userInformation.getName();
        String lastname = userInformation.getLastname();

        // se não for vazio -> entra
        if( !(TextUtils.isEmpty(name) && TextUtils.isEmpty(lastname)) ) {
            //tenta fazer log in, para acessar seu banco de dados
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                // se conseguir
                                // pega quem é o usuário logado
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                // seta as informações para ele
                                DatabaseReference infoDatabase = databaseReference.child(user.getUid()).child("info");
                                // salva nome e sobrenome no banco de dados
                                infoDatabase.setValue(userInformation);

                                // cria o nó de dispositivos, com um genérico que indica vazio
                                DatabaseReference devicesDatabase = databaseReference.child(user.getUid()).child("devices");
                                String id = "0000";
                                devicesDatabase.child(id).setValue(userDevice);

                                Log.d("MainActivity", "UserLogin: success");
                                // vai para a atividade primária
                                finish();
                                startActivity(new Intent(getApplicationContext(), PrimaryActivity.class));
                            }else{
                                Log.w("MainActivity", "UserLogin: failure", task.getException());if(progressDialog.isShowing()) progressDialog.dismiss();
                                Toast.makeText(MainActivity.this, "Falha ao entrar", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }else{
            Toast.makeText(this, "Digite um nome e sobrenome", Toast.LENGTH_SHORT).show();
        }
    }

    private void registerUser(){
        // pega email e senha
        final String email = editTextEmail.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();
        userInformation = new UserInformation( editTextName.getText().toString().trim(), editTextLastname.getText().toString().trim());
        userDevice = new Device("inicial");

        // checa se estão vazios
        if(TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Digite o endereço de e-mail", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Digite a senha", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Registrando usuário...");
        progressDialog.show();
        // registra o usuário
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            if(progressDialog.isShowing()) progressDialog.dismiss();
                            Log.d("MainActivity", "createUser:success");
                            // salva as informações no banco de dados
                            saveUserInformation(email, password);
                        }else{
                            if(progressDialog.isShowing()) progressDialog.dismiss();
                            Log.w("MainActivity", "createUser:failure", task.getException());
                            // Lida com os erros:
                            if(task.getException() instanceof FirebaseAuthUserCollisionException){
                                Toast.makeText(MainActivity.this, "E-mail já cadastrado", Toast.LENGTH_SHORT).show();
                            }else if(task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                                Toast.makeText(MainActivity.this, "Favor checar o E-mail digitado", Toast.LENGTH_SHORT).show();
                            }else if(task.getException() instanceof FirebaseAuthWeakPasswordException){
                                Toast.makeText(MainActivity.this, "A senha deve ter pelo menos seis caracteres", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(MainActivity.this, "Falha ao registrar", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }


    @Override
    public void onClick(View view){
        if(view == buttonRegister) {
            registerUser();
        }
        if(view == textViewSignin){
            // Vai pra tela de log in
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }
}
