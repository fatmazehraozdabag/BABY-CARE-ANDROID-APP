
public abstract class CareRecord {
    private String date;
    private String time;
    private String note;

    public CareRecord(String date, String time, String note){
        this.date=date;
        this.time=time;
        this.note=note;
    }
    public String getDate(){
        return date;
    }
    public String getTime() {
        return time;
    }
    public String getNote(){
        return note;
    }
    public abstract String getSummary();


}
