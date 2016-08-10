package sg.edu.np.atk_teacher.Items;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import sg.edu.np.atk_teacher.BaseClasses.GV;

/**
 * Created by Champ on 10/08/2016.
 */
public class Item_timetable_seperator {
    private String date;

    public Item_timetable_seperator(JSONObject data) {
        try {
            parseDate(data.getString("date"));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getDate() {
        return date;
    }

    public void parseDate(String _date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        try {
            date = format.parse(_date);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);

            int year = cal.get(Calendar.YEAR);
            String month = GV.MONTH_NAME[cal.get(Calendar.MONTH)];
            int day = cal.get(Calendar.DAY_OF_MONTH);

            String day_of_week = GV.DAY_OF_WEEK[cal.get(Calendar.DAY_OF_WEEK)];

            this.date = year + "-" + month + "-" + String.format("%02d", day) + ", " + day_of_week;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
