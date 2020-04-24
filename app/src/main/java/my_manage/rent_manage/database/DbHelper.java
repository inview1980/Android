package my_manage.rent_manage.database;

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

import lombok.Data;
import my_manage.tool.StrUtils;
import my_manage.rent_manage.pojo.PersonDetails;
import my_manage.rent_manage.pojo.RentalRecord;
import my_manage.rent_manage.pojo.RoomDetails;
import my_manage.rent_manage.pojo.show.ShowRoomDetails;
import my_manage.rent_manage.pojo.show.ShowRoomForMain;

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

    /**
     * 统计指定小区中每个房源的资料
     *
     * @param compoundName 小区名称，为空则统计所有
     */
    public List<ShowRoomDetails> getRoomForHouse(String compoundName) {
        List<ShowRoomDetails> resultLst = new ArrayList<>();
        List<RoomDetails> roomDetailsList;
        if (StrUtils.isNotBlank(compoundName))
            roomDetailsList = RentDB.getQueryByWhere(RoomDetails.class, "communityName", new Object[]{compoundName});
        else
            roomDetailsList = RentDB.getQueryAll(RoomDetails.class);

        for (RoomDetails roomDetails : roomDetailsList) {
            ShowRoomDetails srfh = new ShowRoomDetails(roomDetails);
            //查找此户是否已出租,recordId默认0
            if (roomDetails.getRecordId() > 0) {
                RentalRecord record = RentDB.getInfoById(roomDetails.getRecordId(), RentalRecord.class);
                if (null != record) {
                    srfh.setRentalRecord(record);
                }
            }
            //查找租户信息
            if (roomDetails.getManId() > 0) {
                PersonDetails person = RentDB.getInfoById(roomDetails.getManId(), PersonDetails.class);
                if (null != person) {
                    srfh.setPersonDetails(person);
                }
            }
            resultLst.add(srfh);
        }
        return resultLst;
    }


    public List<ShowRoomForMain> getShowRoomDesList() {
        List<RoomDetails> roomDetailsList = RentDB.getQueryAll(RoomDetails.class);
        if (roomDetailsList == null || roomDetailsList.size() == 0) return null;

        //载入数据
        List<ShowRoomForMain> tmpLst = new ArrayList<>();
        roomDetailsList.stream().map(RoomDetails::getCommunityName).distinct().forEach(name -> {
            ShowRoomForMain sr = new ShowRoomForMain();
            List<RoomDetails> showRoomDetailsList = roomDetailsList.stream().filter(rd -> name.equals(rd.getCommunityName())).collect(Collectors.toList());
            sr.setRoomCount((int) showRoomDetailsList.stream().map(RoomDetails::getCommunityName).count());
            sr.setCommunityName(name);
            sr.setRoomAreas(showRoomDetailsList.stream().flatMapToDouble(roomDes -> DoubleStream.of(roomDes.getRoomArea())).sum());
            tmpLst.add(sr);
        });

        //加入全部数据
        ShowRoomForMain sr = new ShowRoomForMain();
        sr.setCommunityName("全部房间");
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

        int columns = heads.length;
        Row row = sheet.createRow(0);
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
                        Calendar c = (Calendar) methods[j].invoke(tList.get(i));
                        String str = null == c ? null : c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DAY_OF_MONTH);
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
        rd = rd == null ? RentDB.getQueryAll(RoomDetails.class) : rd;
        rr = rr == null ? RentDB.getQueryAll(RentalRecord.class) : rr;
        ct = ct == null ? RentDB.getQueryAll(PersonDetails.class) : ct;
        XSSFWorkbook workbook = new XSSFWorkbook();
        boolean isRentalRecordOk = createSheet(rr, workbook, RentalRecord.class.getSimpleName());
        boolean isRoomDesOk = createSheet(rd, workbook, RoomDetails.class.getSimpleName());
        boolean isContactsOk = createSheet(ct, workbook, PersonDetails.class.getSimpleName());

        try {
            File outFile = new File(filename);
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
        Field[] fields = t.getClass().getDeclaredFields();
        List<Field> strings = new ArrayList<>();
        for (Field field : fields) {
            field.setAccessible(true);
            strings.add(field);
        }
        return fields;
    }

    public Method[] getGetMethod(Object ob, Field[] fields) {
        Method[] m = ob.getClass().getMethods();
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
        File inFile = new File(filename);
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
        Field[] fields = getHead(cClass);
        Map<Method, String> maps = getMaps(result, cClass);

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
                str2Obj(t, methodStringEntry.getKey(), lst.get(i).get(methodStringEntry.getValue()));
            }
            resultLst.add(t);
        }
        return resultLst;
    }

    private <T> void str2Obj(T t, Method method, String str) {
        if (StrUtils.isNotBlank(str)) {
            try {
                Object object = null;
                Class cClass = method.getParameterTypes()[0];
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
                        object = Integer.parseInt(str);
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
        Method[] methods = Arrays.stream(cClass.getDeclaredMethods()).filter(mt -> mt.getName().startsWith("set")).toArray(Method[]::new);
        Map<Method, String> result = new HashMap<>();
        for (int i = 0; i < methods.length; i++) {
            String methodName = methods[i].getName().substring(3);
            Set<String> key = lst.get(0).keySet();
            for (String s : key) {
                if (s.equalsIgnoreCase(methodName))
                    result.put(methods[i], s);
            }
        }
        return result;
    }

    /**
     * 从指定的sheet读取内容到字典，第一行数据为标题
     * @param rowsCount 行数
     */
    private List<HashMap<String, String>> readSheetToMap(XSSFSheet sheet, int rowsCount) {
        List<HashMap<String, String>> resultMap = new ArrayList<>();
        HashMap<String, String> tmp;
        Row row = sheet.getRow(0);
        int cellsCount = row.getPhysicalNumberOfCells();
        String[] heads = new String[cellsCount];
        for (int i = 0; i < heads.length; i++) {
            heads[i] = row.getCell(i).getStringCellValue();
        }
        for (int i = 0; i < rowsCount; i++) {
            row = sheet.getRow(i);
            tmp = new HashMap<>();
            for (int j = 0; j < cellsCount; j++) {
                if (row.getCell(j) == null) continue;
                tmp.put(heads[j], row.getCell(j).getStringCellValue());
            }
            resultMap.add(tmp);
        }
        return resultMap;
    }

    public boolean saveRoomDes(RoomDetails roomDetails) {
        RoomDetails tmp = RentDB.getInfoById(roomDetails.getRoomNumber(), RoomDetails.class);
        if (tmp != null) return false;
        if (RentDB.insert(roomDetails) > 0)
            return true;
        return false;
    }

    public boolean delRoomDes(String communityName) {
        if (RentDB.deleteWhere(RoomDetails.class, "communityName", new Object[]{communityName}) > 0)
            return true;
        return false;
    }

    @Data
    public class ExcelData {
        List<RoomDetails> roomDetailsList;
        List<PersonDetails> personDetailsList;
        List<RentalRecord> rentalRecordList;
    }

}
