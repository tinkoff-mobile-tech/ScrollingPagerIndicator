package ru.tinkoff.scrollingpagerindicator;

public abstract class AbstractViewPagerAttacher<T> implements ScrollingPagerIndicator.PagerAttacher<T> {

    public void updateIndicatorOnPagerScrolled(ScrollingPagerIndicator indicator, int position, float positionOffset) {
        final float offset;
        // ViewPager may emit negative positionOffset for very fast scrolling
        if (positionOffset < 0) {
            offset = 0;
        } else if (positionOffset > 1) {
            offset = 1;
        } else {
            offset = positionOffset;
        }
        indicator.onPageScrolled(position, offset);
    }
}
