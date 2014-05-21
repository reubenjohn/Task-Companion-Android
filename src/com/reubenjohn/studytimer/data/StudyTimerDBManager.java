package com.reubenjohn.studytimer.data;

import com.reubenjohn.studytimer.R;
import com.reubenjohn.studytimer.timming.Time;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class StudyTimerDBManager {

	private final Context context;

	private DBHelper helper;
	private SQLiteDatabase DB;

	public StudyTimerDBManager(Context context) {
		this.context = context;
	}

	private static class properties {
		public final static String DATABASE_NAME = "StudyTimer.db";
		public final static int DATABASE_VERSION = 2;
	}

	private static class DBHelper extends SQLiteOpenHelper {

		public DBHelper(Context context) {
			super(context, properties.DATABASE_NAME, null,
					properties.DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			Log.d("StudyTimerDB", "execSQL(" + LapDBManager.CREATE_TABLE + ")");
			db.execSQL(LapDBManager.CREATE_TABLE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.d("StudyTimerDB", "execSQL(" + LapDBManager.DESTROY_TABLE + ")");
			// TODO Auto-generated method stub
			db.execSQL(LapDBManager.DESTROY_TABLE);
			onCreate(db);
		}
	}

	public static class LapDBManager {
		public static final String TABLE_NAME = "laps";

		public static final String KEY_ROWID = "_id";
		public static final String KEY_DURATION = "duration";
		public static final String KEY_ELAPSE = "elapse_duration";

		public static final String[] columns = new String[] { KEY_ROWID,
				KEY_DURATION };
		public static final int[] to = new int[] { R.id.tv_lap_number,
				R.id.tv_lap_duration };

		private static final String CREATE_TABLE = "CREATE TABLE if not exists "
				+ TABLE_NAME
				+ "("
				+ KEY_ROWID
				+ " integer PRIMARY KEY autoincrement,"
				+ KEY_DURATION
				+ " TEXT NOT NULL," + KEY_ELAPSE + " integer" + ");";
		private static final String DESTROY_TABLE = "DROP TABLE IF EXISTS "
				+ TABLE_NAME;

		private static final String countQuery = "SELECT  * FROM " + TABLE_NAME;

		private static final String[] listViewColumns = new String[] {
				LapDBManager.KEY_ROWID, LapDBManager.KEY_DURATION,
				LapDBManager.KEY_ELAPSE };
	}

	public StudyTimerDBManager open() {
		helper = new DBHelper(context);
		DB = helper.getWritableDatabase();
		return StudyTimerDBManager.this;
	}

	public void close() {
		if (helper != null) {
			helper.close();
		}
	}

	public long addLap(String duration, int elapse_duration) {
		ContentValues val = new ContentValues();
		val.put(LapDBManager.KEY_DURATION, duration);
		val.put(LapDBManager.KEY_ELAPSE, elapse_duration);

		Log.d("StudyTimerDB", "insert(" + LapDBManager.TABLE_NAME + ",null, "
				+ val + ")");
		return DB.insert(LapDBManager.TABLE_NAME, null, val);
	}

	public int getAverage() {
		Log.d("StudyTimerDB", "rawQuery(\"SELECT CAST(avg("
				+ LapDBManager.KEY_ELAPSE + ") AS INTEGER) AS "
				+ LapDBManager.KEY_ELAPSE + " from " + LapDBManager.TABLE_NAME
				+ ", null)\"");
		Cursor cursor = DB.rawQuery("SELECT CAST(avg("
				+ LapDBManager.KEY_ELAPSE + ") AS INTEGER) AS "
				+ LapDBManager.KEY_ELAPSE + " from " + LapDBManager.TABLE_NAME,
				null);
		cursor.moveToFirst();
		return (int) cursor.getLong(0);
	}

	public String getFormattedAverage(){
		int average=getAverage();
		return Time.getFormattedTime(average);
	}

	public Cursor fetchAllLaps() {
		Log.d("StudyTimerDB", "query(" + LapDBManager.TABLE_NAME + ", "
				+ getFormattedStringArrayElements(LapDBManager.listViewColumns)
				+ " , null, null, null, null, " + LapDBManager.KEY_ROWID
				+ " DESC)");
		Cursor cursor = DB.query(LapDBManager.TABLE_NAME,
				LapDBManager.listViewColumns, null, null, null, null,
				LapDBManager.KEY_ROWID + " DESC");
		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;
	}

	public int getLapCount() {
		if (DB != null) {
			Log.d("StudyTimerDB", "rawQuery(\"" + LapDBManager.countQuery
					+ "\", null)");
			Cursor cursor = DB.rawQuery(LapDBManager.countQuery, null);
			int cnt = cursor.getCount();
			cursor.close();
			return cnt;
		} else
			return -1;
	}

	protected String getFormattedStringArrayElements(String[] array) {
		String result = "{ ";
		for (String s : array) {
			result += s + ", ";
		}
		result = result.substring(0, (result.length() - 2));
		return result += " }";
	}
}
