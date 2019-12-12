package com.mynotead.md;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import java.util.List;
import android.os.AsyncTask;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
//import android.widget.AdapterView.OnItemClickListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Adapter;
import android.content.Intent;
import java.util.Date;
import android.widget.Toast;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.AbsListView;
import android.view.ActionMode;
import android.view.LayoutInflater;
import java.util.ArrayList;
import android.app.TimePickerDialog;
import android.widget.TimePicker;
import android.app.DatePickerDialog;
import android.widget.DatePicker;
import java.util.Calendar;
import java.util.TimeZone;
import android.app.AlarmManager;
import android.app.PendingIntent;
import com.mynotead.md.alarm.AlarmReceiver;
import android.support.v7.app.AppCompatActivity;
import android.support.design.widget.Snackbar;
import android.support.design.widget.FloatingActionButton;
import android.view.View.OnClickListener;
import android.widget.AbsListView.OnScrollListener;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.view.KeyEvent;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import java.text.SimpleDateFormat;
import android.view.View.*;
import android.view.*;
import android.support.v7.widget.RecyclerView;
import com.mynotead.md.adapter.*;
import android.support.v7.widget.*;
import android.support.v7.widget.helper.*;
import android.util.*;
import java.util.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.support.v4.content.*;
import android.os.*;
import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.OutputStreamWriter;
import android.widget.RelativeLayout;
import android.content.ClipboardManager;
import android.content.ClipData; 

/*
 *  Create by 犯二的二
 */

public class MainActivity extends AppCompatActivity implements OnClickListener, ActionMode.Callback {			//,OnItemClickListener,AbsListView.MultiChoiceModeListener,OnScrollListener{




	RecyclerView recyclerView;
	FloatingActionButton fab;
	//ListView listview;
	List<Note> list;			//使用自定义的列表
	//ListAdapter listAdapter;
	NoteDB noteDB;
	DBHelper dbHelper;
	boolean rem=false;
	protected boolean isActionMod=false;
	TextView num,empty;
	CardView card;
	CoordinatorLayout v;
	RelativeLayout ml;
	private PopupMenu popup;
	private AlarmManager alarmManager;
	private RecycleAdapter recycleAdapter;
	private ActionMode actionMode;
    public Set<Integer> positionSet = new HashSet<>();
	Set<Integer> val;
	int pId;
	public static MainActivity instance;
	ItemTouchHelper.SimpleCallback callback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		init();
		firstOrNot();
		//newFeature();
		//toast(System.currentTimeMillis()+"");
    }

	private void newFeature() {
		SharedPreferences shp=getSharedPreferences("first", MODE_PRIVATE);
		Editor editor=shp.edit();
		if (shp.getBoolean("feature", true)) {
			new AlertDialog.Builder(this).setTitle(R.string.tfeature).setMessage(R.string.mfeature).setPositiveButton("确定", null).show();
			editor.putBoolean("feature", false);
			editor.commit();
		}
	}
	private void firstOrNot() {		//如果是第一次，则弹出对话框，并保存数据
		SharedPreferences sp=getSharedPreferences("first", MODE_PRIVATE);
		Editor editor=sp.edit();
		if (sp.getBoolean("first", true)) {
			new AlertDialog.Builder(this).setTitle(R.string.ftitle).setMessage(R.string.fmessage).setPositiveButton("确定", null).show();
			editor.putBoolean("first", false);
			editor.commit();
		}
	}
	public String formatData(long date) {
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy/MM/dd HH:mm");
		return dateFormat.format(date);
	}
	
	private void init() {
		/*
		 *初始化控件
		 */
		instance = this;
		card = (CardView) findViewById(R.id.mainAppBar);
		fab = (FloatingActionButton) findViewById(R.id.fab);
		num = (TextView) findViewById(R.id.note_num);
		empty = (TextView) findViewById(R.id.empty);
		recyclerView = (RecyclerView) findViewById(R.id.mainRecycleView);
		//listview = (ListView) findViewById(R.id.mlistview);
		ml = (RelativeLayout) findViewById(R.id.mainLinearLayout);
		v = (CoordinatorLayout) findViewById(R.id.mainCoordinatorLayout);
		LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
		recyclerView.setLayoutManager(mLayoutManager);
		noteDB = new NoteDB(this);

		list = new ArrayList<Note>();
		list .addAll(noteDB.getList());
		recycleAdapter = new RecycleAdapter(this, list);
		recyclerView.setAdapter(recycleAdapter);
		recycleAdapter.setOnItemClickLitener(new RecycleAdapter.OnItemClickLitener(){

				@Override
				public void onItemClick(View view, int position) {
					/*
					 *单击事件
					 */

					if (actionMode != null) {
						// 如果当前处于多选状态，则进入多选状态的逻辑
						// 维护当前已选的position
						addOrRemove(position);

					} else {
						// 如果不是多选状态，则进入点击事件的业务逻辑
						// TODO something
						Intent i=new Intent(MainActivity.this, NoteEdt.class);
						i.putExtra("content", list.get(position).getContent());
						i.putExtra("id", list.get(position).getId());
						i.putExtra("title", list.get(position).getTitle());
						if (list.get(position).getEndTime() <= System.currentTimeMillis()) {
							noteDB.endtime("0", list.get(position).getId(), 0);
							i.putExtra("time", 0);
							i.putExtra("endtime", 0);
						} else {
							i.putExtra("time", 1);
							i.putExtra("endtime", list.get(position).getEndTime());
						}
						startActivity(i);
					}
//		//Intent传递数据，并启动新Activity
				}

				@Override
				public void onItemLongClick(View view, int position) {
					if (actionMode == null) {
						actionMode = startActionMode(MainActivity.this);
						recycleAdapter.setActionMode(true);
						addOrRemove(position);
					}
				}

			});
		recycleAdapter.setOnPopClickListener(new RecycleAdapter.OnPopClickListener(){
				@Override
				public void onItemClick(View view, final int position) {
					popup = new PopupMenu(MainActivity.this, view);
					popup.getMenuInflater().inflate(R.menu.popupmenu, popup.getMenu());
					if (list.get(position).getTime() == 1) {//如果当前项为已添加提醒的项目，则更改菜单项
						popup.getMenu().findItem(R.id.popunremind).setVisible(true);
						popup.getMenu().findItem(R.id.popremind).setTitle("更改提醒");

					} else {
						popup.getMenu().findItem(R.id.popremind).setTitle("添加提醒");

					}
					popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener(){

							@Override
							public boolean onMenuItemClick(MenuItem item) {
								switch (item.getItemId()) {
									case R.id.popinfo:
										long rTime=list.get(position).getEndTime();
										//String size="共"+list.get(position).getContent().length()+"字";
										String str=rTime >= System.currentTimeMillis() ?"提醒时间：" + formatData(rTime) + "\n": "";
										new AlertDialog.Builder(MainActivity.this)
											.setTitle("信息")
											.setMessage("内容字数：共"+list.get(position).getContent().trim().length()+"字(含空格)\n"
											+str + "创建时间：" + list.get(position).getCreateTime() + "\n最后修改：" +
														formatData(list.get(position).getLastEdtTime())).show();
										break;
									case R.id.popout:
										outPutContent(position);
										break;
									case R.id.popremind:
										setAlarm(position);
										break;
									case R.id.popdelete:
										final int pos=	position;
										final Note n=list.get(position);
										if(list.get(pos).getTime()==1){
											Intent intent=new Intent(MainActivity.this, AlarmReceiver.class);
											PendingIntent send=PendingIntent.getBroadcast(MainActivity.this, list.get(position).getId(), intent, 0);
											alarmManager.cancel(send);
										}
										//new DeleteAsyncTask(noteDB, MainActivity.this, v).execute(list.get(position).getId());
										noteDB.dataDeleteByIds(list.get(position).getId());
										list.remove(position);
										recycleAdapter.notifyItemRemoved(position);
										Snackbar.make(v, "已删除", Snackbar.LENGTH_LONG)
											.setAction("撤销", new View.OnClickListener() {
												@Override
												public void onClick(View v) {
													noteDB.restroe(n);
													list.add(pos, n);
													showOrH();
													recycleAdapter.notifyItemInserted(pos);
												}
											}).show();
										showOrH();
										break;
									case R.id.popunremind:
										Intent intent=new Intent(MainActivity.this, AlarmReceiver.class);
										PendingIntent send=PendingIntent.getBroadcast(MainActivity.this, list.get(position).getId(), intent, 0);
										alarmManager.cancel(send);
										noteDB.endtime("0", list.get(position).getId(), 0);
										toast("已取消提醒");
										new RefleshAsync().execute();
										break;
								}
								return false;
							}


						});
					popup.show();
				}
			});
		callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder source, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int i) {
				//	handler.post(runnable);
				final int pos = viewHolder.getAdapterPosition();
                final Note item = list.get(pos);
				pId = list.get(pos).getId();
				if(list.get(pos).getTime()==1){
					Intent intent=new Intent(MainActivity.this, AlarmReceiver.class);
					PendingIntent send=PendingIntent.getBroadcast(MainActivity.this, pId, intent, 0);
					alarmManager.cancel(send);
				}
				//new DeleteAsyncTask(noteDB, MainActivity.this, v).execute(list.get(pos).getId());
				noteDB.dataDeleteByIds(pId);
				recycleAdapter.mOnSwiped(viewHolder);
				
				
				Snackbar.make(viewHolder.itemView, "已删除", Snackbar.LENGTH_LONG)
					.setAction("撤销", new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							//handler.removeCallbacks(runnable);
							noteDB.restroe(item);
							list.add(pos, item);
							showOrH();
							recycleAdapter.notifyItemInserted(pos);


						}
					}).show();


				showOrH();
            }

			@Override
			public boolean isItemViewSwipeEnabled() {
				return actionMode == null ?true: false;
			}







			//侧滑背景效果




			@Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                // 释放View时回调，清除背景颜色，隐藏图标
                // 默认是操作ViewHolder的itemView，这里调用ItemTouchUIUtil的clearView方法传入指定的view
                getDefaultUIUtil().clearView(((RecycleAdapter.ViewHolder)viewHolder).itemCardView);
				((RecycleAdapter.ViewHolder) viewHolder).itemBack.setBackgroundColor(Color.TRANSPARENT);
				((RecycleAdapter.ViewHolder  )viewHolder).ivSchedule.setVisibility(View.GONE);
                ((RecycleAdapter.ViewHolder  )viewHolder).ivDone.setVisibility(View.GONE);
            }

            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                // 当viewHolder的滑动或拖拽状态改变时回调
                if (viewHolder != null) {
                    // 默认是操作ViewHolder的itemView，这里调用ItemTouchUIUtil的clearView方法传入指定的view
                    getDefaultUIUtil().onSelected(((RecycleAdapter.ViewHolder) viewHolder).itemCardView);
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                // ItemTouchHelper的onDraw方法会调用该方法，可以使用Canvas对象进行绘制，绘制的图案会在RecyclerView的下方
                // 默认是操作ViewHolder的itemView，这里调用ItemTouchUIUtil的clearView方法传入指定的view
                getDefaultUIUtil().onDraw(c, recyclerView,  ((RecycleAdapter.ViewHolder) viewHolder).itemCardView, dX, dY, actionState, isCurrentlyActive);
                if (dX > 0) { // 向左滑动是的提示
					((RecycleAdapter.ViewHolder)viewHolder).itemBack.setBackgroundResource(R.color.colorSchedule);
					((RecycleAdapter.ViewHolder)viewHolder).ivDone.setVisibility(View.VISIBLE);
					((RecycleAdapter.ViewHolder  )viewHolder).ivSchedule.setVisibility(View.GONE);
                }
                if (dX < 0) { // 向右滑动时的提示
                    ((RecycleAdapter.ViewHolder  )viewHolder).itemBack.setBackgroundResource(R.color.colorSchedule);
                    ((RecycleAdapter.ViewHolder  )viewHolder).ivSchedule.setVisibility(View.VISIBLE);
                    ((RecycleAdapter.ViewHolder  )viewHolder).ivDone.setVisibility(View.GONE);
                }
            }

            @Override
            public void onChildDrawOver(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                // ItemTouchHelper的onDrawOver方法会调用该方法，可以使用Canvas对象进行绘制，绘制的图案会在RecyclerView的上方
                // 默认是操作ViewHolder的itemView，这里调用ItemTouchUIUtil的clearView方法传入指定的view
                getDefaultUIUtil().onDrawOver(c, recyclerView, ((RecycleAdapter.ViewHolder) viewHolder).itemCardView, dX, dY, actionState, isCurrentlyActive);
            }




//			侧滑背景效果
//			作者：zly394
//			链接：https://www.jianshu.com/p/9a5f81c887b8
//			來源：简书
//			简书著作权归作者所有，任何形式的转载都请联系作者获得授权并注明出处。












        };

		callback.isItemViewSwipeEnabled();
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
		fab.setOnClickListener(this);
		num.setText("共" + list.size() + "条记录");
	}
	private void outPutContent(int pos) {
		String name=list.get(pos).getTitle().trim().equals("") ?list.get(pos).getId() + "": list.get(pos).getTitle();
		String content =name + "\n\n" + list.get(pos).getContent();
		File dir=new File(Environment.getExternalStorageDirectory() + "/轻记事/");//
		File file=new File(dir , name + ".txt");
		if (!file.exists()) {
			dir.mkdir();
			try {
				file.createNewFile();
			} catch (IOException e) {
				toast("文件创建失败");
			}
		}
		try {
			FileOutputStream fos=new FileOutputStream(file);
			OutputStreamWriter osw=new OutputStreamWriter(fos);
			osw.write(content);
			osw.flush();
			osw.close();
			fos.close();
			toast("内容已导出到" + file.getPath());
		} catch (FileNotFoundException e) {
			toast("文件未找到，创建失败");
			e.printStackTrace();
		} catch (IOException e) {
			toast("文件创建失败");
			e.printStackTrace();
		}


	}

	private void addOrRemove(int position) {

        if (positionSet.contains(position)) {
            // 如果包含，则撤销选择
			list.get(position).setChecked(false);
            positionSet.remove(position);
        } else {
            // 如果不包含，则添加
			list.get(position).setChecked(true);
            positionSet.add(position);

        }


        if (positionSet.size() == 0) {
            // 如果没有选中任何的item，则退出多选模式
            actionMode.finish();
        } else {

			//actionMode.getMenu().findItem(R.id.remind).setVisible(false);
            // 设置ActionMode标题
            actionMode.setTitle("已选" + positionSet.size() + "项");
            // 更新列表界面，否则无法显示已选的item
            recycleAdapter.notifyDataSetChanged();
        }
    }



    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        if (actionMode == null) {
			mode.getMenuInflater().inflate(R.menu.action, menu); 
			actionMode = mode;
			fab.hide();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {

		switch (item.getItemId()) {

			case R.id.delete:
				if(positionSet.size()==list.size()){
					new AlertDialog.Builder(MainActivity.this).setTitle("删除").setMessage("已选择" + positionSet.size() + "项，确定删除？").setPositiveButton("确定", new DialogInterface.OnClickListener(){
							@Override
							public void onClick(DialogInterface p1, int p2) {
								noteDB.deleteAll();
								new RefleshAsync().execute();
								mode.finish();
								toast("已删除");
							}
						}).setNegativeButton("取消", null).create().show();
				}else{
				final Integer arr[]=new Integer[positionSet.size()]; 
				final Iterator it=positionSet.iterator();
				for (int i=0;it.hasNext();i++) {
					int a=it.next();
					//m[i]=a;
					arr[i] = list.get(a).getId();
				}
				new AlertDialog.Builder(MainActivity.this).setTitle("删除").setMessage("已选择" + positionSet.size() + "项，确定删除？").setPositiveButton("确定", new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface p1, int p2) {
							
//							new Thread(new Runnable(){
//
//									@Override
//									public void run() {
							for (int j=0;j < arr.length;j++) {
//								if(list.get(arr[j]).getTime()==1){
//									Intent intent=new Intent(MainActivity.this, AlarmReceiver.class);
//									PendingIntent send=PendingIntent.getBroadcast(MainActivity.this, arr[j], intent, 0);
//									alarmManager.cancel(send);
//								}
								noteDB.dataDeleteByIds(arr[j]);

//								list.remove(m[j]);
//								recycleAdapter.notifyItemRemoved(m[j]);
							}

							//}


							//}).start();
							new RefleshAsync().execute();
							mode.finish();
							toast("已删除");
						}
					}).setNegativeButton("取消", null).create().show();
}
				break;
			case R.id.selectAll:

				//int p=list.size();
				if (list.size() != positionSet.size()) {
					for (int i=0;i < list.size();i++) {
						positionSet.add(i);
					}
				} else {
					positionSet.clear();
				}
				actionMode.setTitle("已选" + positionSet.size() + "项");
				recycleAdapter.notifyDataSetChanged();
				break;
			case R.id.actionCopy:
				StringBuffer info = new StringBuffer();
				Iterator iterrator=positionSet.iterator();
				while(iterrator.hasNext()){
					int k=iterrator.next();
					info.append((list.get(k).getContent().trim().equals("")?list.get(k).getTitle():list.get(k).getContent()) + "\n\n");
				}
				ClipboardManager cmb = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
				cmb.setPrimaryClip(ClipData.newPlainText("NoteContent",info.toString()));
				toast("已复制内容到剪切板");
				mode.finish();
				break;



		}




//		Set<String> valueSet = new HashSet<>();
////                for (int position : positionSet) {
////                    valueSet.add(recycleAdapter.getItem(position));
////                }
////                for (Note val : valueSet) {
////                    recycleAdapter.remove(val);
////                }
//                mode.finish();
//                return true;
//            default:
		return false;
		// }
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        actionMode = null;
        positionSet.clear();
		fab.show();
		recycleAdapter.setActionMode(false);   //隐藏复选框，显示子菜单
        recycleAdapter.notifyDataSetChanged();

    }





//	@Override
//	public void onItemCheckedStateChanged(ActionMode p1, int position, long p3, boolean isItemSelect) {
//		if (isItemSelect) {
//			
//		}
//		if(intPosition.size()>1){
//			
//		}else if(intPosition.size()==1){
//			
//		}
//		mTitleTextView.setText("已选" + listview.getCheckedItemCount() + "项");  
//		listAdapter.notifyDataSetChanged();
	//}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.fab:
				startActivity(new Intent(this, AddNote.class));
				break;
		}
	}



//	@Override
//	public void onScrollStateChanged(AbsListView p1, int p2) {
//		// TODO: Implement this method
//	}
//
//	@Override
//	public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//		
//		int currentTop;
//
//		View firstChildView = absListView.getChildAt(0);
//		if (firstChildView != null) {
//			currentTop = absListView.getChildAt(0).getTop();
//		} else {
//			//ListView初始化的时候会回调onScroll方法，此时getChildAt(0)仍是为空的
//			return;
//		}
//		//判断上次可见的第一个位置和这次可见的第一个位置
//		if (firstVisibleItem != mLastFirstPostion) {
//			//不是同一个位置
//			if (firstVisibleItem > mLastFirstPostion) {
//				//TODO do down
//				fab.hide();
//				
//			} else {
//				if(!isActionMod){
//				fab.show();
//				//TODO do up
//				}
//			}
//			mLastFirstTop = currentTop;
//		} else {
//			//是同一个位置
//			if(Math.abs(currentTop - mLastFirstTop) > touchSlop){
//				//fab.hide();
//				//避免动作执行太频繁或误触，加入touchSlop判断，具体值可进行调整
//				if (currentTop > mLastFirstTop) {
//					//TODO do up
//					//fab.show();
//				//	Log.i("cs", "equals--->up");
//				} else if (currentTop < mLastFirstTop) {
//					//TODO do down
//					//fab.hide();
//					//Log.i("cs", "equals--->down");
//				}
//				mLastFirstTop = currentTop;
//			}
//		}
//		mLastFirstPostion = firstVisibleItem;
//	}






//	@Override
//	public void onItemClick(AdapterView<?> p1, View p2, int position, long p4) {

//
//
//	}

	private void toast(String str) {
		//代码太长，用方法代替
		Snackbar.make(v, str, Snackbar.LENGTH_SHORT).show();
		//Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
	}


//	public void reflesh() {
//		//list = noteDB.getList();		//从数据库获取数据列表
//		listAdapter.setList(noteDB.getList());
//		
//		if (list.size() == 0) {
//			ml.setVisibility(View.GONE);
//			empty.setVisibility(View.VISIBLE);
//			empty.setText("无记事");
//		} else {
//			ml.setVisibility(View.VISIBLE);
//			empty.setVisibility(View.GONE);
//			num.setText("共" + list.size() + "条记录");
//
//		}
//
//	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);//获取菜单布局
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.item:
				startActivity(new Intent(this, AddNote.class));
				break;
			case R.id.about:
				startActivity(new Intent(this, About.class));
				break;
			case R.id.exit:
				finish();
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		super.onResume();
		/*
		 *	当Activity重新加载时，刷新列表
		 */
		if (actionMode == null) {
			new RefleshAsync().execute();
		}

	}
	private void setAlarm(final int listPosition) {
		final Calendar calendar;
		calendar = Calendar.getInstance();
		final Calendar instant=Calendar.getInstance();
		instant.setTimeInMillis(System.currentTimeMillis());
		Intent intent=new Intent(MainActivity.this, AlarmReceiver.class);
		intent.putExtra("id", list.get(listPosition).getId());
		intent.putExtra("notifycontent", list.get(listPosition).getContent().trim().equals("") ?list.get(listPosition).getTitle(): list.get(listPosition).getContent());
		final PendingIntent send=PendingIntent.getBroadcast(MainActivity.this, list.get(listPosition).getId(), intent, 0);


//				intent.putExtra("notifytitle", list.get(barr[0]).getTitle().equals("")?"":list.get(barr[0]).getTitle());
//				for (int i=0;i < barr.length;i++) {
		if (rem) {
			alarmManager.cancel(send);
			noteDB.endtime("0", list.get(listPosition).getId(), 0);
			toast("已取消提醒");
		} else {
			new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener(){
					@Override
					public void onDateSet(DatePicker datePicker, int years, int month, int day) {
						instant.set(Calendar.YEAR, years);
						instant.set(Calendar.MONTH, month);
						instant.set(Calendar.DAY_OF_MONTH, day);
						new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener(){
								@Override
								public void onTimeSet(TimePicker timePicker, int hours, int minute) {
									instant.set(Calendar.HOUR_OF_DAY, hours);
									instant.set(Calendar.MINUTE, minute);
									instant.set(Calendar.SECOND, 0);
									alarmManager.set(AlarmManager.RTC_WAKEUP, instant.getTimeInMillis(), send);
									noteDB.endtime(instant.getTimeInMillis() + "", list.get(listPosition).getId(), 1);
									recycleAdapter.notifyDataSetChanged();
									toast("设定提醒成功");
									new RefleshAsync().execute();
								}

							}, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
					}
				}, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();

		}
	}


//	@Override
//	public boolean onCreateActionMode(ActionMode mode, Menu menu) {		//进入Action
//		mode.getMenuInflater().inflate(R.menu.action, menu);  
//		View multiSelectActionBarView = LayoutInflater.from(this).inflate(R.layout.action_mode_menu, null);  
//		mode.setCustomView(multiSelectActionBarView);  
//		mTitleTextView = (TextView) multiSelectActionBarView.findViewById(R.id.action_title);  
//		mTitleTextView.setText("已选0项");  
//		listAdapter.setCheckable(true);  
//		listAdapter.notifyDataSetChanged();  
//		isActionMod = true;//
//		fab.hide();
//		return true;
//	}
//
//	@Override
//	public boolean onPrepareActionMode(ActionMode p1, Menu p2) {
//		
//		return false;
//	}
//
//	@Override
//	public boolean onActionItemClicked(ActionMode p1, MenuItem p2) {
//		switch (p2.getItemId()) {
//			case R.id.delete:
//				
//				if(intPosition.size()==1){
//					new DeleteAsyncTask(noteDB, MainActivity.this,v).execute(list.get(intPosition.get(0)).getId());
//				}else{
//					Integer[] b  = new Integer[arrList.size()];//当泛型为Integer时，需要
//					arrList.toArray(b);
//					new DeleteAsyncTask(noteDB, MainActivity.this,v).execute(b);
//				}
//				
//				
//				new RefleshAsync().execute();
//				break;
//			case R.id.remind:
//				setAlarm(intPosition.get(0));
//				break;
//			case R.id.deleteAll:
//				new AlertDialog.Builder(this).setTitle("确认").setMessage("确定要删除所有记事？此操作不可撤销！").setPositiveButton("确定", new DialogInterface.OnClickListener(){
//
//						@Override
//						public void onClick(DialogInterface p1, int p2) {
//							noteDB.daleteAll();
//							toast("已删除");
//							new RefleshAsync().execute();
//						}
//					}).setNegativeButton("取消", null).create().show();
//
//				break;
//
//
//
//		}
//		p1.finish();
//		new RefleshAsync().execute();
//		return true;
//	}
//	@Override
//	public void onDestroyActionMode(ActionMode p1) {
//		isActionMod = false;
//		listAdapter.setCheckable(false);  
//		arrList.clear();				//退出时清除已保存的数据
//		intPosition.clear();
//		fab.show();
//		listAdapter.notifyDataSetChanged();
//	}
//
//	@Override
//	public void onItemCheckedStateChanged(ActionMode p1, int position, long p3, boolean isItemSelect) {
//		if (isItemSelect) {
//			arrList.add(list.get(position).getId());	
//			intPosition.add(position);
//		} else {
//			arrList.remove(Integer.valueOf(list.get(position).getId()));
//			intPosition.remove(Integer.valueOf(position));
//		}
//		if(intPosition.size()>1){
//			p1.getMenu().findItem(R.id.remind).setVisible(false);
//		}else if(intPosition.size()==1){
//			if(list.get(intPosition.get(0)).getEndTime()<System.currentTimeMillis()){
//				p1.getMenu().findItem(R.id.remind).setVisible(true).setTitle("添加提醒").setIcon(R.mipmap.ic_alarm_add_white_24dp);
//				rem=false;
//			}else{
//				p1.getMenu().findItem(R.id.remind).setVisible(true).setTitle("取消提醒").setIcon(R.mipmap.ic_alarm_off_white_24dp);
//				rem=true;
//			}
//		}
//		mTitleTextView.setText("已选" + listview.getCheckedItemCount() + "项");  
//		listAdapter.notifyDataSetChanged();
//	}
	public void showOrH() {
		//更新底栏状态

		empty.setVisibility(list.size() != 0 ?View.GONE: View.VISIBLE);
		if (list.size() == 0) {
			ml.setVisibility(View.GONE);
			card.setVisibility(View.GONE);
			empty.setText("无记事");
		} else {
			ml.setVisibility(View.VISIBLE);
			num.setText("共" + list.size() + "条记录");
			card.setVisibility(View.VISIBLE);
		}
	}


	public class RefleshAsync extends AsyncTask<Void,Void,List<Note>> {

		@Override
		protected List<Note> doInBackground(Void[] p1) {
			list = noteDB.getList();
			recycleAdapter.setList(list);
			//list.clear();
			//list.containsAll(noteDB.getList());
			//list.addAll(noteDB.getList());
			return list;
		}

		@Override
		protected void onPostExecute(List<Note> result) {
			super.onPostExecute(result);
			recycleAdapter.notifyItemRangeChanged(0, result.size());
			showOrH();
			//listAdapter.setList(result);
			//listAdapter.notifyDataSetChanged();

			//listAdapter.notifyDataSetChanged();
		}
	}
//	private long mExitTime;
//	@Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        //判断用户是否点击了“返回键”
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//			//与上次点击返回键时刻作差
//            if ((System.currentTimeMillis() - mExitTime) > 2000) {
//				//大于2000ms则认为是误操作，使用Toast进行提示
//               // Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
//			   toast("再按一次退出");
//                //并记录下本次点击“返回键”的时刻，以便下次进行判断
//                mExitTime = System.currentTimeMillis();
//            } else {
//				//小于2000ms则认为是用户确实希望退出程序-调用System.exit()方法进行退出
//                System.exit(0);
//            }
//            return true;
//		}
//		return super.onKeyDown(keyCode, event);
//    }
//


}

