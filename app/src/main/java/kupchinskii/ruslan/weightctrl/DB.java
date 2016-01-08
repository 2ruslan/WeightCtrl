package kupchinskii.ruslan.weightctrl;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DB  extends SQLiteOpenHelper{
    private static final int DATABASE_VERSION = 2;
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

    //region results viev
    public static final String V_RES = "V_RES";
    public static final String C_RES_IMT = "IMT";
    public static final String C_RES_HIPS_NORM = "HIPSNORM";
    private static final String SQL_CREATE_VRES = "CREATE VIEW " + V_RES + " AS SELECT " +
            DB.getColName(T_RES, C_RES_ID) + " AS " + C_RES_ID + ", " +
            C_RES_ONDATE + ", " +
            C_RES_WEIGHT + ", " +
            C_RES_GROWTH + ", " +
            C_RES_HIPS  + ", " +
            " ROUND( (" + C_RES_WEIGHT + " / 10.0) / ((" + C_RES_GROWTH + " / 100.0) *(" + C_RES_GROWTH + " / 100.0) ), 1) AS " + C_RES_IMT + ", " +
            C_RES_GROWTH + " / 2.0 as " + C_RES_HIPS_NORM +
            " FROM " + T_RES
            ;
    //endregion results view

    //region drop obj ver 1
    public static final String V_RES_FULL = "V_RES_FULL";
    public static final String T_IMT = "IMT";
    public static final String T_STG = "STG";
    //endregion region drop obj ver 1



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
        db.execSQL(SQL_CREATE_VRES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion == 1){
            db.execSQL("DROP VIEW " + V_RES );
            db.execSQL("DROP VIEW " + V_RES_FULL );
            db.execSQL("DROP TABLE " + T_IMT);
            db.execSQL("DROP TABLE " + T_STG);

            db.execSQL(SQL_CREATE_VRES);
        }
    }
    //endregion

}
