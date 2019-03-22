package android.pursuit.org.chat.unusedClasses;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

public abstract class AbstractAdapter<T, VH extends AbstractViewHolder<T>> extends RecyclerView.Adapter<VH> {
    private List<T> list;
    private int itemview;

    public AbstractAdapter(List<T> list, int itemview) {
        this.list = list;
        this.itemview = itemview;
    }

    @NonNull
    @Override
    public abstract VH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i);

    @Override
    public void onBindViewHolder(@NonNull VH chatViewHolder, int i) {}

    @Override
    public int getItemCount() {
        if(list != null) {
            return list.size();
        }
        return 0;
    }
}

