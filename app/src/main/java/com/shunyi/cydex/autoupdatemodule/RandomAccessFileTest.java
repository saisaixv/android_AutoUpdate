package com.shunyi.cydex.autoupdatemodule;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by saisai on 2017/6/13.
 */

public class RandomAccessFileTest {

    String TAG=getClass().getSimpleName();

    Handler handler=new Handler(){

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    totalSize = msg.arg1;
                    Double v = totalSize * 1.0 / slice;
                    totalNum = v.intValue()+1;

                    downloadListener.onStarted();
                    download();
                    break;
                case 1:
                    break;
                case 2:
                    break;
                case 3:
                    break;
            }
        }
    };

    DownloadListener downloadListener;

    private int totalSize;
    private InputStream is;
    private int slice=1024*1024;
    private String savePath;
    int totalNum=0;
    String urlPath;

    public void request(final String urlPath, String savePath,DownloadListener listener){

        this.downloadListener=listener;
        this.savePath = savePath;
        this.urlPath=urlPath;

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    URL url=new URL(urlPath);
                    HttpURLConnection conn= (HttpURLConnection) url.openConnection();

                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(5000);
//                    conn.setRequestProperty("Range", "bytes=" + count*length
//                            + "-" + (count+1)*length + "");// 设置一般请求属性 范围
                    conn.connect();

                    if(conn.getResponseCode()==200){

                        int contentLength = conn.getContentLength();
                        Log.e(TAG,"contentLength = "+contentLength);
                        handler.obtainMessage(0,contentLength,0).sendToTarget();
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    private void download(){

        int count=0;
        while (count<totalNum){
            down(count++);
        }
    }

    private void down(final int i){

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    URL url=new URL(urlPath);
                    HttpURLConnection conn= (HttpURLConnection) url.openConnection();

                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(5000);
//                    if(i==0){
//
//                    }else {
                        conn.setRequestProperty("Range", "bytes=" + i * slice
                                + "-" + ((i + 1) * slice-1) + "");// 设置一般请求属性 范围
//                    }
                    int contentLength = conn.getContentLength();

                    Log.e(TAG,"第"+i+"段长度 = "+contentLength);
                    conn.connect();


                    Log.e(TAG,"第"+i+"段 resultCode = "+conn.getResponseCode());
//                    if(conn.getResponseCode()==200){
                        Log.e(TAG,"第"+i+"段开始下载");
                        RandomAccessFile ras=new RandomAccessFile(new File(savePath),"rwd");
                        ras.seek(i*slice);
                        InputStream is = conn.getInputStream();
                        byte[] buf=new byte[1024];

                        int len=-1;
                        int length=0;

                        while ((len=(is.read(buf)))!=-1){

                            ras.write(buf,0,len);
//                            length+=len;
                        }

                        Log.e(TAG,"第"+i+"段结束");
                        ras.close();

                        is.close();

//                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    Log.e(TAG,e.getMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG,e.getMessage());
                }
            }
        }).start();
    }
}
