package com.example.samsung.smartpcsys.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import com.example.samsung.smartpcsys.resourcepool.Tasks;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class Utils {
//    MainActivity activity = new MainActivity();
    /**
     * Read bytes from a File into a byte[].
     *
     * @param file The File to read.
     * @return A byte[] containing the contents of the File.
     * @throws IOException Thrown if the File is too long to read or couldn't be
     * read fully.
     */
    public static String TAG = "Utils";

    public byte[] readBytesFromFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);

        // Get the size of the file
        long length = file.length();

        // You cannot create an array using a long type.
        // It needs to be an int type.
        // Before converting to an int type, check
        // to ensure that file is not larger than Integer.MAX_VALUE.
        if (length > Integer.MAX_VALUE) {
            throw new IOException("Could not completely read file " + file.getName() + " as it is too long (" + length + " bytes, max supported " + Integer.MAX_VALUE + ")");
        }

        // Create the byte array to hold the data
        byte[] bytes = new byte[(int) length];

        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }

        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file " + file.getName());
        }

        // Close the input stream and return bytes
        is.close();
        return bytes;
    }

    /**
     * Writes the sp
     *ecified byte[] to the specified File path.
     * @param theFile File Object representing the path to write to.
     * @param bytes   The byte[] of data to write to the File.
     * @throws IOException Thrown if there is problem creating or writing the
     *                     File.
     */
    public static void writeBytesToFile(File theFile, byte[] bytes) throws IOException {
        BufferedOutputStream bos = null;

        try {
            FileOutputStream fos = new FileOutputStream(theFile);
            bos = new BufferedOutputStream(fos);
            bos.write(bytes);
        } finally {
            if (bos != null) {
                try {
                    //flush and close the BufferedOutputStream
                    bos.flush();
                    bos.close();
                } catch (Exception e) {
                }
            }
        }
    }

    /* Get uri related content real local file path. */
    public String getUriRealPath(Context ctx, Uri uri, int i) {
        String ret = "";

        if (isAboveKitKat()) {
            // Android OS above sdk version 19.
            ret = getUriRealPathAboveKitkat(ctx, uri, i);
        } else {
            // Android OS below sdk version 19
            ret = getImageRealPath(SngltonClass.get().getContentResolver(), uri, null);
        }

        return ret;
    }

    public String getUriRealPathAboveKitkat(Context ctx, Uri uri, int i) {
        String ret = "";

        if (ctx != null && uri != null) {

            if (isContentUri(uri)) {
                if (isGooglePhotoDoc(uri.getAuthority())) {
                    ret = uri.getLastPathSegment();
                } else {
                    ret = getImageRealPath(SngltonClass.get().getContentResolver(), uri, null);
                }
            } else if (isFileUri(uri)) {
                ret = uri.getPath();
            } else if (isDocumentUri(ctx, uri)) {

                // Get uri related document id.
                String documentId = DocumentsContract.getDocumentId(uri);

                // Get uri authority.
                String uriAuthority = uri.getAuthority();

                if (isMediaDoc(uriAuthority)) {
                    String idArr[] = documentId.split(":");
                    if (idArr.length == 2) {
                        // First item is document type.
                        String docType = idArr[0];

                        // Second item is document real id.
                        String realDocId = idArr[1];

                        // Get content uri by document type.
                        Uri mediaContentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                        if ("image".equals(docType)) {
                            mediaContentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                        } else if ("video".equals(docType)) {
                            mediaContentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                        } else if ("audio".equals(docType)) {
                            mediaContentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                        }

                        // Get where clause with real document id.
                        String whereClause = MediaStore.Images.Media._ID + " = " + realDocId;

                        ret = getImageRealPath(SngltonClass.get().getContentResolver(), mediaContentUri, whereClause);
                    }

                } else if (isDownloadDoc(uriAuthority)) {
                    // Build download uri.
                    Uri downloadUri = Uri.parse("content://downloads/public_downloads");

                    // Append download document id at uri end.
                    Uri downloadUriAppendId = ContentUris.withAppendedId(downloadUri, Long.valueOf(documentId));

                    ret = getImageRealPath(SngltonClass.get().getContentResolver(), downloadUriAppendId, null);

                } else if (isExternalStoreDoc(uriAuthority)) {
                    String idArr[] = documentId.split(":");
                    if (idArr.length == 2) {
                        String type = idArr[0];
                        String realDocId = idArr[1];

                        if ("primary".equalsIgnoreCase(type)) {
                            ret = Environment.getExternalStorageDirectory() + "/" + realDocId;
                        }
                    }
                }
            }
        }

        return ret;
    }

    /* Check whether current android os version is bigger than kitkat or not. */
    public boolean isAboveKitKat() {
        boolean ret = false;
        ret = true;
        return ret;
    }

    /* Check whether this uri represent a document or not. */
    public boolean isDocumentUri(Context ctx, Uri uri) {
        boolean ret = false;
        if (ctx != null && uri != null) {
            ret = DocumentsContract.isDocumentUri(ctx, uri);
        }
        return ret;
    }

    /* Check whether this uri is a content uri or not.
     *  content uri like content://media/external/images/media/1302716
     *  */
    public boolean isContentUri(Uri uri) {
        boolean ret = false;
        if (uri != null) {
            String uriSchema = uri.getScheme();
            if ("content".equalsIgnoreCase(uriSchema)) {
                ret = true;
            }
        }
        return ret;
    }

    /* Check whether this uri is a file uri or not.
     *  file uri like file:///storage/41B7-12F1/DCIM/Camera/IMG_20180211_095139.jpg
     * */
    public boolean isFileUri(Uri uri) {
        boolean ret = false;
        if (uri != null) {
            String uriSchema = uri.getScheme();
            if ("file".equalsIgnoreCase(uriSchema)) {
                ret = true;
            }
        }
        return ret;
    }


    /* Check whether this document is provided by ExternalStorageProvider. */
    public boolean isExternalStoreDoc(String uriAuthority) {
        boolean ret = false;

        if ("com.android.externalstorage.documents".equals(uriAuthority)) {
            ret = true;
        }

        return ret;
    }

    /* Check whether this document is provided by DownloadsProvider. */
    public boolean isDownloadDoc(String uriAuthority) {
        boolean ret = false;

        if ("com.android.providers.downloads.documents".equals(uriAuthority)) {
            ret = true;
        }

        return ret;
    }

    /* Check whether this document is provided by MediaProvider. */
    public boolean isMediaDoc(String uriAuthority) {
        boolean ret = false;

        if ("com.android.providers.media.documents".equals(uriAuthority)) {
            ret = true;
        }

        return ret;
    }

    /* Check whether this document is provided by google photos. */
    public boolean isGooglePhotoDoc(String uriAuthority) {
        boolean ret = false;

        if ("com.google.android.apps.photos.content".equals(uriAuthority)) {
            ret = true;
        }

        return ret;
    }

    /* Return uri represented document file real local path.*/
    public String getImageRealPath(ContentResolver contentResolver, Uri uri, String whereClause) {
        String ret = "";

        // Query the uri with condition.
        Cursor cursor = contentResolver.query(uri, null, whereClause, null, null);

        if (cursor != null) {
            boolean moveToFirst = cursor.moveToFirst();
            if (moveToFirst) {

                // Get columns name by uri type.
                String columnName = MediaStore.Images.Media.DATA;

                if (uri == MediaStore.Images.Media.EXTERNAL_CONTENT_URI) {
                    columnName = MediaStore.Images.Media.DATA;
                } else if (uri == MediaStore.Audio.Media.EXTERNAL_CONTENT_URI) {
                    columnName = MediaStore.Audio.Media.DATA;
                } else if (uri == MediaStore.Video.Media.EXTERNAL_CONTENT_URI) {
                    columnName = MediaStore.Video.Media.DATA;
                }

                // Get column index.
                int imageColumnIndex = cursor.getColumnIndex(columnName);

                // Get column value which is the uri related file local path.
                ret = cursor.getString(imageColumnIndex);
            }
        }

        return ret;
    }

    public static boolean copyFile(InputStream inputStream, OutputStream out) {
        byte buf[] = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
            out.close();
            inputStream.close();
        } catch (IOException e) {
            Log.d("DDDDX", e.toString());
            return false;
        }
        return true;
    }


    public static String stringFromStream(InputStream is) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line = null;

            while ((line = reader.readLine()) != null)
                sb.append(line).append("\n");
            reader.close();
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String stringFromFile(File f) throws IOException {
        FileInputStream fis = new FileInputStream(f);
        String str = stringFromStream(fis);
        fis.close();
        return str;
    }

    public static void writeToFile(File f, String str) throws IOException {
        FileOutputStream fos = new FileOutputStream(f);
        fos.write(str.getBytes());
        fos.close();

    }

    public static void writeToFile(Context context, String fileName, String str) {
        try {
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            fos.write(str.getBytes(), 0, str.length());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String stringFromAsset(Context context, String assetFileName) {
        AssetManager am = context.getAssets();
        try {
            InputStream is = am.open(assetFileName);
            String result = Utils.stringFromStream(is);
            is.close();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String toJSon(Tasks tasks) {
        try {
            //Converting JAVA Object to JSON
            JSONObject jsonTaskObject = new JSONObject();
            jsonTaskObject.put("taskID", tasks.getTaskID());
            jsonTaskObject.put("sourceAddress", tasks.getSourceAddress());
            jsonTaskObject.put("priority", tasks.getPriority());
            jsonTaskObject.put("status", tasks.getStatus());

            //Converting JAVA Object to JSONArray
            JSONArray jsonTaskArray = new JSONArray();
            for (Tasks.TaskFiles taskFiles : tasks.getTaskFilesList()) {
                JSONObject taskFilesObj = new JSONObject();
                taskFilesObj.put("taskFileName", taskFiles.getTaskFileName());
                taskFilesObj.put("taskFileType", taskFiles.getTaskFileType());
                taskFilesObj.put("taskFileSize", taskFiles.getTaskFileSize());
                taskFilesObj.put("taskFileLocation", taskFiles.getCodeLocation());
                taskFilesObj.put("linesOfCode", taskFiles.getLoc());
                jsonTaskArray.put(taskFilesObj);
            }

            JSONArray jsonDataArray = new JSONArray();
            for (Tasks.DataFiles dataFiles : tasks.getDataFilesList()) {
                JSONObject dataFilesObj = new JSONObject();
                dataFilesObj.put("dataFileName", dataFiles.getInDataFileName());
                dataFilesObj.put("dataFileType", dataFiles.getInDataType());
                dataFilesObj.put("dataFileSize", dataFiles.getInDataSize());
                dataFilesObj.put("dataFileLocation", dataFiles.getInDataLocation());
                jsonDataArray.put(dataFilesObj);
            }

            jsonTaskObject.put("TaskFileDetails", jsonTaskArray);
            jsonTaskObject.put("DataFileDetails", jsonDataArray);

            writeToFile(jsonTaskObject.toString(), tasks.getTaskID());
            return String.valueOf(Log.e(TAG, "JSON: " + jsonTaskObject.toString()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void save(Context context, String jsonString, int taskID) throws IOException {
        File rootFolder = context.getExternalFilesDir(null);
        File jsonFile = new File(rootFolder, "metadata_" + taskID + ".json");
        FileWriter writer = new FileWriter(jsonFile);
        writer.write(jsonString);
        writer.close();
        //or IOUtils.closeQuietly(writer);
    }

    public void writeFileOnInternalStorage(Context mcoContext, String sFileName, String sBody) {
        File file = new File(mcoContext.getFilesDir(), "mydir");
        if (!file.exists()) {
            file.mkdir();
        }

        try {
            File gpxfile = new File(file, sFileName);
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(sBody);
            writer.flush();
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public static String readFromFile(int id) {
        String ret = "";
        String filePath = Environment.getExternalStorageDirectory() + "/SmartPCSys/MetaData/";
        try {
            FileInputStream fis = new FileInputStream(filePath + "metaData_" + id + ".json");
            DataInputStream dis = new DataInputStream(fis);
            BufferedReader br = new BufferedReader(new InputStreamReader(dis));
            String receiveString = "";
            StringBuilder stringBuilder = new StringBuilder();

            while ((receiveString = br.readLine()) != null) {
                stringBuilder.append(receiveString);
            }

            ret = stringBuilder.toString();
            dis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    private static void writeToFile(String json, int id) {
        try {
            String filePath = Environment.getExternalStorageDirectory() + "/SmartPCSys/MetaData/";
            File mData = new File(filePath + "metaData_" + id + ".json");
            if (mData.exists()) {
                mData.delete();
            }
            mData.createNewFile();
            FileOutputStream fos = new FileOutputStream(mData);
            fos.write(json.getBytes());
            fos.flush();
            fos.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
}

//        String ret = "";
//        InputStream inputStream = null;
//        try {
//            inputStream = SngltonClass.get().getApplicationContext().openFileInput("metadata_"+id+".json");
//
//            if ( inputStream != null ) {
//                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
//                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//                String receiveString = "";
//                StringBuilder stringBuilder = new StringBuilder();
//
//                while ( (receiveString = bufferedReader.readLine()) != null ) {
//                    stringBuilder.append(receiveString);
//                }
//
//                ret = stringBuilder.toString();
//            }
//        }
//        catch (FileNotFoundException e) {
//            Log.e(TAG, "File not found: " + e.toString());
//        } catch (IOException e) {
//            Log.e(TAG, "Can not read file: " + e.toString());
//        }
//        finally {
//            try {
//                if (inputStream != null) {
//                    inputStream.close();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        return ret;
//    }

//    FileWriter fileWriter = new FileWriter(mData);
//            fileWriter.write(json);
//            fileWriter.flush();
//            fileWriter.close();
//            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(SngltonClass.get().getApplicationContext().openFileOutput("metadata_"+id+".json", Context.MODE_PRIVATE));
//            outputStreamWriter.write(json);
//            outputStreamWriter.close();