package kultalaaki.vpkapuri;

import java.time.DayOfWeek;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

public class WeekTimer {

    private String name, firstDay, secondDay, thirdDay, fourthDay, fifthDay, sixthDay, seventhDay, startTime, stopTime;
    private String [] daysOfWeek;
    Calendar calendar = Calendar.getInstance();

    public WeekTimer (String name){
        this.name = name;
        this.firstDay = "Monday";
        this.secondDay = "Tuesday";
        this.thirdDay = "Wednesday";
        this.fourthDay = "Thursday";
        this.fifthDay = "Friday";
        this.sixthDay = "Saturday";
        this.seventhDay = "Sunday";
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setStopTime(String stopTime) {
        this.stopTime = stopTime;
    }
}
