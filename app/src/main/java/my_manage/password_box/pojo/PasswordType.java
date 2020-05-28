package my_manage.password_box.pojo;

import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.enums.AssignType;

import lombok.Data;
import my_manage.iface.ColumnName;

@Data
public final class PasswordType {
    @PrimaryKey(AssignType.AUTO_INCREMENT)
    private int    id;
    @ColumnName("类型名称")
    private String name;
    @ColumnName("图标地址")
    private String url;
}
