package com.mynotead.md;
import android.widget.BaseAdapter;
import android.view.ViewGroup;
import android.view.View;
import android.content.Context;
import java.util.List;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import java.util.Calendar;
import android.widget.PopupMenu;
import android.view.View.OnClickListener;
import android.view.MenuItem;
import android.app.AlertDialog;
import android.support.design.widget.Snackbar;


public class ListAdapter extends BaseAdapter  {

	
	
	Context context;
	List<Note> list;

	private boolean mCheckable=false;

	private OnPopupMenuClick onPopupMenuItemClick;

	
	public void setOnPopupMenuItemOnClickListener(OnPopupMenuClick onPopupMenuItemClick){
		this.onPopupMenuItemClick=onPopupMenuItemClick;
	}
	public interface OnPopupMenuClick{
		void ItemClick(View v,int position);
	}
	public void setList(List<Note> list){
		this.list=list;
		notifyDataSetChanged();
	}
	
/*
*自定义列表适配器
*/
	public ListAdapter(Context context, List<Note> list) {
		this.context = context;
		this.list = list;
	}
	@Override
	public int getCount() {
		return list.size();
	}
	
	@Override
	public Object getItem(int p1) {
		return list.get(p1);
	}
	
	@Override
	public long getItemId(int p1) {
		return p1;
	}
	public void setCheckable(boolean checkable) {  
		this.mCheckable = checkable;  
	}
	
	
	
	class ViewHolder {
		/*
		*  ViewHolder优化列表
		*/
		MyTextView mt;
		TextView time,content,title;//,endTime;
		CheckBox itemCheck;
		ImageView img,pop;
		public ViewHolder(View view) {
			title=(TextView) view.findViewById(R.id.title);
			time = (TextView) view.findViewById(R.id.time);
			content = (TextView) view.findViewById(R.id.content);
			itemCheck=(CheckBox) view.findViewById(R.id.itemCheckBox);
			img=(ImageView) view.findViewById(R.id.dot);
			pop=(ImageView) view.findViewById(R.id.action);
			//endTime=(TextView) view.findViewById(R.id.endTime);
		}
	}
	
	

	
	@Override
	public View getView(final int position, View view, ViewGroup parent) {
		ViewHolder viewHolder ;
		if (view == null) {
			view = LayoutInflater.from(context).inflate(R.layout.item, null);
			viewHolder = new ViewHolder(view);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ListAdapter.ViewHolder) view.getTag();
		}
		//viewHolder.time.setText(list.get(position).getTime());
		viewHolder.pop.setVisibility(mCheckable?View.GONE:View.VISIBLE);
		viewHolder.content.setText(list.get(position).getContent());
		viewHolder.content.setVisibility(list.get(position).getContent().equals("")?View.GONE:View.VISIBLE);
		if(list.get(position).getTitle().equals("")){
			viewHolder.title.setText("无标题");
		}else{
		viewHolder.title.setText(list.get(position).getTitle());
		}
		if(list.get(position).getTime()==1){
			viewHolder.img.setVisibility(View.VISIBLE);
		}else{
			viewHolder.img.setVisibility(View.GONE);
		}
		
		
		
//		if(list.get(position).getEndTime()==0){
//			viewHolder.endTime.setVisibility(View.GONE);
//		}else{
//		Calendar ca=Calendar.getInstance();
//		ca.setTimeInMillis(list.get(position).getEndTime());
//		viewHolder.endTime.setText(ca.getTimeInMillis()+"");
//		viewHolder.endTime.setVisibility(list.get(position).getEndTime()>=System.currentTimeMillis()?View.VISIBLE:View.GONE);
		//}
		//viewHolder.img.setImageResource(android.R.);
		viewHolder.itemCheck.setVisibility(mCheckable ? View.VISIBLE: View.GONE);
		viewHolder.itemCheck.setChecked(((ListView) parent).isItemChecked(position));
		if(onPopupMenuItemClick!=null){
		viewHolder.pop.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					onPopupMenuItemClick.ItemClick(v,position);
				}
			
		});
		}
		
		return view;
	}

}
