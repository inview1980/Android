package my_manage.tool.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ShowRoomType {
    History(1, "历史"),
    Person(2, "租户信息"),
    Rent(3, "租房"),
    Details(4,"详情");

    private int    index;
    private String details;

    public static ShowRoomType getType(int i) {
        for (final ShowRoomType value : ShowRoomType.values()) {
            if (value.index == i) return value;
        }
        return Details;
    }
}
