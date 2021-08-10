package com.symbol.wfc.pttpro.intent;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private View mParentLayout;
    private Spinner mRecipientTypeView;
    private EditText mUsername;
    private EditText mMessage;
    private Button mActionSend;
    private Button mActionCallEnd;

    private boolean mCall = true;
    private int mSelectedRecipientType = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mParentLayout = findViewById(android.R.id.content);
        mRecipientTypeView = findViewById(R.id.recipient);
        mUsername = findViewById(R.id.username);
        mMessage = findViewById(R.id.message);
        mActionSend = findViewById(R.id.action_send);
        mActionCallEnd = findViewById(R.id.action_end_call);

        List<String> list = getRecipientTypeList();
        SpinnerAdapter adapter = new SpinnerAdapter(this, list);
        mRecipientTypeView.setAdapter(adapter);
        mRecipientTypeView.setSelection(0);
        mRecipientTypeView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int hintId = (position == 0) ? R.string.user : R.string.group;
                mUsername.setHint(getString(R.string.hint_username, getString(hintId)));
                mSelectedRecipientType = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        updateButtonView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.info:
                showInfoView();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.action_call:
                mCall = true;
                updateButtonView();
                break;
            case R.id.action_message:
                mCall = false;
                updateButtonView();
                break;
            case R.id.action_send:
                if (TextUtils.isEmpty(mUsername.getText())) {
                    showSnackBarView(R.string.username_cannot_be_empty);
                    return;
                }
                if (!mCall && TextUtils.isEmpty(mMessage.getText())) {
                    showSnackBarView(R.string.message_cannot_be_empty);
                    return;
                }
                handleSendIntent();
                break;
            case R.id.action_end_call:
                handleCallEnd();
                break;

            case R.id.info_icon:
                int stringId = (mSelectedRecipientType == 0) ? R.string.adhoc_info_message :
                        R.string.group_info_message;
               showSnackBarView(stringId);
                break;
        }
    }

    private List<String> getRecipientTypeList() {
        List<String> list = new ArrayList<>();
        list.add("Adhoc");
        list.add("Group");
        return list;
    }

    private void updateButtonView() {
        int stringId = mCall ? R.string.call : R.string.message;
        mActionSend.setText(getString(stringId));
        mMessage.setVisibility(mCall ? View.GONE : View.VISIBLE);
        mActionCallEnd.setVisibility(mCall ? View.VISIBLE : View.GONE);
    }

    private void handleSendIntent() {
        String user = mUsername.getText().toString();
        String action = mCall ? Utils.ACTION_CALL : Utils.ACTION_MESSAGE;
        Intent intent = new Intent(action);
        Bundle bundle = new Bundle();
        int type = (mSelectedRecipientType == -1) ? 0 : mSelectedRecipientType;
        bundle.putInt(Utils.RECIPIENT_TYPE, type);
        bundle.putString(Utils.RECIPIENT_NAME, user);
        if (!mCall) {
            bundle.putString(Utils.MESSAGE, mMessage.getText().toString());
        }
        intent.putExtras(bundle);
        sendBroadcast(intent);
        updateButtonView();
    }

    private void handleCallEnd() {
        sendBroadcast(new Intent(Utils.ACTION_END_CALL));
        new Handler(Looper.myLooper()).postDelayed(() -> showSnackBarView(R.string.call_ended_successfully), 100);
    }

    private void showInfoView() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View mAlertDialogView = getLayoutInflater().inflate(R.layout.custom_alert_view, null);
        TextView title = mAlertDialogView.findViewById(R.id.message) ;
        title.setText(getString(R.string.info_title));
         Button okButton= mAlertDialogView.findViewById(R.id.ok_button);
        builder.setView(mAlertDialogView);
        AlertDialog mAssignToDialog = builder.create();
        mAssignToDialog.show();
        okButton.setOnClickListener(v -> {
            if (mAssignToDialog != null) {
                mAssignToDialog.dismiss();
            }
        });
    }

    private void showSnackBarView(int stringId) {
        Snackbar.make(mParentLayout, getString(stringId), Snackbar.LENGTH_SHORT).show();
    }
}