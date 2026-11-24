
public class SleepRecord extends CareRecord {
    private final int duration;
    public SleepRecord(String date, String time, String note, int duration){
        super(date,time,note);
        this.duration=duration;
    }
    public int getDuration(){
        return duration;
    }
    @Override
    public String getSummary(){
        return "😴 Uyku\n" + "Tarih: "+getDate() +  "\n" +"Saat: " + getTime()+ "\n" +"süre: " + duration +" dakika\n"+"Not: "+getNote();

    }
}
