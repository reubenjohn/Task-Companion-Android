package com.reubenjohn.studytimer.data;

import com.reubenjohn.studytimer.R;
import com.reubenjohn.studytimer.timming.Time;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
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
			Log.d("StudyTimerDB", "execSQL("
					+ LapDBManager.commands.CREATE_TABLE + ")");
			db.execSQL(LapDBManager.commands.CREATE_TABLE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.d("StudyTimerDB", "execSQL("
					+ LapDBManager.commands.DESTROY_TABLE + ")");
			reset();
		}

		protected void destroyTable(SQLiteDatabase db) {
			db.execSQL(LapDBManager.commands.DESTROY_TABLE);
			onCreate(db);
		}

		public void reset() {
			SQLiteDatabase db = getWritableDatabase();
			Log.d("StudyTimerDB", "execSQL("
					+ LapDBManager.commands.CREATE_TABLE + ")");
			destroyTable(db);
		}

	}

	public static class LapDBManager {
		public static final String TABLE_NAME = "laps";

		public static final class keys {
			public static final String ROWID = "_id";
			public static final String DURATION = "duration";
			public static final String ELAPSE = "elapse_duration";
		}

		public static final class columns {
			public static final String[] columns = new String[] { keys.ROWID,
					keys.DURATION };
			private static final String[] listViewColumns = new String[] {
					LapDBManager.keys.ROWID, LapDBManager.keys.DURATION,
					LapDBManager.keys.ELAPSE };
		}

		public static final int[] to = new int[] { R.id.tv_lap_number,
				R.id.tv_lap_duration };

		public static final class commands {
			private static final String CREATE_TABLE = "CREATE TABLE if not exists "
					+ TABLE_NAME
					+ "("
					+ keys.ROWID
					+ " integer PRIMARY KEY autoincrement,"
					+ keys.DURATION
					+ " TEXT NOT NULL," + keys.ELAPSE + " integer" + ");";
			private static final String DESTROY_TABLE = "DROP TABLE IF EXISTS "
					+ TABLE_NAME;
			private static final String addToEachPrefix = "update "
					+ TABLE_NAME + " set " + keys.ELAPSE + "=" + keys.ELAPSE
					+ "+";
			// TODO put weird regenerateDurationStrings command here:
			private static final String regenerateDurationStrings = "update "
					+ TABLE_NAME + " set " + keys.DURATION + "=" + keys.ELAPSE
					+ "+";
		}

		private static final String countQuery = "SELECT  * FROM " + TABLE_NAME;

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
		val.put(LapDBManager.keys.DURATION, duration);
		val.put(LapDBManager.keys.ELAPSE, elapse_duration);

		Log.d("StudyTimerDB", "insert(" + LapDBManager.TABLE_NAME + ",null, "
				+ val + ")");
		return DB.insert(LapDBManager.TABLE_NAME, null, val);
	}

	public int getAverage() {
		Log.d("StudyTimerDB", "rawQuery(\"SELECT CAST(avg("
				+ LapDBManager.keys.ELAPSE + ") AS INTEGER) AS "
				+ LapDBManager.keys.ELAPSE + " from " + LapDBManager.TABLE_NAME
				+ ", null)\"");
		Cursor cursor = DB.rawQuery(
				"SELECT CAST(avg(" + LapDBManager.keys.ELAPSE
						+ ") AS INTEGER) AS " + LapDBManager.keys.ELAPSE
						+ " from " + LapDBManager.TABLE_NAME, null);
		cursor.moveToFirst();
		return (int) cursor.getLong(0);
	}

	public String getFormattedAverage() {
		int average = getAverage();
		return Time.getFormattedTime(average);
	}

	public Cursor fetchAllLaps() {
		Log.d("StudyTimerDB",
				"query("
						+ LapDBManager.TABLE_NAME
						+ ", "
						+ getFormattedStringArrayElements(LapDBManager.columns.listViewColumns)
						+ " , null, null, null, null, "
						+ LapDBManager.keys.ROWID + " DESC)");
		Cursor cursor = DB.query(LapDBManager.TABLE_NAME,
				LapDBManager.columns.listViewColumns, null, null, null, null,
				LapDBManager.keys.ROWID + " DESC");
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

	public void reset() {
		Log.d("StudyTimer", "Database reset");
		helper.reset();
	}

	public void distributeToLaps(long elapse) {
		long induvidualContribution = elapse / getAverage();
		addToEachLap(induvidualContribution);
	}

	public void addToEachLap(long induvidualContribution) {
		if (DB != null) {
			try {
				Log.d("StudyTimerDB", LapDBManager.commands.addToEachPrefix
						+ induvidualContribution);
				DB.execSQL(LapDBManager.commands.addToEachPrefix
						+ induvidualContribution);
				regenerateLapStrings();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private void regenerateLapStrings() {
		// TODO execute weird command (maybe use cursors)
	}
}
