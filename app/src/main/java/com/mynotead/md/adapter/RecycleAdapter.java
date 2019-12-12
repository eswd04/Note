package com.mynotead.md.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import com.mynotead.md.MainActivity;
import com.mynotead.md.MyTextView;
import com.mynotead.md.Note;
import com.mynotead.md.R;
import com.mynotead.md.adapter.RecycleAdapter;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.File;

public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ViewHolder> implements ItemTouchHelperAdapter,Filterable {

	@Override
	public Filter getFilter() {
		
		return null;
	}
	
	@Override
	public void onItemMove(int fromPosition, int toPosition) {

	}

	@Override
	public void onItemDissmiss(int position) {
		list.remove(position);
		notifyItemRemoved(position);
	}

	Context context;
	List<Note> list;
	private OnPopClickListener onPopClickListener;
	private OnItemClickLitener mOnItemClickLitener;
	boolean actionMode = false;

	public RecycleAdapter(Context context, List<Note> list) {
		this.context = context;
		this.list = list;
	}

	public interface OnPopClickListener {
		void onItemClick(View view, int position);
	}

	public interface OnItemClickLitener {
        void onItemClick(View view, int position);
        void onItemLongClick(View view , int position);
    }
	public void setOnPopClickListener(OnPopClickListener onPopClickListener) {
		this.onPopClickListener = onPopClickListener;
	}
    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }
	public void setList(List<Note> list) {
		this.list = list;
		//notifyItemRangeChanged(0,list.size());
	}
	
	public void setActionMode(boolean actionMode){
		this.actionMode=actionMode;
	}

	@Override
	public RecycleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int p2) {
		View view =LayoutInflater.from(context).inflate(R.layout.item, parent, false);

		ViewHolder viewHolder=new ViewHolder(view);
		return viewHolder;
	}
	public void mOnMove(int fromPos, int toPos) {
        Note prev = list.remove(fromPos);
        list.add(toPos > fromPos ? toPos - 1 : toPos, prev);
        notifyItemMoved(fromPos, toPos);
    }


    public void mOnSwiped(RecyclerView.ViewHolder viewHolder) {
        list.remove(viewHolder.getAdapterPosition());
		notifyItemRemoved(viewHolder.getAdapterPosition());
    }
	private SpannableString initContent(String str){
		SpannableString ss = new SpannableString(str);
		Pattern p=Pattern.compile("(<img>)([\\s\\S]*?)(</img>)");
		Matcher m=p.matcher(str);
		while(m.find()){
			//String uri=m.group().substring(6,m.group().length()-6);
//			Bitmap bm = BitmapFactory.decodeFile(uri);
//			Bitmap rbm= Bitmap.createScaledBitmap(bm, 50, 50, true);
			ImageSpan span = new ImageSpan(context, R.mipmap.ic_photo_black_24dp);
			ss.setSpan(span, m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			
		}
		return ss;
	}

	@Override
	public void onBindViewHolder(final RecycleAdapter.ViewHolder viewHolder, final int position) {
		
		viewHolder.content.setText(initContent(list.get(position).getContent()));
		viewHolder.content.setVisibility(list.get(position).getContent().equals("") ?View.GONE: View.VISIBLE);
		if (list.get(position).getTitle().equals("")) {
			viewHolder.title.setText("无标题");
		} else {
			viewHolder.title.setText(list.get(position).getTitle());
		}
		if (list.get(position).getTime()==1) {
			viewHolder.img.setVisibility(View.VISIBLE);
		} else {
//			if (list.get(position).getEndTime() != 0) {
//				new NoteDB(context).endtime("", list.get(position).getId(),0);
//			}
			viewHolder.img.setVisibility(View.GONE);
		}
			if (viewHolder.positionSet.contains(position)) {
				viewHolder.itemView.setActivated(true);
				viewHolder.itemCheck.setChecked(true);
				
			} else {
				viewHolder.itemView.setActivated(false);
				viewHolder.itemCheck.setChecked(false);
				
			}
		viewHolder.itemCheck.setVisibility(actionMode?View.VISIBLE: View.GONE);
		viewHolder.pop.setVisibility(actionMode?View.GONE:View.VISIBLE);
		

		if (onPopClickListener != null) {
			viewHolder.pop.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View view) {
						onPopClickListener.onItemClick(view, viewHolder.getAdapterPosition());
					}


				});

		}

		if (mOnItemClickLitener != null) {
            viewHolder.itemCardView.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v) {
						int pos = viewHolder.getLayoutPosition();
						mOnItemClickLitener.onItemClick(viewHolder.itemView, pos);

					}
				});



			viewHolder.itemCardView.setOnLongClickListener(new OnLongClickListener()
				{
					@Override
					public boolean onLongClick(View v) {
						int pos = viewHolder.getLayoutPosition();
						mOnItemClickLitener.onItemLongClick(viewHolder.itemView, pos);
						return true;
					}
				});
		}

	}

	@Override
	public int getItemCount() {
		return list.size();
	}
	public void remove(int position) {
		list.remove(position);
		notifyItemRemoved(position);
	}
	public class ViewHolder extends RecyclerView.ViewHolder {

		MyTextView mt;
		TextView time,content,title;//,endTime;
		CheckBox itemCheck;
		public ImageView img,pop,ivDone,ivSchedule;
		public CardView itemCardView;
		public View itemBack;
		Set<Integer> positionSet;
		public ViewHolder(View view) {
			super(view);
			title = (TextView) view.findViewById(R.id.title);
			time = (TextView) view.findViewById(R.id.time);
			content = (TextView) view.findViewById(R.id.content);
			itemCheck = (CheckBox) view.findViewById(R.id.itemCheckBox);
			img = (ImageView) view.findViewById(R.id.dot);
			pop = (ImageView) view.findViewById(R.id.action);
			itemCardView = (CardView) view.findViewById(R.id.itemCardView);
			positionSet = MainActivity.instance.positionSet;
			itemBack=view.findViewById(R.id.itemLinearLayout);
			ivDone = (ImageView) itemView.findViewById(R.id.iv_done);
            ivSchedule = (ImageView) itemView.findViewById(R.id.iv_schedule);
			//endTime=(TextView) view.findViewById(R.id.endTime);
		}




	}
}
