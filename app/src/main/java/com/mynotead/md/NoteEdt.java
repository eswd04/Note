package com.mynotead.md;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;
import com.mynotead.md.alarm.AlarmReceiver;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.FileInputStream;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import android.provider.MediaStore;
import android.database.Cursor;
import java.io.File;

public class NoteEdt extends AppCompatActivity {



	String mContent=null,sTit=null;
	EditText edt,tit;
	int mId=0,time=0;
	ScrollView scrollView;
	private Intent intent;
	private long endTime;
	private AlarmManager alarmManager;
	Bitmap bitmap;
	private Uri originalUri;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		setContentView(R.layout.edt_layout);
		//getActionBar().setDisplayHomeAsUpEnabled(true);
		intent = getIntent();
		mContent = intent.getStringExtra("content");
		sTit = intent.getStringExtra("title");
		edt = (EditText) findViewById(R.id.edt);
		tit = (EditText) findViewById(R.id.etitle);
		tit.setText(sTit);
		edt.setText(mContent);
		init(mContent);
		endTime=intent.getLongExtra("endtime",endTime);
		time = intent.getIntExtra("time",time);
		mId = intent.getIntExtra("id", mId);
//		InputMethodManager ime=(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//		if(ime.hideSoftInputFromWindow(edt.getWindowToken(), 0)){
//			Toast.makeText(this,"测试",Toast.LENGTH_SHORT).show();
//		}
	}
	public void init(String str){
		SpannableString ss = new SpannableString(str);
		Pattern p=Pattern.compile("(<img>)([\\s\\S]*?)(</img>)");
		Matcher m=p.matcher(mContent);
		while(m.find()){
			File uri=new File(m.group().substring(6,m.group().length()-6));
			if(uri.exists()){
			Bitmap bm = BitmapFactory.decodeFile(uri.getAbsolutePath());
			Bitmap rbm= Bitmap.createScaledBitmap(bm, bm.getWidth(), bm.getHeight(), true);
			ImageSpan span = new ImageSpan(this, rbm);
			ss.setSpan(span, m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			
			}
		}
		edt.setText(ss);
	}
	
	
	
	public String EditTime() {
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy/MM/dd HH:mm");
		Date date = new Date();
		return dateFormat.format(date);
	}
	
	public void layoutBottom(View v){
		save();
		finish();
	}
	@Override
	public void onBackPressed() {
		if ((!sTit.trim().equals(tit.getText().toString().trim())) ||(!mContent.trim().equals(edt.getText().toString().trim()))) {
			new AlertDialog.Builder(NoteEdt.this)
				.setTitle("保存？")
				.setMessage("看起来您似乎还没有保存，是否保存修改？")
				.setPositiveButton("保存", new DialogInterface.OnClickListener(){																											
					@Override																												
					public void onClick(DialogInterface p1, int p2) {
						save();																													 
						finish();
					}})
				.setNegativeButton("不保存", new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface p1, int p2) {
						finish();
					}
				})
				.setNeutralButton("返回修改", null)
				.create()
				.show();

		} else {
			finish();
		}

		//save();
		//finish();

	}

	private void save() {
		if (edt.getText().toString().trim().equals("") && tit.getText().toString().trim().equals("")) {
			new NoteDB(this).dataDeleteByIds(mId);
		} else {
			Note note=new Note();
			note.setContent(edt.getText().toString());
			note.setId(mId);
			note.setTitle(tit.getText().toString());
			note.setLastEdtTime(System.currentTimeMillis());
			note.setTime(time);
			new NoteDB(this).updateData(note);
			if(time!=0){
				setAlarm();
			}
			Toast.makeText(this,"已保存",Toast.LENGTH_SHORT).show();
		}

	}


	private void setAlarm() {
		Intent intent=new Intent(NoteEdt.this, AlarmReceiver.class);
		intent.putExtra("id", mId);
		intent.putExtra("notifycontent", tit.getText().toString().trim().equals("") ?edt.getText().toString(): tit.getText().toString());
		final PendingIntent send=PendingIntent.getBroadcast(NoteEdt.this, mId, intent, 0);
		alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
//				intent.putExtra("notifytitle", list.get(barr[0]).getTitle().equals("")?"":list.get(barr[0]).getTitle());
//				for (int i=0;i < barr.length;i++) {
		alarmManager.set(AlarmManager.RTC_WAKEUP, endTime, send);
		new NoteDB(this).endtime(endTime + "", mId, 1);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.addmenu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		ContentResolver resolver = getContentResolver();
		String path=null;
		try{
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                originalUri = data.getData();
				Cursor cursor = getContentResolver().query( originalUri, new String[] { MediaStore.Images.ImageColumns.DATA }, null, null, null );
				if (  cursor !=null) {
					if ( cursor.moveToFirst() ) {
						int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
						if ( index > -1 ) {
							path=cursor.getString( index );
						}
					}
					cursor.close();
				}
				File file=new File(path);
				
				if(file.exists()){
					Bitmap originalBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
					bitmap =Bitmap.createScaledBitmap(originalBitmap,originalBitmap.getWidth(), originalBitmap.getHeight(),true);	
					if (bitmap != null) {
						insertIntoEditText(getBitmapMime(bitmap, file.getAbsolutePath()));
					} else {
						Toast.makeText(NoteEdt.this, "获取图片失败",
									   Toast.LENGTH_SHORT).show();
					}
				}else{
					Toast.makeText(NoteEdt.this,"获取图片失败，尝试切换另一个管理器",Toast.LENGTH_SHORT).show();
				}
                
            }
        }
        if (bitmap != null) {
			
        }
		}catch(Exception e){
			Toast.makeText(NoteEdt.this,"获取图片失败，尝试切换另一个管理器",Toast.LENGTH_SHORT).show();

		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void insertIntoEditText(SpannableString ss) {
		Editable et = edt.getText();// 先获取Edittext中的内容
		int start = edt.getSelectionStart();
		et.insert(start, ss);// 设置ss要添加的位置
		edt.setText(et);// 把et添加到Edittext中
		edt.setSelection(start + ss.length());// 设置Edittext中光标在最后面显示
	}
	
	private SpannableString getBitmapMime(Bitmap pic, String uri) {

       // String path = uri;
		String p="<img>"+uri+"</img>";
        SpannableString ss = new SpannableString(p);
        ImageSpan span = new ImageSpan(this, pic);
        ss.setSpan(span, 0, p.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ss;
    }
	
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.save:
				save();
				finish();
//				
				
				
				break;
				case R.id.menuInsert:
				Intent getImage = new Intent(Intent.ACTION_GET_CONTENT);
                getImage.addCategory(Intent.CATEGORY_OPENABLE);
                getImage.setType("image/*");
                startActivityForResult(getImage, 1);
				break;
				
				
				
			case android.R.id.home:
				onBackPressed();
				break;
		}
		return super.onOptionsItemSelected(item);
	}
}
