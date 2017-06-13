package com.shunyi.cydex.autoupdatemodule;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by saisai on 2017/6/12.
 */

public class DownloadRequest {

    String TAG=getClass().getSimpleName();
    private static DownloadRequest instance;

    public String filePath;
    private int totalSize;
    DownloadListener downloadListener;
    String urlStr;

    private DownloadRequest(){

    }

    public static DownloadRequest getInstance(){
        if(instance==null){
            synchronized (DownloadRequest.class){
                if(instance==null){
                    instance=new DownloadRequest();
                }
            }
        }
        return instance;
    }


    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    int blockSize=1024*1024;

                    int num = (int) (totalSize * 1.0 / blockSize + 1);

                    downloadListener.onStarted();
                    for(int i=0;i<num;i++){
                        cache.add(new Download(urlStr,filePath,i*blockSize,((i+1)*blockSize)-1));
                        new Thread(cache.get(i)).start();
                    }

                    Log.e(TAG,"cachesize = "+cache.size());
                    handler.sendEmptyMessageDelayed(1,500);

                    break;
                case 1:

                    int progress = getProgress();
                    if(progress<100){
                        downloadListener.onProgressChanged(progress,"");
                        handler.sendEmptyMessageDelayed(1,500);
                    }else {
                        handler.removeCallbacksAndMessages(null);
                        downloadListener.onFinished(100,"");
                    }
//                    Log.e(TAG,"progress = "+getProgress());

                    break;
                case 2:
                    break;
            }
        }
    };

    List<Download> cache=new ArrayList<>();

    public void request(final String urlStr, String filePath, DownloadListener listener){

        this.filePath=filePath;
        this.downloadListener=listener;
        this.urlStr=urlStr;
//
//        try {
//            URL url=new URL(urlStr);
//            HttpURLConnection conn= (HttpURLConnection) url.openConnection();
//
//            conn.setRequestMethod("GET");
//            conn.setConnectTimeout(20*1000);
//            conn.setReadTimeout(20*1000);
//            conn.connect();
//
//            totalSize = conn.getContentLength();
//
//            downloadResponseHandler.sendResponseMessage(conn.getInputStream());
//
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                URL url= null;
                try {
                    url = new URL(urlStr);
                    HttpURLConnection conn= (HttpURLConnection) url.openConnection();

                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(5*1000);
                    conn.connect();

                    totalSize = conn.getContentLength();

                    handler.sendEmptyMessage(0);

                } catch (MalformedURLException e) {
                    e.printStackTrace();

                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();

    }

    private int getProgress(){
        int currentLength=0;
        for(Download d:cache){
            currentLength+=d.currentLength;
        }

        return (int) (currentLength * 1.0 / totalSize * 100);
    }


//    public class DownloadResponseHandler{
//
//        private final Handler handler;
//
//        public DownloadResponseHandler(){
//
//            handler = new Handler(Looper.getMainLooper()){
//
//                @Override
//                public void handleMessage(Message msg) {
//
//                }
//            };
//        }

//        public void sendResponseMessage(InputStream is){
//
//            downloadListener.onStarted();
//            RandomAccessFile randomAccessFile=null;
//
//            int completeSize=0;
//            int count=0;
//            try {
//
//                randomAccessFile=new RandomAccessFile(filePath,"rwd");
//
//                byte[] buf=new byte[1024];
//                int length=-1;
//                while ((length=(is.read(buf)))!=-1){
//                    randomAccessFile.write(buf,0,length);
//                    completeSize+=length;
//
//                    if(completeSize<=totalSize){
//
//                        Log.e(TAG,"completeSize = "+completeSize);
//                        Log.e(TAG,"totalSize = "+totalSize);
//                        Log.e(TAG,"percent = "+(int)(Float.parseFloat(getTwoPointFloatStr(completeSize*1.0f/totalSize))*100));
//
//                        count++;
//                        if(count%30==0){
//                            int percent=(int)(Float.parseFloat(getTwoPointFloatStr(completeSize*1.0f/totalSize))*100);
//                            downloadListener.onProgressChanged(percent,urlStr);
//                        }
//                    }
//                }
//
//                downloadListener.onFinished(completeSize,"");
//            } catch (IOException e) {
//                e.printStackTrace();
//                downloadListener.onFailure();
//            }finally {
//                if(randomAccessFile!=null){
//                    try {
//                        randomAccessFile.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                        downloadListener.onFailure();
//                    }
//                }
//            }
//
//        }
//
//        private String getTwoPointFloatStr(float value){
//            DecimalFormat df = new DecimalFormat("0.00000000000");
//            return df.format(value);
//
//        }
//    }




}
