package my_manage.tool.menuEnum;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import my_manage.iface.IActivityMenuForData;
import my_manage.rent_manage.page.RentalForHouseActivity;
import my_manage.rent_manage.pojo.show.ShowRoomDetails;
import my_manage.tool.EnumUtils;

@Getter
@AllArgsConstructor
public enum RentalRoomLongClickEnum implements IActivityMenuForData<RentalForHouseActivity> {
    Add(1, "新建房源") {
        @Override
        public void run(RentalForHouseActivity activity, List<ShowRoomDetails> data, int position) {
            String title = "";
            if (position >= 0 && data != null && data.size() >= position) {
                title =  data.get(position).getCommunityName();
                if (title.contains("全部")) title = "";
            }
            EnumUtils.communityChange(activity, title);
        }
    },
    AddPerson(2, "新建租户") {
        @Override
        public void run(RentalForHouseActivity activity, List<ShowRoomDetails> data, int position) {

        }
    };
    private int index;
    private String name;
}
