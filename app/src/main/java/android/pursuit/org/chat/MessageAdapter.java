package android.pursuit.org.chat;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.TreeMap;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    TreeMap<String,ChatLog> logmap;
    Context context;

    public void updateLogs(ChatLog log){
//        if(logmap.containsKey(log.chattingWith.uid)){
//            logmap.remove(log.chattingWith.uid);
//        }
        logmap.put(log.chattingWith.username,log);
        Log.d("updatelogs",log.messages.size() + " " + log.chattingWith.username);
        notifyDataSetChanged();
    }

    public MessageAdapter(TreeMap<String,ChatLog> logmap,Context context) {
        this.logmap = logmap;
        this.context = context;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View child = LayoutInflater
                .from(viewGroup.getContext())
                .inflate(R.layout.saved_message_itemview, viewGroup, false);
        return new MessageViewHolder(child);
    }
    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder viewHolder, int i) {
        String key = (String) logmap.keySet().toArray()[i];
            viewHolder.onBind(logmap.get(key),context);
    }

    @Override
    public int getItemCount() {
        return logmap.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView user;
        TextView recent_message;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            user = itemView.findViewById(R.id.saved_message_username);
            recent_message = itemView.findViewById(R.id.saved_message);
        }

        public void onBind(@NonNull ChatLog item,Context context) {
            user.setText(item.chattingWith.username);
                if(item.messages.size() > 0) {
                    recent_message.setText(item.getMessages().get(item.messages.size()-1).message);
                }

            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, ChatActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("user", item.chattingWith);
                context.startActivity(intent);
            });
        }
    }
}
