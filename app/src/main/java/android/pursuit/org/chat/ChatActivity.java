package android.pursuit.org.chat;

import android.content.Intent;
import android.os.Bundle;
import android.pursuit.org.chat.unusedClasses.AbstractAdapter;
import android.pursuit.org.chat.unusedClasses.AbstractViewHolder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity {
    RecyclerView chatRecyclerView;
    EditText message;
    TextView recipientID;
    Button sendMessage;
    String currentUser;
    DatabaseReference chatRef_current;
    User recipient;
    List<Chat> chatList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();
        recipient = intent.getParcelableExtra(UserListActivity.USER_KEY);
        currentUser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        chatRecyclerView = findViewById(R.id.chat_recyclerview);
        ChatAdapter adapter = new ChatAdapter(chatList, R.layout.chat_itemview);
        chatRecyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        chatRecyclerView.setLayoutManager(layoutManager);


        RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(chatRecyclerView.getContext()) {
            @Override
            protected int getVerticalSnapPreference() {
                return LinearSmoothScroller.SNAP_TO_END;
            }
        };

        chatRef_current =
                FirebaseDatabase.getInstance()
                        .getReference("/chats/" + currentUser + "/" + recipient.uid);
        chatRef_current.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Chat chat = dataSnapshot.getValue(Chat.class);
                chatList.add(chat);
                adapter.setList(chatList);
                boolean isPageFilledWithItems =
                        chatRecyclerView.computeVerticalScrollRange() > chatRecyclerView.getHeight();

                if (isPageFilledWithItems) {
                    layoutManager.setStackFromEnd(true);
                    smoothScroller.setTargetPosition(chatList.size() - 1);
                    layoutManager.startSmoothScroll(smoothScroller);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        message = findViewById(R.id.chat_editText);
        recipientID = findViewById(R.id.recipient_textView);
        sendMessage = findViewById(R.id.send_message);

        recipientID.setText(recipient.username);

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Long tsLong = System.currentTimeMillis() / 1000;
                String ts = tsLong.toString();

                String messageSent = message.getText().toString();

                DatabaseReference chatRef_recipient = FirebaseDatabase.getInstance()
                        .getReference(
                                "/chats/"
                                        + recipient.uid
                                        + "/"
                                        + currentUser
                                        + "/").child(ts);

                Chat chat = new Chat(currentUser, messageSent);
                chatRef_current.child(ts).setValue(chat);
                chatRef_recipient.setValue(chat);
            }
        });
    }

    public class ChatAdapter extends AbstractAdapter<Chat, ChatViewHolder> {

        final int CURRENT = 0;
        final int RESPONSE = 1;

        List<Chat> list;
        int itemview;

        @Override
        public int getItemViewType(int position) {
            if (list.get(position).uid_sending.equals(currentUser)) {
                return CURRENT;
            } else if (list.get(position).uid_sending.equals(recipient.uid)) {
                return RESPONSE;
            } else {
                return CURRENT;
            }
        }

        public ChatAdapter(List<Chat> list, int itemview) {
            super(list, itemview);
            this.list = list;
            this.itemview = itemview;
        }

        @NonNull
        @Override
        public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            ChatViewHolder viewHolder;
            LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
            switch (viewType) {
                case CURRENT:
                    View current = layoutInflater.inflate(R.layout.chat_itemview, viewGroup, false);
                    viewHolder = new ChatViewHolder(current);
                    break;
                case RESPONSE:
                    View response = layoutInflater.inflate(R.layout.chat_itemview_response, viewGroup, false);
                    viewHolder = new ChatViewHolder(response);
                    break;
                default:
                    View defaultView = layoutInflater.inflate(R.layout.chat_itemview, viewGroup, false);
                    viewHolder = new ChatViewHolder(defaultView);
                    break;
            }
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ChatViewHolder chatViewHolder, int i) {

            switch (chatViewHolder.getItemViewType()) {
                case CURRENT:
                    configureSentMessage(chatViewHolder, i);
                    break;
                case RESPONSE:
                    configureReceivedMessage(chatViewHolder, i);
                    break;
                default:
                    configureSentMessage(chatViewHolder, i);
                    break;
            }
        }

        public void setList(List<Chat> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        private void configureSentMessage(ChatViewHolder vh, int position) {
            Chat chat = list.get(position);
            if (chat != null) {
                TextView message = vh.itemView.findViewById(R.id.chat_message);
                message.setText(chat.message);
            }
        }

        private void configureReceivedMessage(ChatViewHolder vh, int position) {
            Chat chat = list.get(position);
            if (chat != null) {
                TextView message = vh.itemView.findViewById(R.id.chat_message_r);
                message.setText(chat.message);
            }
        }
    }

    public class ChatViewHolder extends AbstractViewHolder<Chat> {
        TextView message;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.chat_message);
        }
    }
}