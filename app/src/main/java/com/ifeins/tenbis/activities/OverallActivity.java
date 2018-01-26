package com.ifeins.tenbis.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ifeins.tenbis.R;

import java.util.HashMap;
import java.util.Map;

public class OverallActivity extends AppCompatActivity {

    private static final String TAG = "OverallActivity";

    private static final CollectionReference mUsersRef =
            FirebaseFirestore.getInstance().collection("users");

    private TextView mWelcomeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overall);

        mWelcomeTextView = findViewById(R.id.welcome_text_view);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            subscribeForUpdates(currentUser);
        } else {
            finish();
            startActivity(new Intent(this, SignInActivity.class));
        }
    }

    public void signOut(View view) {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener((task) -> {
                    if (!task.isSuccessful()) {
                        Log.e(TAG, "signOut: Failed to sign out", task.getException());
                    }
                });
    }



    private void updateUI(@Nullable DocumentSnapshot documentSnapshot) {
        if (documentSnapshot != null && documentSnapshot.exists()) {
            mWelcomeTextView.setText(getString(
                    R.string.welcome_message, documentSnapshot.get("name")));
        } else {
            mWelcomeTextView.setText(null);
        }
    }

    private void updateUser(FirebaseUser user) {
        Map<String, Object> fields = new HashMap<>();
        fields.put("name", user.getDisplayName());
        fields.put("email", user.getEmail());
        fields.put("phone", user.getPhoneNumber());

        mUsersRef.document(user.getUid())
                .set(fields)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        subscribeForUpdates(user);
                    } else {
                        Log.e(TAG, "updateUser: failed to update", task.getException());
                    }
                });
    }

    private void subscribeForUpdates(@NonNull FirebaseUser user) {
        DocumentReference document = mUsersRef.document(user.getUid());
        document.addSnapshotListener(this, (documentSnapshot, e) -> updateUI(documentSnapshot));
    }
}
