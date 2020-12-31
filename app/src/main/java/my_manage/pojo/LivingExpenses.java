package my_manage.pojo;

import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.enums.AssignType;

import java.util.Calendar;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import my_manage.iface.ColumnName;

/**
 * 水、电、燃气费
 *
 * @author inview
 * @Date 2020/12/17 9:57
 * @Description :
 */
@Builder
@NoArgsConstructor@AllArgsConstructor
public final class LivingExpenses {
    @PrimaryKey(AssignType.AUTO_INCREMENT)
    @Setter
    @Getter
    private                     int    primary_id;
    @Setter
    @Getter
    @ColumnName("项目")
    private                     String project;
    /**
     * 房间号
     */
    @Setter
    @Getter
    @ColumnName("房间号")
    private                     String roomNumber;
    /**
     * 付款日期
     */
    @ColumnName("付款日期") private long   paymentDate;
    /**
     * 金额
     */
    @ColumnName("金额") private   int    totalMoney;
    @Setter
    @Getter
    @ColumnName("备注")
    private                     String remarks;

    /**
     * 付款日期
     */
    public Calendar getPaymentDate() {
        if (paymentDate == 0) return null;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(paymentDate);
        return calendar;
    }

    /**
     * 付款日期
     */
    public void setPaymentDate(Calendar localDate) {
        if (localDate == null)
            paymentDate = 0;
        else
            paymentDate = localDate.getTimeInMillis();
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
