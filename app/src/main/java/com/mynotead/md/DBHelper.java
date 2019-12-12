package com.mynotead.md;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;

public class DBHelper extends SQLiteOpenHelper {
	/*
	*	
	*/
	
	private String create="create table Note(" +
	"id integer primary key autoincrement," +
	"time text," +
	"createtime text,"+
	"ledtime text,"+
	"title text,"+
	"content text,"+
//	"imgpath text,"+
	"endtime text"+")";
	DBHelper(Context context, String name, SQLiteDatabase.CursorFactory cf, int version) {
		super(context, name, cf, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(create);
		/*
		*{创建数据表}
		*/
	}

	
	@Override
	public void onUpgrade(SQLiteDatabase sql, int oldVersion, int newVersion) {
		sql.execSQL(create);
	}

}
