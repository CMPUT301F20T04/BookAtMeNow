package ca.ualberta.cmput301f20t04.bookatmenow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

/**
 *
 */
public abstract class BookList extends BaseAdapter {
    private Collection<Book> books;
    protected Context context;

    protected ArrayList<Book> filteredBooks;

    /**
     *
     * @param context
     * @param books
     */
    public BookList(Context context, Collection<Book> books) {
        this.books = books;
        this.context = context;

        filteredBooks = (ArrayList<Book>) books;
    }

    /**
     *
     * @return
     */
    @Override
    public int getCount() {
        return filteredBooks.size();
    }

    /**
     *
     * @param position
     * @return
     */
    @Override
    public Object getItem(int position) {
        return filteredBooks.get(position);
    }

    /**
     *
     * @param position
     * @return
     */
    @Override
    public long getItemId(int position) {
        return Long.parseLong(filteredBooks.get(position).getIsbn());
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
    public abstract View getView(int position, View convertView, ViewGroup parent);

    /**
     *
     * @param convertView
     * @param parent
     * @param xml
     * @return
     */
    protected View inflate_helper(View convertView, ViewGroup parent, int xml) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(xml, parent, false);
        }
        return convertView;
    }

    /**
     *
     * @param status
     */
    public void sort(Book.Status status) {
        Collections.sort(filteredBooks, new CompareByStatus(status));
        notifyDataSetChanged();
    }

    /**
     *
     */
    private static class CompareByStatus implements Comparator<Book> {
        private Book.Status status;

        /**
         *
         * @param status
         */
        CompareByStatus(@Nullable Book.Status status) {
            this.status = status;
        }

        /**
         *
         * @param b1
         * @param b2
         * @return
         */
        @Override
        public int compare(@NonNull Book b1, @NonNull Book b2) {
            if (status == null) { return 0; }

            if (b1.getStatus() == status && b2.getStatus() != status) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    /**
     *
     * @param status
     */
    public void filter(@Nullable Book.Status status) {
        filteredBooks.clear();

        if (status == null) {
            filteredBooks = (ArrayList<Book>) books;
        } else {
            for (Book book : books) {
                if (book.getStatus() == status) {
                    filteredBooks.add(book);
                }
            }
        }

        notifyDataSetChanged();
    }

    /**
     *
     * @param position
     * @throws ArrayIndexOutOfBoundsException
     */
    public void deleteItem(int position) throws ArrayIndexOutOfBoundsException {
        if (position < 0 || position >= filteredBooks.size()) {
            throw new ArrayIndexOutOfBoundsException();
        }

        books.remove(filteredBooks.get(position));
        filteredBooks.remove(position);
        notifyDataSetChanged();
    }
}
