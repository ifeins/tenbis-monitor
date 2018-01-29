package com.ifeins.tenbis.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ifeins.tenbis.models.User;

import java.util.Collections;
import java.util.List;

public class SplashActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1;

    private static final CollectionReference mUsersRef =
            FirebaseFirestore.getInstance().collection("users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            onUserLoggedIn(user);
        } else {
            signIn();
        }
    }

    private void signIn() {
        List<AuthUI.IdpConfig> providers = Collections.singletonList(
                new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()
        );

        Intent intent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build();
        startActivityForResult(intent, RC_SIGN_IN);
    }

    private void onUserLoggedIn(@NonNull FirebaseUser user) {
        mUsersRef.document(user.getUid()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                User.setCurrentUser(new User(user.getUid(), null, null));
                showOverallStatsPage();
            } else {
                showSignUpPage();
            }
        });
    }

    private void showSignUpPage() {
        startActivity(new Intent(this, SignUpActivity.class));
    }

    private void showOverallStatsPage() {
        startActivity(new Intent(this, HomeActivity.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            onUserLoggedIn(FirebaseAuth.getInstance().getCurrentUser());
        }
    }
}
