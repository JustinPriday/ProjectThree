package it.jaschke.alexandria.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
//import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.jaschke.alexandria.Fragments.About;
import it.jaschke.alexandria.Fragments.AddBook;
import it.jaschke.alexandria.Fragments.BookDetail;
import it.jaschke.alexandria.Fragments.ListOfBooks;
//import it.jaschke.alexandria.Fragments.NavigationDrawerFragment;
import it.jaschke.alexandria.R;
import it.jaschke.alexandria.api.Callback;


public class MainActivity extends AppCompatActivity implements Callback,ListOfBooks.FabContainer {
// Removed NavigationDrawerFragment.NavigationDrawerCallbacks
//


    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
//    private NavigationDrawerFragment navigationDrawerFragment;

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence title;
    public static boolean IS_TABLET = false;
    private BroadcastReceiver messageReciever;

    public static final String MESSAGE_EVENT = "MESSAGE_EVENT";
    public static final String MESSAGE_KEY = "MESSAGE_EXTRA";

    private static final String STATE_CURRENT_FRAGMENT = "current_fragment_position";

    @Bind(R.id.fab)
    FloatingActionButton addBookFab;

    @Bind(R.id.toolbar)
    Toolbar actionToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IS_TABLET = isTablet();
        if(IS_TABLET){
            setContentView(R.layout.activity_main_tablet);
        }else {
            setContentView(R.layout.activity_main);
        }

        ButterKnife.bind(this);

        messageReciever = new MessageReciever();
        IntentFilter filter = new IntentFilter(MESSAGE_EVENT);
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReciever,filter);

//        navigationDrawerFragment = (NavigationDrawerFragment)
//                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        title = getTitle();
        setSupportActionBar(actionToolbar);

        // Set up the drawer.
//        navigationDrawerFragment.setUp(R.id.navigation_drawer,
//                    (DrawerLayout) findViewById(R.id.drawer_layout));

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment nextFragment;
        nextFragment = new ListOfBooks();
        fragmentManager.beginTransaction()
                .replace(R.id.container, nextFragment)
                .addToBackStack((String) title)
                .commit();

    }

//    @Override
//    public void onNavigationDrawerItemSelected(int position) {
//
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        Fragment nextFragment;
//
//        switch (position){
//            default:
//            case 0:
//                nextFragment = new ListOfBooks();
//                break;
//            case 1:
//                nextFragment = new AddBook();
//                break;
//            case 2:
//                nextFragment = new About();
//                break;
//
//        }
//
//        fragmentManager.beginTransaction()
//                .replace(R.id.container, nextFragment)
//                .addToBackStack((String) title)
//                .commit();
//    }

    @OnClick(R.id.fab)
    public void onFabClick() {
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        addBookFab.setVisibility(View.GONE);
//        fragmentManager.beginTransaction()
//                .replace(R.id.container, new AddBook())
//                .addToBackStack((String) title)
//                .commit();

        Intent addBookIntent = new Intent(this, AddBookActivity.class);
        ActivityCompat.startActivity(this, addBookIntent, null);
    }

    public void setTitle(int titleId) {
        title = getString(titleId);
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(title);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        //Added Switch Statement to handle back button from Add Book Fragment.
        switch (item.getItemId()) {

            case android.R.id.home: {
                getSupportFragmentManager().popBackStack();
                return true;
            }

            case R.id.action_about: {
                FragmentManager fragmentManager = getSupportFragmentManager();
                Fragment nextFragment;
                nextFragment = new About();
                addBookFab.setVisibility(View.GONE);
                fragmentManager.beginTransaction()
                        .replace(R.id.container, nextFragment)
                        .addToBackStack((String) title)
                        .commit();
                return true;
            }

            default:
                Log.e(LOG_TAG, "Unknown menu item selected");

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReciever);
        super.onDestroy();
    }

    @Override
    public void onItemSelected(String ean) {
        Bundle args = new Bundle();
        args.putString(BookDetail.EAN_KEY, ean);

        BookDetail fragment = new BookDetail();
        fragment.setArguments(args);

        int id = R.id.container;
        if(findViewById(R.id.right_container) != null){
            id = R.id.right_container;
        }

        addBookFab.setVisibility(View.GONE);

        getSupportFragmentManager().beginTransaction()
                .replace(id, fragment)
                .addToBackStack("Book Detail")
                .commit();


    }

    @Override
    public void showFab(Boolean toShow) {
        addBookFab.setVisibility((toShow)?View.VISIBLE:View.GONE);
    }

    private class MessageReciever extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getStringExtra(MESSAGE_KEY)!=null){
                Toast.makeText(MainActivity.this, intent.getStringExtra(MESSAGE_KEY), Toast.LENGTH_LONG).show();
            }
        }
    }

    public void goBack(View view){
        getSupportFragmentManager().popBackStack();
    }

    private boolean isTablet() {
        return (getApplicationContext().getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount()<2){
            finish();
        }
        super.onBackPressed();
    }




}