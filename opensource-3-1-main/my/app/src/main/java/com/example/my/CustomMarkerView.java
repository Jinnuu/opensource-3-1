package com.example.my;

import android.content.Context;
import android.widget.TextView;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import java.util.List;

public class CustomMarkerView extends MarkerView {
    private TextView tvContent;
    private List<String> dateLabels;

    public CustomMarkerView(Context context, List<String> dateLabels) {
        super(context, R.layout.custom_marker_view);
        this.dateLabels = dateLabels;
        tvContent = findViewById(R.id.tvContent);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        int index = (int) e.getX();
        if (index >= 0 && index < dateLabels.size()) {
            tvContent.setText(dateLabels.get(index));
        }
        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2f), -getHeight() - 10);
    }
} 