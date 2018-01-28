package com.ifeins.tenbis.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ifeins.tenbis.R;
import com.ifeins.tenbis.activities.SplashActivity;

import org.threeten.bp.LocalDate;

import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class OverviewFragment extends Fragment {

    private static final String TAG = "OverviewFragment";

    private static final CollectionReference mUsersRef =
            FirebaseFirestore.getInstance().collection("users");

    private TextView mBudgetView;
    private TextView mLunchesView;
    private TextView mTotalSpentView;
    private TextView mAverageLunchView;

    public OverviewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_overview, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mBudgetView = view.findViewById(R.id.budget_view);
        mLunchesView = view.findViewById(R.id.lunches_view);
        mTotalSpentView = view.findViewById(R.id.total_spent_view);
        mAverageLunchView = view.findViewById(R.id.average_lunch_view);
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            subscribeForUpdates(currentUser);
        }
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
        document.addSnapshotListener(getActivity(), (documentSnapshot, e) -> {
            if (e != null) {
                Log.e(TAG, "subscribeForUpdates: Failed to fetch snapshot", e);
            } else {
                updateUI(documentSnapshot);
            }
        });
    }
}
