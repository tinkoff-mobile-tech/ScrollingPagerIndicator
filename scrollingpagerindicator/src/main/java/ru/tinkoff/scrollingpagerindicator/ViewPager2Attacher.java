package ru.tinkoff.scrollingpagerindicator;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

public class ViewPager2Attacher extends AbstractViewPagerAttacher<ViewPager2> {

    private RecyclerView.AdapterDataObserver dataSetObserver;
    private RecyclerView.Adapter attachedAdapter;
    private ViewPager2.OnPageChangeCallback onPageChangeListener;
    private ViewPager2 pager;

    @Override
    public void attachToPager(@NonNull final ScrollingPagerIndicator indicator, @NonNull final ViewPager2 pager) {
        attachedAdapter = pager.getAdapter();
        if (attachedAdapter == null) {
            throw new IllegalStateException("Set adapter before call attachToPager() method");
        }

        this.pager = pager;

        updateIndicatorDotsAndPosition(indicator);

        dataSetObserver = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                indicator.reattach();
            }
        };
        attachedAdapter.registerAdapterDataObserver(dataSetObserver);

        onPageChangeListener = new ViewPager2.OnPageChangeCallback() {

            boolean idleState = true;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixel) {
                updateIndicatorOnPagerScrolled(indicator, position, positionOffset);
            }

            @Override
            public void onPageSelected(int position) {
                if (idleState) {
                    updateIndicatorDotsAndPosition(indicator);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                idleState = state == ViewPager2.SCROLL_STATE_IDLE;
            }
        };

        pager.registerOnPageChangeCallback(onPageChangeListener);
    }

    @Override
    public void detachFromPager() {
        attachedAdapter.unregisterAdapterDataObserver(dataSetObserver);
        pager.unregisterOnPageChangeCallback(onPageChangeListener);
    }

    private void updateIndicatorDotsAndPosition(ScrollingPagerIndicator indicator) {
        indicator.setDotCount(attachedAdapter.getItemCount());
        indicator.setCurrentPosition(pager.getCurrentItem());
    }
}