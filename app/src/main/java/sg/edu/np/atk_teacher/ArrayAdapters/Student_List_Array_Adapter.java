package sg.edu.np.atk_teacher.ArrayAdapters;

/**
 * Created by Lord One on 7/19/2016.
 */
import java.util.List;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import sg.edu.np.atk_teacher.BaseClasses.GV;
import sg.edu.np.atk_teacher.Items.Item_student;
import sg.edu.np.atk_teacher.R;

public class Student_List_Array_Adapter extends ArrayAdapter<Item_student>{

    private Context c;
    private int id;
    private List<Item_student>items;

    public Student_List_Array_Adapter(Context context, int textViewResourceId,
                            List<Item_student> objects) {
        super(context, textViewResourceId, objects);
        c = context;
        id = textViewResourceId;
        items = objects;
    }
    public Item_student getItem(int i)
    {
        return items.get(i);
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

        final Item_student o = items.get(position);
        if (o != null) {
            TextView t1 = (TextView) v.findViewById(R.id.name_text);
            TextView t2 = (TextView) v.findViewById(R.id.studentID_text);
            LinearLayout l1 = (LinearLayout) v.findViewById(R.id.history);
                       /* Take the ImageView from layout and set the city's image */
            ImageView imageCity = (ImageView) v.findViewById(R.id.fd_Icon1);

            Drawable image = null;
            int current_status = o.getCurrent_status();
            if(current_status == GV.attend_code)
                image = ContextCompat.getDrawable(c, R.drawable.dark_green_round_with_stroke);
            if(current_status == GV.late_code)
                image = ContextCompat.getDrawable(c, R.drawable.orange_round_with_stroke);
            if(current_status == GV.absent_code)
                image = ContextCompat.getDrawable(c, R.drawable.red_round_with_stroke);
            if(current_status == GV.notyet_code)
                image = ContextCompat.getDrawable(c, R.drawable.light_grey_round_with_stroke);

            imageCity.setImageDrawable(image);

            if(t1!=null)
                t1.setText(o.getName());
            if(t2!=null)
                t2.setText("ID: " + o.getId());

            TextView tmp;
            tmp = (TextView) v.findViewById(R.id.his_attend_text);
            tmp.setText(String.valueOf(o.getHistory_n_attend()));
            tmp = (TextView) v.findViewById(R.id.his_late_text);
            tmp.setText(String.valueOf(o.getHistory_n_late()));
            tmp = (TextView) v.findViewById(R.id.his_absent_text);
            tmp.setText(String.valueOf(o.getHistory_n_absent()));

        }
        return v;
    }
}