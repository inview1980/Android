package my_manage.pojo;

import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.enums.AssignType;

import java.util.Calendar;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import my_manage.iface.ColumnName;

/**
 * @author inview
 * @Date 2020/11/23 15:14
 * @Description :加油记录
 */
public final class FuelRecord {
    @PrimaryKey(AssignType.AUTO_INCREMENT)
    @Getter
    @Setter
    private                   int    primary_id;
    @ColumnName("加油站")
    @Getter
    @Setter
    private                   String stationName;
    @ColumnName("时间") private long   time;

    @ColumnName("金额") private  long money;
    /**
     * 市场价
     */
    @ColumnName("市场价") private int  marketPrice;
    /**
     * 里程表数
     */
    @ColumnName("里程表数")
    @Setter
    @Getter
    private                    int  odometerNumber;

    /**
     * 升
     */
    @ColumnName("升") private int rise;

    public Calendar getTime() {
        if (time == 0) return null;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return calendar;
    }

    public void setTime(Calendar time) {
        if (time == null)
            this.time = 0;
        else
            this.time = time.getTimeInMillis();
    }

    public double getMoney() {
        return (double) money / 100;
    }

    public void setMoney(double money) {
        this.money = (long) Math.round(money * 100);
    }

    public double getMarketPrice() {
        return (double) marketPrice / 100;
    }

    public void setMarketPrice(double marketPrice) {
        this.marketPrice = (int) Math.round(marketPrice * 100);
    }


    public double getRise() {
        return (double) rise / 100;
    }

    public void setRise(double rise) {
        this.rise = (int) Math.round(rise * 100);
    }

    @Override
    public String toString() {
        return "FuelRecord{" +
                "stationName='" + stationName + '\'' +
                ", time=" + time +
                ", money=" + money +
                ", marketPrice=" + marketPrice +
                ", odometerNumber=" + odometerNumber +
                ", rise=" + rise +
                '}';
    }
}
