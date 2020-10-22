package ca.ualberta.cmput301f20t04.bookatmenow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

/**
 *
 */
public abstract class BookList extends BaseAdapter {
    protected ArrayList<Book> books;
    private Context context;

    protected ArrayList<Book> filteredBooks;

    /**
     *
     * @param context
     * @param books
     */
    public BookList(Context context, ArrayList<Book> books) {
        this.books = books;
        this.context = context;

        filteredBooks = new ArrayList<>();
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
     * @param position
     * @throws ArrayIndexOutOfBoundsException
     */
    public void deleteItem(int position) throws ArrayIndexOutOfBoundsException {
        if (position < 0 || position >= filteredBooks.size()) {
            throw new ArrayIndexOutOfBoundsException();
        }

        books.remove(position);
        filteredBooks.remove(position);
        notifyDataSetChanged();
    }
}
