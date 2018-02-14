package ru.tinkoff.demo;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.WindowManager;
import android.widget.NumberPicker;
import android.widget.Toast;

import ru.tinkoff.scrollingpagerindicator.RecyclerViewAttacher;
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

        // Setup RecyclerView with indicator
        // One page will occupy 1/3 of screen width
        RecyclerView recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        DemoRecyclerViewAdapter recyclerAdapter = new DemoRecyclerViewAdapter(8, screenWidth);
        recyclerView.setAdapter(recyclerAdapter);

        recyclerView.setPadding(screenWidth / 3, 0, screenWidth / 3, 0);
        recyclerView.setClipToPadding(false);

        ScrollingPagerIndicator recyclerIndicator = findViewById(R.id.recycler_indicator);
        // Consider page in the middle current
        recyclerIndicator.attachToRecyclerView(recyclerView);

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
