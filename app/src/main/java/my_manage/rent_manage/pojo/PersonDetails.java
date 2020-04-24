package my_manage.rent_manage.pojo;

import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.enums.AssignType;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 租户、联系人资料
 */
@Data@NoArgsConstructor
public final class PersonDetails {
    @PrimaryKey(AssignType.AUTO_INCREMENT)
    private int primary_id;
    private String name;
    private String tel;
    /**
     * 身份证号码
     */
    private String cord;

    public PersonDetails(String name) {
        this.name = name;
    }
}
