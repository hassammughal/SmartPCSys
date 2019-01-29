package com.example.samsung.smartpcsys.fragments;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.samsung.smartpcsys.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {
    private String TAG = "MainFragment";
    Button btn_slctApp, btn_slctMdFile, btn_slctDFile;
    private static final int REQUEST_CODE0 = 0; // onActivityResult request code
    private static final int REQUEST_CODE1 = 1;
    private static final int REQUEST_CODE2 = 2;
    private static final int PERMISSION_REQUEST_CODE = 200;
    int nameIndex, sizeIndex;
    public static String fileName;
    private File file;
    Intent intent;
    Uri fileUri;
    TextView tv_appFileName, tv_appFileSize, tv_mdFileName, tv_mdFileSize, tv_dataFilesName, tv_dataFileSize;

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

    public void init(View view) {
        btn_slctApp = view.findViewById(R.id.btn_selectApp);
        tv_appFileName = view.findViewById(R.id.tv_appFileName);
        tv_appFileSize = view.findViewById(R.id.tv_appFileSize);

        btn_slctMdFile = view.findViewById(R.id.btn_selectMDFile);
        tv_mdFileName = view.findViewById(R.id.tv_mdFileName);
        tv_mdFileSize = view.findViewById(R.id.tv_mdFileSize);

        btn_slctDFile = view.findViewById(R.id.btn_selectDFiles);
        tv_dataFilesName = view.findViewById(R.id.tv_dFilesName);
        tv_dataFileSize = view.findViewById(R.id.tv_dFilesSize);

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

        btn_slctMdFile.setOnClickListener(new View.OnClickListener() {
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

        btn_slctDFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkPermission()) {
                    requestPermission();
                    openChooser(REQUEST_CODE2);
                } else {
                    Toast.makeText(getActivity(), "Permissions already granted", Toast.LENGTH_LONG).show();
                    openChooser(REQUEST_CODE2);
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
            if (i == REQUEST_CODE0)
                startActivityForResult(selectFile, REQUEST_CODE0);

            if (i == REQUEST_CODE1)
                startActivityForResult(selectFile, REQUEST_CODE1);

            if (i == REQUEST_CODE2)
                startActivityForResult(selectFile, REQUEST_CODE2);
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
                        Uri documentUri = data.getData();
                        try {
                            Toast.makeText(getActivity(),
                                    "File Selected: " + documentUri, Toast.LENGTH_LONG).show();
                            previewFile(documentUri, REQUEST_CODE0);
                            String filePath = getRealPathFromURIPath(documentUri, Objects.requireNonNull(getActivity()), REQUEST_CODE0);//utils.getUriRealPath(MainActivity.this,documentUri);

                            File source = new File(filePath);
                            //utils.readBytesFromFile(source);

                            Log.e(TAG, "FilePath: " + filePath + " Filename: " + source.getName());
                            FileInputStream fis = new FileInputStream(source);

                            ByteArrayOutputStream bos = new ByteArrayOutputStream();
                            byte[] buf = new byte[1024];
                            try {
                                for (int readNum; (readNum = fis.read(buf)) != -1; ) {
                                    bos.write(buf, 0, readNum); //no doubt here is 0
                                    //Writes len bytes from the specified byte array starting at offset off to this byte array output stream.
                                    Log.e(TAG, "read " + readNum + " bytes,");
                                }
                            } catch (IOException ex) {
                                Log.e(TAG, "IOException:" + ex);
                            }
                            byte[] bytes = bos.toByteArray();

                            // sendReceive.write(bytes);
                        } catch (Exception e) {
                            Log.e("MainActivity", "File select error", e);
                        }
                    }
                }
                break;
            case REQUEST_CODE1:
                // If the file selection was successful
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null) {
                        // Get the URI of the selected file
                        Uri documentUri = data.getData();
                        try {
                            Toast.makeText(getActivity(),
                                    "File Selected: " + documentUri, Toast.LENGTH_LONG).show();
                            previewFile(documentUri, REQUEST_CODE1);
                            String filePath = getRealPathFromURIPath(documentUri, Objects.requireNonNull(getActivity()), REQUEST_CODE1);//utils.getUriRealPath(MainActivity.this,documentUri);

                            File source = new File(filePath);
                            //utils.readBytesFromFile(source);

                            Log.e(TAG, "FilePath: " + filePath + " Filename: " + source.getName());
                            FileInputStream fis = new FileInputStream(source);

                            ByteArrayOutputStream bos = new ByteArrayOutputStream();
                            byte[] buf = new byte[1024];
                            try {
                                for (int readNum; (readNum = fis.read(buf)) != -1; ) {
                                    bos.write(buf, 0, readNum); //no doubt here is 0
                                    //Writes len bytes from the specified byte array starting at offset off to this byte array output stream.
                                    Log.e(TAG, "read " + readNum + " bytes,");
                                }
                            } catch (IOException ex) {
                                Log.e(TAG, "IOException:" + ex);
                            }
                            byte[] bytes = bos.toByteArray();

                            // sendReceive.write(bytes);
                        } catch (Exception e) {
                            Log.e("MainActivity", "File select error", e);
                        }
                    }
                }
                break;
            case REQUEST_CODE2:
                // If the file selection was successful
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null) {
                        // Get the URI of the selected file
                        Uri documentUri = data.getData();
                        try {
                            Toast.makeText(getActivity(),
                                    "File Selected: " + documentUri, Toast.LENGTH_LONG).show();
                            previewFile(documentUri, REQUEST_CODE2);
                            String filePath = getRealPathFromURIPath(documentUri, Objects.requireNonNull(getActivity()), REQUEST_CODE2);//utils.getUriRealPath(MainActivity.this,documentUri);

                            File source = new File(filePath);
                            //utils.readBytesFromFile(source);

                            Log.e(TAG, "FilePath: " + filePath + " Filename: " + source.getName());
                            FileInputStream fis = new FileInputStream(source);

                            ByteArrayOutputStream bos = new ByteArrayOutputStream();
                            byte[] buf = new byte[1024];
                            try {
                                for (int readNum; (readNum = fis.read(buf)) != -1; ) {
                                    bos.write(buf, 0, readNum); //no doubt here is 0
                                    //Writes len bytes from the specified byte array starting at offset off to this byte array output stream.
                                    Log.e(TAG, "read " + readNum + " bytes,");
                                }
                            } catch (IOException ex) {
                                Log.e(TAG, "IOException:" + ex);
                            }
                            byte[] bytes = bos.toByteArray();

                            // sendReceive.write(bytes);
                        } catch (Exception e) {
                            Log.e("MainActivity", "File select error", e);
                        }
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void previewFile(Uri uri, int i) {
        if (i == 0) {
            String filePath = getRealPathFromURIPath(uri, Objects.requireNonNull(getActivity()), i);
            file = new File(filePath);
            Log.d(TAG, "Filename " + file.getName());
            tv_appFileName.setText(file.getName());
        } else if (i == 1) {
            String filePath = getRealPathFromURIPath(uri, Objects.requireNonNull(getActivity()), i);
            file = new File(filePath);
            Log.d(TAG, "Filename " + file.getName());
            tv_mdFileName.setText(file.getName());
        } else if (i == 2) {
            String filePath = getRealPathFromURIPath(uri, Objects.requireNonNull(getActivity()), i);
            file = new File(filePath);
            Log.d(TAG, "Filename " + file.getName());
            tv_dataFilesName.setText(file.getName());
        }
    }

    private String getRealPathFromURIPath(Uri contentURI, Activity activity, int i) {
        Cursor cursor = activity.getContentResolver().query(contentURI, null, null, null, null);
        String realPath = "";
        if (cursor == null) {
            realPath = Environment.getExternalStorageDirectory() + "/" + contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME);
            File external[] = Objects.requireNonNull(getActivity()).getApplicationContext().getExternalMediaDirs();
            if (external.length > 1) {
                realPath = external[1].getAbsolutePath();
                realPath = realPath.substring(0, realPath.indexOf("Android")) + cursor.getString(idx);

            }
            //  realPath = Environment.getExternalStorageDirectory() + "/" + cursor.getString(idx);
            nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
            if (i == 0) {
                tv_appFileName.setText(cursor.getString(nameIndex));
                tv_appFileSize.setText(cursor.getString(sizeIndex));
            }
            if (i == 1) {
                tv_mdFileName.setText(cursor.getString(nameIndex));
                tv_mdFileSize.setText(cursor.getString(sizeIndex));
            }
            if (i == 2) {
                tv_dataFilesName.setText(cursor.getString(nameIndex));
                tv_dataFileSize.setText(cursor.getString(sizeIndex));
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


}
