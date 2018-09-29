package ru.tinkoff.scrollingpagerindicator;

import android.support.v7.widget.RecyclerView.AdapterDataObserver;

public interface RecyclerViewAdapterDelegate {

    int getAdapterItemCount();

    void registerAdapterDataObserver(AdapterDataObserver dataObserver);

    void unregisterAdapterDataObserver(AdapterDataObserver dataObserver);
}
