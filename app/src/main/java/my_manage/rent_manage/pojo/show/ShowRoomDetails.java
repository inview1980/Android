package my_manage.rent_manage.pojo.show;

import java.util.Calendar;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import my_manage.rent_manage.pojo.PersonDetails;
import my_manage.rent_manage.pojo.RentalRecord;
import my_manage.rent_manage.pojo.RoomDetails;

@Getter@Setter
@NoArgsConstructor
public final class ShowRoomDetails extends RoomDetails {
    private RentalRecord rentalRecord;
    private PersonDetails personDetails;

    public ShowRoomDetails(RoomDetails roomDetails) {
        super(roomDetails);
    }

    public Calendar getEndDate() {
        if (null != rentalRecord && rentalRecord.getStartDate() != null) {
            Calendar endDate = rentalRecord.getStartDate();
            endDate.add(Calendar.MONTH, rentalRecord.getPayMonth());
            return endDate;
        }
        return null;
    }
}
