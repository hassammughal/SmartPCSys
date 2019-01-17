package com.example.samsung.smartpcsys.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.samsung.smartpcsys.R;
import com.example.samsung.smartpcsys.resourcepool.RoutingTable;
import com.example.samsung.smartpcsys.utils.Global;

import java.util.List;

public class RoutesAdapter extends RecyclerView.Adapter<RoutesAdapter.MyViewHolder> {

    private String TAG = "RoutesAdapter";
    // private List<RoutingTable> rtEntry;

    public RoutesAdapter(List<RoutingTable> rtEntry) {
        Global.rtEntry = rtEntry;
    }

    public void insertRTEntry(RoutingTable insertRoute) {
//        SPSDiffUtilCallBack diffUtilCallBack = new SPSDiffUtilCallBack(rtEntry,insertList);
//        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffUtilCallBack);
        //dataSource.clear();
        Global.rtEntry.add(insertRoute);
        notifyDataSetChanged();
        //diffResult.dispatchUpdatesTo(this);
    }

    public void removeRTEntry(int index) {
        Global.rtEntry.remove(index);
        notifyItemRemoved(index);
        notifyItemRangeChanged(index, Global.rtEntry.size());
        notifyDataSetChanged();
    }

//    public void updateRTEntry (List<RoutingTable> newList){
//        SPSDiffUtilCallBack diffUtilCallBack = new SPSDiffUtilCallBack(rtEntry,newList);
//        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffUtilCallBack);
//        Log.e(TAG, "List Size: "+rtEntry.size());
//        rtEntry.clear();
//        notifyDataSetChanged();
//        rtEntry = new ArrayList<>(newList);
//        notifyDataSetChanged();
//        Log.e(TAG, "After List Size: "+rtEntry.size());
//        Log.e(TAG, "NewList Size: "+newList.size());
//        diffResult.dispatchUpdatesTo(this);
//    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item,viewGroup,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        myViewHolder.tv_sourceAddress.setText("My IP Address: " + Global.rtEntry.get(i).getSourceAddress());
        myViewHolder.tv_destAddress.setText("Host IP Address: " + Global.rtEntry.get(i).getHostAddress());
        myViewHolder.tv_time.setText(Global.rtEntry.get(i).getInsertTime());
    }

    @Override
    public int getItemCount() {
        return Global.rtEntry.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tv_sourceAddress, tv_destAddress, tv_time;
      public  MyViewHolder(@NonNull View itemView){
            super(itemView);
            tv_sourceAddress = itemView.findViewById(R.id.tv_sourceIP);
            tv_destAddress = itemView.findViewById(R.id.tv_destIP);
            tv_time = itemView.findViewById(R.id.tv_Time);
        }
    }
}
