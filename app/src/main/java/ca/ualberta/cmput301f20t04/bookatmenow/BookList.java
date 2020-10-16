package ca.ualberta.cmput301f20t04.bookatmenow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

public class BookList extends BaseAdapter {
    private Collection<Book> books;
    private Context context;

    private ArrayList<Book> filteredBooks;

    public BookList(Context context, Collection<Book> books) {
        this.books = books;
        this.context = context;

        filteredBooks = (ArrayList<Book>) books;
    }

    @Override
    public int getCount() {
        return filteredBooks.size();
    }

    @Override
    public Object getItem(int position) {
        return filteredBooks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong(filteredBooks.get(position).getIsbn());
    }

    @Override
    @NonNull
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.book_row, parent,false);
        }

        Book book = filteredBooks.get(position);

        TextView title = convertView.findViewById(R.id.title_text);
        TextView author = convertView.findViewById(R.id.author_text);
        TextView isbn = convertView.findViewById(R.id.isbn_text);
        TextView status = convertView.findViewById(R.id.status_text);
        TextView owner =  convertView.findViewById(R.id.owner_text);

        title.setText(book.getTitle());
        author.setText(book.getAuthor());
        isbn.setText(book.getIsbn());
        status.setText(book.getStatus().toString());
        owner.setText(book.getOwner());

        return convertView;
    }

    public void sort(Book.Status status) throws NoSuchFieldException {
        Collections.sort(filteredBooks, new CompareByStatus(status));
        notifyDataSetChanged();
    }

    private static class CompareByStatus implements Comparator<Book> {
        private Book.Status status;

        CompareByStatus(Book.Status status) {
            this.status = status;
        }

        @Override
        public int compare(@NonNull Book b1, @NonNull Book b2) {
            if (b1.getStatus() == status && b2.getStatus() != status) {
                return 1;
            } else {
                return 0;
            }
        }
    }

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

    public void deleteItem(int position) throws ArrayIndexOutOfBoundsException {
        if (position < 0 || position >= filteredBooks.size()) {
            throw new ArrayIndexOutOfBoundsException();
        }

        books.remove(filteredBooks.get(position));
        filteredBooks.remove(position);
        notifyDataSetChanged();
    }
}
