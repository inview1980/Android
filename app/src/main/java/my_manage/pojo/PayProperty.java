package my_manage.pojo;

import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.enums.AssignType;

import java.util.Calendar;

import lombok.Getter;
import lombok.Setter;
import my_manage.iface.ColumnName;

/**
 * 物业费缴费情况
 * @author inview
 * @Date 2020/12/17 9:39
 * @Description :
 */
public final class PayProperty {
    @PrimaryKey(AssignType.AUTO_INCREMENT)
    @Getter
    @Setter
    private                   int    primary_id;
    /**
     * 房间号
     */
    @Setter
    @Getter
    @ColumnName("房间号")
    private                   String   roomNumber;
    /**
     * 付款日期
     */
    @ColumnName("付款日期") private   long payDate;
    @ColumnName("开始时间") private long startDate;
    /**
     * 期限（月）
     */
    @Getter
    @Setter
    @ColumnName("期限（月）")
    private int                      payMonth;
    /**
     * 金额
     */
    @ColumnName("金额") private int    totalMoney;

    @Setter
    @Getter
    @ColumnName("备注")
    private String remarks;

    /**
     * 付款日期
     */
    public Calendar getPayDate() {
        if (payDate == 0) return null;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(payDate);
        return calendar;
    }

    /**
     * 付款日期
     */
    public void setPayDate(Calendar localDate) {
        if (localDate == null)
            payDate = 0;
        else
            payDate = localDate.getTimeInMillis();
    }

    public Calendar getStartDate() {
        if (startDate == 0) return null;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(startDate);
        return calendar;
    }
    public void setStartDate(Calendar localDate) {
        if (localDate == null)
            startDate = 0;
        else
            startDate = localDate.getTimeInMillis();
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
