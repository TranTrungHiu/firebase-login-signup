package com.example.baitap01;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends Activity {

    private static final int RC_SIGN_IN = 123;
    private FirebaseAuth auth;
    private EditText username, password;
    private Button loginButton;
    private Button registerButton;
    private GoogleSignInClient GoogleSignInClient;
    private Button loginByGoogleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_form);

        auth = FirebaseAuth.getInstance();
        username = findViewById(R.id.input_username);
        password = findViewById(R.id.input_password);
        loginButton = findViewById(R.id.button_signin);
        registerButton = findViewById(R.id.button_signup);
        loginByGoogleButton = findViewById(R.id.button_loginbygooogle);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("396676757625-988n9scuh052alvcejrl723ks92s6teg.apps.googleusercontent.com") // ID token từ google-services.json
                .requestEmail()
                .build();
        // Khởi tạo GoogleSignInClient
        GoogleSignInClient = GoogleSignIn.getClient(this, gso);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = username.getText().toString();
                String pass = password.getText().toString();
                if (email.isEmpty() || pass.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "PLEASE ENTER EMAIL AND PASSWORD", Toast.LENGTH_SHORT).show();
                } else {
                    login(email, pass);
                }
            }
        });
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        loginByGoogleButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                signInByGoogle();
            }
        });
    }

    private void login(String email, String pass) {
        auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(this, task ->
        {
            if (task.isSuccessful()) {
                FirebaseUser user = auth.getCurrentUser();
                Toast.makeText(this, "LOGIN COMPLETE", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                intent.putExtra("USER_EMAIL", user.getEmail());
                startActivity(intent);
            } else {
                Toast.makeText(this, "LOGIN ERROR", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void signInByGoogle() {
        Intent signInIntent = GoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Đăng nhập thành công, lấy tài khoản Google
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Đăng nhập thất bại
                Toast.makeText(this, "Google sign-in failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Đăng nhập thành công
                        FirebaseUser user = auth.getCurrentUser();
                        Toast.makeText(this, "Welcome " + user.getEmail(), Toast.LENGTH_SHORT).show();

                        // Chuyển sang màn hình chính
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        intent.putExtra("USER_EMAIL", user.getEmail());
                        startActivity(intent);
                    } else {
                        // Đăng nhập thất bại
                        Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }


}