package tutorial.firebase.com.bontsi.firapp;

import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import tutorial.firebase.com.bontsi.firapp.firebase.FirebaseHelper;
import tutorial.firebase.com.bontsi.firapp.model.Spacecraft;
import tutorial.firebase.com.bontsi.firapp.model.User;

public class MainActivity extends AppCompatActivity  implements  View.OnClickListener{

 private static final String TAG = "MainActivity";
 private TextView txtStatus;
 private TextView txtDetail;
 private EditText edtEmail;
 private EditText edtPassword;
 private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtStatus = (TextView) findViewById(R.id.status);
        txtDetail = (TextView) findViewById(R.id.detail);
        edtEmail = (EditText) findViewById(R.id.edt_email);
        edtPassword = (EditText) findViewById(R.id.edt_password);

        findViewById(R.id.btn_email_sign_in).setOnClickListener(this);
        findViewById(R.id.btn_email_create_account).setOnClickListener(this);
        findViewById(R.id.btn_sign_out).setOnClickListener(this);
        findViewById(R.id.btn_test_message).setOnClickListener(this);

        auth = FirebaseAuth.getInstance();

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = auth.getCurrentUser();
        updateUI(user);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();

        if(i== R.id.btn_email_create_account){
            createAccount(edtEmail.getText().toString(),edtPassword.getText().toString());
        }else if(i== R.id.btn_email_sign_in){
            signIn(edtEmail.getText().toString(),edtPassword.getText().toString());
        }else if (i==R.id.btn_sign_out){
            signOut();
        }else if(i== R.id.btn_test_message){
            testMessage();
        }
    }

    private void createAccount(String email, final String password){

        Log.e(TAG,"createAccount:"+email);

        if(!validateForm(email,password)){
            return;
        }

        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){
                    Log.e(TAG,"createAccount: Success!");
                   final FirebaseUser user = auth.getCurrentUser();
                    updateUI(user);
                    writeNewUser(user.getUid(),getUsernameFromEmail(user.getEmail()),user.getEmail(),password);
                    sendEmailVerification();
                }else {
                    Log.e(TAG,"createAccount: Fail",task.getException());
                    Toast.makeText(MainActivity.this,"Account Creation Failed",Toast.LENGTH_SHORT).show();
                    updateUI(null);
                }
            }
        });
    }

    private void sendEmailVerification() {
        // Disable Verify Email button
        final FirebaseUser user = auth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // Re-enable Verify Email button
                         if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Verification email sent to " + user.getEmail(), Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "sendEmailVerification failed!", task.getException());
                            Toast.makeText(getApplicationContext(), "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void signIn(String email, String password){
        Log.e(TAG,"signIn"+email);

        if(!validateForm(email,password)){
            return;
        }

        auth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()){
                            Log.e(TAG,"signIn : Succes!");

                            FirebaseUser user = auth.getCurrentUser();
                            updateUI(user);
                        }else {
                            Log.e(TAG,"signIn: Fail",task.getException());
                            Toast.makeText(MainActivity.this,"Authentication Failed",Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        if (!task.isSuccessful()){
                            txtStatus.setText("Authentication failed!");
                        }
                    }
                });
        }

        private void signOut(){
            auth.signOut();
            updateUI(null);
        }
    private void writeNewUser(String userId,String username,String email,String password){
        User user = new User(username,email,password);
        FirebaseDatabase.getInstance().getReference().child("user").child(userId).setValue(user);
    }

    private boolean validateForm(String email,String password){
        if(TextUtils.isEmpty(email)){
            Toast.makeText(MainActivity.this,"Enter Email",Toast.LENGTH_SHORT).show();
            return false;
        }

        if(TextUtils.isEmpty(password)){
            Toast.makeText(MainActivity.this,"Enter password",Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(MainActivity.this,"Envalid Email",Toast.LENGTH_SHORT).show();
            return false;
        }

        if(password.length()<6){
            Toast.makeText(MainActivity.this,"Password too short",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            txtStatus.setText("User Email: " + user.getEmail());
            txtDetail.setText("Firebase User ID: " + user.getUid());

            findViewById(R.id.email_password_buttons).setVisibility(View.GONE);
            findViewById(R.id.email_password_fields).setVisibility(View.GONE);
            findViewById(R.id.layout_signed_in_control).setVisibility(View.VISIBLE);

        } else {
            txtStatus.setText("Signed Out");
            txtDetail.setText(null);

            findViewById(R.id.email_password_buttons).setVisibility(View.VISIBLE);
            findViewById(R.id.email_password_fields).setVisibility(View.VISIBLE);
            findViewById(R.id.layout_signed_in_control).setVisibility(View.GONE);
        }
    }

    private String getUsernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }

    private void testMessage() {
        startActivity(new Intent(this, MessageActivity.class));
    }

}
