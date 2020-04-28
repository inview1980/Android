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
    private double roomAreas;
    private int roomCount;


    public ShowRoomDetails(RoomDetails roomDetails) {
        super(roomDetails);
    }

    public Calendar getRentalEndDate() {
        if (null != rentalRecord && rentalRecord.getStartDate() != null) {
            Calendar endDate =Calendar.getInstance();
            endDate.setTimeInMillis(rentalRecord.getStartDate().getTimeInMillis());
            endDate.add(Calendar.MONTH, rentalRecord.getPayMonth());
            endDate.add(Calendar.DAY_OF_YEAR,-1);
            return endDate;
        }
        return null;
    }
    public Calendar getPropertyCostEndDate() {
        if (null != rentalRecord && rentalRecord.getRealtyStartDate() != null) {
            Calendar endDate =Calendar.getInstance();
            endDate.setTimeInMillis(rentalRecord.getRealtyStartDate().getTimeInMillis());
            endDate.add(Calendar.MONTH, rentalRecord.getRealtyMonth());
            endDate.add(Calendar.DAY_OF_YEAR,-1);
            return endDate;
        }
        return null;
    }

    public Calendar getContractEndDate() {
        if (null != rentalRecord && rentalRecord.getContractSigningDate() != null) {
            Calendar endDate =Calendar.getInstance();
            endDate.setTimeInMillis(rentalRecord.getContractSigningDate().getTimeInMillis());
            endDate.add(Calendar.MONTH, rentalRecord.getContractMonth());
            endDate.add(Calendar.DAY_OF_YEAR,-1);
            return endDate;
        }
        return null;
    }
}
