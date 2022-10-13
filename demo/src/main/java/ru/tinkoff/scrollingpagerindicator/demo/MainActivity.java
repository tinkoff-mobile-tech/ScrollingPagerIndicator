package ru.tinkoff.scrollingpagerindicator.demo;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import ru.tinkoff.scrollingpagerindicator.ScrollingPagerIndicator;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int screenWidth = getScreenWidth();

        // Setup ViewPager with indicator
        ViewPager pager = findViewById(R.id.pager);
        DemoPagerAdapter pagerAdapter = new DemoPagerAdapter(8);
        pager.setAdapter(pagerAdapter);

        ScrollingPagerIndicator pagerIndicator = findViewById(R.id.pager_indicator);
        pagerIndicator.attachToPager(pager);

        // Setup ViewPager2 with indicator
        ViewPager2 pager2 = findViewById(R.id.pager2);
        DemoRecyclerViewAdapter pagerAdapter2 = new DemoRecyclerViewAdapter(8, ViewGroup.LayoutParams.MATCH_PARENT);
        pager2.setAdapter(pagerAdapter2);

        ScrollingPagerIndicator pagerIndicator2 = findViewById(R.id.pager_indicator2);
        pagerIndicator2.attachToPager(pager2);

        // Setup RecyclerView with indicator
        // One page will occupy 1/3 of screen width
        RecyclerView recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        DemoRecyclerViewAdapter recyclerAdapter = new DemoRecyclerViewAdapter(8, screenWidth / 3);
        recyclerView.setAdapter(recyclerAdapter);

        recyclerView.setPadding(screenWidth / 3, 0, screenWidth / 3, 0);

        ScrollingPagerIndicator recyclerIndicator = findViewById(R.id.recycler_indicator);
        ScrollingPagerIndicator customRecyclerIndicator = findViewById(R.id.custom_recycler_indicator);
        // Consider page in the middle current
        recyclerIndicator.attachToRecyclerView(recyclerView);
        customRecyclerIndicator.attachToRecyclerView(recyclerView);

        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        // Some controls
        NumberPicker pageCountPicker = findViewById(R.id.page_number_picker);
        pageCountPicker.setMaxValue(99);
        pageCountPicker.setMinValue(0);
        pageCountPicker.setValue(pagerAdapter.getCount());

        NumberPicker visibleDotCountPicker = findViewById(R.id.visible_dot_number_picker);
        visibleDotCountPicker.setMinValue(3);
        visibleDotCountPicker.setMaxValue(11);
        visibleDotCountPicker.setValue(pagerIndicator.getVisibleDotCount());

        visibleDotCountPicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
            if (newVal % 2 == 0) {
                Toast.makeText(this, "Visible dot count must be odd number", Toast.LENGTH_SHORT).show();
                return;
            }
            pagerIndicator.setVisibleDotCount(newVal);
            recyclerIndicator.setVisibleDotCount(newVal);
        });

        pageCountPicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
            if (pager.getCurrentItem() >= newVal - 1) {
                pager.setCurrentItem(newVal - 1, false);
            }
            pagerAdapter.setCount(newVal);
            recyclerAdapter.setCount(newVal);
        });
    }

    private int getScreenWidth() {
        @SuppressWarnings("ConstantConditions")
        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point screenSize = new Point();
        display.getSize(screenSize);
        return screenSize.x;
    }
}
