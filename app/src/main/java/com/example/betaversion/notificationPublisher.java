package com.example.betaversion;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class notificationPublisher extends BroadcastReceiver {


    private static final String OPEN_ACTION = "openAction";

    public void onReceive (Context context , Intent intent) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "remainderCH")
                .setContentText(intent.getStringExtra("Content"))
                .setSubText(intent.getStringExtra("SubText"))
                .setContentTitle(intent.getStringExtra("Title"))
                .setChannelId("remainderCH")
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setSmallIcon(R.drawable.ic_round_speaker_notes, Color.BLACK);
        if (!intent.getStringExtra("audioContent").isEmpty()){
            Intent openActionIntent = new Intent(context, NotificationActionService.class).setAction(OPEN_ACTION);
            builder.addAction(new NotificationCompat.Action(R.drawable.ic_play_arrow,"", PendingIntent.getService(context, intent.getIntExtra("index",0),
                    openActionIntent, PendingIntent.FLAG_ONE_SHOT)));
        }

        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        manager.notify(intent.getIntExtra("index",0), builder.build());
    }

    public static class NotificationActionService extends IntentService {
        public NotificationActionService() {
            super(NotificationActionService.class.getSimpleName());
        }

        @Override
        protected void onHandleIntent(Intent intent) {
            String action = intent.getAction();
            if (OPEN_ACTION.equals(action)) {
                Intent si = new Intent(this, reminderActivity.class);
                si.putExtra("audioContent", intent.getStringExtra("audioContent"));
                si.addFlags(FLAG_ACTIVITY_NEW_TASK);
                startActivity(si);
            }
        }
    }
}
