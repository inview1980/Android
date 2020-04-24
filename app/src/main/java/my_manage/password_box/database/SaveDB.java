package my_manage.password_box.database;

import lombok.Data;

@Data
final class SaveDB {
    String salt;
    String password;
    String content;
}