package my_manage.ui.adapter_page;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import my_manage.ui.rent_manage.fragment.RoomDetailsFragment;
import my_manage.pojo.show.ShowRoomDetails;
import my_manage.tool.enums.ShowRoomType;

public final class NewRoomPageAdapter extends FragmentPagerAdapter {
    private List<Fragment> vList = new ArrayList<>();

    public NewRoomPageAdapter(@NonNull FragmentManager fm, List<ShowRoomDetails> vList, ShowRoomType type) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        if (type == ShowRoomType.Rent) {
            //出租
            this.vList.add(new RoomDetailsFragment(vList.get(0), type));
        } else {
            vList.forEach(item -> this.vList.add(new RoomDetailsFragment(item, type)));
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
