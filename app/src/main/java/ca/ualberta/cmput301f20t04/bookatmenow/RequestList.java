package ca.ualberta.cmput301f20t04.bookatmenow;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 *
 * @version 0.3
 */
public class RequestList extends BookList {
    private ArrayList<String> requesters;

    /**
     * @param context
     * @param books
     * @param owner
     */
    public RequestList(Context context, ArrayList<Book> books, User owner) {
        super(context, books);

        // In the case of the request list, filteredBooks is represented as a LinkedList because it
        // is frequently added to and deleted from, and is never sorted, therefore not needing
        // random access.
        filteredBooks = new LinkedList<>();
        requesters = new ArrayList<>();

        for (Book book : books) {
            if (book.getOwner().equals(owner.getUsername()) &&
                Book.StatusEnum.valueOf(book.getStatus()) == Book.StatusEnum.Pending)
            {
                for (String requester : book.getRequests()) {
                    filteredBooks.add(book);
                    requesters.add(requester);
                }
            }
        }
    }

    /**
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
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

    /**
     * A required method from {@link android.widget.BaseAdapter} for getting a unique identifying
     * long value from an element of the internal list at a given position. In this case, because
     * the same book can be in this list multiple times, and the order of the filteredList is never
     * changed, the book's position itself is its unique identifier.
     *
     * @param position
     *      The position in the internal filtered list from which to get a unique feature of the
     *      element
     * @return
     *      The book's position
     */
    @Override
    public long getItemId(int position) {
        return position;
    }
}
