package com.example.pproject01;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";
    int count=0;

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        // 기기 토큰이 갱신될 때 호출되는 메서드
        Log.d(TAG, "Refreshed token: " + token);

        // 여기에서 서버로 토큰을 전송하는 코드를 추가하세요.
        sendRegistrationToServer(token);
    }

    // sendRegistrationToServer 메서드는 이미 이전에 설명한 바와 같이 서버로 토큰을 전송하는 로직을 구현합니다.
    private void sendRegistrationToServer(String token) {
        // Firebase Realtime Database에 토큰 저장
        count++;
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("tokens");
        databaseReference.child("user_"+count).setValue(token);
        Log.d(TAG, "Token saved to Firebase Realtime Database: " + token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            // 여기서 메시지를 처리하거나 사용자에게 알림을 표시하는 로직을 추가하세요.
            sendNotification(remoteMessage.getData().get("message"));
        }
    }

    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, MainActivity.class); // 알림을 클릭했을 때 실행될 액티비티 지정
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = "Your_Channel_ID";
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle("FCM Message")
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Android Oreo 이상에서는 채널을 설정해야 합니다.
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
