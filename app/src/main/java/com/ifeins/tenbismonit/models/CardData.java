package com.ifeins.tenbismonit.models;

/**
 * @author nsulema
 */
public class CardData {

    private String mDescription;
    private CharSequence mValue;
    private boolean mIsPrimaryCard;

    public CardData(String description, CharSequence value) {
        mDescription = description;
        mValue = value;
        mIsPrimaryCard = false;
    }

    public CardData(String description, CharSequence value, boolean isPrimaryCard) {
        mDescription = description;
        mValue = value;
        mIsPrimaryCard = isPrimaryCard;
    }

    public boolean isPrimaryCard() {
        return mIsPrimaryCard;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public CharSequence getValue() {
        return mValue;
    }

    public void setValue(String value) {
        mValue = value;
    }
}
