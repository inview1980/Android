package my_manage.tool.menuEnum;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import my_manage.iface.IActivityMenuForData;
import my_manage.rent_manage.page.RentalForHouseActivity;
import my_manage.rent_manage.pojo.show.ShowRoomDetails;

@Getter
@AllArgsConstructor
public enum RentalRoomRentedClickEnum implements IActivityMenuForData<RentalForHouseActivity> {
    Show(1, "查看详情") {
        @Override
        public void run(RentalForHouseActivity activity, List<ShowRoomDetails> data, int position) {
            RentalRoomNotRentedClickEnum.Show.run(activity, data, position);
        }
    },
    ContinueRent(2,"续租"){
        @Override
        public void run(RentalForHouseActivity activity, List<ShowRoomDetails> data, int position) {
            // TODO: 2020/4/28
        }
    },
    Rent(3, "退租") {
        @Override
        public void run(RentalForHouseActivity activity, List<ShowRoomDetails> data, int position) {
// TODO: 2020/4/26
        }
    },
    ChangeRent(4, "调整租金") {
        @Override
        public void run(RentalForHouseActivity activity, List<ShowRoomDetails> data, int position) {
            RentalRoomNotRentedClickEnum.ChangeRent.run(activity, data, position);
        }
    },
    ChangeDeposit(5,"调整押金"){
        @Override
        public void run(RentalForHouseActivity activity, List<ShowRoomDetails> data, int position) {
            // TODO: 2020/4/28
        }
    },
    RentLost(6, "查看出租记录") {
        @Override
        public void run(RentalForHouseActivity activity, List<ShowRoomDetails> data, int position) {
            RentalRoomNotRentedClickEnum.RentRecords.run(activity, data, position);
        }
    },
    PayPropertyCosts(7, "支付物业费") {
        @Override
        public void run(RentalForHouseActivity activity, List<ShowRoomDetails> data, int position) {
// TODO: 2020/4/26

        }
    },RenewContract(8,"续签合同"){
        @Override
        public void run(RentalForHouseActivity activity, List<ShowRoomDetails> data, int position) {
            // TODO: 2020/4/28
        }
    },
    PersonDetails(9, "租户资料") {
        @Override
        public void run(RentalForHouseActivity activity, List<ShowRoomDetails> data, int position) {
            // TODO: 2020/4/26
        }
    },
    Del(10, "删除房源") {
        @Override
        public void run(RentalForHouseActivity activity, List<ShowRoomDetails> data, int position) {
            RentalRoomNotRentedClickEnum.Del.run(activity, data, position);
        }
    };

    private int index;
    private String name;
}
