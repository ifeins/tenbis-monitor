package com.ifeins.tenbismonit.fragments;

import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.ListenerRegistration;
import com.ifeins.tenbismonit.R;
import com.ifeins.tenbismonit.models.Transaction;
import com.ifeins.tenbismonit.utils.FirebaseUtils;

import org.threeten.bp.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class TransactionsFragment extends Fragment implements HomeAdapterFragment {

    private static final String TAG = "TransactionsFragment";

    private List<Transaction> mTransactions = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private TransactionsAdapter mAdapter;
    @Nullable
    private ListenerRegistration mSnapshotListener;

    public TransactionsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_transactions, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAdapter = new TransactionsAdapter();
        mRecyclerView = view.findViewById(R.id.transactions_view);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(
                getContext(), DividerItemDecoration.HORIZONTAL));
    }

    @Override
    public void onStart() {
        super.onStart();

        subscribeForUpdates();
    }

    @Override
    public void subscribeForUpdates() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        CollectionReference collectionReference = FirebaseUtils.getMonthlyTransactionsReference(user.getUid());
        mSnapshotListener = collectionReference.addSnapshotListener(getActivity(), (documentSnapshots, e) -> {
            if (e != null) {
                Log.e(TAG, "subscribeForUpdates: error fetching transactions", e);
            } else {
                mTransactions = documentSnapshots.toObjects(Transaction.class);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void unsubscribeForUpdates() {
        if (mSnapshotListener != null) {
            mSnapshotListener.remove();
        }
    }

    private static class TransactionViewHolder extends RecyclerView.ViewHolder {

        private final SimpleDraweeView mImageView;
        private final TextView mNameView;
        private final TextView mAmountView;
        private final TextView mDateView;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);

            mImageView = itemView.findViewById(R.id.image_view);
            mNameView = itemView.findViewById(R.id.name_view);
            mAmountView = itemView.findViewById(R.id.amount_view);
            mDateView = itemView.findViewById(R.id.date_view);
        }

        public void bind(@NonNull Transaction transaction, @NonNull Resources res) {
            mImageView.setImageURI(Uri.parse(transaction.getRestaurantLogoUrl()));
            mNameView.setText(transaction.getRestaurantName());
            mAmountView.setText(res.getString(R.string.transaction_amount, transaction.getAmount()));
            mDateView.setText(transaction.getDate().format(DateTimeFormatter.ofPattern("LLL d, yyyy")));
        }
    }

    private class TransactionsAdapter extends RecyclerView.Adapter<TransactionViewHolder> {

        @Override
        public TransactionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_transaction, parent, false);
            return new TransactionViewHolder(view);
        }

        @Override
        public void onBindViewHolder(TransactionViewHolder holder, int position) {
            holder.bind(mTransactions.get(position), getResources());
        }

        @Override
        public int getItemCount() {
            return mTransactions.size();
        }
    }

}
