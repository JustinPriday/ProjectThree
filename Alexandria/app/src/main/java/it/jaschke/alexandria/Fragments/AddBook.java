package it.jaschke.alexandria.Fragments;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import it.jaschke.alexandria.Activity.MainActivity;
import it.jaschke.alexandria.R;
import it.jaschke.alexandria.data.AlexandriaContract;
import it.jaschke.alexandria.services.BookService;
import it.jaschke.alexandria.services.DownloadImage;
import it.jaschke.alexandria.utilities.FragmentIntentIntegrator;


public class AddBook extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,SearchView.OnQueryTextListener {
    private static final String LOG_TAG = AddBook.class.getSimpleName();
    private static final String TAG = "INTENT_TO_SCAN_ACTIVITY";
    private String eanString;
    private final int LOADER_ID = 1;
    private View rootView;
    private final String EAN_CONTENT="eanContent";
    private static final String SCAN_FORMAT = "scanFormat";
    private static final String SCAN_CONTENTS = "scanContents";

    private SearchView searchView = null;

    private String mScanFormat = "Format:";
    private String mScanContents = "Contents:";



    public AddBook(){
    }

    private void hideSoftKeyBoard() {
        View v = getActivity().getWindow().getCurrentFocus();
        if (v != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    private void startBookService(String ean) {
        Intent bookIntent = new Intent(getActivity(), BookService.class);
        bookIntent.putExtra(BookService.EAN, ean);
        bookIntent.setAction(BookService.FETCH_BOOK);
        getActivity().startService(bookIntent);
        eanString = ean;
        AddBook.this.restartLoader();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(eanString!=null) {
            outState.putString(EAN_CONTENT, eanString);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.book_add, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        String mTextSearch = "";
        String mSearchTextHint = getResources().getString(R.string.input_hint);


        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
            searchView.setOnQueryTextListener(this);

            if (mTextSearch != null) {
                Log.d(TAG, "onCreateOptionsMenu() called with: " + "mTextSearch = [" + mTextSearch + "]");
                searchView.setQuery(mTextSearch, false);
            }
            searchView.setInputType(InputType.TYPE_CLASS_NUMBER);
            searchView.setQueryHint(mSearchTextHint);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                getActivity().onBackPressed();
                return true;
            }

            case R.id.action_scan_book: {
                FragmentIntentIntegrator integrator = new FragmentIntentIntegrator(this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
                integrator.setPrompt("Scan a barcode");
                integrator.setCameraId(0);  // Use a specific camera of the device
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(true);
                integrator.initiateScan();
                return true;
            }

            default:
                Log.e(LOG_TAG, "Unknown menu item selected");

        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_add_book, container, false);

        ActionBar aBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (aBar != null) {
            aBar.setDisplayHomeAsUpEnabled(true);
            aBar.setDisplayShowHomeEnabled(true);
            aBar.setTitle(getResources().getString(R.string.scan));
        }
        setHasOptionsMenu(true);

//        ean.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                //no need
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                //no need
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                String ean =s.toString();
//                //catch isbn10 numbers
//                if(ean.length()==10 && !ean.startsWith("978")){
//                    ean="978"+ean;
//                }
//                if(ean.length()<13){
//                    clearFields();
//                    return;
//                }
//                //Once we have an ISBN, start a book intent
//                startBookService(ean);
//            }
//        });

        rootView.findViewById(R.id.scan_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // This is the callback method that the system will invoke when your button is
                // clicked. You might do this by launching another app or by including the
                //functionality directly in this app.
                // Hint: Use a Try/Catch block to handle the Intent dispatch gracefully, if you
                // are using an external app.ø
                //when you're done, remove the toast below.
                Context context = getActivity();
                CharSequence text = "This button should let you scan a book for its barcode!";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();

            }
        });

        rootView.findViewById(R.id.save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                ean.setText("");

            }
        });

        rootView.findViewById(R.id.delete_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent bookIntent = new Intent(getActivity(), BookService.class);
//                bookIntent.putExtra(BookService.EAN, ean.getText().toString());
//                bookIntent.setAction(BookService.DELETE_BOOK);
//                getActivity().startService(bookIntent);
//                ean.setText("");
            }
        });

        if(savedInstanceState!=null){
//            ean.setText(savedInstanceState.getString(EAN_CONTENT));
            eanString = savedInstanceState.getString(EAN_CONTENT);
//            ean.setHint("");
        }

        return rootView;
    }



    private void restartLoader(){
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(eanString.length()==0){
            return null;
        }
        if(eanString.length()==10 && !eanString.startsWith("978")){
            eanString="978"+eanString;
        }
        return new CursorLoader(
                getActivity(),
                AlexandriaContract.BookEntry.buildFullBookUri(Long.parseLong(eanString)),
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst()) {
            return;
        }

        //Based on reviewer feedback adding null catch and no data text string for authors.
        //Understand the importance for authors because of the array processing but,
        //for the sake of being defensive, added all around.

        Context context = getContext();
        String bookTitle = context.getResources().getString(R.string.no_book_title_available_message);
        if (data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.TITLE)) != null) {
            bookTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.TITLE));
        }
        ((TextView) rootView.findViewById(R.id.bookTitle)).setText(bookTitle);

        String bookSubTitle = context.getResources().getString(R.string.no_book_subtitle_available_message);
        if (data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.SUBTITLE)) != null) {
            bookSubTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.SUBTITLE));
        }
        ((TextView) rootView.findViewById(R.id.bookSubTitle)).setText(bookSubTitle);

        if (data.getString(data.getColumnIndex(AlexandriaContract.AuthorEntry.AUTHOR)) != null) {
            String authors = data.getString(data.getColumnIndex(AlexandriaContract.AuthorEntry.AUTHOR));
            String[] authorsArr = authors.split(",");
            ((TextView) rootView.findViewById(R.id.authors)).setLines(authorsArr.length);
            ((TextView) rootView.findViewById(R.id.authors)).setText(authors.replace(",", "\n"));
        } else {
            ((TextView) rootView.findViewById(R.id.authors)).setLines(1);
            ((TextView) rootView.findViewById(R.id.authors)).setText(context.getResources().getString(R.string.no_authors_available_message));
        }

        String imgUrl = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.IMAGE_URL));
        if(Patterns.WEB_URL.matcher(imgUrl).matches()){
            new DownloadImage((ImageView) rootView.findViewById(R.id.bookCover)).execute(imgUrl);
            rootView.findViewById(R.id.bookCover).setVisibility(View.VISIBLE);
        }

        String categories = context.getResources().getString(R.string.no_categories_message);
        if (data.getString(data.getColumnIndex(AlexandriaContract.CategoryEntry.CATEGORY)) != null) {
            categories = data.getString(data.getColumnIndex(AlexandriaContract.CategoryEntry.CATEGORY));
        }
        ((TextView) rootView.findViewById(R.id.categories)).setText(categories);

        rootView.findViewById(R.id.save_button).setVisibility(View.VISIBLE);
        rootView.findViewById(R.id.delete_button).setVisibility(View.VISIBLE);
        hideSoftKeyBoard();
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
    }

    private void clearFields(){
        ((TextView) rootView.findViewById(R.id.bookTitle)).setText("");
        ((TextView) rootView.findViewById(R.id.bookSubTitle)).setText("");
        ((TextView) rootView.findViewById(R.id.authors)).setText("");
        ((TextView) rootView.findViewById(R.id.categories)).setText("");
        rootView.findViewById(R.id.bookCover).setVisibility(View.INVISIBLE);
        rootView.findViewById(R.id.save_button).setVisibility(View.INVISIBLE);
        rootView.findViewById(R.id.delete_button).setVisibility(View.INVISIBLE);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activity.setTitle(R.string.scan);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Log.v(LOG_TAG, "On Query Submit");
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

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.d(LOG_TAG, "onActivityResult() called with: " + "requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], intent = [" + intent + "]");
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        Log.d(LOG_TAG, "onActivityResult: scanResult:" + scanResult);
        if (scanResult != null) {
            String mTextSearch = scanResult.getContents();
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
