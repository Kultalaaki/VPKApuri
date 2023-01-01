package kultalaaki.vpkapuri.alarmdetection;

import java.util.ArrayList;

public class AlarmNumberListsStub implements NumberLists {
    @Override
    public ArrayList<String> getAlarmNumbers() {
        ArrayList<String> alarmNumbers = new ArrayList<>();
        alarmNumbers.add("0401234567");

        return alarmNumbers;
    }

    @Override
    public ArrayList<String> getMemberNumbers() {
        ArrayList<String> memberNumbers = new ArrayList<>();
        memberNumbers.add("0401234566");

        return memberNumbers;
    }

    @Override
    public ArrayList<String> getVapepaNumbers() {
        ArrayList<String> vapepaNumbers = new ArrayList<>();
        vapepaNumbers.add("0401234565");

        return vapepaNumbers;
    }
}
