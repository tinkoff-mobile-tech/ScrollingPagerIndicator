package ru.tinkoff.demo;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.NumberPicker;
import android.widget.Toast;

import ru.tinkoff.scrollingpagerindicator.ScrollingPagerIndicator;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup pager and indicator
        ViewPager pager = findViewById(R.id.pager);
        DemoPagerAdapter adapter = new DemoPagerAdapter(this, 8);
        pager.setAdapter(adapter);

        ScrollingPagerIndicator indicator = findViewById(R.id.indicator);
        indicator.attachToPager(pager);

        // Some controls
        NumberPicker pageCountPicker = findViewById(R.id.page_number_picker);
        pageCountPicker.setMaxValue(99);
        pageCountPicker.setMinValue(0);
        pageCountPicker.setValue(adapter.getCount());

        NumberPicker visibleDotCountPicker = findViewById(R.id.visible_dot_number_picker);
        visibleDotCountPicker.setMinValue(3);
        visibleDotCountPicker.setMaxValue(11);
        visibleDotCountPicker.setValue(indicator.getVisibleDotCount());

        visibleDotCountPicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
            if (newVal % 2 == 0) {
                Toast.makeText(this, "Visible dot count must be odd number", Toast.LENGTH_SHORT).show();
                return;
            }
            indicator.setVisibleDotCount(newVal);
        });

        pageCountPicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
            if (pager.getCurrentItem() >= newVal - 1) {
                pager.setCurrentItem(newVal - 1, false);
            }
            adapter.setCount(newVal);
        });
    }
}
