package com.vibs.downloadlibrary.core;

/**
 * This enumeration describes the type of download to be performed.
 * Created by vgoswami on 3/23/15.
 */
public enum DownloadType {
    /**
     * If the download type is a string.
     */
    TYPE_STRING,
    /**
     * If the download type is a JSON object.
     */
    TYPE_JSON,
    /**
     * If the download type is a JSON array.
     */
    TYPE_JSON_ARRAY,
    /**
     * If the download type is a file.
     * If the download type is File/Image, please specify download save location {@link DownloadData#setDownloadSaveLocation(String)}.
     */
    TYPE_FILE,
    /**
     * If the download type is an image.
     * If the download type is File/Image, please specify download save location {@link DownloadData#setDownloadSaveLocation(String)}.
     */
    TYPE_IMAGE,
    /**
     * If the download type is not defined, it will be assumed as a String.
     */
    TYPE_DEFAULT
}
