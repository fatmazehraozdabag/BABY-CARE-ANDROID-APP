public class BathRecord extends CareRecord{
    private int duration;
    public BathRecord(String date,String time,String note,int duration){
        super(date, time, note);
        this.duration=duration;
    }
    public int getDuration(){
        return duration;
    }
    @Override
    public String getSummary(){
        return "🛀 Banyo\n"+"Tarih: "+getDate()+"\n"+"Saat: "+getTime()+"\n"+"Süre: "+duration+ " dakika\n"+"Not: "+getNote();
    }
}
