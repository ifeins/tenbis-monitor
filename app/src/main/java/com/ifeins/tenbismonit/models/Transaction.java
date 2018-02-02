package com.ifeins.tenbismonit.models;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;

/**
 * @author ifeins
 */

public class Transaction {

    private long id;
    private String date;
    private String restaurantName;
    private String restaurantLogoUrl;
    private float amount;
    private String orderType;
    private String paymentMethod;

    public Transaction() {
        // no-arg constructor required by Firestore
    }

    public long getId() {
        return id;
    }

    public LocalDateTime getDate() {
        return LocalDateTime.parse(date, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public String getRestaurantLogoUrl() {
        return restaurantLogoUrl;
    }

    public float getAmount() {
        return amount;
    }

    public String getOrderType() {
        return orderType;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }
}
