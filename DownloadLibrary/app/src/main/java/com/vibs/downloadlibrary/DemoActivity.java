package com.vibs.downloadlibrary;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.vibs.downloadlibrary.core.DownloadData;
import com.vibs.downloadlibrary.core.DownloadManager;
import com.vibs.downloadlibrary.core.DownloadType;
import com.vibs.downloadlibrary.core.RequestType;

import org.w3c.dom.Text;

import java.util.ArrayList;


public class DemoActivity extends ActionBarActivity implements View.OnClickListener {

    private Spinner spinnerDownloadLibDummyLinks;

    private ArrayList<String> arrDownloadList = new ArrayList<String>();

    private TextView tvDownloadLibraryOutput;

    private Button btnDownload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        spinnerDownloadLibDummyLinks = (Spinner) findViewById(R.id.spinnerDownloadLibDummyLinks);
        String[] arrayLinks = getResources().getStringArray(R.array.download_library_links);
        for (String link : arrayLinks) {
            arrDownloadList.add(link);
        }
        ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrDownloadList);
        spinnerDownloadLibDummyLinks.setAdapter(stringArrayAdapter);
        tvDownloadLibraryOutput = (TextView) findViewById(R.id.tvDownloadLibraryOutput);
        btnDownload = (Button) findViewById(R.id.btnDownload);
        btnDownload.setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_demo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {
        DownloadManager.getInstance().initialize(this, new DownloadManager.IDownloadLib() {
            @Override
            public void receiveTotalDownloadLength(int totalLength) {

            }

            @Override
            public void receiveDownloadProgress(final int progress) {
                //tvDownloadLibraryOutput.setText("Progress: " + progress);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvDownloadLibraryOutput.setText("Progress: " + progress);
                    }
                });
                Log.i("Vibs", "Progress: " + progress);
            }

            @Override
            public void downloadResult(final DownloadData downloadData) {
                //tvDownloadLibraryOutput.append(downloadData.getDownloadResultString());
                Log.i("Vibs", "Result: " + downloadData.getDownloadResultJSONObject().toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvDownloadLibraryOutput.append("\nResult: " + downloadData.getDownloadResultJSONObject().toString());
                    }
                });
            }
        });
        DownloadData downloadData = new DownloadData();
        downloadData.setDownloadLocation(spinnerDownloadLibDummyLinks.getSelectedItem().toString());
        downloadData.setRequestType(RequestType.POST);
        downloadData.setDownloadType(DownloadType.TYPE_JSON);
        DownloadManager.getInstance().beginDownload(downloadData);
    }
}
