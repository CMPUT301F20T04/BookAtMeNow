package ca.ualberta.cmput301f20t04.bookatmenow;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatDialogFragment;

public class FilterDialog extends AppCompatDialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();

        final View view = inflater.inflate(R.layout.filter_dialog, null);

        final CheckBox borrowed = view.findViewById(R.id.borrowed);
        final CheckBox available = view.findViewById(R.id.available);
        final CheckBox pending = view.findViewById(R.id.pending);

        final Spinner spinner = view.findViewById(R.id.sort_by);
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.sort_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

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
                        String selection = spinner.getSelectedItem().toString();

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
