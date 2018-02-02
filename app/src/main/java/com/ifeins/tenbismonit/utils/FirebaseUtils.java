package com.ifeins.tenbismonit.utils;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.threeten.bp.LocalDate;

import java.util.Collections;
import java.util.Locale;

/**
 * @author ifeins
 */

public class FirebaseUtils {

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

    public static void signIn(@NonNull Activity activity, int requestCode, boolean enableSmartLock) {
        Intent intent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setIsSmartLockEnabled(enableSmartLock)
                .setAvailableProviders(Collections.singletonList(new AuthUI.IdpConfig.GoogleBuilder().build()))
                .build();
        activity.startActivityForResult(intent, requestCode);
    }

    private static String getMonthlyReportId() {
        LocalDate now = LocalDate.now();
        return String.format(Locale.getDefault(), "%02d-%d", now.getMonthValue(), now.getYear());
    }


}
