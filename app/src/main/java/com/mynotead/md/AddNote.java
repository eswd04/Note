package com.mynotead.md;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;

public class AddNote extends AppCompatActivity {

	EditText mcontent,mtitle;

	private Uri originalUri;

	private Bitmap bitmap;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edt_layout);
		mtitle = (EditText) findViewById(R.id.etitle);
		mcontent = (EditText) findViewById(R.id.edt);
		handleImage();
	}


	private void handleImage() {
        Intent intent=getIntent();
        String action=intent.getAction();
		CharSequence text =intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT) ;
		if(text!=null){
			mcontent.setText(text);
		}
		if (action != null) {
			String type=intent.getType();
			//Toast.makeText(AddNote.this,type,Toast.LENGTH_SHORT).show();
			
			if (action.equals(Intent.ACTION_SEND) && type.equals("text/plain")) {
				//接收文本
				mcontent.setText(intent.getStringExtra(Intent.EXTRA_TEXT));
				
				//接收多张图片
				//ArrayList<Uri> uris=intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
//            
//            }
				
			}
			
//			else if(action.equals(Intent.ACTION_SEND)&&type.contains("image")){
//				Uri uri=intent.getParcelableExtra(Intent.EXTRA_STREAM);
//				String path=null;
//				
//				
//					
//						Cursor cursor = getContentResolver().query( uri, new String[] { MediaStore.Images.ImageColumns.DATA }, null, null, null );
//						if (  cursor !=null) {
//							if ( cursor.moveToFirst() ) {
//								int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
//								if ( index > -1 ) {
//									path=cursor.getString( index );
//								}
//							}
//							cursor.close();
//						}
//				//Toast.makeText(AddNote.this,uri.getPath() ,Toast.LENGTH_SHORT).show();
//				
//				//ArrayList<Uri> uris=intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
//				
//                   //FileInputStream fileInputStream=new FileInputStream(uri.getPath());
//                    Bitmap b= BitmapFactory.decodeFile(path);
//					bitmap =Bitmap.createScaledBitmap(b,b.getWidth(), b.getHeight(),true);	
//					if(bitmap!=null){
//						getBitmapMime(bitmap,path);
//					}
//               // } catch (FileNotFoundException e) {
//                //    e.printStackTrace();
//              //  }
//				
//			}
			
		}
		
		
		
    }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.addmenu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	public void layoutBottom(View v) {
		if (mcontent.getText()
			.toString().trim().equals("") && mtitle.getText().toString().trim().equals("")) {
			finish();
		} else {
			save();
			Toast.makeText(this, "已保存", Toast.LENGTH_SHORT).show();
			finish();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.save:
				if (mcontent.getText().toString().trim().equals("") && mtitle.getText().toString().trim().equals("")) {
					finish();
				} else {
					save();
					finish();
				}
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
						Toast.makeText(AddNote.this, "获取图片失败",
									   Toast.LENGTH_SHORT).show();
					}
				}else{
					Toast.makeText(AddNote.this,"获取图片失败，尝试切换另一个管理器",Toast.LENGTH_SHORT).show();
				}

            }
		
        }
		
        if (bitmap != null) {

        }
		}catch(Exception e){
			Toast.makeText(AddNote.this,"获取图片失败，尝试切换另一个管理器",Toast.LENGTH_SHORT).show();
			
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void insertIntoEditText(SpannableString ss) {
		Editable et = mcontent.getText();// 先获取Edittext中的内容
		int start = mcontent.getSelectionStart();
		et.insert(start, ss);// 设置ss要添加的位置
		mcontent.setText(et);// 把et添加到Edittext中
		mcontent.setSelection(start + ss.length());// 设置Edittext中光标在最后面显示
	}

	private SpannableString getBitmapMime(Bitmap pic, String uri) {

		// String path = uri;
		String p="<img>"+uri+"</img>";
        SpannableString ss = new SpannableString(p);
        ImageSpan span = new ImageSpan(this, pic);
        ss.setSpan(span, 0, p.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ss;
    }
	
	private String time() {
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy/MM/dd HH:mm");
		Date date=new Date();
		return dateFormat.format(date);
	}
	private void save() {
		String t=time();
		Note note=new Note();
		note.setTitle(mtitle.getText().toString());
		note.setContent(mcontent.getText().toString());
		note.setCreateTime(t);
		note.setLastEdtTime(System.currentTimeMillis());
		new NoteDB(this).dataAdd(note);
	}

	@Override
	public void onBackPressed() {
		if (mcontent.getText().toString().trim().equals("") && mtitle.getText().toString().trim().equals("")) {
			finish();
		} else {
			new AlertDialog.Builder(AddNote.this)
				.setTitle("保存？")
				.setMessage("看起来您似乎还没有保存，是否保存？")
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

		}
	}

}
