package com.example.samsung.smartpcsys.utils;

import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;

import com.example.samsung.smartpcsys.resourcepool.RoutingTable;

import java.util.List;

public class SPSDiffUtilCallBack extends DiffUtil.Callback {

    private List<RoutingTable> oldList;
    private List<RoutingTable> newList;

    public SPSDiffUtilCallBack(List<RoutingTable> oldList, List<RoutingTable> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldPosition, int newPosition) {
        return oldList.get(oldPosition).getHostAddress().equals(newList.get(newPosition).getHostAddress());
    }

    @Override
    public boolean areContentsTheSame(int oldPosition, int newPosition) {
        RoutingTable oldRTEntry = oldList.get(oldPosition);
        RoutingTable newRTEntry = newList.get(newPosition);

        return oldRTEntry.getHostAddress().equals(newRTEntry.getHostAddress()) && oldRTEntry.getInsertTime().equals(newRTEntry.getInsertTime());
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}
