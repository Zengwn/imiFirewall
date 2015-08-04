package com.imiFirewall;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class imiDataBase {
	
	private String imiDB_Name = "imiFirewall";
	private int imiDB_Version = 1;
	
	private String imiDB_TABLE_MSG = "MsgTable";
	private String imiDB_TABLE_CALL = "CallTable";
	private String imiDB_TABLE_BLACKLIST = "BlackTable";
	private String imiDB_TABLE_RUL = "RuleTable";
	
	private String imiDB_CREATE_MSG = "create table MsgTable(id integer primary key autoincrement, name text,"+
			"msg text, date long)";
	private String imiDB_CREATE_CALL = "create table CallTable(id integer primary key autoincrement, name text,"+
			"date long)";
	private String imiDB_CREATE_BLACK = "create table BlackTable(tel text primary key, name text)";
	private String imiDB_CREATE_RUL = "create table RuleTable(rul text primary key)";
	
	private DataHelper imiDBH;
	private SQLiteDatabase imiDB;
	private Context ctx;
	
	public imiDataBase(Context ctx) {
		this.ctx = ctx;
	}
	
	public void open() {
		imiDBH = new DataHelper(ctx);
		if (imiDBH == null) {
			imiDB = imiDBH.getWritableDatabase();
		} else {
			close();
			imiDB = imiDBH.getWritableDatabase();
		}
	}
	
	public void close() {
		imiDBH.close();
	}
	
	//查询全部黑名单
	public List<PersonEntity> queryBlack() {
		List<PersonEntity> res = new ArrayList<PersonEntity>();
		Cursor cursor = imiDB.query(imiDB_TABLE_BLACKLIST, null, null, null, null,null, null);
		if (cursor.getCount() <= 0)
			return res;
		else {
			while (cursor.moveToNext()) {
				PersonEntity personEntity = new PersonEntity();
				personEntity.setName(cursor.getString(1));
				personEntity.setTel(cursor.getString(0));
				res.add(personEntity);
			}
			return res;
		}

	}

	// 查询拦截短信记录
	public List<MsgEntity> queryMsg() {
		List<MsgEntity> res = new ArrayList<MsgEntity>();
		Cursor cursor = imiDB.query(imiDB_TABLE_MSG, null, null, null, null,
				null, null);
		if (cursor.getCount() <= 0)
			return res;
		else {
			while (cursor.moveToNext()) {
				MsgEntity msgEntity = new MsgEntity();
				msgEntity.setPhoneNum(cursor.getString(1));
				msgEntity.setContentName(cursor.getString(2));
				msgEntity.setDateNow(cursor.getLong(3));
				res.add(msgEntity);
			}
			return res;
		}

	}

	// 查询拦截电话记录
	public List<CallEntity> queryCall() {
		List<CallEntity> res = new ArrayList<CallEntity>();
		Cursor cursor = imiDB.query(imiDB_TABLE_CALL, null, null, null, null,
				null, null);
		if (cursor.getCount() <= 0)
			return res;
		else {
			while (cursor.moveToNext()) {
				CallEntity callEntity = new CallEntity();
				callEntity.setNumber(cursor.getString(1));
				callEntity.setDate(cursor.getLong(2));
				res.add(callEntity);
			}
			return res;
		}

	}
	
	//查询短信过滤规则
	public List<String> queryRul() {
		List<String> ruls = new ArrayList<String>();
		Cursor cursor = imiDB.query(imiDB_TABLE_RUL, null, null, null, null, null, null);
		if (cursor.getCount() <= 0)
			return ruls;
		else {
			while (cursor.moveToNext())
				ruls.add(cursor.getString(0));
			return ruls;
		}
	}

	// 删除表的内容
	public void dropAllBlack(String tableName) {
		imiDB.delete(tableName, null, null);
	}
	
	//查询黑名单
	public boolean query(String number) {
		Cursor cursor = imiDB.query(imiDB_TABLE_BLACKLIST, new String[]{"Tel"}, "Tel = " + number, null, null, null, null);
		if (cursor.getCount() > 0)
			return true;
		return false;
	}
	
	//存储数据
	public void insert(String tableName, Object object) {
		if (tableName.equals(imiDB_TABLE_BLACKLIST)) {
			//黑名单
			ContentValues cv = new ContentValues();
			cv.put("name", ((PersonEntity)object).getName());
			cv.put("tel", ((PersonEntity)object).getTel());
			imiDB.insert(imiDB_TABLE_BLACKLIST, null, cv);
			
		} else if (tableName.equals(imiDB_TABLE_CALL)) {
			//电话
			ContentValues cv = new ContentValues();
			cv.put("name", ((CallEntity)object).getNumber());
			cv.put("date", ((CallEntity)object).getDate());
			imiDB.insert(imiDB_TABLE_CALL, null, cv);
			
		} else if (tableName.equals(imiDB_TABLE_MSG)){
			//短信
			ContentValues cv = new ContentValues();
			cv.put("name", ((MsgEntity)object).getPhoneNum());
			cv.put("msg", ((MsgEntity)object).getContentName());
			cv.put("date", ((MsgEntity)object).getDateNow());
			imiDB.insert(imiDB_TABLE_MSG, null, cv);
			
		} else if (tableName.equals(imiDB_TABLE_RUL)) {
			//规则
			ContentValues cv = new ContentValues();
			cv.put("rul", (String)object);
			imiDB.insert(imiDB_TABLE_RUL, null, cv);
			
		}
	}
	
	class DataHelper extends SQLiteOpenHelper {

		public DataHelper(Context context) {
			super(context, imiDB_Name, null, imiDB_Version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(imiDB_CREATE_BLACK);
			db.execSQL(imiDB_CREATE_CALL);
			db.execSQL(imiDB_CREATE_MSG);
			db.execSQL(imiDB_CREATE_RUL);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			
		}
		
	}

}
