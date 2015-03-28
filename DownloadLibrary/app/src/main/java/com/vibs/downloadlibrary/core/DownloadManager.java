package com.vibs.downloadlibrary.core;

import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URI;

/**
 * This is the library class which provides the user accessibility in downloading
 * specific files/JSON objects depending on requirements.
 *
 * Created by vgoswami on 3/23/15.
 */
public class DownloadManager {

    /*============================================================ Constants ============================================================*/

    /**
     * Tag for logging.
     */
    private static final String tag = "DownloadLibrary-Vibhor";

    /**
     * Notification ID
     */
    public static final int DOWNLOAD_NOTIFICATION_ID = 321585;


    private static final int DOWNLOAD_UPDATE_PROGRESS = 1;

    private static final int DOWNLOAD_COMPLETED = 2;

	/*============================================================ Variables ============================================================*/

    /**
     * Context
     */
    private Context appContext;

    /**
     * Notification Manager
     */
    private static NotificationManager mNotificationManager;

    /**
     * Notification compatible builder object.
     */
    private static NotificationCompat.Builder mNotificationCompatBuilder;

    /**
     * The object of the Asynchronous downloader class.
     */
    @SuppressWarnings("unused")
    private AsyncDownloaderClass mAsyncDownloaderClass = new AsyncDownloaderClass();

    /**
     * The download interface.
     * @author Vibhor
     */
    private static IDownloadLib iDownloadInterface;

    /**
     * The response download data.
     * @author Vibhor
     */
    private static DownloadData mResponseDownloadData = new DownloadData();


    /**
     * Download thread.
     * @author Vibhor
     */
    private DownloadThreadWithPost mDownloadThreadWithPost = null;

    /**
     * Download thread.
     * @author Vibhor
     */
    private DownloadThreadWithGet mDownloadThreadWithGet = null;

    /**
     * Download Handler
     * @author Vibhor
     */
    private DownloadHandler mDownloadHandler = new DownloadHandler();


    private static DownloadManager mDownloadManager;



	/*=========================================================== Constructors ==========================================================*/

    /**
     * Private constructor
     */
    private DownloadManager() {}

    /**
     * Initializing the library instance. Please use initialize method only once.
     *
     * @author Vibhor
     */
    public static DownloadManager getInstance() {
        if (mDownloadManager == null) {
            mDownloadManager = new DownloadManager();
        }
        return mDownloadManager;
    }

    /**
     *
     * @param context - the calling application context.
     * @param downloadInterface - the interface implemented for receiving download.
     */
    public void initialize(Context context, IDownloadLib downloadInterface) {
        setAppContext(context);
        iDownloadInterface = downloadInterface;
        initNotificationManager();
    }

	/*========================================================== Private Classes ========================================================*/

    /**
     * Asynchronous class for downloading data of required type.
     * @author Vibhor
     */
    private class AsyncDownloaderClass extends AsyncTask<DownloadData, Integer, DownloadData[]> {

        private static final int MAX_PROG = 100;

        @Override
        protected void onPreExecute() {
            mNotificationCompatBuilder = new NotificationCompat.Builder(getAppContext());
            mNotificationCompatBuilder
                    .setContentTitle("")
                    .setContentText("")
                    .setOngoing(true)
                    .setAutoCancel(false);
            getNotificationManager().notify(DOWNLOAD_NOTIFICATION_ID, mNotificationCompatBuilder.build());
        }

        @Override
        protected DownloadData[] doInBackground(DownloadData... params) {
            DownloadData[] maDownloadData = new DownloadData[2];
            maDownloadData[0] = params[0];
            maDownloadData[1] = new DownloadData();
            try {
                // Create a HTTP client for performing HTTP download.
                HttpClient httpclient = new DefaultHttpClient();
                // Create a HTTP Post
                HttpPost httppost = new HttpPost(new URI(maDownloadData[0].getDownloadLocation()));
                // Execute the http post.
                HttpResponse httpresponse = httpclient.execute(httppost);
                // Sleep for 500 milliseconds.
                Thread.sleep(500);
                HttpEntity entity = httpresponse.getEntity();
                InputStream inputStream = entity.getContent();
                int totalLength = (int) entity.getContentLength();
                if (inputStream == null) {
                    Log.e(tag, "No data received from " + maDownloadData[0].getDownloadLocation());
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    baos.write("No data received from server.".getBytes());
                    maDownloadData[1].setDownloadResult(baos);
                    return maDownloadData;
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                //variable to store total downloaded bytes
                int downloadedSize = 0;
                //create a buffer...
                byte[] buffer = new byte[1024];
                //create a buffer value...
                int bufferLength = 0;

                // Publish total length
                if (iDownloadInterface != null) {
                    iDownloadInterface.receiveTotalDownloadLength(totalLength);
                }

                // publish the progress
                publishProgress(downloadedSize);

                //used to store a temporary size of the buffer
                //now, read through the input buffer and write the contents to the file
                while ( (bufferLength = inputStream.read(buffer)) > 0  ) {
                    baos.write(buffer, 0, bufferLength);
                    //add up the size so we know how much is downloaded
                    downloadedSize += bufferLength;
                    publishProgress(downloadedSize);
                }
                maDownloadData[1].setDownloadResult(baos);
                baos.close();
            }
            catch (Exception ex) {
                Log.e(tag, "Exception in downloading data. " + ex.getMessage());
                maDownloadData[1].setDownloadResult(null);
                return maDownloadData;
            }
            return maDownloadData;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            if (iDownloadInterface != null) {
                iDownloadInterface.receiveDownloadProgress(values[0]);
            }
            mNotificationCompatBuilder.setProgress(MAX_PROG, values[0], false);
            getNotificationManager().notify(MAX_PROG, mNotificationCompatBuilder.build());
        }

        @Override
        protected void onPostExecute(DownloadData[] result) {
            try {
                DownloadData inputDownloadData = result[0];
                DownloadData mDownloadedData = result[1];

                switch (mDownloadedData.getDownloadType()) {

                    default:
                    case TYPE_DEFAULT:
                        break;
                    case TYPE_STRING:
                        mDownloadedData.setDownloadResultString(new String(mDownloadedData.getDownloadResult().toByteArray()));
                        break;

                    case TYPE_FILE:
                    case TYPE_IMAGE:
                        mDownloadedData.setDownloadLocation(inputDownloadData.getDownloadLocation());
                        File file = new File(inputDownloadData.getDownloadSaveLocation());
                        FileOutputStream fos = new FileOutputStream(file);
                        fos.write(mDownloadedData.getDownloadResult().toByteArray());
                        fos.flush();
                        fos.close();
                        break;

                    case TYPE_JSON:
                        JSONObject jsonObject = new JSONObject(new String(mDownloadedData.getDownloadResult().toByteArray()));
                        mDownloadedData.setDownloadResultJSONObject(jsonObject);
                        break;

                    case TYPE_JSON_ARRAY:
                        JSONArray jsonArray = new JSONArray(new String(mDownloadedData.getDownloadResult().toByteArray()));
                        mDownloadedData.setDownloadResultJSONArray(jsonArray);
                        break;

                }
                mDownloadedData.setDownloadType(inputDownloadData.getDownloadType());

            }
            catch (Exception ex) {
                Log.e(tag, "Exception in providing data.");
            }
            mNotificationCompatBuilder.setProgress(0, 0, false);
            getNotificationManager().notify(MAX_PROG, mNotificationCompatBuilder.build());
        }

    }

    /**
     * Download Thread
     * @author Vibhor
     */
    private class DownloadThreadWithPost extends Thread {

        private DownloadData inputDownloadData;

        public DownloadThreadWithPost(DownloadData downloadData) {
            inputDownloadData = downloadData;
        }

        @Override
        public void run() {
            try {
                // Create a HTTP client for performing HTTP download.
                HttpClient httpclient = new DefaultHttpClient();
                // Create a HTTP Post
                HttpPost httppost = new HttpPost(new URI(inputDownloadData.getDownloadLocation()));
                if (inputDownloadData.getNameValuePairDataToServer() != null) {
                    httppost.setEntity(new UrlEncodedFormEntity(inputDownloadData.getNameValuePairDataToServer()));
                }
                // Execute the http post.
                HttpResponse httpresponse = httpclient.execute(httppost);
                // Sleep for 500 milliseconds.
                Thread.sleep(500);
                HttpEntity entity = httpresponse.getEntity();
                InputStream inputStream = entity.getContent();
                int totalLength = (int) entity.getContentLength();
                if (inputStream == null) {
                    Log.e(tag, "No data received from " + inputDownloadData.getDownloadLocation());
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    baos.write("No data received from server.".getBytes());
                    mResponseDownloadData.setDownloadResult(baos);
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                //variable to store total downloaded bytes
                int downloadedSize = 0;
                //create a buffer...
                byte[] buffer = new byte[1024];
                //create a buffer value...
                int bufferLength = 0;

                // Publish total length
                if (iDownloadInterface != null) {
                    iDownloadInterface.receiveTotalDownloadLength(totalLength);
                }

                // publish the progress
                if (iDownloadInterface != null) {
                    iDownloadInterface.receiveDownloadProgress(downloadedSize);
                }
                Thread.sleep(1000);

                //used to store a temporary size of the buffer
                //now, read through the input buffer and write the contents to the file
                while ( (bufferLength = inputStream.read(buffer)) > 0  ) {
                    baos.write(buffer, 0, bufferLength);
                    //add up the size so we know how much is downloaded
                    downloadedSize += bufferLength;
                    if (iDownloadInterface != null) {
                        iDownloadInterface.receiveDownloadProgress(downloadedSize);
                    }
                    mDownloadHandler.obtainMessage(DOWNLOAD_UPDATE_PROGRESS, totalLength, downloadedSize).sendToTarget();
                }
                mResponseDownloadData.setDownloadResult(baos);
                baos.close();

                switch (inputDownloadData.getDownloadType()) {

                    default:
                    case TYPE_DEFAULT:
                        break;
                    case TYPE_STRING:
                        mResponseDownloadData.setDownloadResultString(new String(mResponseDownloadData.getDownloadResult().toByteArray()));
                        break;

                    case TYPE_FILE:
                    case TYPE_IMAGE:
                        mResponseDownloadData.setDownloadLocation(inputDownloadData.getDownloadLocation());
                        File file = new File(inputDownloadData.getDownloadSaveLocation());
                        FileOutputStream fos = new FileOutputStream(file);
                        fos.write(mResponseDownloadData.getDownloadResult().toByteArray());
                        fos.flush();
                        fos.close();
                        break;

                    case TYPE_JSON:
                        JSONObject jsonObject = new JSONObject(new String(mResponseDownloadData.getDownloadResult().toByteArray()));
                        mResponseDownloadData.setDownloadResultJSONObject(jsonObject);
                        break;

                    case TYPE_JSON_ARRAY:
                        JSONArray jsonArray = new JSONArray(new String(mResponseDownloadData.getDownloadResult().toByteArray()));
                        mResponseDownloadData.setDownloadResultJSONArray(jsonArray);
                        break;

                }
                mResponseDownloadData.setDownloadType(inputDownloadData.getDownloadType());
                mDownloadHandler.obtainMessage(DOWNLOAD_COMPLETED).sendToTarget();
            }
            catch (Exception ex) {
                Log.e(tag, "Exception in downloading data. " + ex.getMessage());
                mResponseDownloadData.setDownloadResult(null);
            }
        }

    }

    /**
     * Download Thread
     * @author Vibhor
     */
    private class DownloadThreadWithGet extends Thread {

        private DownloadData inputDownloadData;

        public DownloadThreadWithGet(DownloadData downloadData) {
            inputDownloadData = downloadData;
        }

        @Override
        public void run() {
            try {
                // Create a HTTP client for performing HTTP download.
                HttpClient httpclient = new DefaultHttpClient();
                // Create a HTTP Get
                HttpGet httpget = new HttpGet(new URI(inputDownloadData.getDownloadLocation()));
                // Execute the http get.
                HttpResponse httpresponse = httpclient.execute(httpget);
                // Sleep for 500 milliseconds.
                Thread.sleep(500);
                HttpEntity entity = httpresponse.getEntity();
                InputStream inputStream = entity.getContent();
                int totalLength = (int) entity.getContentLength();
                if (inputStream == null) {
                    Log.e(tag, "No data received from " + inputDownloadData.getDownloadLocation());
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    baos.write("No data received from server.".getBytes());
                    mResponseDownloadData.setDownloadResult(baos);
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                //variable to store total downloaded bytes
                int downloadedSize = 0;
                //create a buffer...
                byte[] buffer = new byte[1024];
                //create a buffer value...
                int bufferLength = 0;

                // Publish total length
                if (iDownloadInterface != null) {
                    iDownloadInterface.receiveTotalDownloadLength(totalLength);
                }

                // publish the progress
                if (iDownloadInterface != null) {
                    iDownloadInterface.receiveDownloadProgress(downloadedSize);
                }
                Thread.sleep(1000);

                //used to store a temporary size of the buffer
                //now, read through the input buffer and write the contents to the file
                while ( (bufferLength = inputStream.read(buffer)) > 0  ) {
                    baos.write(buffer, 0, bufferLength);
                    //add up the size so we know how much is downloaded
                    downloadedSize += bufferLength;
                    if (iDownloadInterface != null) {
                        iDownloadInterface.receiveDownloadProgress(downloadedSize);
                    }
                    mDownloadHandler.obtainMessage(DOWNLOAD_UPDATE_PROGRESS, totalLength, downloadedSize).sendToTarget();
                }
                mResponseDownloadData.setDownloadResult(baos);
                baos.close();

                switch (inputDownloadData.getDownloadType()) {

                    default:
                    case TYPE_DEFAULT:
                        break;
                    case TYPE_STRING:
                        mResponseDownloadData.setDownloadResultString(new String(mResponseDownloadData.getDownloadResult().toByteArray()));
                        break;

                    case TYPE_FILE:
                    case TYPE_IMAGE:
                        mResponseDownloadData.setDownloadLocation(inputDownloadData.getDownloadLocation());
                        File file = new File(inputDownloadData.getDownloadSaveLocation());
                        FileOutputStream fos = new FileOutputStream(file);
                        fos.write(mResponseDownloadData.getDownloadResult().toByteArray());
                        fos.flush();
                        fos.close();
                        break;

                    case TYPE_JSON:
                        JSONObject jsonObject = new JSONObject(new String(mResponseDownloadData.getDownloadResult().toByteArray()));
                        mResponseDownloadData.setDownloadResultJSONObject(jsonObject);
                        break;

                    case TYPE_JSON_ARRAY:
                        JSONArray jsonArray = new JSONArray(new String(mResponseDownloadData.getDownloadResult().toByteArray()));
                        mResponseDownloadData.setDownloadResultJSONArray(jsonArray);
                        break;

                }
                mResponseDownloadData.setDownloadType(inputDownloadData.getDownloadType());
                mDownloadHandler.obtainMessage(DOWNLOAD_COMPLETED).sendToTarget();
            }
            catch (Exception ex) {
                Log.e(tag, "Exception in downloading data. " + ex.getMessage());
                mResponseDownloadData.setDownloadResult(null);
            }
        }

    }

    /**
     * Download handler for communicating the download handler.
     * @author vgoswami001
     */
    private static class DownloadHandler extends Handler {

        @Override
        public void handleMessage(Message message) {
            switch (message.what) {

                case DOWNLOAD_UPDATE_PROGRESS:
                    mNotificationCompatBuilder.setProgress(message.arg1, message.arg2, false);
                    getNotificationManager().notify(message.arg1, mNotificationCompatBuilder.build());
                    break;

                case DOWNLOAD_COMPLETED:
                    getNotificationManager().cancel(DOWNLOAD_NOTIFICATION_ID);
                    getNotificationManager().cancelAll();
                    if (iDownloadInterface != null) {
                        iDownloadInterface.downloadResult(mResponseDownloadData);
                    }
                    break;

            }
        }
    }

    /**
     * Interface to update the user of the download.
     * @author Vibhor
     */
    public interface IDownloadLib {

        /**
         * This method posts the total download length.
         * @param totalLength - the total length of the download.
         * @author Vibhor
         */
        public void receiveTotalDownloadLength(int totalLength);

        /**
         * This method receives the download progress from the background
         * download thread.
         * @param progress - the progress of download.
         * @author Vibhor
         */
        public void receiveDownloadProgress(int progress);

        /**
         * This method receives the result of the download.
         * @param downloadData - the {@link DownloadData} object containing the information
         * of download.
         * @author Vibhor
         */
        public void downloadResult(DownloadData downloadData);
    }

	/*========================================================== Public Methods =========================================================*/

    /**
     * Get the calling application context.
     * @return the appContext
     * @author Vibhor
     */
    public Context getAppContext() {
        return appContext;
    }


    /**
     * Start downloading data. Please register {@link com.vibs.downloadlibrary.core.DownloadData} interface for receiving updates on
     * the download.
     * @param downloadData - the download data object which will contain
     * the necessary information for download.
     * @return Returns <b>True</b> if the download has been started, else will return <b>False</b>.
     * @author Vibhor
     */
    public boolean beginDownload(DownloadData downloadData) {
		/*if (mAsyncDownloaderClass != null) {
			mAsyncDownloaderClass.execute(downloadData);
			return true;
		}
		else {
			return false;
		}*/
        if (mDownloadThreadWithPost == null) {
            switch (downloadData.getRequestType()) {

                case GET:
                    mDownloadThreadWithGet = new DownloadThreadWithGet(downloadData);
                    mDownloadThreadWithGet.start();
                    createNotificationAndShow("DownloadManager", downloadData.getDownloadLocation());
                    break;

                case POST:
                default:
                    mDownloadThreadWithPost = new DownloadThreadWithPost(downloadData);
                    mDownloadThreadWithPost.start();
                    createNotificationAndShow("DownloadManager", downloadData.getDownloadLocation());
                    break;
            }

            return true;
        }
        else {
            return false;
        }
    }


	/*========================================================== Private Methods ========================================================*/

    /**
     * Set the calling application context.
     * @param context the application context to set
     * @author Vibhor
     */
    private void setAppContext(Context context) {
        appContext = context;
    }

    /**
     * Initialize the Notification Manager.
     * @author Vibhor
     */
    private void initNotificationManager() {
        mNotificationManager = (NotificationManager) getAppContext().getSystemService(Context.NOTIFICATION_SERVICE);
    }

    /**
     * Get the notification manager for performing notification tasks.
     * @return the mNotificationManager
     */
    private static NotificationManager getNotificationManager() {
        return mNotificationManager;
    }

    /**
     * Create the notification object and show notification.
     * @param title - the title of the notification.
     * @param downloadLocation - the location of the download.
     * @author Vibhor
     */
    private void createNotificationAndShow(String title, String downloadLocation) {
        mNotificationCompatBuilder = new NotificationCompat.Builder(getAppContext());
        mNotificationCompatBuilder
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setContentTitle(title)
                .setContentText("Downloading from " + downloadLocation)
                .setOngoing(true)
                .setAutoCancel(true);
        getNotificationManager().notify(DOWNLOAD_NOTIFICATION_ID, mNotificationCompatBuilder.build());
    }

	/*========================================================= Protected Methods =======================================================*/



}
