package my_manage.tool;

import android.content.Context;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import lombok.val;
import my_manage.iface.ColumnName;
import my_manage.password_box.R;
import my_manage.pojo.show.ExcelData;
import my_manage.tool.database.DbBase;

public class ExcelUtils {
    private static ExcelUtils excelUtils;
    public         String     outFilePath;

    private ExcelUtils() {
    }

    public static ExcelUtils getInstance() {
        if (null == excelUtils) {
            excelUtils = new ExcelUtils();
        }
        return excelUtils;
    }

    private Map<String, String> getAnnotation(Class cClass) {
        Map<String, String> result = new LinkedHashMap<>();
        Field[]             fields = cClass.getDeclaredFields();
        for (final Field field : fields) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(ColumnName.class)) {
                val column = field.getAnnotation(ColumnName.class);
                result.put(column.value(), field.getName());
            } else {
                result.put(field.getName(), field.getName());
            }
        }
        return result;
    }

    private <T> boolean createSheet(List<T> tList, WritableWorkbook workbook, String sheetName, int sheetNo) {
        if (StrUtils.isBlank(sheetName) || tList == null || tList.size() == 0) return false;

        WritableSheet sheet = workbook.createSheet(sheetName, sheetNo);
        //获取类的所有字段
        Field[] fields = getHead(tList.get(0));
        //写入标题
        String[] heads = getAnnotation(tList.get(0).getClass()).keySet().toArray(new String[0]);
        //获取类的所有get方法
        Method[] methods = getGetMethod(tList.get(0), fields);

        int columns = heads.length;
        try {
            for (int i = 0; i < columns; i++) {
                sheet.addCell(new Label(i, 0, heads[i]));
            }
            //写入内容
            for (int i = 0; i < tList.size(); i++) {
                for (int j = 0; j < columns; j++) {
                    String str;
                    if (methods[j].getReturnType() == Calendar.class) {
                        Calendar c = (Calendar) methods[j].invoke(tList.get(i));
                        str = null == c ? null : c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DAY_OF_MONTH);
                    } else {
                        Object obj = methods[j].invoke(tList.get(i));
                        if (obj == null) continue;
                        str = obj.toString();
                    }
                    sheet.addCell(new Label(j, i + 1, str));
                }
            }
            return true;
        } catch (IllegalAccessException | InvocationTargetException | WriteException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean toExcel(String filename, ExcelData data) {
        if (StrUtils.isBlank(filename)) return false;
        //获取ExcelData类所有字段
        WritableWorkbook workbook = null;
        try {
            val map = getExcelDataFieldType2Map().entrySet();
            if (data == null) {
                data = new ExcelData();
                for (final Map.Entry<Field, Class> item : map) {
                    item.getKey().set(data, DbBase.getLiteOrm().query(item.getValue()));

                }
            }
            int sheetNo = 0;

            workbook = Workbook.createWorkbook(new File(filename));
            for (final Map.Entry<Field, Class> item : map) {
                createSheet((List) item.getKey().get(data), workbook, item.getValue().getSimpleName(), sheetNo++);
            }
            workbook.write();
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean toExcel(String filename) {
        return toExcel(filename, null);
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

    private Method[] getGetMethod(Object ob, Field[] fields) {
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

//    public ExcelData readExcel(String filename) {
//        if (StrUtils.isBlank(filename)) return null;
//        File inFile = new File(filename);
//        if (!inFile.exists()) return null;
//        try {
//            @Cleanup InputStream is = new FileInputStream(inFile);
//            return readExcel(is);
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

    private Map<Field, Class> getExcelDataFieldType2Map() {
        Map<Field, Class> resultMap = new HashMap<>();
        Field[]           fields    = ExcelData.class.getDeclaredFields();
        for (final Field field : fields) {
            field.setAccessible(true);
            //判断是否字段是否List
            if (field.getType() == List.class) {
                Type type = field.getGenericType();
                //提取List泛形中的第一个参数
                if (type instanceof ParameterizedType) {
                    ParameterizedType pt = (ParameterizedType) type;
                    // 得到泛型里的class类型对象
                    Class<?> actualTypeArgument = (Class<?>) pt.getActualTypeArguments()[0];
                    resultMap.put(field, actualTypeArgument);
                }
            }
        }
        return resultMap;
    }

    public ExcelData readExcel(InputStream file) {
        ExcelData excelData = new ExcelData();
        try {
            Workbook workbook = Workbook.getWorkbook(file);
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                //获取ExcelData类所有字段
                val map = getExcelDataFieldType2Map().entrySet();
                for (final Map.Entry<Field, Class> item : map) {
                    //判断xls表格中是否包含指定类的名称
                    if (Arrays.asList(workbook.getSheetNames()).contains(item.getValue().getSimpleName())) {
                        item.getKey().set(excelData, readSheet(workbook.getSheet(item.getValue().getSimpleName()), item.getValue()));
                    }
                }
            }
            return excelData;
        } catch (IOException | BiffException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    private <T> List<T> readSheet(Sheet sheet, Class<T> cClass) {
        int rowsCount = sheet.getRows();
        if (rowsCount < 1) return null;
        List<HashMap<String, String>> result = readSheetToMap(sheet, cClass, rowsCount);

        //获取类的所有字段
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
    private List<HashMap<String, String>> readSheetToMap(Sheet sheet, Class cClass, int rowsCount) {
        List<HashMap<String, String>> resultMap = new ArrayList<>();
        HashMap<String, String>       tmp;
        val                           headRow   = sheet.getRow(0);
        val                           dic       = getAnnotation(cClass);
        List<String>                  headLst   = new ArrayList<>();
        for (int i = 0; i < headRow.length; i++) {
            String str = headRow[i].getContents();
            if (StrUtils.isNotBlank(str) && dic.containsKey(str)) {
                headLst.add(dic.get(str));
            } else {
                headLst.add(str);
            }
        }
        String[] heads = headLst.toArray(new String[0]);
        for (int i = 0; i < rowsCount; i++) {
            val row = sheet.getRow(i);
            tmp = new HashMap<>();
            for (int j = 0; j < row.length; j++) {
                if (row[j] == null) continue;
                tmp.put(heads[j], row[j].getContents());
            }
            resultMap.add(tmp);
        }
        return resultMap;
    }

    /**
     * 将数据库转出为xlsx
     */
    public boolean saveDB(Context context, boolean isShowDialog) {
        outFilePath = context.getApplicationContext().getExternalFilesDir(null).getAbsolutePath()
                + "/" + context.getResources().getString(R.string.rentalFileNameBackup) + "."
                + context.getResources().getString(R.string.extensionName);
        if (isShowDialog) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setMessage("文件将保存在：" + outFilePath)
                    .setPositiveButton(R.string.ok_cn, null)
                    .show();
        }
        if (ExcelUtils.getInstance().toExcel(outFilePath)) {
            if (isShowDialog)
                Toast.makeText(context, "导出数据库文件成功", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }
}
