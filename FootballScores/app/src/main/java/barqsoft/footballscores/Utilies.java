package barqsoft.footballscores;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by yehya khaled on 3/3/2015.
 */

public class Utilies
{
    //Old League IDs still in getLeague. Updated for 2015 Season.
    public static final int SERIE_A = 357;
    public static final int PREMIER_LEGAUE = 354;
    public static final int CHAMPIONS_LEAGUE = 362;
    public static final int PRIMERA_DIVISION = 358;
    public static final int BUNDESLIGA = 351;

    public static final int BUNDESLIGA1_2015 = 394;
    public static final int BUNDESLIGA2_2015 = 395;
    public static final int LIGUE1_2015 = 396;
    public static final int LIGUE2_2015 = 397;
    public static final int PREMIER_LEAGUE_2015 = 398;
    public static final int PRIMERA_DIVISION_2015 = 399;
    public static final int SEGUNDA_DIVISION_2015 = 400;
    public static final int SERIE_A_2015 = 401;
    public static final int PRIMERA_LIGA_2015 = 402;
    public static final int BUNDESLIGA3_2015 = 403;
    public static final int EREDIVISIE_2015 = 404;
    public static final int CHAMPIONS_LEAGUE_2015 = 405;
    public static final int LEAGUE_ONE_2015 = 425;

    public static String getLeague(Context context, int league_num)
    {
        switch (league_num)
        {
            //Not correctly returning translatable string resources.
            //Updated to display @string values
            //Context now required to fetch resources
            case SERIE_A : return  context.getResources().getString(R.string.seriaa);
            case PREMIER_LEGAUE : return context.getResources().getString(R.string.premierleague);
            case CHAMPIONS_LEAGUE : return context.getResources().getString(R.string.champions_league);
            case PRIMERA_DIVISION : return context.getResources().getString(R.string.primeradivison);
            case BUNDESLIGA : return context.getResources().getString(R.string.bundesliga);

            case BUNDESLIGA1_2015 : return context.getResources().getString(R.string.bundesliga1_2015);
            case BUNDESLIGA2_2015 : return context.getResources().getString(R.string.bundesliga2_2015);
            case LIGUE1_2015 : return context.getResources().getString(R.string.ligue1_2015);
            case LIGUE2_2015 : return context.getResources().getString(R.string.ligue2_2015);
            case PREMIER_LEAGUE_2015 : return context.getResources().getString(R.string.premier_league_2015);
            case PRIMERA_DIVISION_2015 : return context.getResources().getString(R.string.primera_divison_2015);
            case SEGUNDA_DIVISION_2015 : return context.getResources().getString(R.string.segunda_division_2015);
            case SERIE_A_2015 : return context.getResources().getString(R.string.serie_a_2015);
            case PRIMERA_LIGA_2015 : return context.getResources().getString(R.string.primera_divison_2015);
            case BUNDESLIGA3_2015 : return context.getResources().getString(R.string.bundesliga3_2015);
            case EREDIVISIE_2015 : return context.getResources().getString(R.string.eredivisie_2015);
            case CHAMPIONS_LEAGUE_2015 : return context.getResources().getString(R.string.champions_league_2015);
            case LEAGUE_ONE_2015 : context.getResources().getString(R.string.league_one_2015);

            default: return "Not known League Please report";

        }
    }
    public static String getMatchDay(int match_day,int league_num)
    {
        if((league_num == CHAMPIONS_LEAGUE) || (league_num == CHAMPIONS_LEAGUE_2015))
        {
            if (match_day <= 6)
            {
                return "Group Stages, Matchday : 6";
            }
            else if(match_day == 7 || match_day == 8)
            {
                return "First Knockout round";
            }
            else if(match_day == 9 || match_day == 10)
            {
                return "QuarterFinal";
            }
            else if(match_day == 11 || match_day == 12)
            {
                return "SemiFinal";
            }
            else
            {
                return "Final";
            }
        }
        else
        {
            return "Matchday : " + String.valueOf(match_day);
        }
    }

    public static String getScores(int home_goals,int awaygoals)
    {
        if(home_goals < 0 || awaygoals < 0)
        {
            return " - ";
        }
        else
        {
            return String.valueOf(home_goals) + " - " + String.valueOf(awaygoals);
        }
    }

    public static int getTeamCrestByTeamName (String teamname)
    {
        if (teamname==null){return R.drawable.no_icon;}
        switch (teamname)
        { //This is the set of icons that are currently in the app. Feel free to find and add more
            //as you go.
            case "Arsenal London FC" : return R.drawable.arsenal;
            case "Manchester United FC" : return R.drawable.manchester_united;
            case "Swansea City" : return R.drawable.swansea_city_afc;
            case "Leicester City" : return R.drawable.leicester_city_fc_hd_logo;
            case "Everton FC" : return R.drawable.everton_fc_logo1;
            case "West Ham United FC" : return R.drawable.west_ham;
            case "Tottenham Hotspur FC" : return R.drawable.tottenham_hotspur;
            case "West Bromwich Albion" : return R.drawable.west_bromwich_albion_hd_logo;
            case "Sunderland AFC" : return R.drawable.sunderland;
            case "Stoke City FC" : return R.drawable.stoke_city;
            default: return R.drawable.no_icon;
        }
    }

    public static String getDateToday() {
        Date today = new Date(System.currentTimeMillis());
        SimpleDateFormat mformat = new SimpleDateFormat("yyyy-MM-dd");
        return mformat.format(today);
    }
}
