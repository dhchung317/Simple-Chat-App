package android.pursuit.org.chat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Consumer;

public class MessageActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    MessageAdapter adapter;
    private MessageRepository messageRepository = new MessageRepository();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        recyclerView = findViewById(R.id.message_recyclerview);
        adapter = new MessageAdapter(new ArrayList<>(), R.layout.saved_message_itemview);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        messageRepository.getChatLogs()
                .subscribe(new Consumer<List<ChatLog>>() {
                    @Override
                    public void accept(List<ChatLog> chatLogs) throws Exception {
                        Log.d("messageactivity", chatLogs.get(1).messages.get(2).message);
                        adapter.setList(chatLogs);
                    }
                },
                        throwable -> {
                    throwable.printStackTrace();
                        });

//        messageRepository.getMessageData()
//                .map(messageData -> {
//                    messageData.get(messageData.size() -1);
//                })
//                .map()
//                .subscribe(messageData -> {
//                    Log.d("", "onCreate: yay");
//                });
//
//        messageRepository.getUser("Ps9q2QdFQxRNfaLcXFs3caK5Nvu1")
//                .subscribe(messageData -> {
//                    Log.d("", "onCreate: yay");
//                });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.message_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MessageActivity.this, LoginActivity.class));
                return true;
            case R.id.new_message:
                startActivity(new Intent(MessageActivity.this, UserListActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRestart() {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }
}


