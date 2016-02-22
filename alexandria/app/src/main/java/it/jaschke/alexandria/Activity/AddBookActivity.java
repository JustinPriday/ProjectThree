package it.jaschke.alexandria.Activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import butterknife.Bind;
import butterknife.ButterKnife;
import it.jaschke.alexandria.R;
import it.jaschke.alexandria.data.AlexandriaContract;
import it.jaschke.alexandria.services.BookService;
import it.jaschke.alexandria.services.DownloadImage;

/**
 * Created by justin on 16/01/06.
 */
public class AddBookActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,SearchView.OnQueryTextListener {
    private static final String LOG_TAG = AddBookActivity.class.getSimpleName();

    private String eanString;

    private final int LOADER_ID = 1;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.bookTitle)
    TextView mBookTitle;
    @Bind(R.id.bookSubTitle)
    TextView mBookSubTitle;
    @Bind(R.id.authors)
    TextView mAuthors;
    @Bind(R.id.categories)
    TextView mCategories;
    @Bind(R.id.bookCover)
    ImageView mBookCover;

    @Bind(R.id.save_button)
    Button mSaveButton;
    @Bind(R.id.delete_button)
    Button mDeleteButton;

    private SearchView searchView = null;

    private void hideSoftKeyBoard() {
        View v = this.getWindow().getCurrentFocus();
        if (v != null) {
            InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    private void startBookService(String ean) {
        Intent bookIntent = new Intent(this, BookService.class);
        bookIntent.putExtra(BookService.EAN, ean);
        bookIntent.setAction(BookService.FETCH_BOOK);
        this.startService(bookIntent);
        eanString = ean;
        restartLoader();
    }

    private void clearFields(){
        mBookTitle.setText("");
        mBookSubTitle.setText("");
        mAuthors.setText("");
        mCategories.setText("");
        mBookCover.setVisibility(View.INVISIBLE);
        mSaveButton.setVisibility(View.INVISIBLE);
        mDeleteButton.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        Log.d(TAG, "onCreate() called with: " + "savedInstanceState = [" + savedInstanceState + "]");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(getTitle());

        if (savedInstanceState != null) {
//            mTextSearch = savedInstanceState.getString(mKeySearchText);
        }
    }

    private void restartLoader() {
        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(eanString.length()==0){
            return null;
        }
        if(eanString.length()==10 && !eanString.startsWith("978")){
            eanString="978"+eanString;
        }
        return new CursorLoader(
                this,
                AlexandriaContract.BookEntry.buildFullBookUri(Long.parseLong(eanString)),
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst()) {
            return;
        }

        //Although feedback pointed to null catch being required in BookDetail.java, it appears the null
        //author book example is causing a crash condition here rather than there.

        Context context = getApplicationContext();
        String bookTitle = context.getResources().getString(R.string.no_book_title_available_message);
        if (data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.TITLE)) != null) {
            bookTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.TITLE));
        }
        mBookTitle.setText(bookTitle);

        String bookSubTitle = context.getResources().getString(R.string.no_book_subtitle_available_message);
        if (data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.SUBTITLE)) != null) {
            bookSubTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.SUBTITLE));
        }
        mBookSubTitle.setText(bookSubTitle);

        if (data.getString(data.getColumnIndex(AlexandriaContract.AuthorEntry.AUTHOR)) != null) {
            String authors = data.getString(data.getColumnIndex(AlexandriaContract.AuthorEntry.AUTHOR));
            String[] authorsArr = authors.split(",");
            mAuthors.setLines(authorsArr.length);
            mAuthors.setText(authors.replace(",", "\n"));
        } else {
            mAuthors.setLines(1);
            mAuthors.setText(context.getResources().getString(R.string.no_authors_available_message));
        }

        String imgUrl = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.IMAGE_URL));
        if(Patterns.WEB_URL.matcher(imgUrl).matches()){
            new DownloadImage(mBookCover).execute(imgUrl);
            mBookCover.setVisibility(View.VISIBLE);
        }

        String categories = context.getResources().getString(R.string.no_categories_message);
        if (data.getString(data.getColumnIndex(AlexandriaContract.CategoryEntry.CATEGORY)) != null) {
            categories = data.getString(data.getColumnIndex(AlexandriaContract.CategoryEntry.CATEGORY));
        }
        mCategories.setText(categories);

        mSaveButton.setVisibility(View.VISIBLE);
        mDeleteButton.setVisibility(View.VISIBLE);
        hideSoftKeyBoard();

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Log.v(LOG_TAG,"On Query Change ("+newText+")");

        String ean = newText;
        //catch isbn10 numbers
        if(ean.length()==10 && !ean.startsWith("978")){
            ean="978"+ean;
        }
        if(ean.length()<13){
            clearFields();
            return false;
        }
        //Once we have an ISBN, start a book intent
        startBookService(ean);

        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.book_add, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) this.getSystemService(Context.SEARCH_SERVICE);
        String mTextSearch = "";
        String mSearchTextHint = getResources().getString(R.string.input_hint);


        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(this.getComponentName()));
            searchView.setOnQueryTextListener(this);

            if (mTextSearch != null) {
                Log.d(LOG_TAG, "onCreateOptionsMenu() called with: " + "mTextSearch = [" + mTextSearch + "]");
                searchView.setQuery(mTextSearch, false);
            }
            searchView.setInputType(InputType.TYPE_CLASS_NUMBER);
            searchView.setQueryHint(mSearchTextHint);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.action_scan_book:
                IntentIntegrator integrator = new IntentIntegrator(this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
                integrator.setPrompt("Scan a barcode");
                integrator.setCameraId(0);  // Use a specific camera of the device
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(true);
                integrator.initiateScan();
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            String ean = scanResult.getContents();
            //catch a null scan, example back pressed on scan to cancel.
            if (ean != null) {
                //catch isbn10 numbers
                if (ean.length() == 10 && !ean.startsWith("978")) {
                    ean = "978" + ean;
                }
                if (ean.length() < 13) {
                    clearFields();
                    return;
                }
                //Once we have an ISBN, start a book intent
                startBookService(ean);
            }
        }
    }
}
