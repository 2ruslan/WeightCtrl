package kupchinskii.ruslan.weightctrl;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DB  extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "results.db";

    //region results table
    public static final String T_RES = "RES";
    public static final String C_RES_ID = "_id";
    public static final String C_RES_ONDATE = "ONDATE";
    public static final String C_RES_WEIGHT = "WEIGHT";
    public static final String C_RES_GROWTH = "GROWTH";
    public static final String C_RES_HIPS = "HIPS";
    //endregion results table

    //region settings table
    public static final String T_STG = "STG";
    public static final String C_STG_ID = "_id";
    public static final String C_STG_SEX = "SEX";
    public static final String C_STG_BIRTHDAY = "BIRTHDAY";
    //endregion settings table

    private static final int DATABASE_VERSION = 1;

    private static final String SQL_CREATE_RES = "CREATE TABLE " + T_RES + " ("
            + C_RES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + C_RES_ONDATE + " DATE, "
            + C_RES_WEIGHT + " INTEGER, "
            + C_RES_GROWTH + " INTEGER, "
            + C_RES_HIPS + " INTEGER "
            + ");";
    private static final String SQL_CREATE_STG = "CREATE TABLE " + T_STG + " ("
            + C_STG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + C_STG_SEX + " INTEGER, "
            + C_STG_BIRTHDAY + " DATE "
            + ");";

    //region utils
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    //endregion
    private static DB _db = null;

    //region main
    private DB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    static void initDB(Context context) {
        if (_db == null)
            _db = new DB(context);
    }

    public static SQLiteDatabase getDBRead() {
        return _db.getReadableDatabase();
    }

    public static SQLiteDatabase getDBWrite() {
        return _db.getWritableDatabase();
    }

    public static String getDataStr(Date dt) {
        return sdf.format(dt);
    }
    //endregion main

    public static Date getData(String dt) {
        try {
            return sdf.parse(dt);
        } catch (Exception ex) {
            return null;
        }
    }

    public static String getColName(String table, String column, String alias){
        return table + "." + column + (alias.equals("") ? "" : " as " + alias);
    }

    public static String getColName(String table, String column) {
        return getColName(table, column, "");
    }

    public static String getColNameNvl(String table, String column, String alias) {
        return "ifnull(" + getColName(table, column, "") + ", 0)" + (alias.equals("") ? "" : " as " + alias);
    }

    public static String getColNameNvl(String table, String column) {
        return getColNameNvl(table, column, "");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_RES);
        db.execSQL(SQL_CREATE_STG);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //endregion

}
