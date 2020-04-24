package my_manage.password_box.menuEnum;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import my_manage.password_box.iface.IActivityMenuForData;
import my_manage.rent_manage.page.RentalForHouse;

@Getter@AllArgsConstructor
public enum RentalRoomLongClickEnum implements IActivityMenuForData<RentalForHouse> {
    Add(1, "新建房源") {
        @Override
        public void run(RentalForHouse activity, List data, int position) {

        }
    },
    AddPerson(2,"新建租户"){
        @Override
        public void run(RentalForHouse activity, List data, int position) {

        }
    };
    private int index;
    private String name;
}
