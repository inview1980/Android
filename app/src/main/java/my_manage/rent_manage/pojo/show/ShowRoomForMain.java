package my_manage.rent_manage.pojo.show;

import lombok.Data;

@Data
public  class ShowRoomForMain {
    private int primary_id;
    private int roomCount;
    /**
     * 社区名称
     */
    private String communityName;
    private String roomNumber;
    private double roomAreas;
    private int contactsId;

    public double getRoomAreas(){
        return (double)Math.round(roomAreas*100)/100;
    }
}
