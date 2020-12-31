package my_manage.pojo;

import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.enums.AssignType;

import java.util.Calendar;

import lombok.Getter;
import lombok.Setter;
import my_manage.iface.ColumnName;

/**
 * @author inview
 * @Date 2020/12/17 10:02
 * @Description :
 */
public final class ShoppingRecord {
    @PrimaryKey(AssignType.AUTO_INCREMENT)
    @Setter
    @Getter
    private                   int    primary_id;
    @Setter
    @Getter
    @ColumnName("项目")
    private                   String project;
    @Setter
    @Getter
    @ColumnName("规格")
    private                   String type;
    /**
     * 日期
     */
    @ColumnName("日期") private long   recordDate;
    /**
     * 金额
     */
    @ColumnName("金额") private int    totalMoney;

    @Setter
    @Getter
    @ColumnName("备注")
    private String remarks;

    /**
     * 日期
     */
    public Calendar getRecordDate() {
        if (recordDate == 0) return null;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(recordDate);
        return calendar;
    }

    /**
     * 日期
     */
    public void setRecordDate(Calendar localDate) {
        if (localDate == null)
            recordDate = 0;
        else
            recordDate = localDate.getTimeInMillis();
    }

    /**
     * 金额
     */
    public double getTotalMoney() {
        return (double) totalMoney / 100;
    }

    /**
     * 金额
     */
    public void setTotalMoney(double totalMoney) {
        this.totalMoney = (int) Math.round(totalMoney * 100);
    }
}
