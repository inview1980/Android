package my_manage.tool;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import my_manage.password_box.iface.IActivityMenu;
import my_manage.password_box.pojo.UserItem;

import org.junit.Test;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

//@RunWith(Parameterized.class)
public class StrUtilsTest {
    private String s1;
    private String s2;
    private boolean result;

//    public StrUtilsTest(String s1, String s2, boolean result) {
//        this.s1 = s1;
//        this.s2 = s2;
//        this.result = result;
//    }

//    public StrUtilsTest(String s1, boolean result) {
//        this.s1 = s1;
//        this.result = result;
//    }

    @Parameterized.Parameters
    public static Collection getData() {
//        Object[][] objects = {{"1", "2", false}, {null, "1", true}, {"1", null, true}, {"", "1", true}, {" ", "1", true}};
        Object[][] objects = {{"", true}, {"1", false}, {" ", true}, {null, true}, {"  ", true}};
        return Arrays.asList(objects);
    }

    @Test
    public void isBlank() {
        assertEquals(result, StrUtils.isBlank(s1));
    }

    @Test
    public void isNotBlank() {
        assertNotEquals(result, StrUtils.isNotBlank(s1));
    }

    @Test
    public void isBlanks() {
        assertFalse(StrUtils.isAnyBlank("1", "2", "j"));
        assertTrue(StrUtils.isAnyBlank(null, "1"));
        assertTrue(StrUtils.isAnyBlank(null, null));
        assertTrue(StrUtils.isAnyBlank(""));
        assertTrue(StrUtils.isAnyBlank(" "));
        assertTrue(StrUtils.isAnyBlank(" ", null));
        assertFalse(StrUtils.isAnyBlank("1"));
    }

    @Test
    public void isAllNotBlank() {
        assertFalse(StrUtils.isAllNotBlank(null, null));
        assertNotEquals(result, StrUtils.isAllNotBlank(s1, s2));
    }

    @Test
    public void test() {
        String value = null;
        UserItem userItem = Optional.ofNullable(value).map(v -> JSONObject.parseObject(v, UserItem.class))
                .orElse(new UserItem());
        System.out.println(userItem);

        userItem.setUserName("jjjj");
        value = JSON.toJSONString(userItem);
        UserItem u2 = Optional.ofNullable(value).map(v -> JSONObject.parseObject(v, UserItem.class))
                .orElse(new UserItem());
        assertNotNull(u2);
        System.out.println(u2);
        assertEquals("jjjj", u2.getUserName());

        value = "{jjjs:}";
        try {
            UserItem u3 = Optional.ofNullable(value).map(v -> JSONObject.parseObject(v, UserItem.class))
                    .orElse(new UserItem());
            assertNotNull(u3);
            System.out.println(u3);
        } catch (JSONException e) {
        }
    }


    @Test
    public void t2(){
//        PwdContextActivityLongClickEnum pe = getEnum(PwdContextActivityLongClickEnum.getEnum(1), 2);
//        System.out.println(pe.getName());
    }



    public static <T extends Enum<T> > T getEnum(T t,int index) {
        T[] enums =t.getDeclaringClass().getEnumConstants();
        for (Enum<T> e : enums) {
            IActivityMenu ie=(IActivityMenu)e;
            if (ie.getIndex() == index) {
                return (T) e;
            }
        }
        return enums[0];
    }
}