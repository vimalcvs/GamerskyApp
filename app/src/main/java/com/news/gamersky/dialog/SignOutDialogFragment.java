package com.news.gamersky.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.news.gamersky.R;

import java.util.Objects;

public class SignOutDialogFragment extends DialogFragment {

    SignOutDialogListener signOutDialogListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        builder.setTitle(R.string.sign_out)
                .setPositiveButton(R.string.ok, (dialog, id) -> signOutDialogListener.onConfirmClick(SignOutDialogFragment.this)).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        signOutDialogListener.onCancelClick(SignOutDialogFragment.this);
                    }
                });
        return builder.create();
    }

    public void setSignOutDialogListener(SignOutDialogListener signOutDialogListener) {
        this.signOutDialogListener = signOutDialogListener;
    }

    public interface SignOutDialogListener {
        void onConfirmClick(DialogFragment dialog);

        void onCancelClick(DialogFragment dialog);
    }
}
