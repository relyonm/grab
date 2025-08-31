package com.relyonm.grab.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class NotificationService {

  public void sendNotification(String token, String title, String body) {
    Message message = Message
      .builder()
      .setToken(token)
      .setNotification(Notification
        .builder()
        .setTitle(title)
        .setBody(body)
        .build())
      .build();

    try {
      FirebaseMessaging.getInstance().sendAsync(message).get();
    } catch (InterruptedException | ExecutionException e) {
      throw new RuntimeException("Failed to send FCM message", e);
    }
  }
}
