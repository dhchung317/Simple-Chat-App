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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class MessageRepository {

    private static MessageRepository instance;

    public static MessageRepository getRepositoryInstance() {
        if (instance == null) {
            instance = new MessageRepository();
        }
        return instance;
    }

    private final String currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    private final DatabaseReference chatDatabaseReference = FirebaseDatabase.getInstance().getReference("/chats/");
    private final DatabaseReference userDatabaseReference = FirebaseDatabase.getInstance().getReference("/users/");
    Set<String> keysForUserHistory = new HashSet<>();
    Set<User> userHistory = new HashSet<>();

    Single<List<ChatLog>> getUpdate(MessageAdapter adapter) {
        return getKeysForUserHistory()
                .subscribeOn(Schedulers.io())
                .map(keys -> getUserHistory(keys))
                .flatMap(users -> users)
                .flatMapIterable(user -> user)
                .flatMap(user -> getMessageData(user),
                        (user, chats) -> new ChatLog(user, chats))
                .doOnNext(chatlog -> {
                    adapter.updateLogs(chatlog);
                }).toList();
    }

    Observable<Set<String>> getKeysForUserHistory() {
        return Observable.create(emitter ->
                chatDatabaseReference.child(currentUserId).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        keysForUserHistory.add(dataSnapshot.getKey());
                        Log.d("messagerepogetkeys", dataSnapshot.getKey());
                        emitter.onNext(keysForUserHistory);
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
                })
        );
    }

    Observable<Set<User>> getUserHistory(Set<String> keys) {
        return Observable.create(emitter -> {
            userDatabaseReference.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    if (keys.contains(dataSnapshot.getKey())) {
                        Log.d("messagerepocontains this", dataSnapshot.getKey());
                        userHistory.add(dataSnapshot.getValue(User.class));
                    }
                    emitter.onNext(userHistory);
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

    Observable<List<Chat>> getMessageData(User user) {
        return Observable.create(emitter -> {
            chatDatabaseReference.child(currentUserId).addChildEventListener(new ChildEventListener() {
                List<Chat> messages = new ArrayList<>();

                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    if (dataSnapshot.getKey().equals(user.uid)) {
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            messages.add(data.getValue(Chat.class));
                        }
                    }
                    emitter.onNext(messages);
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    if (dataSnapshot.getKey().equals(user.uid)) {
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            messages.add(data.getValue(Chat.class));
                        }
                    }
                    emitter.onNext(messages);
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

}