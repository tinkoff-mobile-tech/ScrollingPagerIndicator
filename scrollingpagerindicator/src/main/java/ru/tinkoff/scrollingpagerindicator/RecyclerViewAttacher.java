package ru.tinkoff.scrollingpagerindicator;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * @author Nikita Olifer
 * Attacher for RecyclerView. Supports only LinearLayoutManager with HORIZONTAL orientation.
 */
public class RecyclerViewAttacher implements ScrollingPagerIndicator.PagerAttacher<RecyclerView> {

    private ScrollingPagerIndicator indicator;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private RecyclerView.Adapter<?> attachedAdapter;

    private RecyclerView.OnScrollListener scrollListener;
    private RecyclerView.AdapterDataObserver dataObserver;

    private final boolean centered;
    private final int currentPageLeftCornerX;

    private int measuredChildWidth;

    /**
     * Default constructor. Use this if current page in recycler is centered.
     * All pages must have the same width.
     * Like this:
     *
     * +------------------------------+
     * |---+  +----------------+  +---|
     * |   |  |     current    |  |   |
     * |   |  |      page      |  |   |
     * |---+  +----------------+  +---|
     * +------------------------------+
     */
    public RecyclerViewAttacher() {
        currentPageLeftCornerX = 0; // Unused when centered
        centered = true;
    }

    /**
     * Use this constructor if current page in recycler isn't centered.
     * All pages must have the same width.
     * Like this:
     *
     * +-|----------------------------+
     * | +--------+  +--------+  +----|
     * | | current|  |        |  |    |
     * | |  page  |  |        |  |    |
     * | +--------+  +--------+  +----|
     * +-|----------------------------+
     *   | currentPageLeftCornerX
     *   |
     * @param currentPageLeftCornerX x coordinate of current view left corner relative to recycler view.
     */
    public RecyclerViewAttacher(int currentPageLeftCornerX) {
        this.currentPageLeftCornerX = currentPageLeftCornerX;
        this.centered = false;
    }

    @Override
    public void attachToPager(@NonNull final ScrollingPagerIndicator indicator, @NonNull final RecyclerView pager) {
        if (!(pager.getLayoutManager() instanceof LinearLayoutManager)) {
            throw new IllegalStateException("Only LinearLayoutManager is supported");
        }
        this.layoutManager = (LinearLayoutManager) pager.getLayoutManager();
        if (layoutManager.getOrientation() != LinearLayoutManager.HORIZONTAL) {
            throw new IllegalStateException("Only HORIZONTAL orientation is supported");
        }
        this.recyclerView = pager;
        this.attachedAdapter = pager.getAdapter();
        this.indicator = indicator;

        dataObserver = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                indicator.setDotCount(attachedAdapter.getItemCount());
                updateCurrentOffset();
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                onChanged();
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
                onChanged();
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                onChanged();
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                onChanged();
            }

            @Override
            public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                onChanged();
            }
        };
        attachedAdapter.registerAdapterDataObserver(dataObserver);
        indicator.setDotCount(attachedAdapter.getItemCount());
        updateCurrentOffset();

        scrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE && isInIdleState()) {
                    int newPosition = findCompletelyVisiblePosition();
                    if (newPosition != RecyclerView.NO_POSITION) {
                        indicator.setDotCount(attachedAdapter.getItemCount());
                        if (newPosition < attachedAdapter.getItemCount()) {
                            indicator.setCurrentPosition(newPosition);
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
        attachedAdapter.unregisterAdapterDataObserver(dataObserver);
        recyclerView.removeOnScrollListener(scrollListener);
        measuredChildWidth = 0;
    }

    private void updateCurrentOffset() {
        final View leftView = findFirstVisibleView();
        if (leftView == null) {
            return;
        }

        int position = recyclerView.getChildAdapterPosition(leftView);
        if (position == RecyclerView.NO_POSITION) {
            return;
        }
        final int itemCount = attachedAdapter.getItemCount();

        // In case there is an infinite pager
        if (position >= itemCount && itemCount != 0) {
            position = position % itemCount;
        }

        final float offset = (getCurrentFrameLeft() - leftView.getX()) / leftView.getMeasuredWidth();

        if (offset >= 0 && offset <= 1 && position < itemCount) {
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

    @Nullable
    private View findFirstVisibleView() {
        int childCount = layoutManager.getChildCount();
        if (childCount == 0) {
            return null;
        }

        View closestChild = null;
        int firstVisibleChildX = Integer.MAX_VALUE;

        for (int i = 0; i < childCount; i++) {
            final View child = layoutManager.getChildAt(i);

            // Default implementation change: use getX instead of helper
            int childStart = (int) child.getX();

            // if child is more to start than previous closest, set it as closest

            // Default implementation change:
            // Fix for any count of visible items
            // We make assumption that all children have the same width
            if (childStart + child.getMeasuredWidth() < firstVisibleChildX
                    && childStart + child.getMeasuredWidth() > getCurrentFrameLeft()) {
                firstVisibleChildX = childStart;
                closestChild = child;
            }
        }

        return closestChild;
    }

    private float getCurrentFrameLeft() {
        if (centered) {
            return (recyclerView.getMeasuredWidth() - getChildWidth()) / 2;
        } else {
            return currentPageLeftCornerX;
        }
    }

    private float getCurrentFrameRight() {
        if (centered) {
            return (recyclerView.getMeasuredWidth() - getChildWidth()) / 2 + getChildWidth();
        } else {
            return currentPageLeftCornerX + getChildWidth();
        }
    }

    private float getChildWidth() {
        if (measuredChildWidth == 0) {
            for (int i = 0; i < recyclerView.getChildCount(); i++) {
                View child = recyclerView.getChildAt(i);
                if (child.getMeasuredWidth() != 0) {
                    measuredChildWidth = child.getMeasuredWidth();
                    return measuredChildWidth;
                }
            }
        }
        return measuredChildWidth;
    }
}
