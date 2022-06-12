package com.example.betaversion;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class notificationPublisher extends BroadcastReceiver {

    private static final String PLAY_ACTION = "playAction";
    private static final String DISPLAY_ACTION = "displayAction";

    // on Receive from the activities
    public void onReceive (Context context, Intent intent) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, intent.getStringExtra("channel"))
                .setContentText(intent.getStringExtra("ContentText"))
                .setSubText(intent.getStringExtra("SubText"))
                .setContentTitle(intent.getStringExtra("ContentTitle"))
                .setChannelId(intent.getStringExtra("channel"))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setSmallIcon(R.drawable.ic_round_speaker_notes, Color.BLACK);
        if (intent.getStringExtra("channel").equals("remindersChannel")){
            if (!intent.getStringExtra("audioContent").isEmpty()){
                Intent openActionIntent = new Intent(context, NotificationActionService.class).setAction(PLAY_ACTION);
                builder.addAction(new NotificationCompat.Action(R.drawable.ic_play_arrow,"נגן הקלטה", PendingIntent.getService(context, intent.getIntExtra("index",0),
                        openActionIntent, PendingIntent.FLAG_ONE_SHOT)));
            } else{
                Intent openActionIntent = new Intent(context, NotificationActionService.class).setAction(DISPLAY_ACTION);
                builder.addAction(new NotificationCompat.Action(R.drawable.ic_play_arrow,"פתח", PendingIntent.getService(context, intent.getIntExtra("index",0),
                        openActionIntent, PendingIntent.FLAG_ONE_SHOT)));
            }
        }

        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        manager.notify(intent.getIntExtra("index",0), builder.build());
    }

    public static class NotificationActionService extends IntentService {
        public NotificationActionService() {
            super(NotificationActionService.class.getSimpleName());
        }
        // the extra button on the notification
        @Override
        protected void onHandleIntent(Intent intent) {
            String action = intent.getAction();
            if (PLAY_ACTION.equals(action)) {
                Intent si = new Intent(this, remindersActivity.class);
                si.putExtra("playFromNotification",true);
                Toast.makeText(this, "TRY TO PLAY!!", Toast.LENGTH_SHORT).show();
                si.putExtra("ContentTitle",intent.getStringExtra("ContentTitle"));
                si.putExtra("audioContent", intent.getStringExtra("audioContent"));
                si.addFlags(FLAG_ACTIVITY_NEW_TASK);
                startActivity(si);
            } else if (DISPLAY_ACTION.equals(action)){
                Intent si = new Intent(this, remindersActivity.class);
                Toast.makeText(this, "TRY TO OPEN!!", Toast.LENGTH_SHORT).show();
                si.addFlags(FLAG_ACTIVITY_NEW_TASK);
                startActivity(si);
            }
        }
    }
}
