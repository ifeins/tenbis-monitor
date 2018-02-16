package com.ifeins.tenbismonit.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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

    private TextView mSignUpNoticeView;
    private TextInputLayout mEmailInputLayout;
    private EditText mEmailView;
    private TextInputLayout mPasswordInputLayout;
    private EditText mPasswordView;
    private Button mSignUpButton;
    private ProgressBar mProgressBarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {    
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mEmailInputLayout = findViewById(R.id.input_layout_email);
        mEmailView = findViewById(R.id.input_email);
        mPasswordInputLayout = findViewById(R.id.input_layout_password);
        mPasswordView = findViewById(R.id.input_password);
        mPasswordView.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_DONE) {
                onSignUp(null);
                return true;
            }
            return false;
        });
        mSignUpNoticeView = findViewById(R.id.text_view_sign_up_notice);
        mProgressBarView = findViewById(R.id.progress_sign_up);
        mSignUpButton = findViewById(R.id.btn_sign_up);

        setUpSignUpNotice();
    }

    public void onSignUp(@Nullable View view) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        boolean valid = validateInputLayout(mEmailInputLayout, R.string.missing_email_error);
        valid = validateInputLayout(mPasswordInputLayout, R.string.missing_password_error) && valid;
        if (!valid) return;

        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        User user = new User(currentUser.getUid(), email, password);
        mProgressBarView.setVisibility(View.VISIBLE);
        mSignUpButton.setEnabled(false);
        TenbisMonitorService.getInstance().getUsersService().create(user).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                mProgressBarView.setVisibility(View.INVISIBLE);
                mSignUpButton.setEnabled(true);
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
                mProgressBarView.setVisibility(View.INVISIBLE);
                mSignUpButton.setEnabled(true);
                showError(t.getMessage());
            }
        });
    }

    private void setUpSignUpNotice() {
        UiUtils.setClickableLink(mSignUpNoticeView, getString(R.string.sign_up_notice), new ClickableSpan() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                builder.setMessage(R.string.secure_credentials_message);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    private void showError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }

    private boolean validateInputLayout(@NonNull TextInputLayout inputLayout, @StringRes int errorResId) {
        if (TextUtils.isEmpty(inputLayout.getEditText().getText().toString())) {
            inputLayout.setError(getString(errorResId));
            inputLayout.setErrorEnabled(true);
            return false;
        }

        inputLayout.setError(null);
        inputLayout.setErrorEnabled(false);
        return true;
    }
}

