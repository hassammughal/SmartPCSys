package com.example.samsung.smartpcsys.resourcepool;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.AsyncTask;

import com.example.samsung.smartpcsys.utils.TimestampConverter;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class RTRepository {

//   private RTDao rtDao;
//   private LiveData<List<RoutingTable>> mAllRoutes;
//
//   RTRepository(Application application){
//       RTDatabase db = RTDatabase.getDatabase(application);
//       rtDao = db.rtDao();
//       mAllRoutes = rtDao.fetchAllNodes();
//   }
//
//   LiveData<List<RoutingTable>> fetchAllNodes(){
//       return mAllRoutes;
//   }
//
//    public void insertNode(RoutingTable routingTable) {
//
//       new insertAsyncTask(rtDao).execute(routingTable);
////        RoutingTable rtEntry = new RoutingTable();
////        rtEntry.setModelNo(modelNo);
////        rtEntry.setSourceAddress(sourceAddress);
////        rtEntry.setDestAddress(destAddress);
////        rtEntry.setInsertTime(insertTime);
////        insertNode(rtEntry);
//
//    }
//
//    private static class insertAsyncTask extends AsyncTask<RoutingTable, Void, Void>{
//
//       private RTDao mAsyncTaskDao;
//
//       insertAsyncTask(RTDao dao){
//           mAsyncTaskDao = dao;
//       }
//
//        @Override
//        protected Void doInBackground(RoutingTable... routingTables) {
//           mAsyncTaskDao.insertRTEntry(routingTables[0]);
//            return null;
//        }
//    }
////     void insertNode(final RoutingTable routingTable) {
////        new AsyncTask<Void, Void, Void>() {
////            @Override
////            protected Void doInBackground(Void... voids) {
////                rtDatabase.rtDao().insertRTEntry(routingTable);
////                return null;
////            }
////        }.execute();
////    }
////
////    public void updateTask(final RoutingTable rtEntry) {
////        rtEntry.setModifyTime(getCurrentTime());
////
////        new AsyncTask<Void, Void, Void>() {
////            @Override
////            protected Void doInBackground(Void... voids) {
////                rtDatabase.rtDao().updateRTEntry(rtEntry);
////                return null;
////            }
////        }.execute();
////    }
////
////    public void deleteRTEntry(final String destAddress) {
////        final LiveData<RoutingTable> rtEntryLiveData = getDest(destAddress);
////        if(rtEntryLiveData != null) {
////            new AsyncTask<Void, Void, Void>() {
////                @Override
////                protected Void doInBackground(Void... voids) {
////                    rtDatabase.rtDao().deleteRTEntry(rtEntryLiveData.getValue());
////                    return null;
////                }
////            }.execute();
////        }
////    }
////
////    public void deleteTask(final RoutingTable rtEntry) {
////        new AsyncTask<Void, Void, Void>() {
////            @Override
////            protected Void doInBackground(Void... voids) {
////                rtDatabase.rtDao().deleteRTEntry(rtEntry);
////                return null;
////            }
////        }.execute();
////    }
////
////    public LiveData<RoutingTable> getSource(String sourceAddress) {
////        return rtDatabase.rtDao().getSourceAddress(sourceAddress);
////    }
////
////    public LiveData<RoutingTable> getDest(String destAddress) {
////        return rtDatabase.rtDao().getDestAddress(destAddress);
////    }
////
////    public LiveData<List<RoutingTable>> getNodes() {
////        return rtDatabase.rtDao().fetchAllNodes();
////    }
//
//    private String getCurrentTime(){
//        //Locale locale = new Locale("en-US", "KOREA");
//        DateFormat dateFormat = DateFormat.getDateTimeInstance();
//        TimeZone timeZone = TimeZone.getTimeZone("KST");
//        dateFormat.setTimeZone(timeZone);
//        String date = dateFormat.format(new Date());
//        return date;
//    }
}
