package com.ifeins.tenbis.utils;

import android.support.annotation.NonNull;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.threeten.bp.LocalDate;

import java.util.Locale;

/**
 * @author ifeins
 */

public class FirestoreUtils {

    public static DocumentReference getMonthlyReportReference(@NonNull String userId) {
        return FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .collection("reports")
                .document(getMonthlyReportId());
    }

    public static CollectionReference getMonthlyTransactionsReference(@NonNull String userId) {
        return FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .collection("reports")
                .document(getMonthlyReportId())
                .collection("transactions");
    }

    private static String getMonthlyReportId() {
        LocalDate now = LocalDate.now();
        return String.format(Locale.getDefault(), "%02d-%d", now.getMonthValue(), now.getYear());
    }


}
