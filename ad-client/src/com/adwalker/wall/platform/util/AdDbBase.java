package com.adwalker.wall.platform.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AdDbBase extends SQLiteOpenHelper {
	private final static String DB_NAME = "GuinstalledApp.db";
	private final static int DB_VERSION = 1;
	public static SQLiteDatabase mdb;
	public AdDbBase(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		StringBuffer sql = new StringBuffer();
		sql.append("create table if not exists GuinstalledApp(");
		sql.append("appId integer primary key asc autoincrement,");
		sql.append("appName VARCHAR(200),");
		sql.append("packageName VARCHAR(200),");
		sql.append("createTime timestamp default (datetime('now', 'localtime')))");
		db.execSQL(sql.toString());
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		
	}

	// 查询
	public static boolean needSave(AdDbBase dbHelper, String appName,
			String packageName) {
		boolean isNeedSave = false;
		String selection = "appName = ? and packageName = ?";
		String[] selectionArgs = new String[] { appName, packageName };
		Cursor cursor = mdb.query("GuinstalledApp", null, selection,
				selectionArgs, null, null, null);
		int count = cursor.getCount();
		if (count == 0) {
			isNeedSave = true;
		}
		cursor.close();
//		db.close();
		return isNeedSave;
	}
	// 添加
	public static void Insert(AdDbBase dbHelper, String appName,
			String packageName) {
		ContentValues cv = new ContentValues();
		cv.put("appName", appName);
		cv.put("packageName", packageName);
		mdb.insert("GuinstalledApp", null, cv);
	}


}
