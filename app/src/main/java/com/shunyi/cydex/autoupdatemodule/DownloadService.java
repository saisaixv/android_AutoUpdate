package com.shunyi.cydex.autoupdatemodule;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;

/**
 * Created by saisai on 2017/6/12.
 */

public class DownloadService extends Service{

    String TAG=getClass().getSimpleName();

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {

            int progress=msg.arg1;
            NotificationCompat.Builder builder = new NotificationCompat.Builder(DownloadService.this);
            builder.setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                    .setContentTitle(getString(R.string.app_name));
            Log.e("notifyUser","progress = "+progress);
            if (progress > 0 && progress <= 100) {

                builder.setProgress(100, progress, false);

            } else {
                builder.setProgress(0, 0, false);
            }
            builder.setAutoCancel(true);
            builder.setWhen(System.currentTimeMillis());

            switch (msg.what){
                case 0:
                    builder.setTicker("update_download_progressing");
                    break;
                case 1:
                    builder.setTicker("update_download_finish");
                    break;
                case 2:
                    builder.setTicker("update_download_failed");
                    break;
                case 3:
                    break;
            }

            builder.setContentIntent(progress >= 100 ? getContentIntent() :
                    PendingIntent.getActivity(DownloadService.this, 0, new Intent(), PendingIntent.FLAG_UPDATE_CURRENT));
            Notification notification = builder.build();
            notificationManager.notify(0, notification);
        }
    };
    private String path;
    private NotificationManager notificationManager;
    private String savepath;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG,"onCreate");
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, final int startId) {

        Log.e(TAG,"onStartCommand");
        path = intent.getStringExtra("path");
        savepath = intent.getStringExtra("savepath");
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {

                final SimpleDateFormat format=new SimpleDateFormat("hh:mm:ss");

                DownloadRequest.getInstance().request(path, savepath,new DownloadListener() {
                    long startTime=0;
                    long finishTime=0;
                    @Override
                    public void onStarted() {

                        startTime=System.currentTimeMillis();
                    }

                    @Override
                    public void onProgressChanged(int progress, String downloadUrl) {

                        handler.obtainMessage(0,progress,0).sendToTarget();
                        Log.e(TAG,"progress = "+progress);
                    }

                    @Override
                    public void onFinished(float completeSize, String downloadUrl) {
                        handler.obtainMessage(1,100,0).sendToTarget();

                        finishTime=System.currentTimeMillis();
                        String format1 = format.format(finishTime - startTime);
                        Log.e(TAG,"finish progress = "+100 +"  durationTimer = "+format1);
                    }

                    @Override
                    public void onFailure() {
                        handler.obtainMessage(2,0,0).sendToTarget();
                    }
                });

//            }
//        }).start();

//        RandomAccessFileTest randomAccessFileTest=new RandomAccessFileTest();
//        randomAccessFileTest.request(path, savepath, new DownloadListener() {
//            @Override
//            public void onStarted() {
//
//            }
//
//            @Override
//            public void onProgressChanged(int progress, String downloadUrl) {
//                Log.e(TAG,"progress = "+progress);
//            }
//
//            @Override
//            public void onFinished(float completeSize, String downloadUrl) {
//
//            }
//
//            @Override
//            public void onFailure() {
//
//            }
//        });

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    /**
     * 进入apk安装程序
     *
     * @return
     */
    private PendingIntent getContentIntent() {
        Log.e("tag", "getContentIntent()");
        try {
            File apkFile = new File(savepath);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if(Build.VERSION.SDK_INT >= 24){
                Log.e(TAG,"SDK = "+Build.VERSION.SDK_INT);

                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(
                        FileProvider.getUriForFile(
                                DownloadService.this,
                                "com.shunyi.cydex.autoupdatemodule",
                                apkFile),"application/vnd.android.package-archive");
            }else {
                Log.e(TAG,"SDK = "+Build.VERSION.SDK_INT);
                intent.setDataAndType(Uri.fromFile(new File(savepath)), "application/vnd.android.package-archive");
            }
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            startActivity(intent);
            return pendingIntent;
        }catch (Exception e){
            Log.e(TAG,e.getMessage());
        }

        return null;
    }

//    void a(){
//        Intent i = new Intent(Intent.ACTION_VIEW);
//        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//// 判断是否是7.0
//        if(Build.VERSION.SDK_INT >= 24){
//            // 适配android7.0 ，不能直接访问原路径
//            // 需要对intent 授权
//            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            Uri uri = FileProvider.getUriForFile(context, "com.shunyi.cydex.fileprovider", file);
//            i.setDataAndType(FileProvider.getUriForFile(context, context.getPackageName() + ".fileProvider",new File(filePath)),"application/vnd.android.package-archive");
//        }
//        else{
//            i.setDataAndType(Uri.fromFile(new File(filePath)), "application/vnd.android.package-archive");
//        }
//        context.startActivity(i);
//    }


    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("tag", "onDestroy()");
    }
}
