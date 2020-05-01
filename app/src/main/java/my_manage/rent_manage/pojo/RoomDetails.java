package my_manage.rent_manage.pojo;

import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.enums.AssignType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 住宅小区
 */
@ToString
@NoArgsConstructor
public class RoomDetails {
    public RoomDetails(RoomDetails roomDetails) {
        if(roomDetails!=null) {
            this.communityName = roomDetails.communityName;
            this.manId = roomDetails.manId;
            this.electricMeter = roomDetails.electricMeter;
            this.propertyPrice = roomDetails.propertyPrice;
            this.recordId = roomDetails.recordId;
            this.roomNumber = roomDetails.roomNumber;
            this.roomArea = roomDetails.roomArea;
            this.rentalMoney = roomDetails.rentalMoney;
            this.isDelete = roomDetails.isDelete;
            this.waterMeter=roomDetails.waterMeter;
        }
    }

    /**
     * 小区名称
     */
    @Getter
    @Setter
    private String communityName;
    /**
     * 房间号
     */
    @Setter
    @Getter
    @PrimaryKey(AssignType.BY_MYSELF)
    private String roomNumber;
    private int    roomArea;
    /**
     * 电表号
     */
    @Setter
    @Getter
    private String electricMeter;
    @Setter@Getter
    private String waterMeter;

    /**
     * 现任联系人ID
     */
    @Setter
    @Getter
    private int manId;

    /**
     * 物业费单价
     */
    private int propertyPrice;
    /**
     * 月租金
     */
    @Setter
    @Getter
    private int rentalMoney;


    private int isDelete;

    @Getter
    @Setter
    private int recordId;

    public double getPropertyPrice() {
        return (double) propertyPrice / 100;
    }

    public void setPropertyPrice(double propertyPrice) {
        this.propertyPrice = (int) Math.round(propertyPrice * 100);
    }


    public double getRoomArea() {
        return (double) roomArea / 100;
    }

    public void setRoomArea(double roomArea) {
        this.roomArea = (int) Math.round(roomArea * 100);
    }

    public void setIsDelete(boolean isDelete) {
        this.isDelete = isDelete ? 1 : 0;
    }

    public boolean getIsDelete() {
        return isDelete == 1;
    }
}
