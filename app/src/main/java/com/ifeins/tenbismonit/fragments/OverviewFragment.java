package com.ifeins.tenbismonit.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;
import com.ifeins.tenbismonit.R;
import com.ifeins.tenbismonit.activities.HomeActivity;
import com.ifeins.tenbismonit.utils.FirebaseUtils;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;

/**
 * A simple {@link Fragment} subclass.
 */
public class OverviewFragment extends Fragment implements HomeAdapterFragment {

    private static final String TAG = "OverviewFragment";

    private TextView mBudgetView;
    private TextView mLunchesView;
    private TextView mTotalSpentView;
    private TextView mAverageLunchView;
    private TextView mRemainingAverageLunchView;
    private ProgressBar mProgressBarView;
    private TextView mProgressCaptionView;
    @Nullable
    private ListenerRegistration mSnapshotListener;
    private ImageView mImageView;
    private TextView mLastUpdateView;

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

        mImageView = view.findViewById(R.id.overview_image);
        mBudgetView = view.findViewById(R.id.budget_view);
        mLunchesView = view.findViewById(R.id.lunches_view);
        mTotalSpentView = view.findViewById(R.id.total_spent_view);
        mAverageLunchView = view.findViewById(R.id.average_lunch_view);
        mRemainingAverageLunchView = view.findViewById(R.id.remaining_average_lunch_view);
        mProgressBarView = view.findViewById(R.id.progress_bar);
        mProgressCaptionView = view.findViewById(R.id.progress_bar_caption);
        mLastUpdateView = view.findViewById(R.id.last_update_view);
    }

    @Override
    public void onStart() {
        super.onStart();

        subscribeForUpdates();
    }

    private void updateUI(@Nullable DocumentSnapshot document) {
        if (document == null || !document.exists()) {
            mProgressBarView.setVisibility(View.VISIBLE);
            mProgressCaptionView.setVisibility(View.VISIBLE);
            mImageView.setVisibility(View.GONE);
            mBudgetView.setVisibility(View.GONE);
            mLunchesView.setVisibility(View.GONE);
            mTotalSpentView.setVisibility(View.GONE);
            mAverageLunchView.setVisibility(View.GONE);
            mRemainingAverageLunchView.setVisibility(View.GONE);
            mLastUpdateView.setVisibility(View.GONE);
            ((HomeActivity) getActivity()).syncUserData();
            return;
        }

        String updatedAt = document.getString("updatedAt");

        mProgressBarView.setVisibility(View.GONE);
        mProgressCaptionView.setVisibility(View.GONE);
        mImageView.setVisibility(View.VISIBLE);
        mBudgetView.setVisibility(View.VISIBLE);
        mLunchesView.setVisibility(View.VISIBLE);
        mTotalSpentView.setVisibility(View.VISIBLE);
        mAverageLunchView.setVisibility(View.VISIBLE);
        mRemainingAverageLunchView.setVisibility(View.VISIBLE);
        mLastUpdateView.setVisibility(TextUtils.isEmpty(updatedAt) ? View.GONE : View.VISIBLE);

        mBudgetView.setText(getString(R.string.remaining_budget,
                document.get("remainingMonthlyLunchBudget"), document.get("monthlyLunchBudget")));
        mLunchesView.setText(getString(R.string.remaining_lunches,
                document.get("remainingLunches"), document.get("workDays")));
        mTotalSpentView.setText(getString(R.string.total_spent,
                document.get("totalSpent")));
        mAverageLunchView.setText(getString(R.string.average_lunch_spending,
                document.get("averageLunchSpending")));
        mRemainingAverageLunchView.setText(getString(R.string.remaining_average_lunch_spending,
                document.get("remainingAverageLunchSpending")));

        if (TextUtils.isEmpty(updatedAt)) {
            mLastUpdateView.setText(null);
        } else {
            LocalDateTime time = LocalDateTime.parse(updatedAt, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            mLastUpdateView.setText(getString(R.string.last_update_at, time.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));
        }
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
