package com.vibs.downloadlibrary.core;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * This class will act both as an input and an output.
 *
 * Created by vgoswami on 3/23/15.
 */
public class DownloadData {

    /*============================================================ Constants ============================================================*/


	/*============================================================ Variables ============================================================*/

    /**
     * The type of the download to be performed.
     */
    private DownloadType downloadType;

    /**
     * Location from where the download will begin.
     */
    private String downloadLocation;

    /**
     * The result of the download.
     */
    private ByteArrayOutputStream downloadResult = new ByteArrayOutputStream();

    /**
     * If the download type is File/Image, please specify this field.
     */
    private String downloadSaveLocation;

    /**
     * If the download type is JSON
     */
    private JSONObject downloadResultJSONObject;

    /**
     * If the download type is String
     */
    private String downloadResultString;

    /**
     * If the download type is JSON Array
     */
    private JSONArray downloadResultJSONArray;

    /**
     * Request type
     */
    private RequestType requestType;

    /**
     * Write data to server based on request type
     */
    private List<NameValuePair> nameValuePairDataToServer;

	/*=========================================================== Constructors ==========================================================*/

    /**
     * Default constructor for initializing the download data object.
     * @author Vibhor
     */
    public DownloadData() {
    }


	/*========================================================== Public Methods =========================================================*/


    /**
     * Get the download type to be used.
     * @return the downloadType
     * @author Vibhor
     */
    public DownloadType getDownloadType() {
        return downloadType;
    }

    /**
     * Set the download type.
     * @param downloadType the downloadType to set
     * @author Vibhor
     */
    public void setDownloadType(DownloadType downloadType) {
        this.downloadType = downloadType;
    }


    /**
     * Get the download location, from where the download began.
     * @return the downloadLocation
     * @author Vibhor
     */
    public String getDownloadLocation() {
        return downloadLocation;
    }


    /**
     * Set the location from where the download has to begin.
     * @param downloadLocation the downloadLocation to set
     * @author Vibhor
     */
    public void setDownloadLocation(String downloadLocation) {
        this.downloadLocation = downloadLocation;
    }


    /**
     * @return the downloadResult
     * @author Vibhor
     */
    public ByteArrayOutputStream getDownloadResult() {
        return downloadResult;
    }


    /**
     * Set the result of the download.
     * @param downloadResult the downloadResult to set
     * @author Vibhor
     */
    public void setDownloadResult(ByteArrayOutputStream downloadResult) {
        this.downloadResult = downloadResult;
    }


    /**
     * Get the download location.
     * @return the downloadSaveLocation
     * @author Vibhor
     */
    public String getDownloadSaveLocation() {
        return downloadSaveLocation;
    }


    /**
     * If the download type is File/Image, please specify this field.
     * @param downloadSaveLocation the downloadSaveLocation to set
     * @author Vibhor
     */
    public void setDownloadSaveLocation(String downloadSaveLocation) {
        this.downloadSaveLocation = downloadSaveLocation;
    }

    /**
     * Get the downloaded JSON Object.
     * @return JSONObject
     * @author Vibhor
     */
    public JSONObject getDownloadResultJSONObject() {
        return downloadResultJSONObject;
    }


    /**
     * Set the download JSON Object
     * @param downloadResultJSONObject - the JSON Object result.
     * @author Vibhor
     */
    public void setDownloadResultJSONObject(JSONObject downloadResultJSONObject) {
        this.downloadResultJSONObject = downloadResultJSONObject;
    }


    /**
     * Get the download result string.
     * @return the downloadResultString
     * @author Vibhor
     */
    public String getDownloadResultString() {
        return downloadResultString;
    }


    /**
     * Set the download result string.
     * @param downloadResultString the downloadResultString to set
     * @author Vibhor
     */
    public void setDownloadResultString(String downloadResultString) {
        this.downloadResultString = downloadResultString;
    }


    /**
     * Get the download result JSON Array.
     * @return the downloadResultJSONArray
     * @author Vibhor
     */
    public JSONArray getDownloadResultJSONArray() {
        return downloadResultJSONArray;
    }


    /**
     * Set the download result JSON Array.
     * @param downloadResultJSONArray the downloadResultJSONArray to set
     * @author Vibhor
     */
    public void setDownloadResultJSONArray(JSONArray downloadResultJSONArray) {
        this.downloadResultJSONArray = downloadResultJSONArray;
    }


    /**
     * Get the request type.
     * @return the requestType
     * @author Vibhor
     */
    public RequestType getRequestType() {
        return requestType;
    }


    /**
     * Set the request type.
     * @param requestType the requestType to set
     * @author Vibhor
     */
    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }


    /**
     * @return the nameValuePairDataToServer
     */
    public List<NameValuePair> getNameValuePairDataToServer() {
        return nameValuePairDataToServer;
    }


    /**
     * @param nameValuePairDataToServer the nameValuePairDataToServer to set
     */
    public void setNameValuePairDataToServer(
            List<NameValuePair> nameValuePairDataToServer) {
        this.nameValuePairDataToServer = nameValuePairDataToServer;
    }


}
