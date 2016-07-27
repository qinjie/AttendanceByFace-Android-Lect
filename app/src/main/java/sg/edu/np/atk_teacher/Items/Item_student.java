package sg.edu.np.atk_teacher.Items;

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
}