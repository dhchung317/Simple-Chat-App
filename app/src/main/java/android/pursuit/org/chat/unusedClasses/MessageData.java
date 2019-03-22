package android.pursuit.org.chat.unusedClasses;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;

public class MessageData {
    private String text;
    private String senderId;

    public MessageData(@NonNull DataSnapshot children) {
        text = children.child("message").getValue(String.class);
        senderId = children.child("uid_sending").getValue(String.class);
    }

    public String getSenderId() {
        return senderId;
    }

    public String getText() {
        return text;
    }
}
