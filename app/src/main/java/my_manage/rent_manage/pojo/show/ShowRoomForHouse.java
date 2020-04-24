package my_manage.rent_manage.pojo.show;

import java.util.Calendar;
import java.util.Objects;

import lombok.Data;
import lombok.ToString;

@Data@ToString
public final class ShowRoomForHouse {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShowRoomForHouse that = (ShowRoomForHouse) o;
        return Double.compare(that.roomArea, roomArea) == 0 &&
                communityName.equals(that.communityName) &&
                roomNumber.equals(that.roomNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(communityName, roomNumber, roomArea);
    }

    /**
     * 小区名称
     */
    private String communityName;
    /**
     * 房间号
     */
    private String roomNumber;
    /**
     * 面积
     */
    private double roomArea;
    /**
     * 现住人
     */
    private String manName;
    /**
     * 现住人联系方式
     */
    private String tel;
    /**
     * 月租金
     */
    private double  monthlyRent;
    /**
     * 到期日
     */
    private Calendar endDate;
    /**
     * 是否包含物业费
     */
    private boolean isContainRealty;
    /**
     * 物业费金额
     */
    private double propertyCosts;
    /**
     * 物业费单价
     */
    private double propertyPrice;
    private int manId;
}
