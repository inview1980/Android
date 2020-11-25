package my_manage.tool.database;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;

import com.alibaba.fastjson.JSON;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

import lombok.Cleanup;
import lombok.val;
import my_manage.iface.IShowList;
import my_manage.pojo.FuelRecord;
import my_manage.ui.password_box.R;
import my_manage.pojo.UserItem;
import my_manage.ui.password_box.secret.SecretUtil;
import my_manage.pojo.PersonDetails;
import my_manage.pojo.RentalRecord;
import my_manage.pojo.RoomDetails;
import my_manage.pojo.show.ExcelData;
import my_manage.pojo.show.MenuData;
import my_manage.pojo.show.ShowRoomDetails;
import my_manage.tool.ExcelUtils;
import my_manage.tool.PageUtils;
import my_manage.tool.StrUtils;
import my_manage.tool.enums.MenuTypesEnum;

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
            //去除重复的房号
            val rooms = result.stream().map(rr -> rr.getRoomDetails().getRoomNumber()).collect(Collectors.toList());
            if (!rooms.contains(roomDet.getRoomDetails().getRoomNumber()))
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


    /**
     * 删除并重建数据库
     */
    public void rebuilding() {
//        String path = DbBase.DB_NAME;
        //删除数据库文件
        DbBase.getLiteOrm().deleteDatabase();
        DbBase.getLiteOrm().openOrCreateDatabase();

        //载入默认数据填充数据库
//        dbInit(context, path);
    }

    /**
     * 读取指定文件并填充入数据库
     */
    public boolean loadFile2DB(String filename) {
        if (StrUtils.isBlank(filename)) return false;

        try {
            @Cleanup InputStream is = new FileInputStream(filename);
            return read2DB(is);
        } catch (IllegalAccessException | IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 重建数据库，读取xlsx文件并填入数据
     */
    public boolean loadDefault2DB(Context context) {
        try {
            @Cleanup InputStream is   = context.getResources().openRawResource(R.raw.db);
            boolean              isOk = read2DB(is);
            PageUtils.Log("读取默认数据并写入数据库");
            return isOk;
        } catch (Resources.NotFoundException | IOException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean read2DB(InputStream is) throws IOException, IllegalAccessException {
        ExcelData ed = ExcelUtils.getInstance().readExcel(is);
        is.close();
        //读取文件，如果读取的内容为空则退出
        if (ed != null && ed.getRoomDetailsList().size() > 0 && ed.getRentalRecordList().size() > 0) {
            //重建数据库
            rebuilding();
        } else {
            return false;
        }
        //将读取xls文件的内容写入数据库中
        Field[] fields = ExcelData.class.getDeclaredFields();
        for (final Field field : fields) {
            field.setAccessible(true);
            if (field.getType() != List.class) continue;
            List obj = (List) field.get(ed);
            if (obj != null) {
                DbBase.insertAll(obj);
            }
        }
        return true;
    }

    public boolean saveUserItem(UserItem userItem) {
        if (userItem == null || StrUtils.isBlank(userItem.getSalt())) return false;
        //加密密码
        String pwd = getPassword();
        userItem.setPassword(StrUtils.isNotBlank(userItem.getPassword()) ? SecretUtil.getEncryptString(pwd, userItem.getPassword()) : null);
        userItem.setRemark(StrUtils.isNotBlank(userItem.getRemark()) ? SecretUtil.getEncryptString(pwd, userItem.getRemark()) : null);
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
        try {
            UserItem item = DbBase.getInfoById(1, UserItem.class);
            if (item == null) {
                //新增默认用户和密码
                addDefaultItem(context);
                return pwd.equals(context.getString(R.string.defaultPassword));
            } else {
                String pwdD = SecretUtil.getDecryptSting(item.getSalt(), item.getPassword());
                if (pwdD.equals(pwd)) {
                    //用以判断是否第一次输入密码
                    item.setItemName("logged in");
                    DbBase.update(item);
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void addDefaultItem(Context context) {
        UserItem item = new UserItem();
        item.setSalt(StrUtils.getRandomString(context.getResources().getInteger(R.integer.defaultSaltLength)));
        String doc = context.getString(R.string.defaultPassword);
        String tmp = SecretUtil.getEncryptString(item.getSalt(), doc);
        item.setPassword(tmp);
        item.setId(1);
        DbBase.insert(item);
    }

    public boolean resetPassword(Context context, String oldPwd, String pwd) {
        if (!getPassword().equals(oldPwd)) return false;

        UserItem item = new UserItem();
        item.setSalt(StrUtils.getRandomString(context.getResources().getInteger(R.integer.defaultSaltLength)));
        String str = pwd == null ? context.getString(R.string.defaultPassword) : pwd;
        String tmp = SecretUtil.getEncryptString(item.getSalt(), str);
        item.setPassword(tmp);
        item.setItemName(null);
        item.setId(1);
        if (DbBase.getInfoById(1, UserItem.class) == null) {
            DbBase.insert(item);
        } else {
            DbBase.update(item);
        }
        //重新加密所有项目的密码
        val lst = DbBase.getQueryAll(UserItem.class);
        for (int i = 1; i < lst.size(); i++) {
            String s1 = StrUtils.isNotBlank(lst.get(i).getRemark()) ? SecretUtil.getDecryptSting(oldPwd, lst.get(i).getRemark()) : null;
            String s2 = StrUtils.isNotBlank(lst.get(i).getPassword()) ? SecretUtil.getDecryptSting(oldPwd, lst.get(i).getPassword()) : null;
            if (s1 != null) lst.get(i).setRemark(SecretUtil.getEncryptString(pwd, s1));
            if (s2 != null) lst.get(i).setPassword(SecretUtil.getEncryptString(pwd, s2));
        }
        return DbBase.updateALL(lst) > 0;
    }

    private List<UserItem> decryptItems(List<UserItem> lst) {
        if (lst == null || lst.size() == 0) return null;
        List<UserItem> result = new ArrayList<>();
        try {
            String pwd = getPassword();
            for (UserItem ui : lst) {
                String   tmp = JSON.toJSONString(ui);
                UserItem ui2 = JSON.parseObject(tmp, UserItem.class);
                ui2.setPassword(StrUtils.isNotBlank(ui.getPassword()) ? SecretUtil.getDecryptSting(pwd, ui.getPassword()) : null);
                ui2.setRemark(StrUtils.isNotBlank(ui.getRemark()) ? SecretUtil.getDecryptSting(pwd, ui.getRemark()) : null);
                result.add(ui2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private String getPassword() {
        UserItem userItem = DbBase.getInfoById(1, UserItem.class);
        if (userItem != null) {
            return SecretUtil.getDecryptSting(userItem.getSalt(), userItem.getPassword());
        }
        return "";
    }

    public List<UserItem> getItemsByAfter(Context context, int typeId) {
        List<UserItem> itemList;
        if (typeId != 0) {
            itemList = DbBase.getQueryByWhere(UserItem.class, "typeNameId", new Object[]{typeId});
            itemList = decryptItems(itemList);
        } else {
            itemList = DbBase.getQueryAll(UserItem.class);
            if (itemList.size() > 0)
                itemList.remove(0);
            itemList = decryptItems(itemList);
        }
        return itemList;
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
        return resetPassword(context, old, newStr);
    }

    public List<MenuData> getMenuTypes(Context context, MenuTypesEnum typesEnum) {
        String[] arrays    = context.getResources().getStringArray(R.array.menuTypes);
        val      resultLst = new ArrayList<MenuData>();
        for (final String item : arrays) {
            String[] tmp  = item.split("-");
            int      type = Integer.parseInt(tmp[4]);
            if (type == typesEnum.getId()) {
                int      i  = 0;
                MenuData md = new MenuData();
                md.setPrimary_id(Integer.parseInt(tmp[i++]));
                md.setIcon(tmp[i++]);
                md.setTitle(tmp[i++]);
                md.setColor(tmp[i]);
                md.setType(type);
                resultLst.add(md);
            }
        }
        return resultLst;
    }

    public MenuData getPasswordTypeByID(int typeNameId) {
        return DbBase.getInfoById(typeNameId, MenuData.class);
    }

    public int getPasswordTypeCount(int id) {
        int i = DbBase.getQueryByWhere(UserItem.class, "typeNameId", new Object[]{id}).size();
        return i;
    }

    public boolean deleteRoom(ShowRoomDetails showRoomDetails) {
        if (showRoomDetails != null && showRoomDetails.getRoomDetails() != null && showRoomDetails.getRoomDetails().getRoomNumber() != null) {
            return DbBase.deleteWhere(RoomDetails.class, "roomNumber", new String[]{showRoomDetails.getRoomDetails().getRoomNumber()}) > 0;
        }
        return false;
    }

    public List<FuelRecord> getFuelRecordList() {
        List<FuelRecord> fuelRecordList = null;
        fuelRecordList = DbBase.getQueryAll(FuelRecord.class);
        if (fuelRecordList != null)
            fuelRecordList.sort((f1, f2) -> Integer.compare(f2.getOdometerNumber(), f1.getOdometerNumber()));
        return fuelRecordList;
    }

    public boolean delFuelRecord(int primary_id) {
        int i = DbBase.deleteWhere(FuelRecord.class, "primary_id", new Object[]{primary_id});
        return i!=0;
    }

    public <T extends Activity & IShowList> void delAllFuelRecord(T activity) {
        DbBase.deleteAll(FuelRecord.class);
    }
}
