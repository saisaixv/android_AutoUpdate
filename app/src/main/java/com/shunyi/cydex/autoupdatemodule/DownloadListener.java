package com.shunyi.cydex.autoupdatemodule;

/**
 * Created by saisai on 2017/6/12.
 */

public interface DownloadListener {

    public void onStarted();
    public void onProgressChanged(int progress, String downloadUrl);
    public void onFinished(float completeSize, String downloadUrl);
    public void onFailure();
}
