package ru.tinkoff.scrollingpagerindicator;

import android.support.v7.widget.RecyclerView.AdapterDataObserver;

/**
 * This interface is an abstraction between {@link android.support.v7.widget.RecyclerView.Adapter} and
 * {@link ScrollingPagerIndicator.PagerAttacher}.
 *
 * For example, in case if you have custom {@link android.support.v7.widget.RecyclerView.Adapter} which
 * is infinite ({@link android.support.v7.widget.RecyclerView.Adapter#getItemCount() returns Integer.MAX_VALUE})
 * and has real wrapped Adapter. You have to pass calls to wrapped adapter via RecyclerViewAdapterDelegate
 */
public interface RecyclerViewAdapterDelegate {

    int getAdapterItemCount();

    void registerAdapterDataObserver(AdapterDataObserver dataObserver);

    void unregisterAdapterDataObserver(AdapterDataObserver dataObserver);
}
