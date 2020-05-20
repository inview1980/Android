package my_manage.tool.database;

import android.content.Context;
import android.content.res.Resources;

import com.litesuits.orm.db.assit.Encrypt;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

import lombok.Cleanup;
import lombok.Data;
import my_manage.password_box.R;
import my_manage.password_box.pojo.UserItem;
import my_manage.password_box.secret.SecretUtil;
import my_manage.rent_manage.pojo.PersonDetails;
import my_manage.rent_manage.pojo.RentalRecord;
import my_manage.rent_manage.pojo.RoomDetails;
import my_manage.rent_manage.pojo.show.ShowRoomDetails;
import my_manage.tool.StrUtils;

public final class DbHelper {
    private static DbHelper dbHelper;

    private DbHelper() {
    }

    public static DbHelper getInstance() {
        if (null == dbHelper) {
            dbHelper = new DbHelper();
        }
        return dbHelper;
    }

    public List<String> getCommunityNames() {
        return getRoomDetailsToList().stream()
                .map(RoomDetails::getCommunityName).distinct()
                .collect(Collectors.toList());
    }

    public List<ShowRoomDetails> getDeleteRoomDetails() {
        List<ShowRoomDetails> resultLst       = new ArrayList<>();
        List<RoomDetails>     roomDetailsList = getRoomDetailsByDelete();
        room2ShowRoomDetails(resultLst, roomDetailsList);
        return resultLst;
    }

    /**
     * 统计指定小区中每个房源的资料
     *
     * @param compoundName 小区名称，为空则统计所有
     */
    public List<ShowRoomDetails> getRoomForHouse(String compoundName) {
        List<ShowRoomDetails> resultLst = new ArrayList<>();
        List<RoomDetails>     roomDetailsList;
        if (StrUtils.isNotBlank(compoundName))
            roomDetailsList = DbBase.getQueryByWhere(RoomDetails.class, "communityName", new Object[]{compoundName});
        else
            roomDetailsList = DbBase.getQueryAll(RoomDetails.class);
        //去掉已删除的房源
        roomDetailsList = roomDetailsList.stream().filter(rd -> !rd.getIsDelete()).collect(Collectors.toList());
        room2ShowRoomDetails(resultLst, roomDetailsList);
        return resultLst;
    }

    /**
     * 查找数据库，将roomDetailsList中每项数据根据recordID、personID查找相应的值，返回resultLst
     */
    private void room2ShowRoomDetails(List<ShowRoomDetails> resultLst, List<RoomDetails> roomDetailsList) {
        for (RoomDetails roomDetails : roomDetailsList) {
            ShowRoomDetails srfh = new ShowRoomDetails(roomDetails);
            //查找此户是否已出租,recordId默认0
            if (roomDetails.getRecordId() > 0) {
                RentalRecord record = DbBase.getInfoById(roomDetails.getRecordId(), RentalRecord.class);
                if (null != record) {
                    srfh.setRentalRecord(record);
                    //查找租户信息
                    if (record.getManID() > 0) {
                        PersonDetails person = DbBase.getInfoById(record.getManID(), PersonDetails.class);
                        if (null != person) {
                            srfh.setPersonDetails(person);
                        }
                    }
                }
            }
            resultLst.add(srfh);
        }
    }

    public List<RoomDetails> getRoomDetailsToList() {
        List<RoomDetails> result;
        try {
            result = DbBase.getQueryAll(RoomDetails.class);
            return result.stream().filter(rd -> !rd.getIsDelete()).collect(Collectors.toList());
        } catch (Exception e) {
            return null;
        }
    }

    public List<RoomDetails> getRoomDetailsByDelete() {
        //查询删除的房屋信息，因为数据库中的true存为int=1
        return DbBase.getQueryByWhere(RoomDetails.class, "isDelete", new Object[]{1});
    }

    public List<ShowRoomDetails> getShowRoomDesList() {
        List<RoomDetails> roomDetailsList = getRoomDetailsToList();
        if (roomDetailsList == null || roomDetailsList.size() == 0) return null;

        //载入数据
        List<ShowRoomDetails> tmpLst = new ArrayList<>();
        roomDetailsList.stream().map(RoomDetails::getCommunityName).distinct().forEach(name -> {
            ShowRoomDetails sr = new ShowRoomDetails();
            List<RoomDetails> showRoomDetailsList = roomDetailsList.stream()
                    .filter(rd -> name.equals(rd.getCommunityName()) && !rd.getIsDelete()).collect(Collectors.toList());
            sr.setRoomCount((int) showRoomDetailsList.stream().map(RoomDetails::getCommunityName).count());
            sr.getRoomDetails().setCommunityName(name);
            sr.setRoomAreas(showRoomDetailsList.stream().flatMapToDouble(roomDes -> DoubleStream.of(roomDes.getRoomArea())).sum());
            tmpLst.add(sr);
        });

        //加入全部数据
        ShowRoomDetails sr = new ShowRoomDetails();
        sr.getRoomDetails().setCommunityName("全部房间");
        sr.setRoomAreas(roomDetailsList.stream().flatMapToDouble(rd -> DoubleStream.of(rd.getRoomArea())).sum());
        sr.setRoomCount((int) roomDetailsList.stream().map(RoomDetails::getCommunityName).count());
        tmpLst.add(sr);

        return tmpLst;
    }

    private <T> boolean createSheet(List<T> tList, XSSFWorkbook workbook, String sheetName) {
        if (StrUtils.isBlank(sheetName) || tList == null || tList.size() == 0) return false;

        XSSFSheet sheet = workbook.createSheet(WorkbookUtil.createSafeSheetName(sheetName));
        //获取类的所有字段
        Field[] fields = getHead(tList.get(0));
        //写入标题
        String[] heads = Arrays.stream(fields).map(Field::getName).toArray(String[]::new);
        //获取类的所有get方法
        Method[] methods = getGetMethod(tList.get(0), fields);

        int  columns = heads.length;
        Row  row     = sheet.createRow(0);
        Cell cell;
        for (int i = 0; i < columns; i++) {
            cell = row.createCell(i);
            cell.setCellValue(heads[i]);
        }
        try {
            //写入内容
            for (int i = 0; i < tList.size(); i++) {
                row = sheet.createRow(i + 1);
                for (int j = 0; j < columns; j++) {
                    cell = row.createCell(j);
                    if (methods[j].getReturnType() == Calendar.class) {
                        Calendar c   = (Calendar) methods[j].invoke(tList.get(i));
                        String   str = null == c ? null : c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DAY_OF_MONTH);
                        cell.setCellValue(str);
                    } else {
                        Object obj = methods[j].invoke(tList.get(i));
                        if (obj == null) continue;
                        cell.setCellValue(String.valueOf(obj));
                    }
                }
            }
            return true;
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean toExcel(String filename, List<RoomDetails> rd, List<RentalRecord> rr, List<PersonDetails> ct) {
        if (StrUtils.isBlank(filename)) return false;
        rd = rd == null ? DbBase.getQueryAll(RoomDetails.class) : rd;
        rr = rr == null ? DbBase.getQueryAll(RentalRecord.class) : rr;
        ct = ct == null ? DbBase.getQueryAll(PersonDetails.class) : ct;
        XSSFWorkbook workbook         = new XSSFWorkbook();
        boolean      isRentalRecordOk = createSheet(rr, workbook, RentalRecord.class.getSimpleName());
        boolean      isRoomDesOk      = createSheet(rd, workbook, RoomDetails.class.getSimpleName());
        boolean      isContactsOk     = createSheet(ct, workbook, PersonDetails.class.getSimpleName());

        try {
            File         outFile      = new File(filename);
            OutputStream outputStream = new FileOutputStream(outFile.getAbsolutePath());
            workbook.write(outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            /* proper exception handling to be here */
        }
        return isRentalRecordOk && isRoomDesOk && isContactsOk;
    }

    public boolean toExcel(String filename) {
        return toExcel(filename, null, null, null);
    }

    private <T> Field[] getHead(T t) {
        Field[]     fields  = t.getClass().getDeclaredFields();
        List<Field> strings = new ArrayList<>();
        for (Field field : fields) {
            field.setAccessible(true);
            strings.add(field);
        }
        return fields;
    }

    public Method[] getGetMethod(Object ob, Field[] fields) {
        Method[]     m      = ob.getClass().getMethods();
        List<Method> result = new ArrayList<>();

        for (Field field : fields) {
            for (Method method : m) {
                if (("get" + field.getName()).toLowerCase().equals(method.getName().toLowerCase())) {
                    result.add(method);
                }
            }
        }
        return result.toArray(new Method[0]);
    }

    public ExcelData readExcel(String filename) {
        if (StrUtils.isBlank(filename)) return null;
        File        inFile      = new File(filename);
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(inFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return readExcel(inputStream);
    }

    public ExcelData readExcel(InputStream inputStream) {
        ExcelData excelData = new ExcelData();
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                XSSFSheet sheet = workbook.getSheetAt(i);
                if (sheet.getSheetName().equals(RoomDetails.class.getSimpleName())) {
                    excelData.setRoomDetailsList(readSheet(sheet, RoomDetails.class));
                }
                if (sheet.getSheetName().equalsIgnoreCase(RentalRecord.class.getSimpleName())) {
                    excelData.setRentalRecordList(readSheet(sheet, RentalRecord.class));
                }
                if (sheet.getSheetName().equalsIgnoreCase(PersonDetails.class.getSimpleName())) {
                    excelData.setPersonDetailsList(readSheet(sheet, PersonDetails.class));
                }
            }
            return excelData;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private <T> List<T> readSheet(XSSFSheet sheet, Class<T> cClass) {
        int rowsCount = sheet.getPhysicalNumberOfRows();
        if (rowsCount < 1) return null;
        List<HashMap<String, String>> result = readSheetToMap(sheet, rowsCount);

        //获取类的所有字段
        Field[]             fields = getHead(cClass);
        Map<Method, String> maps   = getMaps(result, cClass);

        try {
            return objToClass(result, maps, cClass);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将读取的集合转换为指定类的集合
     */
    private <T> List<T> objToClass(List<HashMap<String, String>> lst, Map<Method, String> maps, Class<T> cClass) throws InstantiationException, IllegalAccessException {
        List<T> resultLst = new ArrayList<>();
        for (int i = 1; i < lst.size(); i++) {
            //从1开头，因为0为标题，数据从1开始
            T t = cClass.newInstance();
            for (Map.Entry<Method, String> methodStringEntry : maps.entrySet()) {
                String tmp = methodStringEntry.getValue();
                if (tmp == null) continue;
                str2Obj(t, methodStringEntry.getKey(), lst.get(i).get(tmp));
            }
            resultLst.add(t);
        }
        return resultLst;
    }

    private <T> void str2Obj(T t, Method method, String str) {
        if (StrUtils.isNotBlank(str)) {
            try {
                Object object = null;
                Class  cClass = method.getParameterTypes()[0];
                switch (cClass.getSimpleName()) {
                    case "Calendar":
                        String[] a = str.split("-");
                        if (a.length > 2) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(Integer.parseInt(a[0]), Integer.parseInt(a[1]) - 1, Integer.parseInt(a[2]));
                            object = calendar;
                        }
                        break;
                    case "String":
                        object = str;
                        break;
                    case "int":
                        object = (int) Double.parseDouble(str);
                        break;
                    case "double":
                        object = Double.parseDouble(str);
                        break;
                    case "boolean":
                        object = Boolean.parseBoolean(str);
                        break;
                    default:
                        String secStr = cClass.getSimpleName();
                        String name = "parse" + secStr.substring(0, 1).toUpperCase() + secStr.substring(1);
                        cClass.getMethod(name, String.class).invoke(t, str);
                        return;
                }
                method.invoke(t, object);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取指定类的字典，以set开头方法
     */
    private Map<Method, String> getMaps(List<HashMap<String, String>> lst, Class cClass) {
        if (lst == null) return null;
        Method[]            methods = Arrays.stream(cClass.getDeclaredMethods()).filter(mt -> mt.getName().startsWith("set")).toArray(Method[]::new);
        Map<Method, String> result  = new HashMap<>();
        for (int i = 0; i < methods.length; i++) {
            String      methodName = methods[i].getName().substring(3);
            Set<String> key        = lst.get(0).keySet();
            for (String s : key) {
                if (s.equalsIgnoreCase(methodName))
                    result.put(methods[i], s);
            }
        }
        return result;
    }

    /**
     * 从指定的sheet读取内容到字典，第一行数据为标题
     *
     * @param rowsCount 行数
     */
    private List<HashMap<String, String>> readSheetToMap(XSSFSheet sheet, int rowsCount) {
        List<HashMap<String, String>> resultMap  = new ArrayList<>();
        HashMap<String, String>       tmp;
        Row                           row        = sheet.getRow(0);
        int                           cellsCount = row.getPhysicalNumberOfCells();
        String[]                      heads      = new String[cellsCount];
        for (int i = 0; i < heads.length; i++) {
            heads[i] = row.getCell(i).getStringCellValue();
        }
        for (int i = 0; i < rowsCount; i++) {
            row = sheet.getRow(i);
            tmp = new HashMap<>();
            for (int j = 0; j < cellsCount; j++) {
                if (row.getCell(j) == null) continue;
                if (row.getCell(j).getCellType() == Cell.CELL_TYPE_NUMERIC) {
                    tmp.put(heads[j], "" + row.getCell(j).getNumericCellValue());
                } else if (row.getCell(j).getCellType() == Cell.CELL_TYPE_STRING) {
                    tmp.put(heads[j], row.getCell(j).getStringCellValue());
                }
            }
            resultMap.add(tmp);
        }
        return resultMap;
    }

    public boolean saveRoomDes(RoomDetails roomDetails) {
        RoomDetails tmp = DbBase.getInfoById(roomDetails.getRoomNumber(), RoomDetails.class);
        if (tmp != null) {
            return DbBase.update(roomDetails) > 0;
        } else {
            return DbBase.insert(roomDetails) > 0;
        }
    }

    public boolean delRoomDes(String communityName) {
        if (DbBase.deleteWhere(RoomDetails.class, "communityName", new Object[]{communityName}) > 0)
            return true;
        return false;
    }

    public List<PersonDetails> getPersonList(boolean addBlankItem) {
        List<PersonDetails> tmpLst = DbBase.getQueryAll(PersonDetails.class);
        if (tmpLst == null) return new ArrayList<>();
        tmpLst.sort((n1, n2) -> Integer.compare(n2.getPrimary_id(), n1.getPrimary_id()));
        //不添加空白的租户
        if (addBlankItem)
            tmpLst.add(new PersonDetails());
        return tmpLst;
    }

    public List<PersonDetails> getPersonList() {
        return getPersonList(true);
    }

    public List<RentalRecord> getRecords() {
        return DbBase.getQueryAll(RentalRecord.class);
    }

    /**
     * 将房源归入已删除列表，但在数据库中不删除
     */
    public boolean delRoomDes(ShowRoomDetails showRoomDetails) {
        RoomDetails rd = DbBase.getInfoById(showRoomDetails.getRoomDetails().getRoomNumber(), RoomDetails.class);
        if (rd == null) return false;
        rd.setIsDelete(true);
        return DbBase.update(rd) > 0;
    }

    public boolean restoreDelete(ShowRoomDetails showRoomDetails) {
        if (showRoomDetails == null) return false;

        RoomDetails rd = DbBase.getInfoById(showRoomDetails.getRoomDetails().getRoomNumber(), RoomDetails.class);
        if (rd == null) return false;
        rd.setIsDelete(false);
        return DbBase.update(rd) > 0;
    }

    /**
     * 获取指定房号的历史记录
     */
    public List<ShowRoomDetails> getHistoryByRoomNumber(String roomNumber) {
        List<ShowRoomDetails> resultLst  = new ArrayList<>();
        List<RentalRecord>    recordList = DbBase.getQueryByWhere(RentalRecord.class, "roomNumber", new Object[]{roomNumber});
        RoomDetails           rd         = DbBase.getInfoById(roomNumber, RoomDetails.class);
        ShowRoomDetails       show;
        for (final RentalRecord record : recordList) {
            show = new ShowRoomDetails(rd);
            show.setRentalRecord(record);
            PersonDetails pd = DbBase.getInfoById(record.getManID(), PersonDetails.class);
            show.setPersonDetails(pd);
            resultLst.add(show);
        }
        return resultLst;
    }

    public List<ShowRoomDetails> getShowRoomDesForPerson(int personId) {
        List<ShowRoomDetails> result     = new ArrayList<>();
        PersonDetails         person     = DbBase.getInfoById(personId, PersonDetails.class);
        ShowRoomDetails       roomDet;
        List<RentalRecord>    historyLst = DbBase.getQueryByWhere(RentalRecord.class, "manID", new Object[]{personId});
        for (final RentalRecord record : historyLst) {
            roomDet = new ShowRoomDetails(DbBase.getInfoById(record.getRoomNumber(), RoomDetails.class));
            roomDet.setRentalRecord(record);
            roomDet.setPersonDetails(person);
            result.add(roomDet);
        }
        return result;
    }

    public int delPersonById(int primary_id) {
        return DbBase.deleteWhere(PersonDetails.class, "primary_id", new Object[]{primary_id});
    }

    public int insert(RentalRecord rr) {
        if (DbBase.insert(rr) > 0) {
            int max = DbBase.getQueryAll(RentalRecord.class).stream().mapToInt(RentalRecord::getPrimary_id).max().getAsInt();
            return max;
        }
        return -1;
    }

    @Data
    public class ExcelData {
        List<RoomDetails>   roomDetailsList;
        List<PersonDetails> personDetailsList;
        List<RentalRecord>  rentalRecordList;
    }

    /**
     * 删除并重建数据库
     */
    public void rebuilding(Context context) {
        String path = DbBase.DB_NAME;
        //删除数据库文件
        List<RoomDetails> rd = DbHelper.getInstance().getRoomDetailsToList();
        DbBase.getLiteOrm().deleteDatabase();
        DbBase.getLiteOrm().openOrCreateDatabase();

        //重新初始化数据库
        dbInit(context, path);
    }

    /**
     * 初始化数据库，如数据库无数据，读取xlsx文件并填入数据
     */
    public void dbInit(Context context, String DBFilePath) {
        //初始化数据库
        DbBase.createCascadeDB(context, DBFilePath);

        List<RoomDetails> rd = DbHelper.getInstance().getRoomDetailsToList();
        if (rd != null && rd.size() != 0) return;
        //当数据库空时，填充数据库内容
        try {
            @Cleanup InputStream is = context.getResources().openRawResource(R.raw.db);
            DbHelper.ExcelData   ed = DbHelper.getInstance().readExcel(is);
            is.close();
            DbBase.insertAll(ed.getPersonDetailsList());
            DbBase.insertAll(ed.getRentalRecordList());
            DbBase.insertAll(ed.getRoomDetailsList());
        } catch (Resources.NotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

    public boolean saveUserItem(UserItem userItem) {
        if (userItem == null || StrUtils.isBlank(userItem.getSalt())) return false;
        //加密密码
        userItem.setPassword(SecretUtil.getEncryptString(userItem.getSalt(), userItem.getPassword()));
        if (userItem.getId() == 0) {
            return DbBase.insert(userItem) > 0;
        }
        long resultNo;
        if (DbBase.getInfoById(userItem.getId(), UserItem.class) != null) {
            resultNo = DbBase.update(userItem);
        } else {
            resultNo = DbBase.insert(userItem);
        }
        return resultNo > 0;
    }

    public boolean loadIn(Context context, String pwd) {
        if (pwd == null) {
            //默认为指纹通过后，直接通过
            return true;
        }
        UserItem item = DbBase.getInfoById(1, UserItem.class);
        if (item == null) {
            //新增默认用户和密码
            addDefaultItem(context);
            return pwd.equals(context.getString(R.string.defaultPassword));
        } else {
            String pa = Encrypt.getMD5EncString(new StringBuilder(item.getSalt())
                    .insert(context.getResources().getInteger(R.integer.defaultSaltLength) / 2, pwd).toString());
            if (pa.equals(item.getPassword())) {
                return true;
            } else {
                return false;
            }
        }
    }

    private void addDefaultItem(Context context) {
        UserItem item = new UserItem();
        item.setSalt(StrUtils.getRandomString(context.getResources().getInteger(R.integer.defaultSaltLength)));
        String doc = context.getString(R.string.defaultPassword);
        String tmp = Encrypt.getMD5EncString(new StringBuilder(item.getSalt())
                .insert(context.getResources().getInteger(R.integer.defaultSaltLength) / 2, doc).toString());
        item.setPassword(tmp);
        item.setId(1);
        DbBase.insert(item);
    }

    public boolean resetPassword(Context context, String pwd) {
        UserItem item = new UserItem();
        item.setSalt(StrUtils.getRandomString(context.getResources().getInteger(R.integer.defaultSaltLength)));
        String str = pwd == null ? context.getString(R.string.defaultPassword) : pwd;
        String tmp = Encrypt.getMD5EncString(new StringBuilder(item.getSalt())
                .insert(context.getResources().getInteger(R.integer.defaultSaltLength) / 2, str).toString());
        item.setPassword(tmp);
        item.setId(1);
        if (DbBase.getInfoById(1, UserItem.class) == null) {
            DbBase.insert(item);
        } else {
            DbBase.update(item);
        }
        return true;
    }

    public List<UserItem> getItemsByAfter(Context context) {
        List<UserItem> itemList = DbBase.getLiteOrm().query(UserItem.class);
        if (itemList == null || itemList.size() == 0) {
            //数据库无数据时
            addDefaultItem(context);
            return new ArrayList<>();
        }
        //解密
        try {
            List<UserItem> result = new ArrayList<>();
            for (int i = 1; i < itemList.size(); i++) {
                UserItem im   = itemList.get(i);
                UserItem item = new UserItem();
                item.setId(im.getId());
                item.setSalt(im.getSalt());
                item.setAddress(im.getAddress());
                item.setItemName(im.getItemName());
                item.setRemark(im.getRemark());
                item.setUserName(im.getUserName());
                item.setPassword(SecretUtil.getDecryptSting(im.getSalt(), im.getPassword()));
                result.add(item);
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public boolean delUserItem(UserItem userItem) {
        if (userItem == null || userItem.getId() == 0) return false;
        return delUserItem(userItem.getId());
    }

    public boolean delUserItem(int id) {
        return DbBase.deleteWhere(UserItem.class, "id", new Object[]{id}) > 0;
    }

    public boolean changePassword(Context context, String old, String newStr) {
        if (!loadIn(context, old)) return false;
        return resetPassword(context, newStr);
    }
}
