package com.mynotead.md.alarm;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.Context;
import android.app.NotificationManager;
import android.app.Notification;
import android.widget.Toast;
import com.mynotead.md.R;
import android.app.PendingIntent;
import com.mynotead.md.MainActivity;
import android.graphics.Color;

public class AlarmReceiver extends BroadcastReceiver
{

	@Override
	public void onReceive(Context context, Intent intent) {
		int id=0;
		id=intent.getIntExtra("id",id);
		Intent i=new Intent(context,MainActivity.class);//用于点击通知跳转回主界面
		PendingIntent pi=PendingIntent.getActivity(context,0,i,0);
		NotificationManager notificationManager=(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);//取得通知服务
		Notification.Builder notify=new Notification.Builder(context);
		notify.setSmallIcon(R.mipmap.ic_launcher)		//设置图标
		.setAutoCancel(true)		//点击通知后取消
		.setContentTitle("您有新事项待处理")		//标题
		.setContentText(intent.getStringExtra("notifycontent"))
		.setContentIntent(pi)//跳转
		.setDefaults(Notification.DEFAULT_ALL)
		.setPriority(Notification.PRIORITY_MAX);//设置通知等级
		notify.setLights(Color.BLUE,2000,2500);//设置指示灯及闪烁时长
		Notification notification=notify.build();
		notification.ledOnMS=2000;
		notification.ledOffMS=2500;
		notification.ledARGB=Color.BLUE;
		notification.flags=Notification.FLAG_SHOW_LIGHTS;
		notification.flags=Notification.FLAG_AUTO_CANCEL;
		notificationManager.notify(id,notification);
		Toast.makeText(context,"您有新事项待处理",Toast.LENGTH_SHORT).show();
	}
	
}
