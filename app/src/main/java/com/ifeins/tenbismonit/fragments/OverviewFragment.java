package com.ifeins.tenbismonit.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;
import com.ifeins.tenbismonit.R;
import com.ifeins.tenbismonit.activities.HomeActivity;
import com.ifeins.tenbismonit.adapters.OverviewAdapter;
import com.ifeins.tenbismonit.models.CardData;
import com.ifeins.tenbismonit.utils.FirebaseUtils;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class OverviewFragment extends Fragment implements HomeAdapterFragment {

    private static final String TAG = "OverviewFragment";

    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBarView;
    private TextView mProgressCaptionView;
    @Nullable
    private ListenerRegistration mSnapshotListener;
    private TextView mLastUpdateView;
    private TextView mErrorView;
    private GridLayoutManager mLayoutManager;
    private OverviewAdapter mAdapter;

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

        mProgressBarView = view.findViewById(R.id.progress_bar);
        mProgressCaptionView = view.findViewById(R.id.progress_bar_caption);
        mLastUpdateView = view.findViewById(R.id.last_update_view);
        mErrorView = view.findViewById(R.id.error_view);
        mRecyclerView = view.findViewById(R.id.overview_grid);

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new GridLayoutManager(getContext(), 2);
        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return position == 0 ? 2 : 1;
            }
        });
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new OverviewAdapter();
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();

        subscribeForUpdates();
    }

    private void updateUI(@Nullable DocumentSnapshot document) {
        mErrorView.setVisibility(View.GONE);

        if (document == null || !document.exists()) {
            mRecyclerView.setVisibility(View.GONE);
            mProgressBarView.setVisibility(View.VISIBLE);
            mProgressCaptionView.setVisibility(View.VISIBLE);
            ((HomeActivity) getActivity()).syncUserData();
            return;
        }

        String updatedAt = document.getString("updatedAt");

        mRecyclerView.setVisibility(View.VISIBLE);
        mProgressBarView.setVisibility(View.GONE);
        mProgressCaptionView.setVisibility(View.GONE);

        ArrayList<CardData> data = new ArrayList<>();
        String remainingBudgetString = getString(R.string.remaining_budget_value, document.get("remainingMonthlyLunchBudget"));
        String remainingBudgetTotalString = getString(R.string.remaining_budget_total_value, document.get("monthlyLunchBudget"));
        SpannableStringBuilder remainingBudget = getRelativeSizedString(remainingBudgetString, remainingBudgetTotalString);

        String remainingLunchesString = getString(R.string.remaining_lunches_value, document.get("remainingLunches"));
        String remainingLunchesTotalString = getString(R.string.remaining_lunches_total_value, document.get("workDays"));
        SpannableStringBuilder remainingLunches = getRelativeSizedString(remainingLunchesString, remainingLunchesTotalString);

        data.add(new CardData(getString(R.string.today_budget), getString(R.string.single_money_value, document.get("todayBudget"))));
        data.add(new CardData(getString(R.string.remaining_budget), remainingBudget));
        data.add(new CardData(getString(R.string.remaining_lunches), remainingLunches));
        data.add(new CardData(getString(R.string.total_spent), getString(R.string.single_money_value, document.get("totalSpent"))));
        data.add(new CardData(getString(R.string.average_lunch_spending), getString(R.string.single_money_value, document.get("averageLunchSpending"))));

        mAdapter.setData(data);
        mAdapter.notifyDataSetChanged();

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
        FragmentActivity activity = getActivity();
        if (user == null || activity == null) return;

        DocumentReference document = FirebaseUtils.getMonthlyReportReference(user.getUid());
        mSnapshotListener = document.addSnapshotListener(activity, (documentSnapshot, e) -> {
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

    @Override
    public void onRefreshError() {
        mProgressBarView.setVisibility(View.GONE);
        mProgressCaptionView.setVisibility(View.GONE);
        mErrorView.setVisibility(View.VISIBLE);
    }

    private SpannableStringBuilder getRelativeSizedString(String bigValue, String smallValue) {
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(bigValue);
        Spannable smallValueSpan = new SpannableString(smallValue);
        smallValueSpan.setSpan(new RelativeSizeSpan(0.7f), 0, smallValueSpan.length(), 0);
        stringBuilder.append(smallValueSpan);
        return stringBuilder;
    }
}
