package my_manage.pojo.show;

import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.enums.AssignType;

import lombok.Data;
import my_manage.iface.ColumnName;

@Data
public final class MenuData {
    @PrimaryKey(AssignType.AUTO_INCREMENT)
    private                    int    primary_id;
    @ColumnName("图标") private  String icon;
    @ColumnName("标签名") private String title;
    @ColumnName("颜色") private  String    color;
    @ColumnName("图标大小")private int iconSize;
    @ColumnName("字体大小")private int fontSize;
    @ColumnName("类型")private int type;
}
