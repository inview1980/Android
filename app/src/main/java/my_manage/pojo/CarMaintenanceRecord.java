package my_manage.pojo;

import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.enums.AssignType;

import java.util.Calendar;

import lombok.Getter;
import lombok.Setter;
import my_manage.iface.ColumnName;

/**
 * 小车维修记录
 *
 * @author inview
 * @Date 2020/11/27 8:26
 * @Description :
 */
public final class CarMaintenanceRecord {
    @PrimaryKey(AssignType.AUTO_INCREMENT)
    @Getter
    @Setter
    private                   int    primary_id;
    @ColumnName("时间") private long   date;
    @ColumnName("金额") private int    money;
    @ColumnName("地址")
    @Setter
    @Getter
    private                   String address;
    @ColumnName("维护种类")
    @Setter
    @Getter
    private                   String maintenanceType;
    @ColumnName("备注")@Getter@Setter String remark;
    @ColumnName("里程")@Getter@Setter int odometerNumber;

    public Calendar getDate() {
        if (date == 0) return null;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        return calendar;
    }

    public void setDate(Calendar date) {
        if (date == null)
            this.date = 0;
        else
            this.date = date.getTimeInMillis();
    }

    public double getMoney() {
        return (double) money / 100;
    }

    public void setMoney(double money) {
        this.money = (int) Math.round(money * 100);
    }

    @Override
    public String toString() {
        return "CarMaintenanceRecord{" +
                "date=" + date +
                ", money=" + money +
                ", address='" + address + '\'' +
                ", maintenanceType='" + maintenanceType + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
}
