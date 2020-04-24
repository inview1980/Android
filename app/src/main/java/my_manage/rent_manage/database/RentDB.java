package my_manage.rent_manage.database;

import android.annotation.SuppressLint;
import android.content.Context;

import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.DataBaseConfig;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.litesuits.orm.db.assit.WhereBuilder;
import com.litesuits.orm.db.model.ColumnsValue;
import com.litesuits.orm.db.model.ConflictAlgorithm;

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
    public static String DB_NAME;
    private static LiteOrm liteOrm;
    public static Context mContext;


    /**
     * 数据库名称
     *
     * @return
     */
    private static String getUserDatabaseName() {
        return "rent_info";
    }

    /**
     * 创建级联数据库
     *
     * @param context
     */
    public static boolean createCascadeDB(Context context, String path) {
        mContext = context.getApplicationContext();
        DB_NAME = StrUtils.isBlank(path) ? getUserDatabaseName() : path;
        liteOrm = LiteOrm.newSingleInstance(getDBConfig());
//        liteOrm.setDebugged(LogUtil.isDebuggable());
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
            if (mContext == null) {
                mContext = MainActivity.context;
            }
            DB_NAME = getUserDatabaseName();
            liteOrm = LiteOrm.newSingleInstance(getDBConfig());
//            liteOrm.setDebugged(LogUtil.isDebuggable());
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
     *
     * @param list
     */
    public static <T> int insertAll(List<T> list) {
        return getLiteOrm().save(list);
    }

    /**
     * 以某种条件作为插入标准
     *
     * @param
     * @return
     */
    public static <T> long insertAll(Collection<T> t, ConflictAlgorithm config) {
        return getLiteOrm().insert(t, config);
    }

    /**
     * 以某种条件作为插入标准
     *
     * @param
     * @return
     */
    public static <T> long insertAll(List<T> t, ConflictAlgorithm config) {
        return getLiteOrm().insert(t, config);
    }


    /**
     * 查询所有
     *
     * @param cla
     * @return
     */
    public static <T> List<T> getQueryAll(Class<T> cla) {
        return getLiteOrm().query(cla);
    }

    /**
     * 根据ID查询
     *
     * @param cla
     * @return
     */
    public static <T> T getInfoById(String id, Class<T> cla) {
        return getLiteOrm().queryById(id, cla);
    }

    /**
     * 根据ID查询
     *
     * @param cla
     * @return
     */
    public static <T> T getInfoById(long id, Class<T> cla) {
        return getLiteOrm().queryById(id, cla);
    }

    /**
     * 查询 某字段 等于 Value的值
     *
     * @param cla
     * @param field
     * @param value
     * @return
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> List<T> getQueryByWhere(Class<T> cla, String field, Object[] value) {
        return getLiteOrm().<T>query(new QueryBuilder(cla).where(field + "=?", value));
    }

    /**
     * 模糊查询
     *
     * @param cla
     * @param field
     * @param value
     * @param <T>
     * @return
     */
    public static <T> List<T> getQueryByTime(Class<T> cla, String field, Object[] value) {
        return getLiteOrm().<T>query(new QueryBuilder(cla).where(field + " LIKE ?", value));
    }

    /**
     * 查询 某字段 等于 Value的值 可以指定从1-20，就是分页
     *
     * @param cla
     * @param field
     * @param value
     * @param start
     * @param length
     * @return
     */
    public static <T> List<T> getQueryByWhereLength(Class<T> cla, String field, String[] value, int start, int length) {
        return getLiteOrm().<T>query(new QueryBuilder(cla).where(field + "=?", value).limit(start, length));
    }

    /**
     * 删除所有 某字段等于 Vlaue的值
     *
     * @param cla
     * @param field
     * @param value
     */
    @SuppressWarnings("deprecation")
    public static <T> int deleteWhere(Class<T> cla, String field, String[] value) {
        return getLiteOrm().delete(cla, WhereBuilder.create(cla, field + "=?", value));
    }

    /**
     * 删除所有 某字段等于 Vlaue的值
     *
     * @param cla
     * @param field
     * @param value
     */
    public static <T> int deleteWhere(Class<T> cla, String field, Object[] value) {
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
     * @param t
     */
    public static <T> int update(T t) {
        return getLiteOrm().update(t, ConflictAlgorithm.Replace);
    }

    /**
     * 以某种条件来整体更新
     *
     * @param list
     * @param config
     * @return
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
                        new String[]{queryValue}),
                new ColumnsValue(new String[]{updateCol},
                        new Object[]{updateValue}), ConflictAlgorithm.None);

    }

    @SuppressLint("NewApi")
    public void closeDB() {
        if (liteOrm != null) {
            liteOrm.close();
        }
    }
}
