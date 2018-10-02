package ru.tinkoff.scrollingpagerindicator;

import android.support.v7.widget.RecyclerView;

public class DefaultRecyclerViewAdapterDelegate implements RecyclerViewAdapterDelegate {

    private final RecyclerView.Adapter adapter;

    public DefaultRecyclerViewAdapterDelegate(RecyclerView.Adapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public int getAdapterItemCount() {
        return adapter.getItemCount();
    }

    @Override
    public void registerAdapterDataObserver(RecyclerView.AdapterDataObserver dataSetObserver) {
        adapter.registerAdapterDataObserver(dataSetObserver);
    }

    @Override
    public void unregisterAdapterDataObserver(RecyclerView.AdapterDataObserver dataSetObserver) {
        adapter.unregisterAdapterDataObserver(dataSetObserver);
    }
}
