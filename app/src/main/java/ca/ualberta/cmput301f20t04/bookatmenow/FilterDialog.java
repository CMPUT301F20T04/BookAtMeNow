package ca.ualberta.cmput301f20t04.bookatmenow;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;

import androidx.appcompat.app.AppCompatDialogFragment;

/**
 * Filter dialog.
 * TODO: rework using fragments, enable searching.
 */
public class FilterDialog extends AppCompatDialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();

        final View view = inflater.inflate(R.layout.filter_dialog, null);

        final CheckBox borrowed = view.findViewById(R.id.borrowed);
        final CheckBox available = view.findViewById(R.id.available);
        final CheckBox pending = view.findViewById(R.id.pending);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        return builder
                .setView(view)
                .setTitle("Filter Books")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (borrowed.isChecked()) {
                        }
                        if (available.isChecked()) {
                        }
                        if (pending.isChecked()) {
                        }
                    }
                })
                .create();
    }
}