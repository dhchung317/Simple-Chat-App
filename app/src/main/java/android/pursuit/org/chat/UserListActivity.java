package android.pursuit.org.chat;

import android.content.Intent;
import android.os.Bundle;
import android.pursuit.org.chat.unusedClasses.AbstractAdapter;
import android.pursuit.org.chat.unusedClasses.AbstractViewHolder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UserListActivity extends AppCompatActivity {
    public static final String USER_KEY = "user";
    RecyclerView recyclerView;
    List<User> userlist = new ArrayList<>();
    String currentUserName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userlist);
        recyclerView = findViewById(R.id.userlist_recyclerview);
        UserAdapter adapter = new UserAdapter(userlist, R.layout.user_itemview);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        DatabaseReference dref = FirebaseDatabase.getInstance().getReference("/users/");

        dref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    if(!FirebaseAuth.getInstance().getCurrentUser().getUid()
                            .equals(childSnapshot.getValue(User.class).uid)) {
                        userlist.add(childSnapshot.getValue(User.class));
                    }
                    adapter.setList(userlist);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public class UserViewHolder extends AbstractViewHolder<User> {
        TextView user;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            user = itemView.findViewById(R.id.user_textview);
        }

        public void onBind(final User item) {
            user.setText(item.username);
            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(UserListActivity.this, ChatActivity.class);
                intent.putExtra(USER_KEY,item);

                startActivity(intent);
            });
        }
    }

    public class UserAdapter extends AbstractAdapter<User, UserViewHolder> {
        public void setList(List<User> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        List<User> list;
        int itemview;

        public UserAdapter(List<User> list, int itemview) {
            super(list, itemview);
            this.list = list;
            this.itemview = itemview;
        }

        @NonNull
        @Override
        public UserViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View child = LayoutInflater
                    .from(viewGroup.getContext())
                    .inflate(itemview, viewGroup, false);
            return new UserViewHolder(child);
        }

        @Override
        public void onBindViewHolder(@NonNull UserViewHolder viewHolder, int i) {
            if(!list.get(i).username.equals(currentUserName))
            viewHolder.onBind(list.get(i));
        }
    }
}
