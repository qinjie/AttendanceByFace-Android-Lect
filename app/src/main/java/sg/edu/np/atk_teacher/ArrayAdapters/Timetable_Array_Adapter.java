package sg.edu.np.atk_teacher.ArrayAdapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import sg.edu.np.atk_teacher.Items.Item_timetable;
import sg.edu.np.atk_teacher.R;

/**
 * Created by Lord One on 7/20/2016.
 */
public class Timetable_Array_Adapter extends ArrayAdapter<Item_timetable> {
    private Context c;
    private int id;
    private List<Item_timetable> subjects;

    public Timetable_Array_Adapter(Context context, int textViewResourceId,
                                    List<Item_timetable> objects) {
        super(context, textViewResourceId, objects);
        c = context;
        id = textViewResourceId;
        subjects = objects;
    }
    public Item_timetable getItem(int i)
    {
        return subjects.get(i);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(id, null);
        }

               /* create a new view of my layout and inflate it in the row */
        //convertView = ( RelativeLayout ) inflater.inflate( resource, null );

        final Item_timetable o = subjects.get(position);
        if (o != null) {
            TextView t1 = (TextView) v.findViewById(R.id.classid_text);
            TextView t2 = (TextView) v.findViewById(R.id.subject_text);
            TextView t3 = (TextView) v.findViewById(R.id.timeanddate_text);
            TextView t4 = (TextView) v.findViewById(R.id.all_taken_text);

            ImageView imageCity = (ImageView) v.findViewById(R.id.percent_history_image);

            Drawable image = null;
            int percent_taken;
            if(o.getN_students() > 0)
                percent_taken = o.getN_students_taken() * 100 / o.getN_students();
            else
                percent_taken = 0;
            if(percent_taken >=  90)
                image = ContextCompat.getDrawable(c, R.drawable.dark_green_round_with_stroke);
            else if(percent_taken >= 50)
                image = ContextCompat.getDrawable(c, R.drawable.orange_round_with_stroke);
            else
                image = ContextCompat.getDrawable(c, R.drawable.red_round_with_stroke);

            imageCity.setImageDrawable(image);


            if(t1!=null)
                t1.setText(o.getLesson_id());
            if(t2!=null)
                t2.setText(o.getSubject_name());
            if(t3!=null)
                t3.setText(o.getTimeAndDate());
            if(t4!=null)
                t4.setText(String.valueOf(o.getN_students_taken()) + '/' + String.valueOf(o.getN_students()));

        }
        return v;
    }
}
