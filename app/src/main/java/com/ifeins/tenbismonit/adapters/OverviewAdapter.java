package com.ifeins.tenbismonit.adapters;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ifeins.tenbismonit.R;
import com.ifeins.tenbismonit.models.CardData;

import java.util.ArrayList;
import java.util.List;

/**
 * @author nsulema
 */
public class OverviewAdapter extends RecyclerView.Adapter<OverviewAdapter.ViewHolder> {

    private List<CardData> mData = new ArrayList<>();

    public void setData(List<CardData> data) {
        mData = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View cardView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_overview_card, parent, false);
        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CardData card = mData.get(position);
        holder.setCard(card, position);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView mTitleView;
        private final TextView mValueView;
        private final CardView mCardView;
        private CardData mCard;
        private Context mContext;
        private int mPosition;
        private ArrayList<Integer> cardColors;

        ViewHolder(View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            mTitleView = itemView.findViewById(R.id.card_title);
            mValueView = itemView.findViewById(R.id.card_value);
            mCardView = itemView.findViewById(R.id.overview_card);
            cardColors = getCardColors();
        }

        void setCard(CardData card, int position) {
            mCard = card;
            mPosition = position;
            int index = mPosition % cardColors.size();
            mCardView.setCardBackgroundColor(cardColors.get(index));

            if (mPosition == 0) {
                mTitleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimension(R.dimen.first_card_title_text_size));
                mValueView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimension(R.dimen.first_card_value_text_size));
            }

            mTitleView.setText(mCard.getDescription());
            mValueView.setText(mCard.getValue());
        }

        private ArrayList<Integer> getCardColors() {
            TypedArray typedArray = mContext.getResources().obtainTypedArray(R.array.cardBackgrounds);
            ArrayList<Integer> colors = new ArrayList<>();
            for (int i = 0; i < typedArray.length(); i++) {
                colors.add(typedArray.getColor(i, 0));
            }
            typedArray.recycle();
            return colors;
        }
    }
}
