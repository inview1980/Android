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

@Data
@NoArgsConstructor
@AllArgsConstructor(suppressConstructorProperties = true)
public final class UserItem {
    @PrimaryKey(AssignType.AUTO_INCREMENT)
    private int id;
    private String itemName;
    private String address;
    private String userName;
    private String password;
    private String remark;
    private String salt;

}
