
public class FeedingRecord extends CareRecord{
    private int amount;
    private int duration;

    public FeedingRecord(String date, String time, String note, int amount,int duration){
        super(date,time,note);
        this.amount=amount;
        this.duration=duration;
    }
    public int getAmount(){
        return amount;
    }
    public int getDuration(){
        return duration;
    }
    @Override
    public String getSummary(){
        return "🍼 Beslenme\n"+"Tarih: "+getDate()+"\n"+"Saat: "+getTime()+"\n"+"Miktar: "+amount+" ml\n"+"Süre: "+duration+" dakika\n"+"Not: "+getNote();
    }
}
