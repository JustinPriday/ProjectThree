package barqsoft.footballscores.Data;

import android.database.Cursor;
import android.util.Log;

import barqsoft.footballscores.DatabaseContract;

/**
 * Created by justinpriday on 2016/02/10.
 */
public class MatchFixture {

    private static final String LOG_TAG = MatchFixture.class.getSimpleName();
    public String matchId;
    public String matchDate;
    public String matchTime;
    public Integer matchDay;

    public String homeName;
    public Integer homeGoals;

    public String awayName;
    public Integer awayGoals;

    public Integer leagueId;

    public static MatchFixture fromCursor(Cursor cursor) {
        MatchFixture matchFixture = new MatchFixture();

        matchFixture.matchDate = cursor.getString(cursor.getColumnIndex(DatabaseContract.scores_table.DATE_COL));
        matchFixture.matchTime = cursor.getString(cursor.getColumnIndex(DatabaseContract.scores_table.TIME_COL));

        matchFixture.homeName = cursor.getString(cursor.getColumnIndex(DatabaseContract.scores_table.HOME_COL));
        matchFixture.homeGoals = cursor.getInt(cursor.getColumnIndex(DatabaseContract.scores_table.HOME_GOALS_COL));

        matchFixture.awayName = cursor.getString(cursor.getColumnIndex(DatabaseContract.scores_table.AWAY_COL));
        matchFixture.awayGoals = cursor.getInt(cursor.getColumnIndex(DatabaseContract.scores_table.AWAY_GOALS_COL));

        matchFixture.leagueId = cursor.getInt(cursor.getColumnIndex(DatabaseContract.scores_table.LEAGUE_COL));

        return matchFixture;
    }
}
