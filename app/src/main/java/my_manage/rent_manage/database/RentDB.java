package my_manage.rent_manage.database;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;

import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.DataBaseConfig;
import com.litesuits.orm.db.annotation.NotNull;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.litesuits.orm.db.assit.WhereBuilder;
import com.litesuits.orm.db.model.ColumnsValue;
import com.litesuits.orm.db.model.ConflictAlgorithm;

import org.apache.poi.ss.formula.functions.T;

import java.util.Collection;
import java.util.List;

import my_manage.MainActivity;
import my_manage.tool.StrUtils;

/**
 * 数据库工具类
 *
 * @author mazhanzhu
 */
public final class RentDB {
    public static  String  DB_NAME;
    private static LiteOrm liteOrm;
    public static  Context mContext;


    /**
     * 数据库名称
     */
    private static String getUserDatabaseName() {
        return "rent_info";
    }

    /**
     * 创建级联数据库
     */
    public static boolean createCascadeDB(Context context, String path) {
        if(liteOrm==null) {
            mContext = context.getApplicationContext();
            DB_NAME = StrUtils.isBlank(path) ? getUserDatabaseName() : path;
            liteOrm = LiteOrm.newSingleInstance(getDBConfig());
        }
        return true;
    }

    private static DataBaseConfig getDBConfig() {
//创建config信息
        DataBaseConfig config = new DataBaseConfig(mContext);
//数据库名，可设置存储路径。默认在内部存储位置databases文件夹下
        config.dbName = DB_NAME;
        config.debugged = false; //是否打Log
        config.dbVersion = 1; // database Version
        config.onUpdateListener = null; //升级
        return config;
    }

    public static LiteOrm getLiteOrm() {
        if (liteOrm == null) {
            DB_NAME = getUserDatabaseName();
            liteOrm = LiteOrm.newSingleInstance(getDBConfig());
        }
        return liteOrm;
    }

    public static LiteOrm getLiteOrm(@NonNull Context context, @NonNull String path) {
        if (liteOrm == null) {
            mContext = context;
            DB_NAME = path;
            liteOrm = LiteOrm.newSingleInstance(getDBConfig());
        }
        return liteOrm;
    }

    /**
     * 插入一条记录
     *
     * @param t
     */
    public static <T> long insert(T t) {
        return getLiteOrm().save(t);
    }

    /**
     * 插入所有记录
     */
    public static <T> int insertAll(List<T> list) {
        return getLiteOrm().save(list);
    }

    /**
     * 以某种条件作为插入标准
     */
    public static <T> long insertAll(Collection<T> t, ConflictAlgorithm config) {
        return getLiteOrm().insert(t, config);
    }

    /**
     * 以某种条件作为插入标准
     */
    public static <T> long insertAll(List<T> t, ConflictAlgorithm config) {
        return getLiteOrm().insert(t, config);
    }


    /**
     * 查询所有
     *
     */
    static <T> List<T> getQueryAll(Class<T> cla) {
        return getLiteOrm().query(cla);
    }

    /**
     * 根据ID查询
     *
     */
    public static <T> T getInfoById(String id, Class<T> cla) {
        return getLiteOrm().queryById(id, cla);
    }

    /**
     * 根据ID查询
     *
     */
    public static <T> T getInfoById(long id, Class<T> cla) {
        return getLiteOrm().queryById(id, cla);
    }

    /**
     * 查询 某字段 等于 Value的值
     *
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    static <T> List<T> getQueryByWhere(Class<T> cla, String field, Object[] value) {
        return getLiteOrm().<T>query(new QueryBuilder(cla).where(field + "=?", value));
    }

    /**
     * 模糊查询
     *
     */
    public static <T> List<T> getQueryByTime(Class<T> cla, String field, Object[] value) {
        return getLiteOrm().<T>query(new QueryBuilder(cla).where(field + " LIKE ?", value));
    }


    /**
     * 删除所有 某字段等于 Vlaue的值
     *
     */
    public static <T> int deleteWhere(Class<T> cla, String field, String[] value) {
        return getLiteOrm().delete(cla, WhereBuilder.create(cla, field + "=?", value));
    }

    /**
     * 删除所有 某字段等于 Vlaue的值
     *
     */
    static <T> int deleteWhere(Class<T> cla, String field, Object[] value) {
        return getLiteOrm().delete(cla, WhereBuilder.create(cla, field + "=?", value));
    }

    /**
     * 删除所有
     *
     * @param cla
     */
    public static <T> int deleteAll(Class<T> cla) {
        return getLiteOrm().deleteAll(cla);
    }

    /**
     * 仅在以存在时更新
     *
     */
    public static <T> int update(T t) {
        return getLiteOrm().update(t, ConflictAlgorithm.Replace);
    }

    /**
     * 以某种条件来整体更新
     *
     */
    public static <T> int updateAll(List<T> list, ConflictAlgorithm config) {
        return getLiteOrm().update(list, config);
    }

    public static <T> int updateALL(List<T> list) {
        return getLiteOrm().update(list);
    }

    public static <T> void update(Class<T> cla, String queryCol,
                                  String queryValue, String updateCol, String updateValue) {
        getLiteOrm().update(
                new WhereBuilder(cla).where(queryCol + " = ?",
                        new Object[]{queryValue}),
                new ColumnsValue(new String[]{updateCol},
                        new Object[]{updateValue}), ConflictAlgorithm.None);

    }

    @SuppressLint("NewApi")
    public static void closeDB() {
        if (liteOrm != null) {
            liteOrm.close();
        }
    }
}
