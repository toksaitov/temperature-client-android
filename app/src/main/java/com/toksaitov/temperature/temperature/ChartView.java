package com.toksaitov.temperature.temperature;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

public class ChartView extends View {
    private final static float BRUSH_WIDTH = 30;
    private final static float SCALE = 300.0f;

    private int brushColor;
    private Paint brushPaint;
    private Path brush;

    private ArrayList<Float> dataPoints;

    public ChartView(Context context, AttributeSet attrs) {
        super(context, attrs);

        extractAttributes(context, attrs);
        setupBrush();

        dataPoints  = new ArrayList<>();
    }

    public void setDataPoints(ArrayList<Float> dataPoints) {
        this.dataPoints = dataPoints;

        invalidate();
    }

    private void extractAttributes(Context context, AttributeSet attrs) {
        TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ChartView, 0, 0);
        try {
            brushColor = attributes.getColor(R.styleable.ChartView_fillColor, 0xFF000000);
        } finally {
            attributes.recycle();
        }
    }

    private void setupBrush() {
        brushPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        brushPaint.setColor(brushColor);
        brushPaint.setStrokeWidth(BRUSH_WIDTH);
        brushPaint.setStrokeJoin(Paint.Join.ROUND);
        brushPaint.setStrokeCap(Paint.Cap.ROUND);
        brushPaint.setStyle(Paint.Style.FILL);
        brush = new Path();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        invalidate();
    }

    @Override
    protected void onDraw(Canvas frontBufferCanvas) {
        super.onDraw(frontBufferCanvas);

        float width = getWidth();
        float height = getHeight();

        float position = width;
        float shift = width / (float) (dataPoints.size() - 1);

        brush.reset();
        brush.moveTo(width, height);
        for (float dataPoint : dataPoints) {
            brush.lineTo(position, height - (dataPoint * SCALE));
            position -= shift;
        }
        brush.lineTo(0.0f, height);
        brush.lineTo(width, height);
        brush.close();

        frontBufferCanvas.drawPath(brush, brushPaint);
    }

}
