package sg.edu.np.atk_teacher.Items;

import sg.edu.np.atk_teacher.BaseClasses.GV;

/**
 * Created by Lord One on 7/19/2016.
 */

public class Item_student implements Comparable<Item_student>{
    private String name;
    private String id;
    private int current_status;
    private int history_n_attend;
    private int history_n_late;
    private int history_n_absent;

    public Item_student(String _name, String _id, int _current_status,
                        int _history_n_attend, int _history_n_late, int _history_n_absent)
    {
        name = _name;
        id = _id;
        current_status = _current_status;
        history_n_attend = _history_n_attend;
        history_n_late = _history_n_late;
        history_n_absent = _history_n_absent;
    }
    public String getName()
    {
        return name;
    }

    public String getId() {
        return id;
    }

    public int getCurrent_status() {
        return current_status;
    }

    public int getHistory_n_attend() {
        return history_n_attend;
    }

    public int getHistory_n_late() {
        return history_n_late;
    }

    public int getHistory_n_absent() {
        return history_n_absent;
    }

    public int compareTo(Item_student o) {
        if(this.name != null && o.getName() != null) {
            if(this.name.toLowerCase().compareTo(o.getName().toLowerCase()) != 0)
                return this.name.toLowerCase().compareTo(o.getName().toLowerCase());
            return this.getId().compareTo(o.getId());
        }
        else
            throw new IllegalArgumentException();
    }

    public void setCurrent_status(int current_status) {
        this.current_status = current_status;
    }

    public void setHistory_n_attend(int history_n_attend) {
        this.history_n_attend = history_n_attend;
    }

    public void setHistory_n_late(int history_n_late) {
        this.history_n_late = history_n_late;
    }

    public void setHistory_n_absent(int history_n_absent) {
        this.history_n_absent = history_n_absent;
    }

    public int modifyHistory(int oldstt, int newstt) {
        if(oldstt == GV.attend_code)
            history_n_attend -= 1;
        if(oldstt == GV.late_code)
            history_n_late -= 1;
        if(oldstt == GV.absent_code)
            history_n_absent -= 1;

        if(newstt == GV.attend_code)
            history_n_attend += 1;
        if(newstt == GV.late_code)
            history_n_late += 1;
        if(newstt == GV.absent_code)
            history_n_absent += 1;

        if((oldstt == GV.absent_code || oldstt == GV.notyet_code) && (newstt == GV.attend_code || newstt == GV.late_code))
            return 1;
        if((oldstt == GV.attend_code || oldstt == GV.late_code) && (newstt == GV.absent_code || newstt == GV.notyet_code))
            return -1;
        return 0;
    }
}