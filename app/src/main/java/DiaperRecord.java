
public class DiaperRecord extends CareRecord {
    private String diaperType;

    public DiaperRecord(String date, String time, String note, String diaperType){
        super(date,time,note);
        this.diaperType=diaperType;
    }
    public String getDiaperType(){
        return diaperType;
    }

    @Override
    public String getSummary(){
        return "👶 Bez / Tuvalet\n" +
                "Tarih: " + getDate() + "\n" +
                "Saat: " + getTime() + "\n" +
                "Tür: " + diaperType + "\n" +
                "Not: " + getNote();
    }
}
