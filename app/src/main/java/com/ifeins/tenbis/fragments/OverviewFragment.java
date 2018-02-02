package com.ifeins.tenbis.fragments;

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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;
import com.ifeins.tenbis.R;
import com.ifeins.tenbis.utils.FirebaseUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class OverviewFragment extends Fragment implements HomeAdapterFragment {

    private static final String TAG = "OverviewFragment";

    private TextView mBudgetView;
    private TextView mLunchesView;
    private TextView mTotalSpentView;
    private TextView mAverageLunchView;
    @Nullable
    private ListenerRegistration mSnapshotListener;

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

        subscribeForUpdates();
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

    @Override
    public void subscribeForUpdates() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        DocumentReference document = FirebaseUtils.getMonthlyReportReference(user.getUid());
        mSnapshotListener = document.addSnapshotListener(getActivity(), (documentSnapshot, e) -> {
            if (e != null) {
                Log.e(TAG, "subscribeForUpdates: Failed to fetch snapshot", e);
            } else {
                updateUI(documentSnapshot);
            }
        });
    }

    @Override
    public void unsubscribeForUpdates() {
        if (mSnapshotListener != null) {
            mSnapshotListener.remove();
        }
    }
}
