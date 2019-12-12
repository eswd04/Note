package com.mynotead.md;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import java.util.List;
import java.util.ArrayList;
import android.database.Cursor;
import android.content.ContentValues;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.util.Log;
import android.os.Environment;
import java.io.File;
import java.io.IOException;

public class NoteDB{
	/*
	*	数据库的主要功能操作都在这了
	*/
	private static final int  version=1;
	private static final String name="notepad.db";
	private SQLiteDatabase sql;
	public static final String table="Note";
	

	
	public NoteDB(Context context){
		String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/.Note/";
		File pathFile = new File(path);
        File file = new File(path,name);
        try{
            if(!pathFile.exists()){
                pathFile.mkdirs();
            }
            if(!file.exists()){
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
		sql=SQLiteDatabase.openOrCreateDatabase(file,null);
		DBHelper db=new DBHelper(context,file.getAbsolutePath(),null,version);
		sql=db.getWritableDatabase();
	}
	

	public void deleteAll() {
		sql.delete(table,null,null);
		
	}
	public List<Note> getList(){
		List<Note> list; list=new ArrayList<Note>();
		Note note;
		Cursor cursor = sql.query(table,null,null,null,null,null,"time desc,ledtime desc,id desc");
		while(cursor.moveToNext()){
			note=new Note();
			note.setId(cursor.getInt(cursor.getColumnIndex("id")));
			note.setTime(cursor.getInt(cursor.getColumnIndex("time")));
			note.setContent(cursor.getString(cursor.getColumnIndex("content")));
			note.setTitle(cursor.getString(cursor.getColumnIndex("title")));
			//note.setImgPath(cursor.getString(cursor.getColumnIndex("imgpath")));
			note.setEndTime(cursor.getLong(cursor.getColumnIndex("endtime")));
			note.setCreateTime(cursor.getString(cursor.getColumnIndex("createtime")));
			note.setLastEdtTime(cursor.getLong(cursor.getColumnIndex("ledtime")));
			list.add(note);
		}
		return list;
	}
	public void endtime(String endtime,int id,int time){
		ContentValues values = new ContentValues();
		values.put("endtime",endtime);
		values.put("time",time);
		sql.update(table,values,"id="+id,null);
	}
	
	public void dataAdd(Note note){
		if(note!=null){
			ContentValues values = new ContentValues();
			values.put("createtime",note.getCreateTime());
			values.put("title",note.getTitle());
			values.put("content",note.getContent());
		//	values.put("imgpath",note.getImgPath());
			values.put("ledtime",note.getLastEdtTime());
			values.put("endtime","0");
			values.put("time","0");
			sql.insert("Note",null,values);
			
		}
	}
	
	public void restroe(Note note){
		if(note!=null){
			ContentValues values = new ContentValues();
			values.put("id",note.getId());
			values.put("createtime",note.getCreateTime());
			values.put("title",note.getTitle());
			//values.put("imgpath",note.getImgPath());
			values.put("content",note.getContent());
			values.put("ledtime",note.getLastEdtTime());
			values.put("endtime","0");
			values.put("time","0");
			sql.insert("Note",null,values);
		}
	}
	
	
	public void updateData(Note note){
		if(note!=null){
			ContentValues values = new ContentValues();
			values.put("title",note.getTitle());
			values.put("content",note.getContent());
			//values.put("imgpath",note.getImgPath());
			values.put("ledtime",note.getLastEdtTime());
			values.put("time",note.getTime());
			sql.update(table,values,"id="+note.getId(),null);
		}
		
	}
	//批量删除
	public void dataDeleteByIds(int ids){
		//for(int i=0;i<ids.length;i++){
			sql.delete(table,"id="+ids,null);
		//}
	}
	
	
}
