package my_manage.password_box.pojo;

import java.util.Objects;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor(suppressConstructorProperties = true)
public final class UserItem {
    private String itemName;
    private String address;
    private String userName;
    private String password;
    private String remark;

    public String getItemName() {
        return Optional.ofNullable(itemName).orElse("");
    }

    public String getAddress() {
        return Optional.ofNullable(address).orElse("");
    }

    public String getUserName() {
        return Optional.ofNullable(userName).orElse("");
    }

    public String getPassword() {
        return Optional.ofNullable(password).orElse("");
    }

    public String getRemark() {
        return Optional.ofNullable(remark).orElse("");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserItem userItem = (UserItem) o;
        return Objects.equals(itemName, userItem.itemName) &&
                Objects.equals(address, userItem.address) &&
                Objects.equals(userName, userItem.userName);
    }

    @Override
    public int hashCode() {
        return (itemName + address + userName).hashCode();
    }
}
