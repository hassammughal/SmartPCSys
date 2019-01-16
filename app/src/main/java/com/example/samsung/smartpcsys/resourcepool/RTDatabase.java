package com.example.samsung.smartpcsys.resourcepool;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

//@Database(entities = RoutingTable.class, version = 1)
public abstract class RTDatabase extends RoomDatabase {
//    public abstract RTDao rtDao();
//    public static volatile RTDatabase INSTANCE;
//
//    static RTDatabase getDatabase(final Context context) {
//        if (INSTANCE == null) {
//            synchronized (RTDatabase.class) {
//                if (INSTANCE == null) {
//                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
//                            RTDatabase.class, "routing_table")
//                            .build();
//                }
//            }
//        }
//        return INSTANCE;
//    }
}
