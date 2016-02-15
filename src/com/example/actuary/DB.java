package com.example.actuary;

import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author Administrator
 *
 */
public class DB
{
	private Context mContext = null;
	private DatabaseHelper dbHelper;
	private SQLiteDatabase db;
	private static final String DATABASE_NAME = "notes.db";
	private static final int DATABASE_VERSION = 7;
	private static final String TABLE_NAME = "notes";
	private static final String CREATE_TABLE = 
								"CREATE TABLE notes(" 
								+ "_id INTEGER PRIMARY KEY,"
								+ "name TEXT NOT NULL," 		
								+ "note TEXT NOT NULL," 
								+ "created TIMESTAMP," 
								+ "modified TIMESTAMP" + ");";
	
	/*note table column*/
	public static final String KEY_ROWID = "_id";
	public static final String KEY_NAME = "name";
	public static final String KEY_NOTE = "note";
	public static final String KEY_CREATED = "created";
	public static final String KEY_MODIFIED = "modified";
	
	public DB(Context context){
		this.mContext = context;
	}
	
	public DB open() throws SQLException{
		dbHelper = new DatabaseHelper(mContext);
		db = dbHelper.getWritableDatabase();
		return this;
	}
	
	public void close(){
		dbHelper.close();
	}
	
	public Cursor get(long rowId)
	{
		Cursor mCursor = db.query(true,TABLE_NAME,new String[]{
				KEY_ROWID,
				KEY_NOTE,
				KEY_CREATED,
				KEY_MODIFIED
		}, KEY_ROWID + "=" + rowId, null, null, null, null, null, null);
		if(mCursor != null){
			mCursor.moveToFirst();
		}
		return mCursor;
//		return db.query(true, TABLE_NAME, new String[]{KEY_ROWID,KEY_NOTE,KEY_CREATED,KEY_MODIFIED}, KEY_ROWID +" = ?",new String[]{String.valueOf(rowId)} , null, null, null, null, null);
	}
	
	public Cursor getAll(){
		return db.rawQuery("SELECT * FROM "+TABLE_NAME, null);
	}
	
	public long insert(String note){
		Date nowDate = new Date();
		ContentValues args = new ContentValues();
		args.put(KEY_NOTE, note);
		args.put(KEY_NAME, note);
		args.put(KEY_CREATED, nowDate.getTime());
		args.put(KEY_MODIFIED, nowDate.getTime());
		
		return db.insert(TABLE_NAME, null, args);
	}
	
	public boolean delete(long rowId){
		return db.delete(TABLE_NAME, KEY_ROWID +" = ?", new String[]{String.valueOf(rowId)}) > 0;
	}
	
	public boolean update(long rowId, String note, String name)
	{
		Date now = new Date();
		ContentValues args = new ContentValues();
		if(note!=null){
			args.put(KEY_NOTE, note);
		}
		if(name!=null){
			args.put(KEY_NAME, name);
		}
		args.put(KEY_MODIFIED, now.getTime());
		
		return db.update(TABLE_NAME, args, KEY_ROWID +" = ?", new String[]{String.valueOf(rowId)})>0;
	}

	private static class DatabaseHelper extends SQLiteOpenHelper
	{

		public DatabaseHelper(Context context)
		{
			super(context, TABLE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db)
		{
			db.execSQL(CREATE_TABLE);

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
		{
			db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME);
			onCreate(db);
		}
	}
}
