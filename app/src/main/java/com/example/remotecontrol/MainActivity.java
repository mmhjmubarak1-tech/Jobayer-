package com.example.remotecontrol;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {
    private static final int CAMERA_PERMISSION_CODE = 100;
    private Button startBtn, stopBtn, copyLinkBtn;
    private TextView statusText;
    private EditText linkText;
    private CameraServer cameraServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startBtn = findViewById(R.id.startCameraBtn);
        stopBtn = findViewById(R.id.stopCameraBtn);
        copyLinkBtn = findViewById(R.id.copyLinkBtn);
        statusText = findViewById(R.id.statusText);
        linkText = findViewById(R.id.linkText);

        cameraServer = new CameraServer(this);

        startBtn.setOnClickListener(v -> startCamera());
        stopBtn.setOnClickListener(v -> stopCamera());
        copyLinkBtn.setOnClickListener(v -> copyLink());

        checkPermissions();
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.INTERNET},
                        CAMERA_PERMISSION_CODE);
            }
        }
    }

    private void startCamera() {
        try {
            cameraServer.startServer();
            statusText.setText("Running: " + cameraServer.getServerURL());
            linkText.setText(cameraServer.getServerURL());
            Toast.makeText(this, "Server started! Share the link.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            statusText.setText("Error: " + e.getMessage());
            Toast.makeText(this, "Failed to start", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopCamera() {
        cameraServer.stopServer();
        statusText.setText("Stopped");
        linkText.setText("");
        Toast.makeText(this, "Server stopped!", Toast.LENGTH_SHORT).show();
    }

    private void copyLink() {
        String link = linkText.getText().toString();
        if (!link.isEmpty()) {
            android.content.ClipboardManager clipboard =
                    (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Remote Link", link);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "Link copied to clipboard!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permissions granted!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraServer.stopServer();
    }
}
