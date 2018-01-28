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

import org.threeten.bp.LocalDate;

import java.util.Locale;

public class OverallActivity extends AppCompatActivity {

    private static final String TAG = "OverallActivity";

    private static final CollectionReference mUsersRef =
            FirebaseFirestore.getInstance().collection("users");

    private TextView mBudgetView;
    private TextView mLunchesView;
    private TextView mTotalSpentView;
    private TextView mAverageLunchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overall);

        mBudgetView = findViewById(R.id.budget_view);
        mLunchesView = findViewById(R.id.lunches_view);
        mTotalSpentView = findViewById(R.id.total_spent_view);
        mAverageLunchView = findViewById(R.id.average_lunch_view);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            subscribeForUpdates(currentUser);
        } else {
            finish();
            startActivity(new Intent(this, SplashActivity.class));
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

    private void updateUI(@Nullable DocumentSnapshot document) {
        if (document == null || !document.exists()) return;

        mBudgetView.setText(getString(R.string.remaining_budget,
                document.get("remainingMonthlyLunchBudget"), document.get("monthlyLunchBudget")));
        mLunchesView.setText(getString(R.string.remaining_lunches,
                document.get("remainingLunches"), document.get("workDays")));
        mTotalSpentView.setText(getString(R.string.total_spent,
                document.get("totalSpent")));
        mAverageLunchView.setText(getString(R.string.average_lunch_spending,
                document.get("averageLunchSpending")));
    }

    private void subscribeForUpdates(@NonNull FirebaseUser user) {
        LocalDate now = LocalDate.now();
        String reportId = String.format(Locale.getDefault(), "%02d-%d", now.getMonthValue(), now.getYear());
        DocumentReference document = mUsersRef.document(user.getUid()).collection("reports").document(reportId);
        document.addSnapshotListener(this, (documentSnapshot, e) -> {
            if (e != null) {
                Log.e(TAG, "subscribeForUpdates: Failed to fetch snapshot", e);
            } else {
                updateUI(documentSnapshot);
            }
        });
    }
}
