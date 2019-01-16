package com.example.samsung.smartpcsys.adapters;

import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.example.samsung.smartpcsys.R;
import com.example.samsung.smartpcsys.resourcepool.RoutingTable;

public class NodesAdapter extends ListAdapter<RoutingTable, NodesAdapter.NodesHolder> {
    private OnItemClickListener listener;

    public NodesAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public NodesHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item,viewGroup,false);
        return new NodesHolder(itemView);
    }

    private static final DiffUtil.ItemCallback<RoutingTable> DIFF_CALLBACK = new DiffUtil.ItemCallback<RoutingTable>() {
        @Override
        public boolean areItemsTheSame(@NonNull RoutingTable oldItem, @NonNull RoutingTable newItem) {
            return oldItem.getHostAddress().equals(newItem.getHostAddress());
        }

        @Override
        public boolean areContentsTheSame(@NonNull RoutingTable oldItem, @NonNull RoutingTable newItem) {
            return oldItem.getHostAddress().equals(newItem.getHostAddress()) && oldItem.getInsertTime().equals(newItem.getInsertTime());
        }


    };

    @Override
    public void onBindViewHolder(@NonNull NodesHolder nodesHolder, int i) {
        RoutingTable rtEntry = getItem(i);
//        nodesHolder.tv_nodeID.setText(rtEntry.getId());
//        nodesHolder.tv_modelNo.setText(rtEntry.getModelNo());
        nodesHolder.tv_sourceAddress.setText(rtEntry.getSourceAddress());
        nodesHolder.tv_destAddress.setText(rtEntry.getHostAddress());
        nodesHolder.tv_time.setText(rtEntry.getInsertTime());
    }



    class NodesHolder extends RecyclerView.ViewHolder {
        private TextView tv_nodeID, tv_sourceAddress, tv_destAddress, tv_modelNo, tv_time;


        NodesHolder(View itemView) {
            super(itemView);
//            tv_nodeID = itemView.findViewById(R.id.tv_nodeId);
//            tv_modelNo = itemView.findViewById(R.id.tv_modelNo);
            tv_sourceAddress = itemView.findViewById(R.id.tv_sourceIP);
            tv_destAddress = itemView.findViewById(R.id.tv_destIP);
            tv_time = itemView.findViewById(R.id.tv_Time);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(getItem(position));
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(RoutingTable rtEntry);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
