package sg.edu.np.atk_teacher.Items;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import sg.edu.np.atk_teacher.BaseClasses.GV;

/**
 * Created by Lord One on 7/20/2016.
 */
public class Item_timetable {
    private String catalog_number;
    private String class_section;
    private String lesson_id;
    private int start_hour;
    private int start_minute;
    private int end_hour;
    private int end_minute;
    private int year;
    private int month;
    private int day;
    private int n_students_taken;
    private int n_students;
    private String location;
    private String date;

    public Item_timetable(JSONObject item) {
        try {
            catalog_number = item.getString("catalog_number");
            class_section = item.getString("class_section");
            lesson_id = item.getString("lesson_id");
            n_students_taken = item.getInt("presentStudent");
            n_students = item.getInt("totalStudent");
            location = item.getString("location");
            start_hour = Integer.valueOf(item.getString("start_time").substring(0, 2));
            start_minute = Integer.valueOf(item.getString("start_time").substring(3, 5));
            end_hour = Integer.valueOf(item.getString("end_time").substring(0, 2));
            end_minute = Integer.valueOf(item.getString("start_time").substring(3, 5));

            String _date = item.getString("date");
            parseDate(_date);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getCatalog_number() {
        return catalog_number;
    }

    public String getClass_section() {
        return class_section;
    }

    public String getLesson_id() {
        return lesson_id;
    }

    public int getStart_hour() {
        return start_hour;
    }

    public int getStart_minute() {
        return start_minute;
    }

    public int getEnd_hour() {
        return end_hour;
    }

    public int getEnd_minute() {
        return end_minute;
    }

    public int getN_students_taken() {
        return n_students_taken;
    }

    public int getN_students() {
        return n_students;
    }

    public String getTime() {
        return String.format("%02d", start_hour) + ":" + String.format("%02d", start_minute) +
                " - " + String.format("%02d", end_hour) + ":" + String.format("%02d", end_minute);
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

            year = cal.get(Calendar.YEAR);
            month = cal.get(Calendar.MONTH) + 1;
            day = cal.get(Calendar.DAY_OF_MONTH);

            this.date = year + "-" + String.format("%02d",month) + "-" + String.format("%02d", day);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getLocation() {
        return location;
    }

    public void add_to_n_students_taken(int n) {
        n_students_taken += n;
    }

    public int compareTo(Item_timetable o) {
        if(this.getN_students() < o.getN_students())
            return -1;
        return 1;
    }
}
