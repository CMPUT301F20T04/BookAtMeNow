package ca.ualberta.cmput301f20t04.bookatmenow;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * A BaseAdapter class specialized for a database of {@link Book}s to be owned, borrowed, requested,
 * or simply displayed.
 *
 * @author Warren Stix
 * @see BookList
 * @see android.widget.BaseAdapter
 * @version 0.7
 */
public class BorrowList extends BookList {
    /**
     * Identify the "View Mode" this adapter is launched in.
     *
     * @author Warren Stix
     * @see Book.StatusEnum
     * @version 0.1
     */
    enum ViewMode {
        /**
         * Display all {@link Book}s in the system
         */
        ALL,
        /**
         * Display the {@link Book}s owned by the {@link User} with a given UUID
         */
        OWNED,
        /**
         * Display the {@link Book}s being borrowed by the {@link User} with a given UUID
         */
        BORROWED,
        /**
         * Display the {@link Book}s being requested by the {@link User} with a given UUID
         */
        REQUESTED;
    }

    @NonNull private ViewMode viewMode;
    @Nullable private String uuid;

    /**
     * Construct a view of all books in the system.
     *
     * @param context
     *      The context of the calling activity, used to display objects on the screen
     */
    public BorrowList(Context context, ArrayList<Book> filteredBooks) {
        super(context, filteredBooks);

        viewMode = ViewMode.ALL;
        uuid = null;
    }

    /**
     *  Construct a view of books that a user owns, has borrowed, or has requested to borrow.
     *
     * @param context
     *      The context of the calling activity, used to display objects on the screen
     * @param viewMode
     *      The "view mode" of this list of books - indicates how many books in the system need to
     *      be displayed and re-displayed after filtering
     * @param uuid
     *      The UUID of the user whose books are being displayed
     */
    public BorrowList(Context context, ArrayList<Book> filteredBooks, @NonNull ViewMode viewMode,
                      @Nullable String uuid)
    {
        super(context, filteredBooks);

        this.viewMode = viewMode;
        this.uuid = uuid;

//        db.getAllBooks(new OnSuccessListener<List<Book>>() {
//                    @Override
//                    public void onSuccess(List<Book> books) {
//
//                        for (Book book : books) {
//                            if (checkUser(book)) {
//                                filteredBooks.add(book);
//                            }
//                        }
//
//                       Log.d(ProgramTags.DB_ALL_FOUND, "All books in database successfully found");
//                    }
//                },
//                new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.d(ProgramTags.DB_ERROR, "Not all books could be found!" + e.toString());
//                    }
//                });
    }

    /**
     * A helper method checking the given book to ensure that it should be displayed in the
     * adapter's current view mode.
     *
     * @param book
     *      The current book to check
     * @return
     *      A boolean representing whether or not the current book should be displayed
     */
    public static boolean checkUser(Book book, String uuid, ViewMode viewMode) {
        if (uuid == null) {
            return true;
        }

        switch (viewMode) {
            case OWNED:
                return uuid.equals(book.getOwner());
            case BORROWED:
                return uuid.equals(book.getBorrower());
            case REQUESTED:
                for (String requester : book.getRequests()) {
                    if (uuid.equals(requester)) {
                        return true;
                    }
                }
                return false;
            default:
                return true;
        }
    }

    /**
     * A required method from {@link android.widget.BaseAdapter} for displaying an element of the
     * internal list at a given position.
     *
     * @param position
     *      The position of the element to display from the internal list
     * @param convertView
     *      The external {@link View} in which to display the element's data
     * @param parent
     *      The {@link ViewGroup} containing the elements of the {@link android.widget.ListView}
     * @return
     *      The original given {@link View}, converted into a row of the internal list
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
     * A required method from {@link android.widget.BaseAdapter} for getting a unique identifying
     * long value from an element of the internal list at a given position. In this case, the ISBN
     * of a book can be used, as a 64-bit long can represent up to 19 digits, while an ISBN has at
     * most 13 digits.
     *
     * @param position
     *      The position in the internal filtered list from which to get a unique feature of the
     *      element
     * @return
     *      The book's unique identifying ISBN at this position
     */
    @Override
    public long getItemId(int position) {
        return Long.parseLong(filteredBooks.get(position).getIsbn());
    }

    /**
     * Sort the internal list based on a given {@link Book.StatusEnum}.
     *
     * @param statusEnum
     *      The status of books to come first in the internal list
     * @see CompareByStatus
     */
    public void sort(Book.StatusEnum statusEnum) {
        Collections.sort(filteredBooks, new CompareByStatus(statusEnum));
        notifyDataSetChanged();
    }

    /**
     * A class used to compare two books by their current status.
     * @author Warren Stix
     * @version 0.2
     * @see Comparator
     */
    public static class CompareByStatus implements Comparator<Book> {
        // TODO: only temporarily public for testing purposes!
        private Book.StatusEnum statusEnum;

        /**
         * Construct a comparator that prioritizes books with a given status
         *
         * @param statusEnum
         *      The status to prioritize
         */
        CompareByStatus(@Nullable Book.StatusEnum statusEnum) {
            this.statusEnum = statusEnum;
        }

        /**
         * A required method from the {@link Comparator} interface used to compare two elements of
         * the same class. In this case, a {@link Book} with the {@link Book.StatusEnum} that this
         * instance of this class was constructed with will have a lower value than a {@link Book}
         * with a different {@link Book.StatusEnum}. All other {@link Book}s will be considered
         * equal.
         *
         * @param book1
         *      The first {@link Book} to compare
         * @param book2
         *      The second {@link Book} to compare
         * @return
         *      The comparative value of book1 to book2
         */
        @Override
        public int compare(@NonNull Book book1, @NonNull Book book2) {
            if (statusEnum == null) { return 0; }

            if (Book.StatusEnum.valueOf(book1.getStatus()) == statusEnum &&
                    Book.StatusEnum.valueOf(book2.getStatus()) != statusEnum)
            {
                return -1;
            } else if (Book.StatusEnum.valueOf(book1.getStatus()) != statusEnum &&
                        Book.StatusEnum.valueOf(book2.getStatus()) == statusEnum)
            {
                return 1;
            } else {
                return 0;
            }
        }
    }

//    /**
//     * Change the internal filtered list to only show books with a given status.
//     *
//     * @param statusEnum
//     *      The status to filter by
//     */
//    public void filter(@Nullable final Book.StatusEnum statusEnum) {
//        db.getAllBooks(new OnSuccessListener<List<Book>>() {
//                    @Override
//                    public void onSuccess(List<Book> books) {
//                        for (Book book : books) {
//                            if (checkUser(book) &&
//                                (statusEnum == null ||
//                                 Book.StatusEnum.valueOf(book.getStatus()) == statusEnum))
//                            {
//                                filteredBooks.add(book);
//                            }
//                        }
//                       Log.d(ProgramTags.DB_ALL_FOUND, "All books in database successfully found");
//                    }
//                },
//                new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Log.d(ProgramTags.DB_ERROR, "Not all books could be found!" + e.toString());
//                    }
//                });
//        filteredBooks.clear();
//
//
//        notifyDataSetChanged();
//    }

    /**
     * Delete a book from the filtered list of books.
     *
     * This method is temporary until deletion of books from the database can be implemented.
     *
     * @param position
     *      the position in the filtered list at which a book must be deleted
     */
    public void delete(int position) {
        String isbn = filteredBooks.get(position).getIsbn();
        filteredBooks.remove(position);
    }
}
