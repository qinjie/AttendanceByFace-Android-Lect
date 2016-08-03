package sg.edu.np.atk_teacher.RequestClasses;

/**
 * Created by Champ on 29/07/2016.
 */
public class TimetableClass {
    String fromDate;
    String classSection;

    public TimetableClass(String fromDate, String classSection) {
        this.fromDate = fromDate;
        this.classSection = classSection;
    }
}
