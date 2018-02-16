package com.ifeins.tenbismonit.activities;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ifeins.tenbismonit.R;
import com.ifeins.tenbismonit.models.User;
import com.ifeins.tenbismonit.services.TenbisMonitorService;
import com.ifeins.tenbismonit.utils.UiUtils;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {

    private EditText mEmailView;
    private EditText mPasswordView;
    private TextView mSignUpNoticeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mEmailView = findViewById(R.id.input_email);
        mPasswordView = findViewById(R.id.input_password);
        mPasswordView.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_DONE) {
                onSignUp(null);
                return true;
            }
            return false;
        });
        mSignUpNoticeView = findViewById(R.id.text_view_sign_up);

        setUpSignUpNotice();
    }

    public void onSignUp(@Nullable View view) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean valid = true;
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError("You must provide an email");
            valid = false;
        }
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError("You must provide a password");
            valid = false;
        }
        if (!valid) return;

        User user = new User(currentUser.getUid(), email, password);
        TenbisMonitorService.getInstance().getUsersService().create(user).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    User.setCurrentUser(user);
                    startActivity(new Intent(SignUpActivity.this, HomeActivity.class));
                } else {
                    try {
                        showError(response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                        showError(e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                showError(t.getMessage());
            }
        });
    }

    private void setUpSignUpNotice() {
        UiUtils.setClickableLink(mSignUpNoticeView, getString(R.string.sign_up_notice), new ClickableSpan() {
            @Override
            public void onClick(View view) {
                // TODO: show a dialog
            }
        });
    }

    private void showError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }
}

