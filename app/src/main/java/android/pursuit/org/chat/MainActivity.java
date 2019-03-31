package android.pursuit.org.chat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    EditText registerEmail;
    EditText registerUsername;
    EditText registerPassword;
    Button profile;
    Button register;
    TextView goToLogin;
    Uri selectedProfileImage;
    private FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if(user != null){
                startActivity(new Intent(MainActivity.this, MessageActivity.class));
                finish();
            }
        };

        registerEmail = findViewById(R.id.signup_email);
        registerUsername = findViewById(R.id.signup_username);
        registerPassword = findViewById(R.id.signup_password);
        profile = findViewById(R.id.signup_profile);

        register = findViewById(R.id.signup_button);
        goToLogin = findViewById(R.id.alreadyHaveAccount);


        goToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        profile.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 0);
        });

        register.setOnClickListener(v -> {
            if (registerEmail.getText().toString().isEmpty()
                    || registerPassword.getText().toString().isEmpty()) {
                Toast.makeText(v.getContext(), "fields cannot be empty", Toast.LENGTH_SHORT).show();
            } else {
                register();
            }
        });

    }

    private void register() {
        String email = registerEmail.getText().toString();
        String password = registerPassword.getText().toString();

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        if (selectedProfileImage != null) {
                            uploadImageToFirebase();
                        } else {
                            saveUserToFirebase("");
                        }
                    }
                }).addOnFailureListener(e -> Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void uploadImageToFirebase() {
        String filename = UUID.randomUUID().toString();
        final StorageReference ref = FirebaseStorage.getInstance().getReference("/images/" + filename);

        ref.putFile(selectedProfileImage).addOnSuccessListener(taskSnapshot -> {
            Log.d("uploadimage", taskSnapshot.getMetadata().getPath());
            saveUserToFirebase(selectedProfileImage.toString());
        });
    }

    private void saveUserToFirebase(String imgUrl) {
        String uid = FirebaseAuth.getInstance().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("/users/" + uid);
        User user = new User(uid, registerUsername.getText().toString(), imgUrl);
        ref.setValue(user).addOnSuccessListener(aVoid -> {
            Toast.makeText(MainActivity.this, "user saved", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(
                    MainActivity.this, WelcomeActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK)
            );
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            selectedProfileImage = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedProfileImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
            BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmap);
            profile.setBackgroundDrawable(bitmapDrawable);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        firebaseAuth.addAuthStateListener(authStateListener);
    }
}