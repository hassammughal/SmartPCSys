package com.example.samsung.smartpcsys.activities;

import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.example.samsung.smartpcsys.R;
import com.example.samsung.smartpcsys.adapters.TabAdapter;
import com.example.samsung.smartpcsys.fragments.AboutUs;
import com.example.samsung.smartpcsys.fragments.DevicesListFragment;
import com.example.samsung.smartpcsys.fragments.MainFragment;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";
    private TabAdapter tabAdapter;  //adapter for tabs
    private TabLayout tabLayout;   //layout for tabs
    private ViewPager viewPager;    //for swiping among the tabs
    private int[] tabIcons; //tab icons

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init(); //initialize the layout items
    }

    private void init() {
        tabIcons = new int[]{R.drawable.ic_action_home, R.drawable.ic_action_group, R.drawable.ic_action_account_circle};
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabAdapter = new TabAdapter(getSupportFragmentManager(), this);
        tabAdapter.addFragment(new MainFragment(), "Home", tabIcons[0]);
        tabAdapter.addFragment(new DevicesListFragment(), "Devices", tabIcons[1]);
        tabAdapter.addFragment(new AboutUs(), "About Us", tabIcons[2]);
        viewPager.setAdapter(tabAdapter);
        tabLayout.setupWithViewPager(viewPager);
        highLightCurrentTab(0);

        //to create the folders for each type of files
        File mFolder = new File(Environment.getExternalStorageDirectory() + "/SmartPCSys/MetaData");
        File rFolder = new File(Environment.getExternalStorageDirectory() + "/SmartPCSys/Receive");
        File aFolder = new File(Environment.getExternalStorageDirectory() + "/SmartPCSys/Application");
        File dFolder = new File(Environment.getExternalStorageDirectory() + "/SmartPCSys/Data");

        if (!mFolder.exists() && !rFolder.exists() && !aFolder.exists() && !dFolder.exists()) {
            mFolder.mkdirs();
            rFolder.mkdirs();
            aFolder.mkdirs();
            dFolder.mkdirs();
        }

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                highLightCurrentTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

    }

    private void highLightCurrentTab(int position) {
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            assert tab != null;
            tab.setCustomView(null);
            tab.setCustomView(tabAdapter.getTabView(i));
        }
        TabLayout.Tab tab = tabLayout.getTabAt(position);
        assert tab != null;
        tab.setCustomView(null);
        tab.setCustomView(tabAdapter.getSelectedTabView(position));
    }




}
//        rtViewModel = ViewModelProviders.of(this).get(RTViewModel.class);
//        rtViewModel.getRTEntry().observe(this, new Observer<List<RoutingTable>>(){
//
//            @Override
//            public void onChanged(@Nullable List<RoutingTable> routingTables) {
//                adapter.submitList(Global.rtEntry);
//            }
//        });