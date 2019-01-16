package com.example.samsung.smartpcsys.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.samsung.smartpcsys.R;
import com.example.samsung.smartpcsys.adapters.NodesAdapter;
import com.example.samsung.smartpcsys.adapters.RoutesAdapter;
import com.example.samsung.smartpcsys.discoverynmonitoringmanager.DiscoveryThread;
import com.example.samsung.smartpcsys.resourcepool.RoutingTable;
import com.example.samsung.smartpcsys.utils.Global;
import com.example.samsung.smartpcsys.viewmodels.RTViewModel;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";
    private RTViewModel rtViewModel;
    RecyclerView recyclerView;
    public static List<RoutingTable> rtEntries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Thread discoveryThread = new Thread(DiscoveryThread.getInstance());
        discoveryThread.start();
//        try {
//            Thread.sleep(500);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        recyclerView = findViewById(R.id.recyclerview);
        //Button button = findViewById(R.id.btn_update);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        final RoutesAdapter adapter = new RoutesAdapter(Global.rtEntry);
       // final NodesAdapter adapter = new NodesAdapter();
//        Log.e(TAG, "List Size: "+rtEntries.size());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        //adapter.updateData(Global.tables);

//        rtViewModel = ViewModelProviders.of(this).get(RTViewModel.class);
//        rtViewModel.getRTEntry().observe(this, new Observer<List<RoutingTable>>(){
//
//            @Override
//            public void onChanged(@Nullable List<RoutingTable> routingTables) {
//                adapter.submitList(Global.rtEntry);
//            }
//        });

    }

}
