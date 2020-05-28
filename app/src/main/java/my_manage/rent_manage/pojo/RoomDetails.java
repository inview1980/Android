package my_manage.rent_manage.pojo;

import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.enums.AssignType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import my_manage.iface.ColumnName;

/**
 * 住宅小区
 */
@ToString
@NoArgsConstructor
public class RoomDetails {
    /**
     * 小区名称
     */
    @Getter
    @Setter
    @ColumnName("小区名称")
    private                   String communityName;
    /**
     * 房间号
     */
    @Setter
    @Getter
    @PrimaryKey(AssignType.BY_MYSELF)
    @ColumnName("房间号")
    private                   String roomNumber;
    @ColumnName("面积") private int    roomArea;
    /**
     * 电表号
     */
    @Setter
    @Getter
    @ColumnName("电表号")
    private                   String electricMeter;
    @Setter
    @Getter
    @ColumnName("水表号")
    private                   String waterMeter;

    /**
     * 物业费单价
     */
    @ColumnName("物业费单价")
    private int propertyPrice;
    @ColumnName("是否删除的房源")
    private int isDelete;

    @Getter
    @Setter
    @ColumnName("记录号")
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
