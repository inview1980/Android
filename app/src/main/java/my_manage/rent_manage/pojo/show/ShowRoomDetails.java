package my_manage.rent_manage.pojo.show;

import java.util.Calendar;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import my_manage.rent_manage.pojo.PersonDetails;
import my_manage.rent_manage.pojo.RentalRecord;
import my_manage.rent_manage.pojo.RoomDetails;

@Getter
@Setter
@NoArgsConstructor
public final class ShowRoomDetails {
  private RentalRecord  rentalRecord;
  private PersonDetails personDetails;
  private RoomDetails   roomDetails;
  private double        roomAreas;
  private int           roomCount;

  public RentalRecord getRentalRecord() {
    if (rentalRecord == null) rentalRecord = new RentalRecord();
    return rentalRecord;
  }

  public PersonDetails getPersonDetails() {
    if (personDetails == null) personDetails = new PersonDetails();
    return personDetails;
  }

  public RoomDetails getRoomDetails() {
    if (roomDetails == null) roomDetails = new RoomDetails();
    return roomDetails;
  }

  public ShowRoomDetails(RoomDetails roomDetails) {
    this.roomDetails = roomDetails;
  }

  /**
   * 房租结束日期
   */
  public Calendar getRentalEndDate() {
    if (null != rentalRecord && rentalRecord.getStartDate() != null) {
      Calendar endDate = Calendar.getInstance();
      endDate.setTimeInMillis(rentalRecord.getStartDate().getTimeInMillis());
      endDate.add(Calendar.MONTH, rentalRecord.getPayMonth());
      endDate.add(Calendar.DAY_OF_YEAR, -1);
      return endDate;
    }
    return null;
  }

  /**
   * 物业费结束日期
   */
  public Calendar getPropertyCostEndDate() {
    if (null != rentalRecord && rentalRecord.getRealtyStartDate() != null) {
      Calendar endDate = Calendar.getInstance();
      endDate.setTimeInMillis(rentalRecord.getRealtyStartDate().getTimeInMillis());
      endDate.add(Calendar.MONTH, rentalRecord.getPropertyTime());
      endDate.add(Calendar.DAY_OF_YEAR, -1);
      return endDate;
    }
    return null;
  }

  /**
   * 合同结束日期
   */
  public Calendar getContractEndDate() {
    if (null != rentalRecord && rentalRecord.getContractSigningDate() != null) {
      Calendar endDate = Calendar.getInstance();
      endDate.setTimeInMillis(rentalRecord.getContractSigningDate().getTimeInMillis());
      endDate.add(Calendar.MONTH, rentalRecord.getContractMonth());
      endDate.add(Calendar.DAY_OF_YEAR, -1);
      return endDate;
    }
    return null;
  }
}