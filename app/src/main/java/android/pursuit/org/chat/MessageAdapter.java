package android.pursuit.org.chat;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    List<ChatLog> list;
    int itemview;

    public void setList(List<ChatLog> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public MessageAdapter(List<ChatLog> list, int itemview) {
        this.list = list;
        this.itemview = itemview;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View child = LayoutInflater
                .from(viewGroup.getContext())
                .inflate(itemview, viewGroup, false);
        return new MessageViewHolder(child);
    }

    public void onBindViewHolder(@NonNull MessageViewHolder viewHolder, int i) {
        viewHolder.onBind(list.get(i));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView user;
        TextView recent_message;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            user = itemView.findViewById(R.id.saved_message_username);
            recent_message = itemView.findViewById(R.id.saved_message);
        }

        public void onBind(@NonNull ChatLog item) {
            user.setText(item.chattingWith.username);
            recent_message.setText(item.messages.get(item.messages.size() - 1).message);

            itemView.setOnClickListener(v -> {
                // FIXME fix me
//                Intent intent = new Intent(context, ChatActivity.class);
//                intent.putExtra(USER_KEY, item);
//                context.startActivity(intent);
            });
        }
    }
}
