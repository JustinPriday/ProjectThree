package layout;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.MainActivity;
import barqsoft.footballscores.R;
import barqsoft.footballscores.Utilies;
import barqsoft.footballscores.service.myFetchService;

/**
 * Implementation of App Widget functionality.
 */
public class FootballScoresWidget extends AppWidgetProvider {
    public static String LOG_TAG = FootballScoresWidget.class.getSimpleName();

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,0);

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.football_scores_widget);
//        views.setTextViewText(R.id.appwidget_text, widgetText);
        views.setOnClickPendingIntent(R.id.launch_app_button, pendingIntent);

        ContentResolver resolver = context.getContentResolver();

        Uri allScoresUri = DatabaseContract.BASE_CONTENT_URI;

        String todayDate = Utilies.getDateToday();
        String todayRequest = DatabaseContract.scores_table.DATE_COL+"=?";

        Cursor data = resolver.query(allScoresUri,null,todayRequest,new String[]{todayDate},null);

        if ((data == null) || (!data.moveToFirst())) {
            Log.d(LOG_TAG,"No Data in Cursor from updateAppWidget");
        } else {
            int dataCount = data.getCount();
            Log.d(LOG_TAG,"Got "+dataCount+" items in Cursor");
            while (!data.isAfterLast()) {
                String fetchedDate = data.getString(data.getColumnIndex(DatabaseContract.scores_table.DATE_COL));
                Log.d(LOG_TAG,"Got Time "+fetchedDate);
                data.moveToNext();
            }
        }

        views.setTextViewText(R.id.test_text_widget, "Did Update");

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

