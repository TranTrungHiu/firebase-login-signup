package com.example.baitap01;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

public class HomeActivity extends Activity {
    private TextView tvEmail, tvStatus;
    private Button btnSendLink;
    private FirebaseAuth auth;
    private String email;
    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);

        // Lấy thông tin email từ Intent
        email = getIntent().getStringExtra("USER_EMAIL");

        // Khởi tạo VideoView
        videoView = findViewById(R.id.videoView);
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        // Đặt đường dẫn video từ tài nguyên raw
        String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.sample_video;
        videoView.setVideoPath(videoPath);
        videoView.start();

        // Khởi tạo FirebaseAuth
        auth = FirebaseAuth.getInstance();

        // Ánh xạ các view
        tvEmail = findViewById(R.id.tvEmail);
        tvStatus = findViewById(R.id.tvStatus);
        btnSendLink = findViewById(R.id.btnSendLink);

        // Hiển thị email đang đăng nhập
        tvEmail.setText("Email đang đăng nhập: " + email);

        // Xử lý sự kiện khi nhấn nút gửi liên kết Dynamic Link
        btnSendLink.setOnClickListener(v -> sendVerificationLink(email));
    }

    // Phương thức gửi liên kết Dynamic Link
    private void sendVerificationLink(String email) {
        // Tạo Dynamic Link trỏ đến URL xác thực
        String dynamicLinkUrl = "https://baitap01.page.link/verifyEmail?email=" + email; // Liên kết Dynamic cần cập nhật

        // Tạo ActionCodeSettings với Dynamic Link
        ActionCodeSettings actionCodeSettings = ActionCodeSettings.newBuilder()
                .setUrl(dynamicLinkUrl)  // URL mà người dùng sẽ nhấp vào
                .setHandleCodeInApp(true)
                .build();

        // Gửi Dynamic Link qua email
        auth.sendSignInLinkToEmail(email, actionCodeSettings)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        tvStatus.setText("Liên kết xác thực đã được gửi qua Dynamic Link. Vui lòng kiểm tra email.");
                    } else {
                        tvStatus.setText("Gửi liên kết xác thực thất bại.");
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Kiểm tra Dynamic Link khi người dùng mở ứng dụng từ liên kết
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, pendingDynamicLinkData -> {
                    if (pendingDynamicLinkData != null) {
                        Uri deepLink = pendingDynamicLinkData.getLink();
                        if (deepLink != null) {
                            String email = deepLink.getQueryParameter("email");  // Lấy email từ URL
                            if (email != null) {
                                // Hiển thị thông báo xác thực email hoặc thông báo thành công
                                tvStatus.setText("Email đã được xác thực: " + email);
                            }
                        }
                    }
                })
                .addOnFailureListener(this, e -> {
                    // Xử lý lỗi nếu không có Dynamic Link
                    tvStatus.setText("Lỗi khi nhận Dynamic Link.");
                });
    }
}
