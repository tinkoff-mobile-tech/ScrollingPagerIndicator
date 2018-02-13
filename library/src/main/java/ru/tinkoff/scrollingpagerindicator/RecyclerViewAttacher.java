package ru.tinkoff.scrollingpagerindicator;

import android.graphics.RectF;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * @author Nikita Olifer
 */
public class RecyclerViewAttacher implements ScrollingPagerIndicator.PagerAttacher<RecyclerView> {

    private ScrollingPagerIndicator indicator;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private RecyclerView.Adapter<?> adapter;

    private RecyclerView.OnScrollListener scrollListener;
    private RecyclerView.AdapterDataObserver dataObserver;

    private RectF currentPageFrame;

    /**
     * Default constructor. Current page frame will occupy all recycler view size.
     * You should use this constructor if each page has the same size as recycler view.
     */
    public RecyclerViewAttacher() {
    }

    /**
     * Use this constructor if there is more then one page visible in idle state of the recycler view.
     * @param currentPageFrame the frame in coordinates relative to recycler view.
     *                         It is used for current page and it's offset calculation.
     */
    public RecyclerViewAttacher(RectF currentPageFrame) {
        this.currentPageFrame = currentPageFrame;
    }

    @Override
    public void attachToPager(final ScrollingPagerIndicator indicator, final RecyclerView pager) {
        this.recyclerView = pager;
        this.adapter = pager.getAdapter();
        this.layoutManager = (LinearLayoutManager) pager.getLayoutManager();
        this.indicator = indicator;

        dataObserver = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                indicator.setDotCount(adapter.getItemCount());
                updateCurrentOffset();
            }
        };
        adapter.registerAdapterDataObserver(dataObserver);
        indicator.setDotCount(adapter.getItemCount());
        updateCurrentOffset();

        scrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (isInIdleState()) {
                        int newPosition = findCompletelyVisiblePosition();
                        if (newPosition != RecyclerView.NO_POSITION) {
                            // Notify
                            indicator.setDotCount(adapter.getItemCount());
                            if (newPosition >= 0 && newPosition < adapter.getItemCount()) {
                                indicator.setCurrentPosition(newPosition);
                            }
                        }
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                updateCurrentOffset();
            }
        };

        recyclerView.addOnScrollListener(scrollListener);
    }

    @Override
    public void detachFromPager() {
        adapter.unregisterAdapterDataObserver(dataObserver);
        recyclerView.removeOnScrollListener(scrollListener);
    }

    private void updateCurrentOffset() {
        final View leftView = findFirstVisibleView();
        if (leftView == null) {
            return;
        }
        RecyclerView.ViewHolder holder = recyclerView.findContainingViewHolder(leftView);
        if (holder == null) {
            return;
        }
        int position = holder.getAdapterPosition();

        final float offset = (getCurrentFrameLeft() - leftView.getX())
                / leftView.getMeasuredWidth();

        if (offset >= 0 && offset <= 1 && position != RecyclerView.NO_POSITION && position < adapter.getItemCount()) {
            indicator.onPageScrolled(position, offset);
        }
    }

    private int findCompletelyVisiblePosition() {
        for (int i = 0; i < recyclerView.getChildCount(); i++) {
            View child = recyclerView.getChildAt(i);
            if (child.getX() >= getCurrentFrameLeft() && child.getX() + child.getMeasuredWidth() <= getCurrentFrameRight()) {
                RecyclerView.ViewHolder holder = recyclerView.findContainingViewHolder(child);
                if (holder != null && holder.getAdapterPosition() != RecyclerView.NO_POSITION) {
                    return holder.getAdapterPosition();
                }
            }
        }
        return RecyclerView.NO_POSITION;
    }

    private boolean isInIdleState() {
        return findCompletelyVisiblePosition() != RecyclerView.NO_POSITION;
    }

    private View findFirstVisibleView() {
        int childCount = layoutManager.getChildCount();
        if (childCount == 0) {
            return null;
        }

        View closestChild = null;
        int startest = Integer.MAX_VALUE;

        for (int i = 0; i < childCount; i++) {
            final View child = layoutManager.getChildAt(i);

            // Default implementation change: use getX instead of helper
            int childStart = (int) child.getX();

            // if child is more to start than previous closest, set it as closest

            // Default implementation change:
            // Fix for any count of visible items
            // We make assumption that all children have the same width
            if (childStart + child.getMeasuredWidth() < startest
                    && childStart + child.getMeasuredWidth() > getCurrentFrameLeft()) {
                startest = childStart;
                closestChild = child;
            }
        }

        return closestChild;
    }

    private float getCurrentFrameLeft() {
        if (currentPageFrame == null) {
            currentPageFrame = new RectF(0, 0, recyclerView.getMeasuredWidth(), recyclerView.getMeasuredHeight());
        }
        return currentPageFrame.left;
    }

    private float getCurrentFrameRight() {
        if (currentPageFrame == null) {
            currentPageFrame = new RectF(0, 0, recyclerView.getMeasuredWidth(), recyclerView.getMeasuredHeight());
        }
        return currentPageFrame.right;
    }
}
