package android.pursuit.org.chat.unusedClasses;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;

public class ChatMessage {
    private String senderName;
    private String recipientName;
    private String userImageUrl;
    private String text;

    public ChatMessage(
            @NonNull String senderName,
            @NonNull String recipientName,
            @NonNull String userImageUrl,
            @NonNull String text) {

        this.senderName = senderName;
        this.recipientName = recipientName;
        this.userImageUrl = userImageUrl;
        this.text = text;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public String getUserImageUrl() {
        return userImageUrl;
    }

    public String getText() {
        return text;
    }
}
