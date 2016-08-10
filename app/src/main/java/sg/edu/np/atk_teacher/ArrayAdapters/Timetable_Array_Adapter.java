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
import java.util.TreeSet;

import sg.edu.np.atk_teacher.Items.Item_timetable;
import sg.edu.np.atk_teacher.Items.Item_timetable_seperator;
import sg.edu.np.atk_teacher.R;

/**
 * Created by Lord One on 7/20/2016.
 */
public class Timetable_Array_Adapter extends ArrayAdapter<Item_timetable> {
    private Context c;
    private int id_item;
    private int id_sp;
    private List subjects;
    private TreeSet<Integer> spIdx = null;

    public static final int TYPE_ITEM = 0;
    public static final int TYPE_SEPARATOR = 1;
    public static final int TYPE_MAX_COUNT = TYPE_SEPARATOR + 1;

    public Timetable_Array_Adapter(Context context, int itemViewResourceId, int spViewResourceId,
                                    List objects) {
        super(context, itemViewResourceId, objects);
        c = context;
        id_item = itemViewResourceId;
        id_sp = spViewResourceId;
        subjects = objects;
        spIdx = new TreeSet<>();
    }
    public Item_timetable getItem(int i)
    {
        return (Item_timetable) subjects.get(i);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if(getItemViewType(position) == TYPE_ITEM) {
                v = vi.inflate(id_item, null);
            }
            else {
                v = vi.inflate(id_sp, null);
            }
        }

        if(getItemViewType(position) == TYPE_ITEM)
            v = inflateItem(v, position);
        else
            v = inflateSeparator(v, position);

        return v;
               /* create a new view of my layout and inflate it in the row */
        //convertView = ( RelativeLayout ) inflater.inflate( resource, null );


    }

    public View inflateItem(View v, int position) {
        final Item_timetable o = (Item_timetable) subjects.get(position);
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
                t1.setText(o.getCatalog_number() + ", " + o.getClass_section());
            if(t2!=null)
                t2.setText(o.getTime());
            if(t3!=null)
                t3.setText(o.getLocation());
            if(t4!=null)
                t4.setText(String.valueOf(o.getN_students_taken()) + '/' + String.valueOf(o.getN_students()));

        }
        return v;
    }

    public View inflateSeparator(View v, int position) {
        final Item_timetable_seperator o = (Item_timetable_seperator) subjects.get(position);
        if(o != null) {
            TextView t1 = (TextView) v.findViewById(R.id.separator_text);
            if(t1 != null)
                t1.setText(o.getDate());
        }
        return v;
    }

    public void addSeparatorIdx(int idx) {
        spIdx.add(idx);
    }

    public void clearSpIdx() {
        if(spIdx != null)
            spIdx.clear();
    }

    @Override
    public int getItemViewType(int position) {
        return spIdx.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_MAX_COUNT;
    }

}
