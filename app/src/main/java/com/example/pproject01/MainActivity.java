package com.example.pproject01;


import android.Manifest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    // 권한 요청 코드를 정의
    private static final int SMS_PERMISSION_REQUEST_CODE = 101;
    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        databaseReference = FirebaseDatabase.getInstance().getReference("raspberrypi/sensor");

        // SMS 권한요청
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    SMS_PERMISSION_REQUEST_CODE);
        }
        //알림 권한요청
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},101
                    );
        }

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("MainActivity", "Data changed: " + dataSnapshot.getValue());
                sendSMS("01092804875", "test02");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 데이터베이스 읽기가 취소되었을 때 호출됨
                Log.w("MainActivity", "Failed to read value.", databaseError.toException());
            }
        };
        databaseReference.addValueEventListener(valueEventListener);

        Button Button01 = findViewById(R.id.button);
        Button01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                sendSMS("01092804875", "test01");
                //TODO - list로 추가된 전화번호 for문돌리면서 여러명한테 문자전송
            }
        });

        Button Button02 = findViewById(R.id.button2);
        Button02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 버튼이 클릭되었을 때 Firebase Database에 정보를 올리는 메서드 호출
                uploadDataToFirebase("Hello, Firebase!!");
            }
        });
    }

    private void sendSMS(String phoneNumber, String message){
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(getApplicationContext(), "SMS sent successfully", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "SMS sending failed", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void uploadDataToFirebase(String data) {
        // Firebase Database에 데이터를 쓰기
        // "your_data_path"에 해당하는 경로에 데이터를 쓰는 예제
        databaseReference.child("/hello1").setValue(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // 데이터 쓰기 성공 시 동작
                        Log.d("MainActivity", "Data uploaded");
                        // 필요에 따라 추가적인 동작을 수행할 수 있습니다.
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // 데이터 쓰기 실패 시 동작
                        Log.d("MainActivity", "Data upload failed");
                        // 오류 처리를 추가할 수 있습니다.
                    }
                });
    }
}

