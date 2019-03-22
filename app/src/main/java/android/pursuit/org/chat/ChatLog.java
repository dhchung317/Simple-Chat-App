package android.pursuit.org.chat;

import java.util.List;


public class ChatLog {
    User chattingWith;
    List<Chat> messages;

    public ChatLog(User chattingWith, List<Chat> messages) {
        this.chattingWith = chattingWith;
        this.messages = messages;
    }
}
