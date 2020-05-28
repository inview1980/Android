package my_manage.rent_manage.pojo;

import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.enums.AssignType;

import lombok.Data;
import lombok.NoArgsConstructor;
import my_manage.iface.ColumnName;

/**
 * 租户、联系人资料
 */
@Data
@NoArgsConstructor
public final class PersonDetails {
    @PrimaryKey(AssignType.AUTO_INCREMENT)
    private                    int    primary_id;
    @ColumnName("姓名") private  String name;
    @ColumnName("电话") private  String tel;
    /**
     * 身份证号码
     */
    @ColumnName("证件号") private String cord;
    @ColumnName("其他") private  String other;
    /**
     * 公司
     */
    @ColumnName("公司") private  String company;

    public PersonDetails(String name) {
        this.name = name;
    }
}
