package com.gracecode.android.presentation.task;

import android.os.AsyncTask;
import com.gracecode.android.presentation.Huaban;
import com.gracecode.android.presentation.helper.FileHelper;
import com.gracecode.android.presentation.util.PstManager;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import java.io.File;

public class DownloadPstTask extends AsyncTask<String, Integer, Integer> {
    private final PstManager mManager;
    private final DownloadListener mListener;
    private DefaultHttpClient mHttpClient;

    public abstract interface DownloadListener {
        public abstract void onStart();

        public abstract void onProgressUpdate();

        public abstract void onError(Exception e);

        public abstract void onFinished();
    }


    public DownloadPstTask(PstManager manager, DownloadListener listener) {
        mManager = manager;
        mListener = listener;
    }


    @Override
    protected void onPreExecute() {
        mHttpClient = new DefaultHttpClient();

        HttpParams httpParams = mHttpClient.getParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, Huaban.TIMEOUT);
        HttpConnectionParams.setSoTimeout(httpParams, Huaban.TIMEOUT);
        HttpProtocolParams.setUserAgent(httpParams, Huaban.USER_AGENT);

        mListener.onStart();
    }


    @Override
    protected Integer doInBackground(String... urls) {
        int downloaded = 0;
        for (int i = 0, size = urls.length; i < size; i++) {
            String url = urls[i];
            HttpGet getRequest;
            HttpResponse httpResponse;
            HttpEntity entity;
            try {
                getRequest = new HttpGet(url);
                getRequest.setHeader("Accept-Encoding", "gzip, deflate");
                getRequest.setHeader("Referer", Huaban.URL_HUABAN);

                httpResponse = mHttpClient.execute(getRequest);

                File cacheFile = new File(mManager.getDownloadFileName(url));
                int statusCode = httpResponse.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.SC_OK) {
                    entity = httpResponse.getEntity();
                    if (FileHelper.putFileContent(cacheFile, entity.getContent())) {
                        downloaded++;
                    }

                } else {
                    getRequest.abort();
                    cacheFile.delete();
                    mListener.onError(new RuntimeException());
                }
            } catch (Exception e) {
                mListener.onError(e);
            }
        }

        return downloaded;
    }


    @Override
    protected void onProgressUpdate(Integer... values) {
//        for (Integer value : values) {
//            mNotificationCompat
//                    //.setTicker(value + "/" + urls.size())
//                    .setProgress(urls.size(), value, false);
//            mNotificationManager.notify(DOWNLOAD_NOTIFY_ID, mNotificationCompat.build());
//        }

        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Integer result) {
        mListener.onFinished();
//        String text = String.format(mContext.getString(R.string.downloaded_offline_image_complete), result);
//        mNotificationCompat.setContentTitle(mContext.getString(R.string.app_name))
//                .setContentText(text)
//                .setTicker(text)
//                .setSmallIcon(R.drawable.ic_launcher);
//
//        if (result != 0) {
//            mNotificationCompat.setProgress(0, 0, false);
//            mNotificationManager.notify(DOWNLOAD_NOTIFY_ID, mNotificationCompat.build());
//        }
//
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } finally {
//            mNotificationManager.cancel(DOWNLOAD_NOTIFY_ID);
//            mDatabase.close();
//        }
//
//        super.onPostExecute(result);
    }

    @Override
    protected void onCancelled() {

    }
}
