package com.fortunekidew.pewaad.helpers.Files;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.webkit.MimeTypeMap;

import com.fortunekidew.pewaad.R;
import com.fortunekidew.pewaad.api.APIService;
import com.fortunekidew.pewaad.api.FilesDownloadService;
import com.fortunekidew.pewaad.app.AppConstants;
import com.fortunekidew.pewaad.app.EndPoints;
import com.fortunekidew.pewaad.app.PewaaApplication;
import com.fortunekidew.pewaad.helpers.AppHelper;
import com.fortunekidew.pewaad.helpers.PreferenceManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Abderrahim El imame on 6/12/16.
 *
 * @Email : abderrahim.elimame@gmail.com
 * @Author : https://twitter.com/bencherif_el
 */

public class FilesManager {


    /**
     * ********************************************************************************* ************************************************
     * *************************************************** Methods to create  Files path ************************************************
     * **********************************************************************************************************************************
     */

    /**
     * method to create root  directory
     *
     * @return root directory
     */
    private static File getMainPath(Context mContext) {

        // External sdcard location
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), mContext.getApplicationContext().getString(R.string.app_name));
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                AppHelper.LogCat("Oops! Failed create " + mContext.getApplicationContext().getString(R.string.app_name) + " directory");
                return null;
            }
        }

        return mediaStorageDir;
    }

    /**
     * method to create root images directory
     *
     * @return all images directory
     */
    private static File getImagesPath(Context mContext) {

        // External sdcard location
        File mediaStorageDir = new File(getMainPath(mContext), PewaaApplication.getAppContext().getString(R.string.app_name) + " " + PewaaApplication.getAppContext().getString(R.string.images_directory));
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                AppHelper.LogCat("Oops! Failed create " + PewaaApplication.getAppContext().getString(R.string.app_name) + " directory");
                return null;
            }
        }

        return mediaStorageDir;
    }

    /**
     * method to create image profile directory
     *
     * @return image profile directory
     */
    private static File getImagesProfilePath(Context mContext) {

        // External sdcard location
        File mediaStorageDir = new File(getMainPath(mContext), PewaaApplication.getAppContext().getString(R.string.app_name) + " " + PewaaApplication.getAppContext().getString(R.string.images_profile_directory));
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                AppHelper.LogCat("Oops! Failed create " + PewaaApplication.getAppContext().getString(R.string.app_name) + " directory");
                return null;
            }
        }

        return mediaStorageDir;
    }


    /**
     * method to create image profile directory
     *
     * @return image profile directory
     */
    private static File getImagesOtherPath(Context mContext) {

        // External sdcard location
        File mediaStorageDir = new File(getMainPath(mContext), PewaaApplication.getAppContext().getString(R.string.app_name) + " " + PewaaApplication.getAppContext().getString(R.string.images_other_directory));
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                AppHelper.LogCat("Oops! Failed create " + PewaaApplication.getAppContext().getString(R.string.app_name) + " directory");
                return null;
            }
        }

        return mediaStorageDir;
    }


    private static File getVideosPath(Context mContext) {

        // External sdcard location
        File mediaStorageDir = new File(getMainPath(mContext), PewaaApplication.getAppContext().getString(R.string.app_name) + " " + PewaaApplication.getAppContext().getString(R.string.videos_directory));
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                AppHelper.LogCat("Oops! Failed create " + PewaaApplication.getAppContext().getString(R.string.app_name) + " directory");
                return null;
            }
        }

        return mediaStorageDir;
    }

    private static File getAudiosPath(Context mContext) {

        // External sdcard location
        File mediaStorageDir = new File(getMainPath(mContext), PewaaApplication.getAppContext().getString(R.string.app_name) + " " + PewaaApplication.getAppContext().getString(R.string.audios_directory));
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                AppHelper.LogCat("Oops! Failed create " + PewaaApplication.getAppContext().getString(R.string.app_name) + " directory");
                return null;
            }
        }

        return mediaStorageDir;
    }


    private static File getDocumentsPath(Context mContext) {

        // External sdcard location
        File mediaStorageDir = new File(getMainPath(mContext), PewaaApplication.getAppContext().getString(R.string.app_name) + " " + PewaaApplication.getAppContext().getString(R.string.documents_directory));
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                AppHelper.LogCat("Oops! Failed create " + PewaaApplication.getAppContext().getString(R.string.app_name) + " directory");
                return null;
            }
        }

        return mediaStorageDir;
    }
    /**
     * ********************************************************************************* ************************************************
     * *************************************************** Methods to get Files absolute path string ************************************
     * **********************************************************************************************************************************
     */

    /**
     * @return Images profile path string
     */
    private static String getImagesProfilePathString(Context mContext) {
        return String.valueOf(getImagesProfilePath(mContext));
    }

    /**
     * @return Images group path string
     */
    private static String getImagesGroupPathString(Context mContext) {
        return String.valueOf(getImagesProfilePath(mContext));
    }

    /**
     * @return other Images path string
     */
    private static String getImagesOtherPathString(Context mContext) {
        return String.valueOf(getImagesOtherPath(mContext));
    }

/**
 * ********************************************************************************* ************************************************
 * *************************************************** Methods to Check if Files exists *********************************************
 * **********************************************************************************************************************************
 */

    /**
     * Check file if exists method
     *
     * @param Id this is the first parameter isFileImagesProfileExists method
     * @return Boolean
     */
    public static boolean isFileImagesProfileExists(Context mContext,String Id) {

        File file = new File(getImagesProfilePathString(mContext), Id);
        return file.exists();
    }

    /**
     * Check file if exists method
     *
     * @param Id       this is the first parameter isFileImagesExists method
     * @param mContext this is the second parameter isFileImagesExists method
     * @return Boolean
     */
    public static boolean isFileImagesExists(Context mContext, String Id) {
        File file = new File(getImagesPathString(mContext), Id);
        return file.exists();
    }

    /**
     * Check file if exists method
     *
     * @param Id this is the first parameter isFileImagesOtherExists method
     * @return Boolean
     */
    public static boolean isFileImagesOtherExists(Context mContext, String Id) {
        File file = new File(getImagesOtherPathString(mContext), Id);
        return file.exists();
    }

    /**
     * Check file if exists method
     *
     * @param Path this is the first parameter isFileVideosExists method
     * @return Boolean
     */
    public static boolean isFileRecordExists(String Path) {
        File file = new File(Path);
        return file.exists();
    }


    /**
     * ********************************************************************************* ************************************************
     * *************************************************** Methods to get Files *********************************************************
     * **********************************************************************************************************************************
     */

    /**
     * method to get file
     *
     * @param Identifier this is first parameter of getFileImageProfile method

     * @return file
     */
    public static File getFileImageProfile(Context mContext, String Identifier) {
        return new File(getFileProfilePhotoPath(mContext, Identifier));
    }

    /**
     * ********************************************************************************* ************************************************
     * *************************************************** Methods to get Files Paths (use those methods in other classes) **************
     * **********************************************************************************************************************************
     */

    public static String getDataCached(String Identifier) {
        return String.format("Data-%s", Identifier);
    }

    public static String getProfileImage(String Identifier) {
        return String.format("IMG-Profile-%s", Identifier + ".jpg");
    }

    /**
     * **************************************************************** *****************************************************************
     * *************************************************** Methods to get String Paths **************************************************
     * **********************************************************************************************************************************
     */

    /**
     * @param Identifier this is parameter of getFileImagesSentPath method
     * @return String path
     */
    public static String getFileDataCachedPath(Context mContext, String Identifier) {
        return String.format(getDataCachedPathString(mContext) + File.separator + "Data-%s", Identifier);
    }

    /**
     * @param Identifier this is parameter of getFileImagesSentPath method
     * @return String path
     */
    public static String getFileProfilePhotoPath(Context mContext, String Identifier) {
        return String.format(getProfilePhotosPathString(mContext) + File.separator + "IMG-Profile-%s", Identifier + ".jpg");
    }


    /**
     * @param Identifier this is first parameter of getFileImagesPath method
     * @return String path
     */
    public static String getFileImagesPath(Context mContext, String Identifier) {
        return String.format(getImagesPathString(mContext) + File.separator + "IMG-%s", Identifier + ".jpg");
    }

    /**
     * @param Identifier this is parameter of getFileImagesSentPath method
     * @return String path
     */
    public static String getFileImagesSentPath(Context mContext, String Identifier) {
        return String.format(getImagesSentPathString(mContext) + File.separator + "IMG-%s", Identifier + ".jpg");
    }

    /**
     * @param mContext
     * @return thumbnail Videos  path string
     */
    private static String getVideosThumbnailPathString(Context mContext) {
        return String.valueOf(getVideosThumbnailPath(mContext));
    }


    /**
     * @param mContext
     * @return String path
     */
    public static String getFileThumbnailPath(Context mContext) {
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        return String.format(getVideosThumbnailPathString(mContext) + File.separator + "THUMB_%s", timeStamp + ".jpg");
    }
    /**
     * **************************************************************** *****************************************************************
     * *************************************************** Methods to get downloads files ***********************************************
     * **********************************************************************************************************************************
     * */


    /**
     * method to do
     *
     * @param mContext   this is the first parameter downloadFilesToDevice method
     * @param fileUrl    this is the second parameter downloadFilesToDevice method
     * @param Identifier this is the third parameter downloadFilesToDevice method
     */
    public static void downloadFilesToDevice(Context mContext, String fileUrl, String Identifier, String type) {

        APIService apiService = new APIService(mContext);
        final FilesDownloadService downloadService = apiService.RootService(FilesDownloadService.class, PreferenceManager.getToken(mContext), EndPoints.ASSETS_BASE_URL);

        new AsyncTask<Void, Long, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                Call<ResponseBody> call = downloadService.downloadSmallFileSizeUrlSync(fileUrl);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            AppHelper.LogCat("server contacted and has file");
                               try{
                                   writeResponseBodyToDisk(mContext, response.body(), Identifier, type);
                               }catch (Exception e){
                                   AppHelper.LogCat("file download was a failed");
                               }


                           //AppHelper.LogCat("file download was a success? " + writtenToDisk);
                        } else {
                            AppHelper.LogCat("server contact failed");
                        }

                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        AppHelper.LogCat("download failed " + t.getMessage());
                    }


                });

                return null;
            }
        }.execute();
    }

    /* --------------------------  received fies       ---------------------------------------*/

    /**
     * method to get file
     *
     * @param Identifier this is  parameter of getFileImage method
     * @return file
     */
    public static File getFileImage(Context mContext, String Identifier) {
        return new File(getFileImagesPath(mContext, Identifier));
    }

    /**
     * @param mContext
     * @return sent Images path string
     */
    private static String getImagesSentPathString(Context mContext) {
        return String.valueOf(getImagesSentPath(mContext));
    }

    /**
     * @param mContext
     * @return Images path string
     */
    public static String getImagesPathString(Context mContext) {
        return String.valueOf(getImagesPath(mContext));
    }

    /**
     * @param mContext
     * @return Images profile path string
     */
    public static String getProfilePhotosPathString(Context mContext) {
        return String.valueOf(getProfilePhotosPath(mContext));
    }

    /**
     * @param mContext
     * @return Images profile path string
     */
    public static String getDataCachedPathString(Context mContext) {
        return String.valueOf(getImagesCachePath(mContext));
    }

    /* --------------------------  cached fies       ---------------------------------------*/

    /**
     * method to create thumb videos  directory
     *
     * @param mContext
     * @return return value
     */
    private static File getVideosThumbnailPath(Context mContext) {

        // External sdcard location
        File mediaStorageDir = new File(getVideosPath(mContext), mContext.getApplicationContext().getString(R.string.app_name) + " " + mContext.getApplicationContext().getString(R.string.video_thumbnail));
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                AppHelper.LogCat("Oops! Failed create " + mContext.getApplicationContext().getString(R.string.app_name) + " directory");
                return null;
            }
        }

        return mediaStorageDir;
    }

    /**
     * method to create cached images directory
     *
     * @param mContext
     * @return return value
     */
    public static File getImagesCachePath(Context mContext) {

        // External sdcard location
        File mediaStorageDir = new File(mContext.getCacheDir(), mContext.getApplicationContext().getString(R.string.app_name) + "_" + mContext.getApplicationContext().getString(R.string.data_cache_directory));
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                AppHelper.LogCat("Oops! Failed create " + mContext.getApplicationContext().getString(R.string.app_name) + "_" + mContext.getApplicationContext().getString(R.string.data_cache_directory) + " directory");
                return null;
            }
        }

        return mediaStorageDir;
    }

    /**
     * method to create images profile directory
     *
     * @param mContext
     * @return return value
     */
    private static File getProfilePhotosPath(Context mContext) {

        // External sdcard location
        File mediaStorageDir = new File(getMainPath(mContext), mContext.getApplicationContext().getString(R.string.app_name) + " " + mContext.getApplicationContext().getString(R.string.profile_photos));
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                AppHelper.LogCat("Oops! Failed create " + mContext.getApplicationContext().getString(R.string.app_name) + " directory");
                return null;
            }
        }

        return mediaStorageDir;
    }

    /**
     * Check file if exists method
     *
     * @param Id       this is the first parameter isFileImagesSentExists method
     * @param mContext this is the second parameter isFileImagesSentExists method
     * @return Boolean
     */
    public static boolean isFileImagesSentExists(Context mContext, String Id) {
        File file = new File(getImagesSentPathString(mContext), Id);
        return file.exists();
    }

    /**
     * @param body       this is the first parameter writeResponseBodyToDisk method
     * @param Identifier this is the first parameter writeResponseBodyToDisk method
     * @return boolean
     */
    private static boolean writeResponseBodyToDisk(Context mContext, ResponseBody body, String Identifier, String type) {
        boolean deleted = true;

        if (!deleted) {
            AppHelper.LogCat(" not deleted ");
            return false;
        } else {
            AppHelper.LogCat("deleted");
            File downloadedFile = null;
            switch (type) {
                case AppConstants.DATA_CACHED:
                    downloadedFile = new File(getFileDataCachedPath(mContext, Identifier));
                    break;
            }

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

            /*long fileSize = body.contentLength();
            long fileSizeDownloaded = 0;*/

                inputStream = body.byteStream();
                try {
                    if (downloadedFile != null) {
                        outputStream = new FileOutputStream(downloadedFile);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                while (true) {
                    int read = 0;
                    try {
                        read = inputStream.read(fileReader);
                    } catch (java.io.IOException e) {
                        e.printStackTrace();
                    }

                    if (read == -1) {
                        break;
                    }

                    try {
                        if (outputStream != null) {
                            outputStream.write(fileReader, 0, read);
                        }
                    } catch (java.io.IOException e) {
                        e.printStackTrace();
                    }

                /*fileSizeDownloaded += read;*/

                /*AppHelper.LogCat("file download: " + fileSizeDownloaded + " of " + fileSize);*/
                }

                try {
                    if (outputStream != null) {
                        outputStream.flush();
                    }
                } catch (java.io.IOException e) {
                    e.printStackTrace();
                }

                return true;
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (java.io.IOException e) {
                        e.printStackTrace();
                    }
                }

                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (java.io.IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * method to create sent images  directory
     *
     * @param mContext
     * @return return value
     */
    private static File getImagesSentPath(Context mContext) {

        // External sdcard location
        File mediaStorageDir = new File(getImagesPath(mContext), mContext.getApplicationContext().getString(R.string.app_name) + " " + mContext.getApplicationContext().getString(R.string.directory_sent));
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                AppHelper.LogCat("Oops! Failed create " + mContext.getApplicationContext().getString(R.string.app_name) + " directory");
                return null;
            }
        }

        return mediaStorageDir;
    }

    /**
     * method to get mime type of files
     *
     * @param url
     * @return
     */
    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }


    public static String getFileSize(long size) {
        if (size <= 0) return "0";
        final String[] units = new String[]{"B", "kB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }


    public static File getFileThumbnail(Context mContext, Bitmap bmp) throws java.io.IOException {
        File file = new File(getFileThumbnailPath(mContext));
        file.getParentFile().mkdirs();
        FileOutputStream out = new FileOutputStream(file);
        bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
        out.close();
        return file;
    }

    /**
     * Check file if exists method
     *
     * @param Id       this is the first parameter isFileDocumentsSentExists method
     * @param mContext this is the second parameter isFileDocumentsSentExists method
     * @return Boolean
     */
    public static boolean isFileDataCachedExists(Context mContext, String Id) {

        File file = new File(getDataCachedPathString(mContext), Id);
        return file.exists();
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri     The Uri to query.
     */
    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    private static String getDataColumn(Context context, Uri uri, String selection,
                                        String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * method to get sent file
     *
     * @param Identifier this is  parameter of getFileImageSent method
     * @return file
     */
    public static File getFileDataCached(Context mContext, String Identifier) {
        return new File(getFileDataCachedPath(mContext, Identifier));
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static void copyFile(File src, File dst) throws java.io.IOException {
        FileInputStream fileInputStream = new FileInputStream(src);
        FileOutputStream fileOutputStream = new FileOutputStream(dst);
        byte[] var4 = new byte[1024];

        int var5;
        while ((var5 = fileInputStream.read(var4)) > 0) {
            fileOutputStream.write(var4, 0, var5);
        }
        fileInputStream.close();
        fileOutputStream.close();
    }

    public static String getImage(String Identifier) {
        return String.format("IMG-%s", Identifier + ".jpg");
    }
}
