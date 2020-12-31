package my_manage.tool.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum MenuTypesEnum {
    IsBorrowed(2), NotRented(3), PasswordType(1),DeletedRooms(4),LivingExpenses(5);
    private int id;
}
