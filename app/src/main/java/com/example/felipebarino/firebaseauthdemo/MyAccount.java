package com.example.felipebarino.firebaseauthdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MyAccount extends AppCompatActivity implements View.OnClickListener {

    private Button buttonChangePassword;
    private Button buttonCancel;
    private EditText editTextOldPassword;
    private EditText editTextNewPassword;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private DatabaseReference databaseReference, userDatabase, infoDatabase, devicesDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        buttonChangePassword = (Button) findViewById(R.id.buttonConfirm);
        buttonChangePassword.setOnClickListener(this);

        buttonCancel = (Button) findViewById(R.id.buttonCancel);
        buttonCancel.setOnClickListener(this);

        editTextOldPassword = (EditText) findViewById(R.id.editTextOldPassword);
        editTextNewPassword = (EditText) findViewById(R.id.editTextNewPassword);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        if(user == null){
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        // pega o banco de dados
        databaseReference = FirebaseDatabase.getInstance().getReference();
        // pega o banco de dados do usuário, informações e dispositivos
        userDatabase = databaseReference.child(user.getUid());
        infoDatabase = userDatabase.child("info");
    }

    @Override
    public void onClick(View view) {
        if(view == buttonChangePassword){
            changePassword();
            Toast.makeText(this, "Senha trocada com sucesso!", Toast.LENGTH_SHORT).show();
            finish();
            startActivity(new Intent(this, PrimaryActivity.class));
        }
        if (view == buttonCancel){
            finish();
            startActivity(new Intent(this, PrimaryActivity.class));
        }
    }

    private void changePassword() {
        final String password, newPass;
        password = editTextOldPassword.getText().toString().trim();
        newPass = editTextNewPassword.getText().toString().trim();

        AuthCredential credential = EmailAuthProvider
                .getCredential(user.getEmail(), password);

        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            user.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("Change password: ", "Password updated");
                                    } else {
                                        Log.d("Change password: ", "Error password not updated");
                                    }
                                }
                            });
                        } else {
                            Log.d("Change password: ", "Error auth failed");
                        }
                    }
                });
    }
}
