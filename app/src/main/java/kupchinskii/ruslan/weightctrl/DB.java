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
    private static final String SQL_CREATE_RES = "CREATE TABLE " + T_RES + " ("
            + C_RES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + C_RES_ONDATE + " DATE, "
            + C_RES_WEIGHT + " INTEGER, "
            + C_RES_GROWTH + " INTEGER, "
            + C_RES_HIPS + " INTEGER "
            + ");";
    //endregion results table

    //region imt values table
    public static final String T_IMT = "IMT";
    public static final String C_IMT_ID = "_id";
    public static final String C_IMT_AGE_LOW = "AGE_LOW";
    public static final String C_IMT_AGE_UPP = "AGE_UPP";
    public static final String C_IMT_IMT_LOW = "IMT_LOW";
    public static final String C_IMT_IMT_UPP = "IMT_UPP";
    public static final String C_IMT_IMT_DEC = "DECODE";
    private static final String SQL_CREATE_IMT = "CREATE TABLE " + T_IMT + " ("
            + C_IMT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + C_IMT_AGE_LOW + " INTEGER, "
            + C_IMT_AGE_UPP + " INTEGER, "
            + C_IMT_IMT_LOW + " INTEGER, "
            + C_IMT_IMT_UPP + " NUMBER, "
            + C_IMT_IMT_DEC + " INTEGER "
            + ");";
    private static final String SQL_DATA_IMT =
            " INSERT INTO " + T_IMT + "(" +
                    C_IMT_AGE_LOW + ", " +
                    C_IMT_AGE_UPP + ", " +
                    C_IMT_IMT_LOW + ", " +
                    C_IMT_IMT_UPP + ", " +
                    C_IMT_IMT_DEC + ") " +
            "       SELECT 19, 200, 0, 19, 0 " + // низкая масса тела для всех возрастов
                    // нормальная маасса тела
            " UNION SELECT 0,  18, 19, 24, 1" + // до 18 лет вес нормальным считается у всех
            " UNION SELECT 19, 24, 19, 24, 1" +
            " UNION SELECT 25, 34, 19, 25, 1" +
            " UNION SELECT 35, 44, 19, 26, 1" +
            " UNION SELECT 45, 54, 19, 27, 1" +
            " UNION SELECT 55, 64, 19, 28, 1" +
            " UNION SELECT 65, 200, 19, 29, 1" +
                    // Избыточная масса тела
            " UNION SELECT 19, 24, 25, 30, 2" +
            " UNION SELECT 25, 34, 26, 31, 2" +
            " UNION SELECT 35, 44, 27, 32, 2" +
            " UNION SELECT 45, 54, 28, 33, 2" +
            " UNION SELECT 55, 64, 29, 33, 2" +
            " UNION SELECT 65, 200, 30, 34, 2" +
                    // Ожирение первой степени
            " UNION SELECT 19, 24, 30, 35, 3" +
            " UNION SELECT 25, 34, 31, 36, 3" +
            " UNION SELECT 35, 44, 32, 37, 3" +
            " UNION SELECT 45, 54, 33, 38, 3" +
            " UNION SELECT 55, 64, 34, 39, 3" +
            " UNION SELECT 65, 200, 35, 40, 3" +
                    // Ожирение второй степени
             " UNION SELECT 19, 24, 35, 40, 4" +
             " UNION SELECT 25, 34, 36, 41, 4" +
             " UNION SELECT 35, 44, 37, 42, 4" +
             " UNION SELECT 45, 54, 38, 43, 4" +
             " UNION SELECT 55, 64, 39, 44, 4" +
             " UNION SELECT 65, 200, 40, 45, 4" +
                    //Ожирение третьей степени (морбидное)
             " UNION SELECT 19, 24, 40, 99, 5" +
             " UNION SELECT 25, 34, 41, 99, 5" +
             " UNION SELECT 35, 44, 42, 99, 5" +
             " UNION SELECT 45, 54, 43, 99, 5" +
             " UNION SELECT 55, 64, 44, 99, 5" +
             " UNION SELECT 65, 200, 45, 99, 5" +
            "";

    //endregion imt values table

    //region settings table
    public static final String T_STG = "STG";
    public static final String C_STG_ID = "_id";
    public static final String C_STG_BIRTHDAY = "BIRTHDAY";
    private static final String SQL_CREATE_STG = "CREATE TABLE " + T_STG + " ("
            + C_STG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + C_STG_BIRTHDAY + " DATE "
            + ");";
    //endregion settings table

    //region results viev
    public static final String V_RES = "V_RES";
    public static final String C_RES_IMT = "IMT";
    public static final String C_RES_HIPS_NORM = "IMT";
    public static final String C_RES_AGE = "AGE";
    private static final String SQL_CREATE_VRES = "CREATE VIEW " + V_RES + " AS SELECT " +
            DB.getColName(T_RES, C_RES_ID) + " AS " + C_RES_ID + ", " +
            C_RES_ONDATE + ", " +
            C_RES_WEIGHT + ", " +
            C_RES_GROWTH + ", " +
            C_RES_HIPS  + ", " +
            " ROUND( (" + C_RES_WEIGHT + " / 100) / ((" + C_RES_GROWTH + " / 100.0) *(" + C_RES_GROWTH + " / 100.0) ), 1) AS " + C_RES_IMT + ", " +
            C_RES_GROWTH + " / 2.0 as " + C_RES_HIPS_NORM + ", " +
            " CAST (" + C_RES_ONDATE + " - " + C_STG_BIRTHDAY + " +( CASE WHEN strftime( '%m-%d', " + C_RES_ONDATE + ") < strftime( '%m-%d', " + C_STG_BIRTHDAY + " ) THEN -1 ELSE 0 END ) AS INT)  AS " + C_RES_AGE +
            " FROM " + T_RES + ", " + T_STG
            ;
    //endregion results view

    //region results full viev
    public static final String V_RES_FULL = "V_RES_FULL";
    public static final String C_RES_IMT_0 = "IMT_0";
    public static final String C_RES_IMT_1 = "IMT_1";
    public static final String C_RES_IMT_2 = "IMT_2";
    public static final String C_RES_IMT_3 = "IMT_3";
    public static final String C_RES_IMT_4 = "IMT_4";
    public static final String C_RES_IMT_5 = "IMT_5";
    private static final String SQL_CREATE_VRES_FULL = "CREATE VIEW " + V_RES_FULL + " AS SELECT " +
            C_RES_ID + ", " +
            C_RES_ONDATE + ", " +
            C_RES_WEIGHT + ", " +
            C_RES_GROWTH + ", " +
            C_RES_HIPS  + ", " +
            C_RES_IMT + ", " +
            C_RES_HIPS_NORM + ", " +
            C_RES_AGE + ", " +
            " ( SELECT " + C_IMT_IMT_UPP + " FROM " + T_IMT + " WHERE " + C_IMT_IMT_DEC + " = 0 AND " + C_RES_AGE  + " BETWEEN " + C_IMT_AGE_LOW + " AND "+ C_IMT_AGE_UPP  + " ) AS " + C_RES_IMT_0 + ", " +
            " ( SELECT " + C_IMT_IMT_UPP + " FROM " + T_IMT + " WHERE " + C_IMT_IMT_DEC + " = 1 AND " + C_RES_AGE  + " BETWEEN " + C_IMT_AGE_LOW + " AND "+ C_IMT_AGE_UPP  + " ) AS " + C_RES_IMT_1 +", " +
            " ( SELECT " + C_IMT_IMT_UPP + " FROM " + T_IMT + " WHERE " + C_IMT_IMT_DEC + " = 2 AND " + C_RES_AGE  + " BETWEEN " + C_IMT_AGE_LOW + " AND "+ C_IMT_AGE_UPP  + " ) AS " + C_RES_IMT_2 +", " +
            " ( SELECT " + C_IMT_IMT_UPP + " FROM " + T_IMT + " WHERE " + C_IMT_IMT_DEC + " = 3 AND " + C_RES_AGE  + " BETWEEN " + C_IMT_AGE_LOW + " AND "+ C_IMT_AGE_UPP  + " ) AS " + C_RES_IMT_3 +", " +
            " ( SELECT " + C_IMT_IMT_UPP + " FROM " + T_IMT + " WHERE " + C_IMT_IMT_DEC + " = 4 AND " + C_RES_AGE  + " BETWEEN " + C_IMT_AGE_LOW + " AND "+ C_IMT_AGE_UPP  + " ) AS " + C_RES_IMT_4 +", " +
            " ( SELECT " + C_IMT_IMT_UPP + " FROM " + T_IMT + " WHERE " + C_IMT_IMT_DEC + " = 5 AND " + C_RES_AGE  + " BETWEEN " + C_IMT_AGE_LOW + " AND "+ C_IMT_AGE_UPP  + " ) AS " + C_RES_IMT_5 +
            " FROM " + V_RES
            ;
    //endregion results full view

    private static final int DATABASE_VERSION = 1;

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
        return dt != null ? sdf.format(dt) : null;
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
        db.execSQL(SQL_CREATE_IMT);
        db.execSQL(SQL_DATA_IMT);
        db.execSQL(SQL_CREATE_VRES);
        db.execSQL(SQL_CREATE_VRES_FULL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //endregion

}
