package ca.ualberta.cmput301f20t04.bookatmenow;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.Collection;

/**
 *
 */
public class BorrowList extends BookList {
    /**
     *
     * @param context
     * @param books
     */
    public BorrowList(Context context, Collection<Book> books) { super(context, books); }

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
        convertView = inflate_helper(convertView, parent, R.layout.book_row);

        Book book = filteredBooks.get(position);

        TextView title = convertView.findViewById(R.id.title_text);
        TextView author = convertView.findViewById(R.id.author_text);
        TextView isbn = convertView.findViewById(R.id.isbn_text);
        TextView status = convertView.findViewById(R.id.status_text);
        TextView owner = convertView.findViewById(R.id.owner_text);

        title.setText(book.getTitle());
        author.setText(book.getAuthor());
        isbn.setText(book.getIsbn());
        status.setText(book.getStatus());
        owner.setText(book.getOwner());

        return convertView;
    }
}
