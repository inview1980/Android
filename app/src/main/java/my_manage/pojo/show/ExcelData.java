package my_manage.pojo.show;

import java.util.List;

import lombok.Data;
import my_manage.pojo.CarMaintenanceRecord;
import my_manage.pojo.FuelRecord;
import my_manage.pojo.PayProperty;
import my_manage.pojo.ShoppingRecord;
import my_manage.pojo.UserItem;
import my_manage.pojo.PersonDetails;
import my_manage.pojo.RentalRecord;
import my_manage.pojo.RoomDetails;
import my_manage.pojo.LivingExpenses;

@Data
public final class ExcelData {
    private List<RoomDetails>          roomDetailsList;
    private List<PersonDetails>        personDetailsList;
    private List<RentalRecord>         rentalRecordList;
    private List<UserItem>             userItemList;
    private List<FuelRecord>           fuelRecordList;
    private List<CarMaintenanceRecord> carList;
    private List<ShoppingRecord> shoppingRecordList;
    private List<LivingExpenses> livingExpensesList;
    private List<PayProperty>    payPropertyList;
//    private List<MenuData>      menuDataList;
}
