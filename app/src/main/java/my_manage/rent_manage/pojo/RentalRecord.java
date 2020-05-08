package my_manage.rent_manage.pojo;

import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.enums.AssignType;

import java.util.Calendar;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 租房记录
 */
@ToString
@EqualsAndHashCode
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
    private int  payMonth;
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
    private int propertyCosts;

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
    private int propertyTime;
    @Setter
    @Getter
    private int manID;
    /**
     * 押金
     */
    @Setter
    @Getter
    private int deposit;

    /**
     * 月租金
     */
    private int monthlyRent;

    /**
     * 签合同时间
     */
    private long contractSigningDate;

    /**
     * 合同期限（月）
     */
    @Getter
    @Setter
    private int contractMonth;

    /**
     * 月租金
     */
    public double getMonthlyRent() {
        return (double) monthlyRent / 100;
    }

    /**
     * 月租金
     */
    public void setMonthlyRent(double monthlyRent) {
        this.monthlyRent = (int) Math.round(monthlyRent * 100);
    }

    /**
     * 签合同时间
     */
    public Calendar getContractSigningDate() {
        if (contractSigningDate == 0) return null;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(contractSigningDate);
        return calendar;
    }

    /**
     * 签合同时间
     */
    public void setContractSigningDate(Calendar date) {
        if (date == null)
            contractSigningDate = 0;
        else
            contractSigningDate = date.getTimeInMillis();
    }

    public Calendar getStartDate() {
        if (startDate == 0) return null;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(startDate);
        return calendar;
    }

    /**
     * 物业费开始时间
     */
    public Calendar getRealtyStartDate() {
        if (realtyStartDate == 0) return null;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(realtyStartDate);
        return calendar;
    }

    /**
     * 物业费开始时间
     */
    public void setRealtyStartDate(Calendar date) {
        if (date == null)
            realtyStartDate = 0;
        else
            realtyStartDate = date.getTimeInMillis();
    }

    public double getPropertyCosts() {
        return (double) propertyCosts / 100;
    }

    public void setPropertyCosts(double propertyCosts) {
        this.propertyCosts = (int) Math.round(propertyCosts * 100);
    }

    public void setStartDate(Calendar localDate) {
        if (localDate == null)
            startDate = 0;
        else
            startDate = localDate.getTimeInMillis();
    }

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
