package com.news.gamersky.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.news.gamersky.R;

import java.util.Objects;

public class ImageDialogFragment extends DialogFragment {
    // Use this instance of the interface to deliver action events
    ImageDialogListener listener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (ImageDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(getActivity().toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());


        builder.setItems(R.array.image_box, (dialog, which) -> {

            if (which == 0) {
                listener.onDownloadClick(ImageDialogFragment.this);
            }
            if (which == 1) {
                listener.onShareClick(ImageDialogFragment.this);
            }
        });
        return builder.create();
    }

    public interface ImageDialogListener {
        void onDownloadClick(DialogFragment dialog);

        void onShareClick(DialogFragment dialog);
    }
}
