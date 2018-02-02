package com.ifeins.tenbismonit.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ifeins.tenbismonit.models.User;
import com.ifeins.tenbismonit.utils.FirebaseUtils;

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
            FirebaseUtils.signIn(this, RC_SIGN_IN, true);
        }
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
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                onUserLoggedIn(FirebaseAuth.getInstance().getCurrentUser());
            } else {
                Toast.makeText(this, "Failed to sign in: " + response, Toast.LENGTH_LONG).show();
            }
        }
    }
}
