package android.pursuit.org.chat;

public class Chat {

    String uid_sending;
    String message;

    public Chat() {
    }

    public Chat(String user_uid, String message) {
        this.uid_sending = user_uid;
        this.message = message;
    }
}
