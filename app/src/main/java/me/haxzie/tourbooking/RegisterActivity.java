package me.haxzie.tourbooking;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    Button registerBtn;
    EditText email, password, name, phone;

    private FirebaseAuth mAuth;
    private Context mContext = RegisterActivity.this;
    private final String TAG = "TravelApp";

    private void initViews() {
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        name = findViewById(R.id.name);
        phone = findViewById(R.id.phone);
        registerBtn = findViewById(R.id.register);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        initViews();

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uEmail = email.getText().toString();
                String uPassword = password.getText().toString();
                String uName = name.getText().toString();
                String uPhone = phone.getText().toString();

                if (uEmail.length() < 6 || !isValid(uEmail)) {
                    Toast.makeText(mContext, "Invalid email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (uPassword.length() < 6) {
                    Toast.makeText(mContext, "Password must be 6 chars long", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (uName.length() < 3) {
                    Toast.makeText(mContext, "Invalid name", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (uPhone.length() < 10 ) {
                    Toast.makeText(mContext, "Invalid phone", Toast.LENGTH_SHORT).show();
                    return;
                }

                createAccount(uEmail, uPassword, uName, uPhone);
            }
        });
    }


    private boolean isValid(String email) {
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(regex);
    }
    private void createNewUser(String userId, String email, String name, String phone) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> user = new HashMap<>();
        user.put("uid", userId);
        user.put("email", email);
        user.put("name", name);
        user.put("phone", phone);

        db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        Toast.makeText(mContext, "User Creates!", Toast.LENGTH_LONG).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error creating new user", e);
                    }
                });
    }

    private void createAccount(final String email, String password, final String name, final String phone) {
        Log.d(TAG, "createAccount:" + email);


        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            Toast.makeText(RegisterActivity.this, "Authentication Success.",
                                    Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            String userId = user.getUid();
                            createNewUser(userId, email, name, phone);

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
