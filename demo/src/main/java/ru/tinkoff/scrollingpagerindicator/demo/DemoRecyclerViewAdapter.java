package ru.tinkoff.scrollingpagerindicator.demo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

/**
 * @author Nikita Olifer
 */
public class DemoRecyclerViewAdapter extends RecyclerView.Adapter<DemoRecyclerViewAdapter.ViewHolder> {

    private int count;
    private final int itemWidth;

    DemoRecyclerViewAdapter(int count, int itemWidth) {
        this.count = count;
        this.itemWidth = itemWidth;
    }

    void setCount(int count) {
        this.count = count;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.demo_page, parent, false);
        view.getLayoutParams().width = itemWidth;
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.title.setText(String.valueOf(position));
    }

    @Override
    public int getItemCount() {
        return count;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        final TextView title;

        ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.demo_page_label);
        }
    }
}
