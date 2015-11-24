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
    public static void SaveResults(Integer pGrown, Integer pHips, Integer pWeight) {
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

        Integer GrownLast = null;
        Integer WeightLast = null;
        Integer HipsLast = null;
        int index;
        try {
            db = DB.getDBRead();
            cursor = db.query(DB.T_RES, new String[]{DB.C_RES_GROWTH, DB.C_RES_WEIGHT, DB.C_RES_HIPS},
                    null, null, null, null, DB.C_RES_ID  + " DESC LIMIT 1" );

            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                if (cursor.isNull(index=cursor.getColumnIndex(DB.C_RES_GROWTH)))
                    GrownLast = cursor.getInt(index);
                if (cursor.isNull(index=cursor.getColumnIndex(DB.C_RES_WEIGHT)))
                    WeightLast = cursor.getInt(index);
                if (cursor.isNull(index=cursor.getColumnIndex(DB.C_RES_HIPS)))
                    HipsLast = cursor.getInt(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
            db.close();
        }

        ContentValues values = new ContentValues();
        values.put(DB.C_RES_GROWTH, pGrown != null ? pGrown : GrownLast );
        values.put(DB.C_RES_HIPS, pHips != null ? pHips : HipsLast);
        values.put(DB.C_RES_WEIGHT, pWeight != null ? pWeight :WeightLast);

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
                          }, null, null, null, null, DB.C_RES_ONDATE + " DESC LIMIT 1");

            int index;
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                if (cursor.isNull(index=cursor.getColumnIndex(DB.C_RES_GROWTH)))
                    res.growth = cursor.getInt(index);
                if (cursor.isNull(index=cursor.getColumnIndex(DB.C_RES_HIPS)))
                    res.hips = cursor.getInt(index);
                if (cursor.isNull(index=cursor.getColumnIndex(DB.C_RES_WEIGHT)))
                    res.weight = cursor.getInt(index);
                if (cursor.isNull(index=cursor.getColumnIndex(DB.C_RES_ONDATE)))
                    res.onDate = DB.getData(cursor.getString(index));
                if (cursor.isNull(index=cursor.getColumnIndex(DB.C_STG_BIRTHDAY)))
                    res.birthday = DB.getData(cursor.getString(index));
            }
        } finally {
            if (cursor != null)
                cursor.close();
            db.close();
        }
        return  res;
    }

    public static class result {
        public result()
        {
            onDate = null;
            growth = null;
            hips = null;
            weight = null;
            birthday = null;
        }
        Date onDate;
        Integer growth;
        Integer hips;
        Integer weight;
        Date birthday;
    }
    //endregion results

    //region setings
    public static void SaveSettings(Date pBirthDay) {
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

    //endregion setings

}
