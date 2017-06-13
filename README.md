# android_AutoUpdate
该项目是as项目，下载到本地之后作为 一个 module 导入到 as中即可。

使用：

      Intent intent=new Intent(MainActivity.this,DownloadService.class);
      intent.putExtra("path",url);
      intent.putExtra("savepath", filePath);
      startService(intent);
      
    DownloadService和app不在同一个进程中，所以可以退出app，并不会影响下载，下载完成之后自动提醒安装。
