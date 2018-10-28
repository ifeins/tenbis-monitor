package com.ifeins.tenbismonit.models;

/**
 * @author nsulema
 */
public class CardData {

    private String mDescription;
    private CharSequence mValue;

    public CardData(String description, CharSequence value) {
        mDescription = description;
        mValue = value;
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
