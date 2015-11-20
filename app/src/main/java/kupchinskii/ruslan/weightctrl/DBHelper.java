package kupchinskii.ruslan.weightctrl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.ContextCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Calendar;
import java.util.Date;

public class DBHelper {

    //region results
    private static final String sql_sel_res_id =
            " SELECT " + DB.C_RES_ID +
            " FROM " + DB.T_RES +
            " WHERE " + DB.C_RES_ONDATE + " = ?"
            ;
    private static final String sql_upd_res =
            " UPDATE " + DB.T_RES + " SET " +
            "   " + DB.C_RES_GROWTH + " = ? " +
            "  ," + DB.C_RES_HIPS + " = ? " +
            "  ," + DB.C_RES_WEIGHT + " = ? " +
            " WHERE " + DB.C_RES_ID + " = ?"
            ;
    private static final String sql_add_res =
            " INSERT INTO " + DB.T_RES + " ( " +
            "  ," + DB.C_RES_ONDATE +
            "  ," + DB.C_RES_GROWTH +
            "  ," + DB.C_RES_HIPS +
            "  ," + DB.C_RES_WEIGHT  +
            " ) VALUES (?, ?, ?, ?)"
            ;
    private static final String sql_sel_res_all =
            " SELECT " + DB.C_RES_ID +
            "      , " + DB.C_RES_ONDATE +
            "      , " + DB.C_RES_WEIGHT +
            "      , " + DB.C_RES_HIPS +
            "      , " + DB.C_RES_GROWTH +
            " FROM " + DB.T_RES +
            " ORDER BY " + DB.C_RES_ONDATE + " DESC "
            ;
    public static void SaveResults(int pGrown, int pHips, int pWeight) {
        Cursor cursor = null;
        long id = -1;
        String onDade = DB.getDataStr(Calendar.getInstance().getTime());

        SQLiteDatabase db = DB.getDBRead();
        // get current
        try {
            cursor = db.rawQuery(sql_sel_res_id,
                    new String[]{onDade});

            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                id = cursor.getLong(cursor.getColumnIndex(DB.C_RES_ID));
            }
        } finally {
            if (cursor != null)
                cursor.close();
            db.close();
        }
        // add current
        if (id == -1) {
            try {
                db = DB.getDBWrite();
                db.execSQL(sql_add_res, new String[]{
                        onDade,
                        String.valueOf(pWeight),
                        String.valueOf(pHips),
                        String.valueOf(pGrown)
                        });
                ContentValues values = new ContentValues();
                values.put(DB.C_RES_ONDATE, onDade);

                id = db.insert(DB.T_RES, null, values );
            } finally {
                db.close();
            }
        }
        else {
            // upd current
            try {
                db = DB.getDBWrite();
                db.execSQL(sql_upd_res, new String[]{
                                String.valueOf(pGrown),
                                String.valueOf(pHips),
                                String.valueOf(pWeight)}
                );
            } finally {
                db.close();
            }
        }
    }

    public static Cursor GetReusultsAll()
    {
        SQLiteDatabase db = DB.getDBRead();
        return db.rawQuery(sql_sel_res_all, new String[]{});
    }
    //endregion results

    //region setings
    private static final String sql_sel_stg_id =
            " SELECT " + DB.C_STG_ID +
                    " FROM " + DB.T_STG +
                    " LIMIT 1"
            ;
    private static final String sql_add_stg =
            " INSERT INTO " + DB.T_STG + " ( " +
            "   " + DB.C_STG_SEX +
            "  ," + DB.C_STG_BIRTHDAY +
            " ) VALUES (?, ?)"
            ;
    private static final String sql_upd_stg =
            " UPDATE " + DB.T_STG + " SET " +
            "   " + DB.C_STG_SEX + " = ? " +
            "  ," + DB.C_STG_BIRTHDAY + " = ? "
            ;
    public static void SaveSettings(int pSex /* 0 - male 1 - female */
                                   ,Date pBirthDay
                                  ) {
        Cursor cursor = null;
        long id = -1;

        SQLiteDatabase db = DB.getDBRead();
        // get current
        try {
            cursor = db.rawQuery(sql_sel_stg_id,
                    new String[]{});

            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                id = cursor.getLong(cursor.getColumnIndex(DB.C_STG_ID));
            }
        } finally {
            if (cursor != null)
                cursor.close();
            db.close();
        }

		ContentValues values = new ContentValues();
		values.put(DB.C_STG_SEX, pSex);
		values.put(DB.C_STG_BIRTHDAY, pBirthDay);
		
        // add
		try {
			db = DB.getDBWrite();
            if (id == -1) 
				db.insert(DB.T_STG, null, values)
            else 
                db.update(DB.T_STG, values, null, new String[]{});
			} finally {
                db.close();
            }
    }

    public static settings GetSettings() {
        Cursor cursor = null;
        long id = -1;
        settings res = new settings();
        SQLiteDatabase db = DB.getDBRead();
        // get current
        try {
            cursor = db.rawQuery(sql_sel_stg_id,
                    new String[]{});

            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                res.sex = cursor.getInt(cursor.getColumnIndex(DB.C_STG_ID));
                res.birthday = DB.getData(cursor.getString(cursor.getColumnIndex(DB.C_STG_BIRTHDAY)));
            }
        } finally {
            if (cursor != null)
                cursor.close();
            db.close();
        }
        return res;
    }

    public static class settings {
        int sex;
        Date birthday;
    }
    //endregion setings

}
