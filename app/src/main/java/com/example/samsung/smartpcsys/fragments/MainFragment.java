package com.example.samsung.smartpcsys.fragments;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.samsung.smartpcsys.R;
import com.example.samsung.smartpcsys.activities.MainActivity;
import com.example.samsung.smartpcsys.communicationmanager.CommunicationManager;
import com.example.samsung.smartpcsys.discoverynmonitoringmanager.DiscoveryAndMonitoringManager;
import com.example.samsung.smartpcsys.dispatcher.TaskManager;
import com.example.samsung.smartpcsys.resourceallocator.Scheduler;
import com.example.samsung.smartpcsys.resourcepool.Tasks;
import com.example.samsung.smartpcsys.taskqueue.TaskQueue;
import com.example.samsung.smartpcsys.taskqueuemanager.TaskQueueManager;
import com.example.samsung.smartpcsys.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {
    private String TAG = "MainFragment";
    Button btn_slctApp, btn_slctDFile, btn_submit, btn_readFile; //btn_slctMdFile,
    private static final int REQUEST_CODE0 = 0; // onActivityResult request code
    private static final int REQUEST_CODE1 = 1;
    //    private static final int REQUEST_CODE2 = 2;
    private static final int PERMISSION_REQUEST_CODE = 200;
    int nameIndex, sizeIndex;
    public static String fileName;
    private File file;
    Intent intent;
    Uri fileUri;
    TextView tv_appFileName, tv_appFileSize, tv_dataFilesName, tv_dataFileSize; //tv_mdFileName, tv_mdFileSize,
    ListView lv_dFiles;
    TaskQueueManager taskQueueManager = new TaskQueueManager();
    TaskQueue taskQueue = new TaskQueue();
    private Context mContext;
    Utils utils = new Utils();
    MainActivity mainActivity;
    private ArrayList<File> fileList = new ArrayList<File>();
    private ArrayList<String> fileNames = new ArrayList<String>();
    Tasks task = new Tasks();
    private int id = 0;
    ArrayList<Tasks.TaskFiles> taskFilesArrayList = new ArrayList<>();
    ArrayList<Tasks.DataFiles> dataFilesArrayList = new ArrayList<>();
    TaskManager taskManager = new TaskManager();
    String fileLocation = null;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        intent = new Intent();
        fileUri = intent.getData();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }

    public void init(View view) {
        btn_slctApp = view.findViewById(R.id.btn_selectApp);
        tv_appFileName = view.findViewById(R.id.tv_appFileName);
        tv_appFileSize = view.findViewById(R.id.tv_appFileSize);
        btn_slctDFile = view.findViewById(R.id.btn_selectDFiles);
        tv_dataFilesName = view.findViewById(R.id.tv_dFilesName);
        tv_dataFileSize = view.findViewById(R.id.tv_dFilesSize);
        btn_submit = view.findViewById(R.id.btn_submitTask);
        btn_readFile = view.findViewById(R.id.btn_readfile);

        btn_slctApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkPermission()) {
                    requestPermission();
                    openChooser(REQUEST_CODE0);
                } else {
                    Toast.makeText(getActivity(), "Permissions already granted", Toast.LENGTH_LONG).show();
                    openChooser(REQUEST_CODE0);
                }

            }
        });

        btn_slctDFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkPermission()) {
                    requestPermission();
                    openChooser(REQUEST_CODE1);
                } else {
                    Toast.makeText(getActivity(), "Permissions already granted", Toast.LENGTH_LONG).show();
                    openChooser(REQUEST_CODE1);
                }
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                task.setTaskID(id);
                task.setStatus(Tasks.Status.CREATED);
                task.setSourceAddress(CommunicationManager.getInstance().getLocalIpAddr());
                task.setPriority(1);
                task.setTaskFilesList(taskFilesArrayList);
                task.setDataFilesList(dataFilesArrayList);
                taskQueueManager.insertTask(task);
//                Gson gson = new Gson();
//                String jsonString = gson.toJson(task);
//                try {
//                    Utils.save(getActivity(),jsonString);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                Scheduler scheduler = new Scheduler();

                Utils.toJSon(task);

                tv_appFileName.setText("File Name");
                tv_appFileSize.setText("File Size");
                tv_dataFileSize.setText("File Name");
                tv_dataFilesName.setText("File Size");

                Log.e(TAG, "Task Queue Size in mainFragment: " + TaskQueue.taskQueue.size());
                for (int i = 0; i < TaskQueue.taskQueue.size(); i++) {
                    Log.e(TAG, "Task ID: " + TaskQueue.taskQueue.get(i).getTaskID());
                    Log.e(TAG, "Source Address: " + TaskQueue.taskQueue.get(i).getSourceAddress());
                    Log.e(TAG, "Task Status: " + TaskQueue.taskQueue.get(i).getStatus());
                    Log.e(TAG, "Task Priority: " + TaskQueue.taskQueue.get(i).getPriority());
                    for (int j = 0; j < TaskQueue.taskQueue.get(i).getTaskFilesList().size(); j++) {
                        Log.e(TAG, "Task File Name: " + TaskQueue.taskQueue.get(i).getTaskFilesList().get(j).getTaskFileName());
                        Log.e(TAG, "Task File Type: " + TaskQueue.taskQueue.get(i).getTaskFilesList().get(j).getTaskFileType());
                        Log.e(TAG, "Task File Size: " + TaskQueue.taskQueue.get(i).getTaskFilesList().get(j).getTaskFileSize());
                        Log.e(TAG, "Task File Location: " + TaskQueue.taskQueue.get(i).getTaskFilesList().get(j).getCodeLocation());
                        fileLocation = TaskQueue.taskQueue.get(i).getTaskFilesList().get(j).getCodeLocation();
                    }
                    for (int k = 0; k < TaskQueue.taskQueue.get(i).getDataFilesList().size(); k++) {
                        Log.e(TAG, "Data File Name: " + TaskQueue.taskQueue.get(i).getDataFilesList().get(k).getInDataFileName());
                        Log.e(TAG, "Data File Type: " + TaskQueue.taskQueue.get(i).getDataFilesList().get(k).getInDataType());
                        Log.e(TAG, "Data File Size: " + TaskQueue.taskQueue.get(i).getDataFilesList().get(k).getInDataSize());
                        Log.e(TAG, "Data File Location: " + TaskQueue.taskQueue.get(i).getDataFilesList().get(k).getInDataLocation());
                    }
                }
                scheduler.Schedule(task, fileLocation, DiscoveryAndMonitoringManager.nodesList);
                //
                id++;
                Toast.makeText(getActivity(), "Task Submitted Successfully!", Toast.LENGTH_LONG).show();
            }
        });


        btn_readFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.e(TAG,Utils.readFromFile(id-1));
                String json = Utils.readFromFile(id - 1);
                try {
                    JSONObject jsonObject = new JSONObject(json);

                    int taskId = jsonObject.getInt("taskID");
                    String sourceAddress = jsonObject.getString("sourceAddress");
                    String priority = jsonObject.getString("priority");
                    String status = jsonObject.getString("status");
                    Log.e(TAG, "Values in JSON: taskID: " + taskId + ", SourceAddress: " + sourceAddress + ", priority: " + priority + ", status: " + status);

                    JSONArray jsonArray = jsonObject.getJSONArray("TaskFileDetails");
                    String taskFileName, taskFileType, taskFileSize, taskFileLocation;
                    int loc;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonTaskObject = jsonArray.getJSONObject(i);
                        taskFileName = jsonTaskObject.getString("taskFileName");
                        taskFileType = jsonTaskObject.getString("taskFileType");
                        taskFileSize = jsonTaskObject.getString("taskFileSize");
                        taskFileLocation = jsonTaskObject.getString("taskFileLocation");
                        loc = jsonTaskObject.getInt("linesOfCode");

                        Log.e(TAG, "Values in JSONTASKARRAYLIST: TaskFileName: " + taskFileName + ", TaskFileType: " + taskFileType + ", TaskFileSize: " + taskFileSize +
                                ", TaskFileLocation: " + taskFileLocation + ", LineOfCode: " + loc);
                    }

                    JSONArray jsonDataArray = jsonObject.getJSONArray("DataFileDetails");
                    String dataFileName, dataFileType, dataFileSize, dataFileLocation;
                    for (int j = 0; j < jsonDataArray.length(); j++) {
                        JSONObject jsonDataObject = jsonDataArray.getJSONObject(j);
                        dataFileName = jsonDataObject.getString("dataFileName");
                        dataFileType = jsonDataObject.getString("dataFileType");
                        dataFileSize = jsonDataObject.getString("dataFileSize");
                        dataFileLocation = jsonDataObject.getString("dataFileLocation");

                        Log.e(TAG, "Values in JSONDATAARRAYLIST: DataFileName: " + dataFileName + ", DataFileType: " + dataFileType + ", DataFileSize: " + dataFileSize +
                                ", DataFileLocation: " + dataFileLocation);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void openChooser(int i) {
        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
        } else {
            final Intent selectFile = new Intent(Intent.ACTION_GET_CONTENT);
            // The MIME data type filter
            selectFile.setType("*/*");

            // Only return URIs that can be opened with ContentResolver
            selectFile.addCategory(Intent.CATEGORY_OPENABLE);
            if (i == REQUEST_CODE0) {
                selectFile.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(selectFile, REQUEST_CODE0);
            }

            if (i == REQUEST_CODE1) {
                selectFile.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(selectFile, REQUEST_CODE1);
            }
        }
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getActivity(), READ_EXTERNAL_STORAGE);

        return result1 == PackageManager.PERMISSION_GRANTED && result == PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermission() {

        ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()), new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        switch (requestCode) {
            case REQUEST_CODE0:
                // If the file selection was successful
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null) {
                        // Get the URI of the selected file
                        if (data.getClipData() != null) { // checking multiple selection or not
                            ArrayList<Uri> arrList = new ArrayList<Uri>();
                            for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                                Uri documentUri = data.getClipData().getItemAt(i).getUri();
                                arrList.add(documentUri);
                            }
                            ArrayList<String> filePath = new ArrayList<>();
                            for (int j = 0; j < arrList.size(); j++) {
                                Uri uri = arrList.get(j);
//                                Toast.makeText(getActivity(),  "Total Number of File(s) Selected:" + arrList.size() + " File Selected: " + uri, Toast.LENGTH_LONG).show();
                                Log.e(TAG, "Total Number of File(s) Selected:" + arrList.size() + " File Selected: " + uri);
                                filePath.add(getRealPathFromURIPath(uri, Objects.requireNonNull(getActivity()), REQUEST_CODE0));

                            }
                            Log.e(TAG, "FilePAth Size: " + filePath.size());

                        } else {
                            Uri documentUri = data.getData();
                            try {
                                Toast.makeText(getActivity(),
                                        "File Selected: " + documentUri, Toast.LENGTH_LONG).show();
                                //  previewFile(documentUri, REQUEST_CODE2);
                                String filePath = getRealPathFromURIPath(documentUri, Objects.requireNonNull(getActivity()), REQUEST_CODE0);
                                fileLocation = filePath;
                                Log.e(TAG, "FilePath: " + filePath);
                            } catch (Exception e) {
                                Log.e(TAG, "File select error", e);
                            }
                        }
                    }
                }
                break;
            case REQUEST_CODE1:
                // If the file selection was successful
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null) {
                        // Get the URI of the selected file
                        if (data.getClipData() != null) { // checking multiple selection or not
                            ArrayList<Uri> arrList = new ArrayList<Uri>();
                            for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                                Uri documentUri = data.getClipData().getItemAt(i).getUri();
                                arrList.add(documentUri);
                            }
                            ArrayList<String> filePath = new ArrayList<>();
                            for (int j = 0; j < arrList.size(); j++) {
                                Uri uri = arrList.get(j);
//                                Toast.makeText(getActivity(),  "Total Number of File(s) Selected:" + arrList.size() + " File Selected: " + uri, Toast.LENGTH_LONG).show();
                                Log.e(TAG, "Total Number of File(s) Selected:" + arrList.size() + " File Selected: " + uri);
                                filePath.add(getRealPathFromURIPath(uri, Objects.requireNonNull(getActivity()), REQUEST_CODE1));
                            }
                            Log.e(TAG, "FilePAth Size: " + filePath.size());
                        } else {
                            Uri documentUri = data.getData();
                            try {
                                Toast.makeText(getActivity(),
                                        "File Selected: " + documentUri, Toast.LENGTH_LONG).show();
                                //  previewFile(documentUri, REQUEST_CODE2);
                                String filePath = getRealPathFromURIPath(documentUri, Objects.requireNonNull(getActivity()), REQUEST_CODE1);

                                Log.e(TAG, "FilePath: " + filePath);
                            } catch (Exception e) {
                                Log.e(TAG, "File select error", e);
                            }
                        }
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    public static boolean hasRealRemovableSdCard(Context context) {
        return ContextCompat.getExternalFilesDirs(context, null).length >= 2;
    }

    private String getRealPathFromURIPath(Uri contentURI, Activity activity, int i) {
        Cursor cursor = activity.getContentResolver().query(contentURI, null, null, null, null);
        String realPath = "";
        ContentResolver cR = activity.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String type = mime.getExtensionFromMimeType(cR.getType(contentURI)); //
        Log.e(TAG, "Type of file selected: " + type);
        Tasks.TaskFiles taskFiles = new Tasks.TaskFiles();
        Tasks.DataFiles dataFiles = new Tasks.DataFiles();
        if (i == REQUEST_CODE0) {
            if (type != null) {
                if (type.equalsIgnoreCase("jpg") || type.equalsIgnoreCase("jpeg") || type.equalsIgnoreCase("png") || type.equalsIgnoreCase("bmp")
                        || type.equalsIgnoreCase("gif") || type.equalsIgnoreCase("WebP") || type.equalsIgnoreCase("heif")) {
                    Log.e(TAG, "Image File is Selected");
                    taskFiles.setTaskFileType("Image");
                    //task.setTaskFilesList(taskFiles);
                } else if (type.equalsIgnoreCase("mp3") || type.equalsIgnoreCase("midi") || type.equalsIgnoreCase("aac") || type.equalsIgnoreCase("ogg")
                        || type.equalsIgnoreCase("wav") || type.equalsIgnoreCase("mid") || type.equalsIgnoreCase("xmf") || type.equalsIgnoreCase("flac")) {
                    Log.e(TAG, "Audio File is Selected");
                    taskFiles.setTaskFileType("Audio");
                } else if (type.equalsIgnoreCase("mp4") || type.equalsIgnoreCase("h.263") || type.equalsIgnoreCase("3gp") || type.equalsIgnoreCase("h.264")
                        || type.equalsIgnoreCase("mkv") || type.equalsIgnoreCase("webm")) {
                    Log.e(TAG, "Video File is Selected");
                    taskFiles.setTaskFileType("Video");
                } else if (type.equalsIgnoreCase("java") || type.equalsIgnoreCase("cpp") || type.equalsIgnoreCase("py") || type.equalsIgnoreCase("kt")
                        || type.equalsIgnoreCase("js") || type.equalsIgnoreCase("php") || type.equalsIgnoreCase("class") || type.equalsIgnoreCase("c")) {
                    Log.e(TAG, "Code File is Selected");
                    taskFiles.setTaskFileType("Code");
                } else if (type.equalsIgnoreCase("apk")) {
                    Log.e(TAG, "Application File is Selected");
                    taskFiles.setTaskFileType("Application");
                } else if (type.equalsIgnoreCase("xml")) {
                    Log.e(TAG, "Layout File is Selected");
                    taskFiles.setTaskFileType("Layout");
                } else if (type.equalsIgnoreCase("json")) {
                    Log.e(TAG, "MetaData File is Selected");
                    taskFiles.setTaskFileType("MetaData");
                } else if (type.equalsIgnoreCase("doc") || type.equalsIgnoreCase("docx") || type.equalsIgnoreCase("txt") || type.equalsIgnoreCase("xls") ||
                        type.equalsIgnoreCase("xlsx") || type.equalsIgnoreCase("ppt") || type.equalsIgnoreCase("pptx") || type.equalsIgnoreCase("mdb")
                        || type.equalsIgnoreCase("rtf") || type.equalsIgnoreCase("pdf")) {
                    Log.e(TAG, "Document File is Selected");
                    taskFiles.setTaskFileType("Document");
                } else {
                    Log.e(TAG, "System File is Selected");
                    taskFiles.setTaskFileType("SystemFiles");
                }
            } else {
                Log.e(TAG, "System File is Selected");
                taskFiles.setTaskFileType("SystemFiles");
            }
        }

        if (i == REQUEST_CODE1) {
            if (type != null) {
                if (type.equalsIgnoreCase("jpg") || type.equalsIgnoreCase("jpeg") || type.equalsIgnoreCase("png") || type.equalsIgnoreCase("bmp")
                        || type.equalsIgnoreCase("gif") || type.equalsIgnoreCase("WebP") || type.equalsIgnoreCase("heif")) {
                    Log.e(TAG, "Image File is Selected");
                    dataFiles.setInDataType("Image");
                } else if (type.equalsIgnoreCase("mp3") || type.equalsIgnoreCase("midi") || type.equalsIgnoreCase("aac") || type.equalsIgnoreCase("ogg")
                        || type.equalsIgnoreCase("wav") || type.equalsIgnoreCase("mid") || type.equalsIgnoreCase("xmf") || type.equalsIgnoreCase("flac")) {
                    Log.e(TAG, "Audio File is Selected");
                    dataFiles.setInDataType("Audio");
                } else if (type.equalsIgnoreCase("mp4") || type.equalsIgnoreCase("h.263") || type.equalsIgnoreCase("3gp") || type.equalsIgnoreCase("h.264")
                        || type.equalsIgnoreCase("mkv") || type.equalsIgnoreCase("webm")) {
                    Log.e(TAG, "Video File is Selected");
                    dataFiles.setInDataType("Video");
                } else if (type.equalsIgnoreCase("java") || type.equalsIgnoreCase("cpp") || type.equalsIgnoreCase("py") || type.equalsIgnoreCase("kt")
                        || type.equalsIgnoreCase("js") || type.equalsIgnoreCase("php") || type.equalsIgnoreCase("class") || type.equalsIgnoreCase("c")) {
                    Log.e(TAG, "Code File is Selected");
                    dataFiles.setInDataType("Code");
                } else if (type.equalsIgnoreCase("apk")) {
                    Log.e(TAG, "Application File is Selected");
                    dataFiles.setInDataType("Application");
                } else if (type.equalsIgnoreCase("xml")) {
                    Log.e(TAG, "Layout File is Selected");
                    dataFiles.setInDataType("Layout");
                } else if (type.equalsIgnoreCase("json")) {
                    Log.e(TAG, "MetaData File is Selected");
                    dataFiles.setInDataType("MetaData");
                } else if (type.equalsIgnoreCase("doc") || type.equalsIgnoreCase("docx") || type.equalsIgnoreCase("txt") || type.equalsIgnoreCase("xls") ||
                        type.equalsIgnoreCase("xlsx") || type.equalsIgnoreCase("ppt") || type.equalsIgnoreCase("pptx") || type.equalsIgnoreCase("mdb")
                        || type.equalsIgnoreCase("rtf") || type.equalsIgnoreCase("pdf")) {
                    Log.e(TAG, "Document File is Selected");
                    dataFiles.setInDataType("Document");
                } else {
                    Log.e(TAG, "System File is Selected");
                    dataFiles.setInDataType("SystemFiles");
                }
            } else {
                Log.e(TAG, "System File is Selected");
                dataFiles.setInDataType("SystemFiles");
            }
        }


        if (hasRealRemovableSdCard(getActivity())) {
            Log.e(TAG, "SD Card is Present ");
            if (cursor == null) {
                realPath = Environment.getExternalStorageDirectory() + "/" + contentURI.getPath();
            } else {
                cursor.moveToFirst();
                Log.e(TAG, "Cursor size: " + cursor.getCount());
                int idx = cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME);
                File external[] = Objects.requireNonNull(getActivity()).getApplicationContext().getExternalMediaDirs();
                if (external.length > 1) {
                    realPath = external[1].getAbsolutePath();
                    realPath = realPath.substring(0, realPath.indexOf("Android")) + cursor.getString(idx);
                }
                Log.e(TAG, "Real Path: " + realPath);

                //  realPath = Environment.getExternalStorageDirectory() + "/" + cursor.getString(idx);
                nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);

                if (i == 0) {
                    tv_appFileName.setText(cursor.getString(nameIndex));
                    tv_appFileSize.setText(formatSize(cursor.getLong(sizeIndex)));
                    taskFiles.setTaskFileName(cursor.getString(nameIndex));
                    taskFiles.setTaskFileSize(formatSize(cursor.getLong(sizeIndex)));
                    taskFiles.setCodeLocation(realPath);
                    taskFilesArrayList.add(taskFiles);
                }
                if (i == 1) {
                    tv_dataFilesName.setText(cursor.getString(nameIndex));
                    tv_dataFileSize.setText(formatSize(cursor.getLong(sizeIndex)));
                    dataFiles.setInDataFileName(cursor.getString(nameIndex));
                    dataFiles.setInDataSize(formatSize(cursor.getLong(sizeIndex)));
                    dataFiles.setInDataLocation(realPath);
                    dataFilesArrayList.add(dataFiles);
//
                }
            }
        } else {
            Log.e(TAG, "SD Card is not Present ");
            if (cursor == null) {
                Log.e(TAG, "cursor is null ");
                realPath = Environment.getDataDirectory() + "/" + contentURI.getPath();
            } else {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME);
                realPath = Environment.getExternalStorageDirectory() + "/" + cursor.getString(idx);
                Log.e(TAG, "RealPath: " + realPath);
                nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);

                if (i == 0) {
                    Log.e(TAG, "FileName in Cursor: " + cursor.getString(nameIndex));
                    Log.e(TAG, "FileSize in Cursor: " + formatSize(cursor.getLong(sizeIndex)));
                    tv_appFileName.setText(cursor.getString(nameIndex));
                    tv_appFileSize.setText(formatSize(cursor.getLong(sizeIndex)));
                    taskFiles.setTaskFileName(cursor.getString(nameIndex));
                    taskFiles.setTaskFileSize(formatSize(cursor.getLong(sizeIndex)));
                    taskFiles.setCodeLocation(realPath);
                    taskFilesArrayList.add(taskFiles);
                }
//                if (i == 1) {
//                    tv_mdFileName.setText(cursor.getString(nameIndex));
//                    tv_mdFileSize.setText(formatSize(cursor.getLong(sizeIndex)));
//                }
                if (i == 1) {
                    Log.e(TAG, "FileName in Cursor: " + cursor.getString(nameIndex));
                    Log.e(TAG, "FileSize in Cursor: " + formatSize(cursor.getLong(sizeIndex)));
                    tv_dataFilesName.setText(cursor.getString(nameIndex));
                    tv_dataFileSize.setText(formatSize(cursor.getLong(sizeIndex)));
                    dataFiles.setInDataFileName(cursor.getString(nameIndex));
                    dataFiles.setInDataSize(formatSize(cursor.getLong(sizeIndex)));
                    dataFiles.setInDataLocation(realPath);
                    dataFilesArrayList.add(dataFiles);
                }
            }
        }
        if (cursor != null) {
            cursor.close();
        }

        return realPath;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (locationAccepted && cameraAccepted)
                        Toast.makeText(getActivity(), "Permission Granted, Now you can access location data and camera.", Toast.LENGTH_LONG).show();
                    else {

                        Toast.makeText(getActivity(), "Permission Denied, You cannot access location data and camera.", Toast.LENGTH_LONG).show();

                        if (shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE) && shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) {
                            showMessageOKCancel(
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            requestPermissions(new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE},
                                                    PERMISSION_REQUEST_CODE);
                                        }
                                    });
                            return;
                        }

                    }
                }

                break;
        }
    }

    private void showMessageOKCancel(DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getActivity())
                .setMessage("You need to allow access to both the permissions")
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    public static String formatSize(long bytes) {

        if (bytes <= 0) {
            return "0";
        } else {
            final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
            int digitGroups = (int) (Math.log10(bytes) / Math.log10(1024));
            return new DecimalFormat("#,##0.#").format(bytes / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
        }
    }
}

//    private void previewFiles(ArrayList<Uri> uriList, ArrayList<String> filePath) {
//        file = new File(String.valueOf(filePath));
//        StringBuilder files = new StringBuilder(file.getName());
//
//        for (int j = 0; j < uriList.size(); j++) {
//            //Append all the values to a string
//            files.append(files.toString()).append("\n");
//        }
//        Log.e(TAG, "FilesName " + file.getName());
//        Log.e(TAG, "Filenames " + files);
//
//        tv_dataFilesName.setText(files);
//    }

//    private void previewFile(Uri uri, int i) {
//        if (i == 0) {
//            String filePath = getRealPathFromURIPath(uri, Objects.requireNonNull(getActivity()), i);
//            file = new File(filePath);
//            Log.d(TAG, "Filename " + file.getName());
//            tv_appFileName.setText(file.getName());
//        }
//        if (i == 1) {
//            String filePath = getRealPathFromURIPath(uri, Objects.requireNonNull(getActivity()), i);
//            file = new File(filePath);
//            Log.d(TAG, "Filename " + file.getName());
//            tv_mdFileName.setText(file.getName());
//        }
//        if (i == 2) {
//            String filePath = getRealPathFromURIPath(uri, Objects.requireNonNull(getActivity()), i);
//            file = new File(filePath);
//            Log.d(TAG, "Filename " + file.getName());
//            tv_dataFilesName.setText(file.getName());
//        }
//    }

//        btn_slctMdFile = view.findViewById(R.id.btn_selectMDFile);
//        tv_mdFileName = view.findViewById(R.id.tv_mdFileName);
//        tv_mdFileSize = view.findViewById(R.id.tv_mdFileSize);


//        btn_slctMdFile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!checkPermission()) {
//                    requestPermission();
//                    openChooser(REQUEST_CODE1);
//                } else {
//                    Toast.makeText(getActivity(), "Permissions already granted", Toast.LENGTH_LONG).show();
//                    openChooser(REQUEST_CODE1);
//                }
//            }
//        });


//            if (i == REQUEST_CODE2) {
//                selectFile.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//                startActivityForResult(selectFile, REQUEST_CODE1);
//
//            }

//previewFiles(arrList, filePath);
//                            Toast.makeText(getActivity(),  "Total Number of File(s) Selected:" + arrList.size() + " File Selected: " + documentUri, Toast.LENGTH_LONG).show();
//                            previewFile(documentUri, REQUEST_CODE2);
//                            String filePath = getRealPathFromURIPath(documentUri, Objects.requireNonNull(getActivity()), REQUEST_CODE2);
//                            Log.e(TAG, "FilePath: " + filePath);

//                            String filerp = utils.getUriRealPath(getActivity(),documentUri,REQUEST_CODE0);
//                            Log.e(TAG, "FilerPather: " + filerp);
//File source = new File(filePath);
//utils.readBytesFromFile(source);

// Log.e(TAG, "FilePath: " + filePath + " Filename: " + source.getName());
//                            FileInputStream fis = new FileInputStream(source);
//
//                            ByteArrayOutputStream bos = new ByteArrayOutputStream();
//                            byte[] buf = new byte[1024];
//                            try {
//                                for (int readNum; (readNum = fis.read(buf)) != -1; ) {
//                                    bos.write(buf, 0, readNum); //no doubt here is 0
//                                    //Writes len bytes from the specified byte array starting at offset off to this byte array output stream.
//                                    Log.e(TAG, "read " + readNum + " bytes,");
//                                }
//                            } catch (IOException ex) {
//                                Log.e(TAG, "IOException:" + ex);
//                            }
//                            byte[] bytes = bos.toByteArray();

// sendReceive.write(bytes);


//previewFiles(arrList, filePath);
//                            Toast.makeText(getActivity(),  "Total Number of File(s) Selected:" + arrList.size() + " File Selected: " + documentUri, Toast.LENGTH_LONG).show();
//                            previewFile(documentUri, REQUEST_CODE2);
//                            String filePath = getRealPathFromURIPath(documentUri, Objects.requireNonNull(getActivity()), REQUEST_CODE2);
//                            Log.e(TAG, "FilePath: " + filePath);


//                            //utils.readBytesFromFile(source);
//
//                            Log.e(TAG, "FilePath: " + filePath + " Filename: " + source.getName());
//                            FileInputStream fis = new FileInputStream(source);
//
//                            ByteArrayOutputStream bos = new ByteArrayOutputStream();
//                            byte[] buf = new byte[1024];
//                            try {
//                                for (int readNum; (readNum = fis.read(buf)) != -1; ) {
//                                    bos.write(buf, 0, readNum); //no doubt here is 0
//                                    //Writes len bytes from the specified byte array starting at offset off to this byte array output stream.
//                                    Log.e(TAG, "read " + readNum + " bytes,");
//                                }
//                            } catch (IOException ex) {
//                                Log.e(TAG, "IOException:" + ex);
//                            }
//                            byte[] bytes = bos.toByteArray();

// sendReceive.write(bytes);


/*            case REQUEST_CODE2:
                // If the file selection was successful
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null) {
                        // Get the URI of the selected file
                        if (data.getClipData() != null) { // checking multiple selection or not
                            ArrayList<Uri> arrList = new ArrayList<Uri>();
                            for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                                Uri documentUri = data.getClipData().getItemAt(i).getUri();
                                arrList.add(documentUri);
                            }
                            ArrayList<String> filePath = new ArrayList<>();
                            for (int i = 0; i < arrList.size(); i++) {
                                Uri uri = arrList.get(i);
//                                Toast.makeText(getActivity(),  "Total Number of File(s) Selected:" + arrList.size() + " File Selected: " + uri, Toast.LENGTH_LONG).show();
                                Log.e(TAG, "Total Number of File(s) Selected:" + arrList.size() + " File Selected: " + uri);
                                filePath.add(getRealPathFromURIPath(uri, Objects.requireNonNull(getActivity()), REQUEST_CODE2));
                            }
                            Log.e(TAG, "FilePAth Size: " + filePath.size());

                            //previewFiles(arrList, filePath);
//                            Toast.makeText(getActivity(),  "Total Number of File(s) Selected:" + arrList.size() + " File Selected: " + documentUri, Toast.LENGTH_LONG).show();
//                            previewFile(documentUri, REQUEST_CODE2);
//                            String filePath = getRealPathFromURIPath(documentUri, Objects.requireNonNull(getActivity()), REQUEST_CODE2);
//                            Log.e(TAG, "FilePath: " + filePath);
                        } else {
                            Uri documentUri = data.getData();
                            try {
                                Toast.makeText(getActivity(),
                                        "File Selected: " + documentUri, Toast.LENGTH_LONG).show();
                                //  previewFile(documentUri, REQUEST_CODE2);
                                String filePath = getRealPathFromURIPath(documentUri, Objects.requireNonNull(getActivity()), REQUEST_CODE2);
                                Log.e(TAG, "FilePath: " + filePath);
//                            File source = new File(filePath);
//                            //utils.readBytesFromFile(source);
//
//                            Log.e(TAG, "FilePath: " + filePath + " Filename: " + source.getName());
//                            FileInputStream fis = new FileInputStream(source);
//
//                            ByteArrayOutputStream bos = new ByteArrayOutputStream();
//                            byte[] buf = new byte[1024];
//                            try {
//                                for (int readNum; (readNum = fis.read(buf)) != -1; ) {
//                                    bos.write(buf, 0, readNum); //no doubt here is 0
//                                    //Writes len bytes from the specified byte array starting at offset off to this byte array output stream.
//                                    Log.e(TAG, "read " + readNum + " bytes,");
//                                }
//                            } catch (IOException ex) {
//                                Log.e(TAG, "IOException:" + ex);
//                            }
//                            byte[] bytes = bos.toByteArray();

                                // sendReceive.write(bytes);
                            } catch (Exception e) {
                                Log.e(TAG, "File select error", e);
                            }
                        }
                    }
                }
                break;
*/
