package ca.ualberta.cmput301f20t04.bookatmenow;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.ListIterator;

public class RequestList extends BookList {
    private ArrayList<String> requesters;

    /**
     * @param context
     * @param books
     * @param owner
     */
    public RequestList(Context context, ArrayList<Book> books, String owner) {
        super(context, books);

        requesters = new ArrayList<>();

        for (Book book : books) {
            if (book.getOwner().equals(owner) &&
                Book.StatusEnum.valueOf(book.getStatus()) == Book.StatusEnum.PENDING)
            {
                for (String requester : book.getRequests()) {
                    filteredBooks.add(book);
                    requesters.add(requester);
                }
            }
        }
    }

    @Override
    @NonNull
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflate_helper(convertView, parent, R.layout.request_row);

        Book book = filteredBooks.get(position);

        TextView title = convertView.findViewById(R.id.req_title_text);
        TextView username = convertView.findViewById(R.id.username_text);

        title.setText(book.getTitle());
        username.setText(requesters.get(position));

        return convertView;
    }
}
