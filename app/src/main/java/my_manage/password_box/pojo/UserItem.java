package my_manage.password_box.pojo;

import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.enums.AssignType;

import java.util.Objects;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import my_manage.iface.ColumnName;

@Data
@NoArgsConstructor
@AllArgsConstructor(suppressConstructorProperties = true)
public final class UserItem {
    @PrimaryKey(AssignType.AUTO_INCREMENT)
    private int    id;
    @ColumnName("项目名称")
    private String itemName;
    @ColumnName("地址")
    private String address;
    @ColumnName("用户名")
    private String userName;
    @ColumnName("密码")
    private String password;
    @ColumnName("备注")
    private String remark;
    private String salt;
    @ColumnName("类型")
    private int typeNameId;
}
