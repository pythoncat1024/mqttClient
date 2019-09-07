package org.victor.mqttcat.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.victor.mqttcat.R;
import org.victor.mqttcat.model.Msg;

import java.util.ArrayList;
import java.util.List;

public class ReceivedAdapter extends RecyclerView.Adapter<ReceivedAdapter.VH> {

    private List<Msg> msgList;


    public void setMsgList(List<Msg> list) {
        this.msgList = list;
        notifyDataSetChanged();
    }

    public void addMsg(Msg msg) {
        if (this.msgList == null) {
            msgList = new ArrayList<>();
        }
        msgList.add(0, msg);
        notifyItemInserted(0);
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_received, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        if (msgList == null) {
            return;
        }
        Msg msg = msgList.get(position);
        holder.tvQos.setText("QOS:" + msg.qos);
        holder.tvTopic.setText(msg.topic);
        holder.tvContent.setText(msg.content);
    }

    @Override
    public int getItemCount() {
        return msgList == null ? 0 : msgList.size();
    }

    static class VH extends RecyclerView.ViewHolder {

        private TextView tvTopic;
        private TextView tvContent;
        private TextView tvQos;

        public VH(@NonNull View itemView) {
            super(itemView);
            tvContent = itemView.findViewById(R.id.item_content);
            tvTopic = itemView.findViewById(R.id.item_topic);
            tvQos = itemView.findViewById(R.id.item_qos);
        }
    }
}
