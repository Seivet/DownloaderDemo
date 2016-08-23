package com.geekband.Test;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Administrator on 2016/6/27.
 */
public class MainActivity extends Activity implements View.OnClickListener {

    public static final String GEEK_BAND = "GeekBand";
    private Button mDownloadButton;
    private EditText mDownloadET;
    private TextView mDownloadTV;
    private ProgressBar mDownloadProgress;
    private static final String APK_URL = "http://download.sj.qq.com/upload/connAssitantDownload/upload/MobileAssistant_1.apk";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDownloadET = (EditText) findViewById(R.id.download_et);
        mDownloadButton = (Button) findViewById(R.id.download_button);
        mDownloadProgress = (ProgressBar) findViewById(R.id.download_progress);
        mDownloadTV = (TextView) findViewById(R.id.download_tv);

        mDownloadButton.setOnClickListener(this);

        // 提供默认url 供测试用
        mDownloadET.setText(APK_URL);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.download_button:

                if(mDownloadET.getText() != null ) {
                    String APKUrl = mDownloadET.getText().toString();

                    new DownloadAsyncTask().execute(APKUrl);
                }else {
                    Toast.makeText(MainActivity.this, "URL不能为空", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    /**
     * UI更新进度条部分：
     * 使用AsyncTask提供的onProgressUpdate()方法来实现，
     * onProgressUpdate()方法 由AsyncTask另一个方法publishProgress()激活，
     * publishProgress(progress)的progress参数传递给onProgressUpdate()；
     */
    class DownloadAsyncTask extends AsyncTask<String, Integer, String> {

        // 在子线程运行
        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                URLConnection urlConnection = url.openConnection();

                InputStream inputStream = urlConnection.getInputStream();

                int contentLength = urlConnection.getContentLength();

                String downloadFoldersName = Environment.getExternalStorageDirectory() + File.separator + GEEK_BAND + File.separator;
                File file = new File(downloadFoldersName);
                if (!file.exists()) {
                    file.mkdir();
                }

                String fileName = downloadFoldersName + params[0].substring(params[0].lastIndexOf("/") + 1);;
                File apkFile = new File(fileName);
                if (apkFile.exists()) {
                    apkFile.delete();
                }

                int downloadSize = 0;

                byte[] bytes = new byte[1024];
                int length = 0;

                OutputStream outputStream = new FileOutputStream(fileName);

                while ((length = inputStream.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, length);
                    downloadSize += length;
                    int progress = downloadSize * 100 / contentLength;

                    // 更新UI
                    publishProgress(progress); //AsyncTask里的方法,把progress参数传递给下面的onProgressUpdate()

                }
                inputStream.close();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        // 主线程运行
        @Override
        protected void onProgressUpdate(Integer... values) {
            mDownloadProgress.setProgress(values[0]);
            mDownloadTV.setText("下载进度:" + values[0]);
            if (values[0] == 100) {
                Toast.makeText(MainActivity.this, "下载成功", Toast.LENGTH_SHORT).show();
            }
        }

    }

}
