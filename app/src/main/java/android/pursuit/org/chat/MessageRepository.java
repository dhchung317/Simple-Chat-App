package android.pursuit.org.chat;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.reactivex.Observable;
import io.reactivex.Single;

public class MessageRepository {
    private final String currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    private final DatabaseReference chatDatabaseReference = FirebaseDatabase.getInstance().getReference("/chats/");
    private final DatabaseReference userDatabaseReference = FirebaseDatabase.getInstance().getReference("/users/");




    public Single<List<ChatLog>> getChatLogs() {
//            List<ChatLog> log = new ArrayList<>();
                    return getUsersChattedWith()
                    .flatMapIterable(users -> users)
                    .flatMap(user -> getMessageData(user),
                            (chattingWith, messages) -> new ChatLog(chattingWith, messages))
                            .toList();

    }

    public Observable<List<User>> getUsersChattedWith(){
        return Observable.create(emitter -> {
            Map<String,User> allUsers = new HashMap<>();
            List<User> returnlist = new ArrayList<>();
            userDatabaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot data: dataSnapshot.getChildren()) {
                        allUsers.put(data.getValue(User.class).uid,data.getValue(User.class));
                        Log.d("chatloggetuserschattedwith", data.getValue().toString());
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            chatDatabaseReference.child(currentUserId).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                    for (:) {
//
//                    }
                    if(allUsers.containsKey(dataSnapshot.getKey())){
                        returnlist.add(allUsers.get(dataSnapshot.getKey()));
                        Log.d("chatloggetuserschattedonchildadded", allUsers.get(dataSnapshot.getKey()).username);
                    }
                emitter.onNext(returnlist);
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

        });
    }


    public Observable<List<Chat>> getMessageData(User user) {
        return Observable.create(emitter -> {
            chatDatabaseReference.child(user.uid).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

//                    Log.d("chatloggetmessagedata", dataSnapshot.toString());
                    List<Chat> messages = new ArrayList<>();
                    for (DataSnapshot child: dataSnapshot.getChildren()) {
                        Log.d("dannyhsbcjhabjh", child.getValue(Chat.class).message);
                        messages.add(child.getValue(Chat.class));
                    }
                    Log.d("danny",messages.get(0).message);
                    emitter.onNext(messages);
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
//            chatDatabaseReference.child(currentUserId).addChildEventListener(listener);
        });
    }

//    public Observable<ChatMessage> getLatestMessageWithFriend(@NonNull String friendUserId) {
//         return getMessageData()
//                .flatMapIterable(data -> data)
//                 .takeLast(1)
//                 .flatMap(messageData -> {
//
//                 });
//                 .map(messageData -> new ChatMessage());
//    }

    public Observable<List<User>> getUser(@NonNull String userId) {
        return Observable.empty(); // TODO implement
    }

    public Observable<List<User>> getAllFriends() {
        return Observable.create(emitter -> {
            ValueEventListener listener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<User> users = new ArrayList<>();
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        if (!FirebaseAuth.getInstance().getCurrentUser().getUid()
                                .equals(Objects.requireNonNull(childSnapshot.getValue(User.class)).uid)) {
                            users.add(childSnapshot.getValue(User.class));
                        }
                    }
                    emitter.onNext(users);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };

            userDatabaseReference.addListenerForSingleValueEvent(listener);
        });
    }
}
