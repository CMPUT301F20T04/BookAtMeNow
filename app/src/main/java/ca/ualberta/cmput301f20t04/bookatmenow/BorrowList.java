package ca.ualberta.cmput301f20t04.bookatmenow;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 *
 */
public class BorrowList extends BookList {
    /**
     * An enum to identify the "View Mode" this adapter is launched in - if all books are being
     * viewed, this will be null.
     */
    enum ViewMode {
        ALL,
        OWNED,
        BORROWED,
        REQUESTED;
    }

    @NonNull private ViewMode viewMode;
    @Nullable private User user;

    /**
     * Construct a view of all books in the system
     *
     * @param context
     *      The context of the calling activity, used to display objects on the screen
     * @param books
     *      A temporary parameter representing the books in the database
     */
    public BorrowList(Context context, ArrayList<Book> books) {
        super(context, books);

        viewMode = ViewMode.ALL;
        user = null;
        filteredBooks = books;
    }

    /**
     *  Construct a view of books that a user owns, has borrowed, or has requested to borrow.
     *
     * @param context
     *      The context of the calling activity, used to display objects on the screen
     * @param books
     *      A temporary parameter representing the books in the database
     * @param viewMode
     *      The "view mode" of this list of books - indicates how many books in the system need to
     *      be displayed and re-displayed after filtering
     * @param user
     *      The user whose books are being displayed
     */
    public BorrowList(Context context, ArrayList<Book> books, @NonNull ViewMode viewMode,
                      @Nullable User user)
    {
        super(context, books);

        this.viewMode = viewMode;
        this.user = user;

        for (Book book : books) {
            if (checkUser(book)) {
                filteredBooks.add(book);
            }
        }
    }

    /**
     * Checks the given book to ensure that it should be displayed in the adapter's current view
     * mode
     *
     * @param book
     * @return
     *      a boolean representing whether or not the current book should be displayed
     */
    private boolean checkUser(Book book) {
        if (user == null) {
            return true;
        }

        switch (viewMode) {
            case OWNED:
                return user.getUsername().equals(book.getOwner());
            case BORROWED:
                return user.getUsername().equals(book.getBorrower());
            case REQUESTED:
                for (String requester : book.getRequests()) {
                    if (user.getUsername().equals(requester)) {
                        return true;
                    }
                }
                return false;
            default:
                return true;
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
        convertView = inflate_helper(convertView, parent, R.layout.borrow_row);

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

    /**
     *
     * @param statusEnum
     */
    public void sort(Book.StatusEnum statusEnum) {
        Collections.sort(filteredBooks, new CompareByStatus(statusEnum));
        notifyDataSetChanged();
    }

    /**
     *
     */
    private static class CompareByStatus implements Comparator<Book> {
        private Book.StatusEnum statusEnum;

        /**
         *
         * @param statusEnum
         */
        CompareByStatus(@Nullable Book.StatusEnum statusEnum) {
            this.statusEnum = statusEnum;
        }

        /**
         *
         * @param b1
         * @param b2
         * @return
         */
        @Override
        public int compare(@NonNull Book b1, @NonNull Book b2) {
            if (statusEnum == null) { return 0; }

            if (Book.StatusEnum.valueOf(b1.getStatus()) == statusEnum &&
                    Book.StatusEnum.valueOf(b2.getStatus()) != statusEnum)
            {
                return 1;
            } else {
                return 0;
            }
        }
    }

    /**
     *
     * @param statusEnum
     */
    public void filter(@Nullable Book.StatusEnum statusEnum) {
        filteredBooks.clear();

        for (Book book : books) {
            if (checkUser(book) &&
                (statusEnum == null || Book.StatusEnum.valueOf(book.getStatus()) == statusEnum))
            {
                filteredBooks.add(book);
            }
        }

        notifyDataSetChanged();
    }
}
