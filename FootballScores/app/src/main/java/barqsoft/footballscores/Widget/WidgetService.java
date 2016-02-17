package barqsoft.footballscores.Widget;

import android.annotation.TargetApi;
import android.appwidget.AppWidgetManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;

import barqsoft.footballscores.Data.MatchFixture;
import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.R;
import barqsoft.footballscores.ScoresProvider;
import barqsoft.footballscores.Utilies;

/**
 * Created by justinpriday on 2016/02/10.
 */
public class WidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WidgetRemoteViewFactory(getApplicationContext(), intent);
    }
}

class WidgetRemoteViewFactory implements RemoteViewsService.RemoteViewsFactory {

    private static final String LOG_TAG = WidgetRemoteViewFactory.class.getSimpleName();
    private final Context mContext;
    private final ContentResolver mContentResolver;
    private final int mAppWidgetId;
    private ArrayList<MatchFixture> mMatchFixtures;


    public WidgetRemoteViewFactory(Context context, Intent intent) {
        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        mContentResolver = mContext.getContentResolver();
        mMatchFixtures = new ArrayList<>();
    }

    @Override
    public void onCreate() {
        Cursor cursor = mContentResolver.query(
                DatabaseContract.scores_table.buildScoreWithDate(),
                null,
                DatabaseContract.scores_table.DATE_COL + " = ?",
                new String[]{Utilies.getDateToday()},
                null
        );

        Log.v(LOG_TAG,"Got "+cursor.getCount()+" items");
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                mMatchFixtures.add(MatchFixture.fromCursor(cursor));
                cursor.moveToNext();
            }
        }


    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void onDestroy() {
        mMatchFixtures.clear();

    }

    @Override
    public int getCount() { return mMatchFixtures.size(); }

    @Override
    public RemoteViews getViewAt(int position) {
        MatchFixture matchFixture = mMatchFixtures.get(position);

        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.football_scores_widget_item);

        if ((matchFixture.homeGoals > -1) && (matchFixture.awayGoals > -1)) {
            remoteViews.setTextViewText(R.id.widget_item_score_text,matchFixture.homeGoals+" - "+matchFixture.awayGoals);
        } else {
            remoteViews.setTextViewText(R.id.widget_item_score_text," - ");
        }
        remoteViews.setTextViewText(R.id.widget_item_home_name,matchFixture.homeName);
        remoteViews.setTextViewText(R.id.widget_item_away_name,matchFixture.awayName);
        remoteViews.setTextViewText(R.id.widget_item_match_time,matchFixture.matchTime);

        remoteViews.setImageViewResource(R.id.widget_item_home_icon, Utilies.getTeamCrestByTeamName(matchFixture.homeName));
        remoteViews.setImageViewResource(R.id.widget_item_away_icon,Utilies.getTeamCrestByTeamName(matchFixture.awayName));

//        mHolder.home_crest.setImageResource(Utilies.getTeamCrestByTeamName(
//                cursor.getString(COL_HOME)));
//        mHolder.away_crest.setImageResource(Utilies.getTeamCrestByTeamName(
//                cursor.getString(COL_AWAY)));
        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
