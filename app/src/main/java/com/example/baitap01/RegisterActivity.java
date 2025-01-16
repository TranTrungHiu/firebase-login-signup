package com.example.baitap01;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class RegisterActivity extends Activity {

    private FirebaseAuth auth;
    private EditText username, password, repeatpass, phoneNumber, otpInput;
    private Button registerButton, resendOtpButton;
    private String verificationId;
    private int resendTime = 60; // Thời gian đếm ngược (tính bằng giây)
    private boolean canResendOtp = true; // Biến kiểm tra quyền gửi OTP

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_form);

        auth = FirebaseAuth.getInstance();
        username = findViewById(R.id.input_username_register);
        password = findViewById(R.id.input_password_register);
        repeatpass = findViewById(R.id.input_repeat_password_register);
        phoneNumber = findViewById(R.id.input_phonenumber_register);
        otpInput = findViewById(R.id.input_otp);
        registerButton = findViewById(R.id.button_register);
        resendOtpButton = findViewById(R.id.button_send_otp);

        resendOtpButton.setOnClickListener(v -> {
            if (canResendOtp) {
                String phone = phoneNumber.getText().toString();
                if (!TextUtils.isEmpty(phone)) {
                    sendVerificationCode(phone);
                    startResendTimer();
                } else {
                    Toast.makeText(RegisterActivity.this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
                }
            }
        });

        registerButton.setOnClickListener(v -> {
            String email = username.getText().toString();
            String passwordText = password.getText().toString();
            String repeatPassword = repeatpass.getText().toString();
            String phone = phoneNumber.getText().toString();
            String otp = otpInput.getText().toString();
            if (email.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "KHÔNG ĐƯỢC BỎ TRỐNG EMAIL", Toast.LENGTH_SHORT).show();
            } else if (!isValidEmail(email)) {
                Toast.makeText(RegisterActivity.this, "EMAIL KHÔNG HỢP LỆ", Toast.LENGTH_SHORT).show();
            } else if (passwordText.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "KHÔNG ĐƯỢC BỎ TRỐNG PASSWORD", Toast.LENGTH_SHORT).show();
            } else if (passwordText.length() < 6) {
                Toast.makeText(RegisterActivity.this, "PASSWORD PHẢI CÓ ÍT NHẤT 6 KÝ TỰ", Toast.LENGTH_SHORT).show();
            } else if (!passwordText.equals(repeatPassword)) {
                Toast.makeText(RegisterActivity.this, "MẬT KHẨU NHẬP LẠI KHÔNG GIỐNG", Toast.LENGTH_SHORT).show();
            } else if (phone.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "VUI LÒNG NHẬP SỐ ĐIỆN THOẠI+84", Toast.LENGTH_SHORT).show();
            } else if (!TextUtils.isEmpty(otp)) {
                verifyOtp(otp);
            } else {
                Toast.makeText(RegisterActivity.this, "VUI LÒNG NHẬP OTP", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void startResendTimer() {
        new CountDownTimer(resendTime * 1000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                resendOtpButton.setText(String.format("Resend OTP (%ds)", millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                resendOtpButton.setText("Resend OTP");
                resendOtpButton.setEnabled(true);
                resendOtpButton.setBackgroundResource(R.drawable.rounded_corner); // Đặt màu nút lại
                canResendOtp = true;
            }
        }.start();

        resendOtpButton.setEnabled(false);
        resendOtpButton.setBackgroundResource(R.drawable.rounded_white); // Đổi màu nút
        canResendOtp = false;
    }

    private void sendVerificationCode(String phone) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone,
                60,
                TimeUnit.SECONDS,
                this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                        signInWithPhoneAuthCredential(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        Toast.makeText(RegisterActivity.this, "Verification failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(verificationId, forceResendingToken);
                        RegisterActivity.this.verificationId = verificationId;
                        Toast.makeText(RegisterActivity.this, "OTP sent", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void verifyOtp(String otp) {
        if (verificationId != null) {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);
            signInWithPhoneAuthCredential(credential);
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                Toast.makeText(RegisterActivity.this, "Authentication successful", Toast.LENGTH_SHORT).show();
                // Proceed to register the user
                String email = username.getText().toString();
                String pass = password.getText().toString();
                register(email, pass);
            } else {
                Toast.makeText(RegisterActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(RegisterActivity.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void register(String email, String pass) {
        if (isValidEmail(email) && !TextUtils.isEmpty(pass) && pass.equals(repeatpass.getText().toString())) {
            auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "REGISTER COMPLETE", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, "REGISTER ERROR, PLEASE TRY AGAIN", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(RegisterActivity.this, "Invalid email or passwords don't match", Toast.LENGTH_SHORT).show();
        }
    }

}
