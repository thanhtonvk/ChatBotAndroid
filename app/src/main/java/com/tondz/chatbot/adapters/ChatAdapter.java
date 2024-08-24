package com.tondz.chatbot.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tondz.chatbot.R;
import com.tondz.chatbot.models.ChatContent;

import java.util.List;

import io.noties.markwon.Markwon;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private static final int MSG_LEFT = 319;
    private static final int MSG_RIGHT = 276;
    private Context context;
    private List<ChatContent> list;
    Markwon markwon;

    public ChatAdapter(Context context, List<ChatContent> list) {
        this.context = context;
        this.list = list;
        markwon = Markwon.create(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        switch (viewType) {
            case MSG_LEFT:
                view = LayoutInflater.from(context).inflate(R.layout.item_chat_left, parent, false);
                break;
            case MSG_RIGHT:
                view = LayoutInflater.from(context).inflate(R.layout.item_chat_right, parent, false);
                break;
        }
        return new ChatAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatContent content = list.get(position);

        markwon.setMarkdown(holder.tv_message, content.getMessage());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position).getSenderUser().equals("1")) {
            return MSG_RIGHT;
        } else {
            return MSG_LEFT;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_message;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_message = itemView.findViewById(R.id.show_message);
        }
    }
}
