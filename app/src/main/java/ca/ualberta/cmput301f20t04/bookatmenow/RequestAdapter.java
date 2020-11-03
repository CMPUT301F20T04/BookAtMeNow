package ca.ualberta.cmput301f20t04.bookatmenow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.LinkedList;
import java.util.List;

/**
 * A simple {@link ArrayAdapter} to be used in concert with {@link DBHandler#bookRequests}.
 *
 * @author Warren Stix
 * @version 0.1
 */
public class RequestAdapter extends ArrayAdapter<User> {
    LinkedList<User> requests;
    Context context;

    RequestAdapter(Context context, LinkedList<User> requests) {
        super(context, 0, requests);

        this.context = context;
        this.requests = requests;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.request_row, parent,false);
        }

        User requester = requests.get(position);

        TextView displayName = convertView.findViewById(R.id.display_name_text);

        if (requester.getUsername() != null) {
            displayName.setText(requester.getUsername());
        } else {
            displayName.setText(requester.getEmail());
        }

        return convertView;
    }
}
