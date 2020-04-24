package my_manage.rent_manage.pojo;

import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.enums.AssignType;

import java.util.Calendar;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 租房记录
 */
@ToString
public final class RentalRecord {
    @PrimaryKey(AssignType.AUTO_INCREMENT)
    @Setter
    @Getter
    private int primary_id;

    private long startDate;
    /**
     * 房租时长
     */
    @Getter
    @Setter
    private int payMonth;
    /**
     * 付款日期
     */
    private long paymentDate;
    /**
     * 付款金额
     */
    private int totalMoney;
    /**
     * 物业费
     */
    private int realtyMoney;

    /**
     * 是否包含物业费
     */
    private int isContainRealty;

    /**
     * 房间号
     */
    @Setter
    @Getter
    private String roomNumber;

    /**
     * 物业费开始时间
     */
    private long realtyStartDate;
    /**
     * 物业费时长
     */
    @Getter
    @Setter
    private int realtyMonth;
    @Setter
    @Getter
    private int manID;

    public Calendar getStartDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(startDate);
        return calendar;
    }

    public Calendar getRealtyStartDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(realtyStartDate);
        return calendar;
    }

    public void setRealtyStartDate(Calendar date) {
        realtyStartDate = date.getTimeInMillis();
    }

    public double getRealtyMoney() {
        return (double) realtyMoney / 100;
    }

    public void setRealtyMoney(double realtyMoney) {
        this.realtyMoney = (int) Math.round(realtyMoney * 100);
    }

    public void setStartDate(Calendar localDate) {
        startDate = localDate.getTimeInMillis();
    }

    public Calendar getPaymentDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(paymentDate);
        return calendar;
    }

    public void setPaymentDate(Calendar localDate) {
        paymentDate = localDate.getTimeInMillis();
    }

    public double getTotalMoney() {
        return (double) totalMoney / 100;
    }

    public void setTotalMoney(double totalMoney) {
        this.totalMoney = (int) Math.round(totalMoney * 100);
    }


    public boolean getIsContainRealty() {
        return isContainRealty != 0;
    }

    public void setIsContainRealty(boolean isContainRealty) {
        this.isContainRealty = isContainRealty ? 1 : 0;
    }

    @Setter
    @Getter
    private String remarks;

}
