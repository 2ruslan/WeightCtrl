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
    public static void SaveResults(int pGrown, int pHips, int pWeight) {
        Cursor cursor = null;
        long id = -1;
        String onDade = DB.getDataStr(Calendar.getInstance().getTime());

        SQLiteDatabase db = DB.getDBRead();
        try {
            cursor = db.query(DB.T_RES, new String[]{DB.C_RES_ID}, DB.C_RES_ONDATE + " = ?", new String[]{onDade}, null, null, null);

            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                id = cursor.getLong(cursor.getColumnIndex(DB.C_RES_ID));
            }
        } finally {
            if (cursor != null)
                cursor.close();
            db.close();
        }

        int GrownLast = 0;
        int WeightLast = 0;
        int HipsLast = 0;
        try {
            db = DB.getDBRead();
            cursor = db.query(DB.T_RES, new String[]{DB.C_RES_GROWTH, DB.C_RES_WEIGHT, DB.C_RES_HIPS},
                    null, null, null, null, DB.C_RES_ID  + " DESC LIMIT 1" );

            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                GrownLast = cursor.getInt(cursor.getColumnIndex(DB.C_RES_GROWTH));
                WeightLast = cursor.getInt(cursor.getColumnIndex(DB.C_RES_WEIGHT));
                HipsLast = cursor.getInt(cursor.getColumnIndex(DB.C_RES_HIPS));
            }
        } finally {
            if (cursor != null)
                cursor.close();
            db.close();
        }

        ContentValues values = new ContentValues();
        values.put(DB.C_RES_GROWTH, pGrown > 0 ? pGrown : GrownLast );
        values.put(DB.C_RES_HIPS, pHips > 0 ? pHips : HipsLast);
        values.put(DB.C_RES_WEIGHT, pWeight > 0 ? pWeight :WeightLast);
        try {
            db = DB.getDBWrite();
            if (id == -1) {
                values.put(DB.C_RES_ONDATE, onDade);
                db.insert(DB.T_RES, null, values);
            }
            else
                db.update(DB.T_RES, values, DB.C_RES_ID + " = " + id, null);
        } finally {
            db.close();
        }
    }

    public static Cursor GetReusultsAll()
    {
        SQLiteDatabase db = DB.getDBRead();
        return  db.query(DB.T_RES, new String[]{DB.C_RES_ID, DB.C_RES_ONDATE, DB.C_RES_WEIGHT
                , DB.C_RES_GROWTH, DB.C_RES_HIPS}
                , null, null, null, null, null);
    }

    public static result GetReusultLast()
    {
        Cursor cursor = null;
        result res = new result();
        SQLiteDatabase db = DB.getDBRead();
        try{
            cursor =  db.query(DB.T_RES, new String[]{DB.C_RES_ID, DB.C_RES_ONDATE, DB.C_RES_WEIGHT
                         , DB.C_RES_GROWTH, DB.C_RES_HIPS
                        ," (SELECT " + DB.C_STG_BIRTHDAY + " FROM " + DB.T_STG  + " LIMIT 1) AS " + DB.C_STG_BIRTHDAY
                        ," (SELECT " + DB.C_STG_SEX + " FROM " + DB.T_STG  + " LIMIT 1) AS " + DB.C_STG_SEX
                          }, null, null, null, null, DB.C_RES_ONDATE + " DESC LIMIT 1");

            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                res.growth = cursor.getInt(cursor.getColumnIndex(DB.C_RES_GROWTH));
                res.hips = cursor.getInt(cursor.getColumnIndex(DB.C_RES_HIPS));
                res.weight = cursor.getInt(cursor.getColumnIndex(DB.C_RES_WEIGHT));
                res.onDate = DB.getData(cursor.getString(cursor.getColumnIndex(DB.C_RES_ONDATE)));
                res.sex = cursor.getInt(cursor.getColumnIndex(DB.C_STG_SEX));
                res.birthday = DB.getData(cursor.getString(cursor.getColumnIndex(DB.C_STG_BIRTHDAY)));
            }
        } finally {
            if (cursor != null)
                cursor.close();
            db.close();
        }
        return  res;
    }

    public static class result {
        Date onDate;
        int growth;
        int hips;
        int weight;
        int sex;
        Date birthday;
    }
    //endregion results

    //region setings
    public static void SaveSettings(int pSex /* 1 - male 0 - female */
                                   ,Date pBirthDay
                                  ) {
        Cursor cursor = null;
        long id = -1;

        SQLiteDatabase db = DB.getDBRead();
        // get current
        try {
            cursor =  db.query(DB.T_STG, new String[]{DB.C_STG_ID}, null, null, null, null, null);

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
		values.put(DB.C_STG_BIRTHDAY, DB.getDataStr(pBirthDay));
		
		try {
			db = DB.getDBWrite();
            if (id == -1) 
				db.insert(DB.T_STG, null, values);
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
            cursor = db.query(DB.T_STG, new String[]{DB.C_STG_ID, DB.C_STG_BIRTHDAY, DB.C_STG_SEX}
                    , null, null, null, null, null);

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
