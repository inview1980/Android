package my_manage.pojo.show;

import java.util.List;

import lombok.Data;
import my_manage.pojo.UserItem;
import my_manage.pojo.PersonDetails;
import my_manage.pojo.RentalRecord;
import my_manage.pojo.RoomDetails;

@Data
public final class ExcelData {
    private List<RoomDetails>   roomDetailsList;
    private List<PersonDetails> personDetailsList;
    private List<RentalRecord>  rentalRecordList;
    private List<UserItem>      userItemList;
//    private List<MenuData>      menuDataList;
}
