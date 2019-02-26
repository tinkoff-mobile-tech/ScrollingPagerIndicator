package ru.tinkoff.scrollingpagerindicator;

import android.support.annotation.NonNull;

/**
 * Interface for attaching to custom pagers.
 *
 * @param <T> custom pager's class
 */
public interface PagerAttacher<T> {

    /**
     * Here you should add all needed callbacks to track pager's item count, position and offset
     * You must call:
     * {@link IndicatorView#setDotCount(int)} - initially and after page selection,
     * {@link IndicatorView#setCurrentPosition(int)} - initially and after page selection,
     * {@link IndicatorView#onPageScrolled(int, float)} - in your pager callback to track scroll offset,
     * {@link IndicatorView#reattach()} - each time your adapter items change.
     *
     * @param indicator indicator
     * @param pager     pager to attach
     */
    void attachToPager(@NonNull IndicatorView indicator, @NonNull T pager);

    /**
     * Here you should unregister all callbacks previously added to pager and adapter
     */
    void detachFromPager();
}
