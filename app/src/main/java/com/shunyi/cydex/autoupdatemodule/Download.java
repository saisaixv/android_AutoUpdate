package com.shunyi.cydex.autoupdatemodule;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by saisai on 2017/6/13.
 */

public class Download implements Runnable {

    String TAG=getClass().getSimpleName();

    String path;
    String savePath;
    int startPoint;
    int endPoint;
    int currentLength=0;
    public Download(String path,String savePath,int startPoint,int endPoint){

        Log.e(TAG,"startPoint = "+startPoint+"    endPoint = "+endPoint);
        this.path=path;
        this.savePath=savePath;
        this.startPoint=startPoint;
        this.endPoint=endPoint;
    }
    @Override
    public void run() {

        HttpURLConnection conn=null;
        RandomAccessFile ras=null;
        InputStream is=null;

        try {
            URL url=new URL(path);
            conn= (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setRequestProperty("Range","bytes=" + startPoint
                    + "-" + endPoint + "");// 设置一般请求属性 范围
            conn.connect();

            ras=new RandomAccessFile(savePath,"rwd");
            ras.seek(startPoint);
            is = conn.getInputStream();
            byte[] buf=new byte[1024];
            int length=-1;

            while ((length=(is.read(buf)))!=-1){
                ras.write(buf,0,length);
                currentLength+=length;
            }

            ras.close();
            is.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(is!=null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(ras!=null){
                try {
                    ras.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if(conn!=null){
                conn.disconnect();
            }
        }
    }
}
