package my_manage.adapter_page;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

import my_manage.rent_manage.fragment.RoomDetailsFragment;
import my_manage.rent_manage.pojo.show.ShowRoomDetails;
import my_manage.tool.enums.ShowRoomType;

public final class NewRoomPageAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> vList = new ArrayList<>();

    public NewRoomPageAdapter(@NonNull FragmentManager fm, List<ShowRoomDetails> vList, ShowRoomType type) {
        super(fm);
        if (type == ShowRoomType.Rent) {
            //出租
            this.vList.add(new RoomDetailsFragment(vList.get(0), type));
        } else {
            for (int i = 0; i < vList.size(); i++) {
                this.vList.add(new RoomDetailsFragment(vList.get(i), type));
            }
        }
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return this.vList.get(position);
    }

    @Override
    public int getCount() {
        return vList.size();
    }
}
