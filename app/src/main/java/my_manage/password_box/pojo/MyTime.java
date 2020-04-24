package my_manage.password_box.pojo;

import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.enums.AssignType;

import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Data@NoArgsConstructor@ToString@AllArgsConstructor(suppressConstructorProperties = true)
public final class MyTime {
    @PrimaryKey(AssignType.AUTO_INCREMENT)
    private int primary_id;
    private String itemName;
    private boolean[] dayForWeeks;
    private boolean isFinish;
    private String remark;
    private Date alarmTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyTime myTime = (MyTime) o;
        return isFinish == myTime.isFinish &&
                itemName.equals(myTime.itemName) &&
                Arrays.equals(dayForWeeks, myTime.dayForWeeks) &&
                alarmTime.equals(myTime.alarmTime);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(itemName, isFinish, alarmTime);
        result = 31 * result + Arrays.hashCode(dayForWeeks);
        return result;
    }
}
