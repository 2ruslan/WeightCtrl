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
        SaveResults(pGrown, pHips,pWeight,null);
    }
    public static void SaveResults(Integer pGrown, Integer pHips, Integer pWeight, Date ondt) {
        Cursor cursor = null;
        long id = -1;
        String onDade = DB.getDataStr(  ondt == null ?  Calendar.getInstance().getTime() : ondt) ;

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
                    DB.C_RES_ONDATE + " <= ?", new String[]{onDade}, null, null, DB.C_RES_ONDATE  + " DESC LIMIT 1" );

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
        return  db.query(DB.T_RES, new String[]{DB.C_RES_ID, DB.C_RES_ONDATE
                , DB.C_RES_WEIGHT + " / 10.0 AS " + DB.C_RES_WEIGHT
                , DB.C_RES_GROWTH, DB.C_RES_HIPS,
                }
                , null, null, null, null, DB.C_RES_ONDATE );
    }

    public static Cursor GetReusults(int year, int month)
    {
        Calendar cStart = Calendar.getInstance();
        cStart.set(year, month, 1);

        Calendar cEnd = Calendar.getInstance();
        cEnd.set(year, month + 1, -1, 23, 59, 59);



        SQLiteDatabase db = DB.getDBRead();
        return  db.query(DB.T_RES, new String[]{DB.C_RES_ID, DB.C_RES_ONDATE
                , DB.C_RES_WEIGHT + " / 10.0 AS " + DB.C_RES_WEIGHT
                , DB.C_RES_GROWTH, DB.C_RES_HIPS }
                , "strftime('%Y', "  + DB.C_RES_ONDATE + ") = ? AND strftime('%m'," + DB.C_RES_ONDATE + ") = ?"
                , new String[]{String.valueOf(year), (((++ month) <= 9) ?  "0" : "") + String.valueOf(month)}, null, null, DB.C_RES_ONDATE);
    }

    public static result GetReusultLast()
    {
        String onDade = DB.getDataStr(Calendar.getInstance().getTime());
        Cursor cursor = null;
        Cursor cursorG = null;

        result res = new result();
        SQLiteDatabase db = DB.getDBRead();
        try{
            cursor = db.query(DB.V_RES, new String[]{DB.C_RES_ID, DB.C_RES_ONDATE, DB.C_RES_WEIGHT
                    , DB.C_RES_HIPS
                    , DB.C_RES_IMT
                    , DB.C_RES_HIPS_NORM
            }, DB.C_RES_ONDATE + " <= ?", new String[]{onDade}, null, null, DB.C_RES_ONDATE + " DESC LIMIT 1");

            cursorG = db.query(DB.V_RES, new String[]{"MAX(" + DB.C_RES_GROWTH + ") as MAX_" + DB.C_RES_GROWTH}
                    , DB.C_RES_ONDATE + " <= ?", new String[]{onDade}, null, null, null);

            int index;
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                cursorG.moveToFirst();

                if (!cursorG.isNull(index=cursorG.getColumnIndex("MAX_" + DB.C_RES_GROWTH)))
                    res.growth = cursorG.getInt(index);
                if (!cursor.isNull(index=cursor.getColumnIndex(DB.C_RES_HIPS)))
                    res.hips = cursor.getInt(index);
                if (!cursor.isNull(index=cursor.getColumnIndex(DB.C_RES_WEIGHT)))
                    res.weight = cursor.getInt(index);
                if (!cursor.isNull(index=cursor.getColumnIndex(DB.C_RES_ONDATE)))
                    res.onDate = DB.getData(cursor.getString(index));
                if (!cursor.isNull(index=cursor.getColumnIndex(DB.C_RES_IMT )))
                    res.imt = cursor.getDouble(index);
                if (!cursor.isNull(index=cursor.getColumnIndex(DB.C_RES_HIPS_NORM )))
                    res.hipsNorm = (int)cursor.getDouble(index);

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
            imt = null;
            hipsNorm = null;
        }
        Date onDate;
        Integer growth;
        Integer hips;
        Integer hipsNorm;
        Integer weight;
        Double imt;

    }
    //endregion results
}
