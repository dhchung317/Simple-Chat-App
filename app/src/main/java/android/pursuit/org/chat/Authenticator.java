package android.pursuit.org.chat;

import android.support.annotation.NonNull;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import io.reactivex.Observable;

public class Authenticator {
    public Observable<AuthResult> signIn(@NonNull String email, @NonNull String password) {
        return Observable.create(emitter ->
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener(authResult -> {
                            emitter.onNext(authResult);
                            emitter.onComplete();
                        })
                        .addOnFailureListener(e -> emitter.onError(e))
                        .addOnCanceledListener(() -> emitter.onComplete()));
    }
}
