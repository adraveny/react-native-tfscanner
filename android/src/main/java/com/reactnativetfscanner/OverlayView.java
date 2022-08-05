package com.reactnativetfscanner;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;

import androidx.annotation.NonNull;

import java.util.LinkedList;
import java.util.List;

import org.tensorflow.lite.support.label.Category;
import org.tensorflow.lite.task.vision.detector.Detection;

class OverlayView extends View {

  private final int BOUNDING_RECT_TEXT_PADDING = 8;


  private List<Detection> results = new LinkedList<Detection>();
  private Paint boxPaint;
  private Paint textBackgroundPaint;
  private Paint textPaint;

  private float scaleFactor = 1.0f;

  private Rect bounds = new Rect();

  public static class OverlayViewParams {
    private int textBackgroundPaintColor = Color.BLACK;
    private float textSize = 50f;
    private int textColor = Color.WHITE;
    private int boxColor = Color.BLACK;
    private float strokeWidth = 8F;

    public void setTextBackgroundPaintColor(int textBackgroundPaintColor) {
      this.textBackgroundPaintColor = textBackgroundPaintColor;
    }

    public void setTextSize(float textSize) {
      this.textSize = textSize;
    }

    public void setTextColor(int textColor) {
      this.textColor = textColor;
    }

    public void setBoxColor(int boxColor) {
      this.boxColor = boxColor;
    }

    public void setStrokeWidth(float strokeWidth) {
      this.strokeWidth = strokeWidth;
    }

    public int getTextBackgroundPaintColor() {
      return textBackgroundPaintColor;
    }

    public float getTextSize() {
      return textSize;
    }

    public int getTextColor() {
      return textColor;
    }

    public int getBoxColor() {
      return boxColor;
    }

    public float getStrokeWidth() {
      return strokeWidth;
    }
  }

  public static class OverlayViewBuilder {
    private OverlayView overlayView;
    public OverlayViewBuilder(@NonNull Context context) {
      super();
      overlayView = new OverlayView(context);
    }

    public OverlayViewBuilder setTextBackgroundPaintColor(int textBackgroundPaintColor) {
      overlayView.setTextBackgroundPaintColor(textBackgroundPaintColor);
      return this;
    }

    public OverlayViewBuilder setTextSize(float textSize) {
      overlayView.setTextSize(textSize);
      return this;
    }

    public OverlayViewBuilder setTextColor(int textColor) {
      overlayView.setTextColor(textColor);
      return this;
    }

    public OverlayViewBuilder setBoxColor(int boxColor) {
      overlayView.setBoxColor(boxColor);
      return this;
    }

    public OverlayViewBuilder setStrokeWidth(float strokeWidth) {
      overlayView.setStrokeWidth(strokeWidth);
      return this;
    }

    public OverlayViewBuilder setParams(OverlayViewParams params) {
      overlayView.setParams(params);
      return this;
    }

    public static OverlayViewBuilder fromParams(@NonNull Context context, OverlayViewParams params) {
      return new OverlayViewBuilder(context).setParams(params);
    }

    public OverlayView build() {
      overlayView.clear();
      return overlayView;
    }
  }

  private OverlayViewParams params;

  public OverlayView(@NonNull Context context) {
    super(context);

    boxPaint = new Paint();
    textBackgroundPaint = new Paint();
    textPaint = new Paint();

    params = new OverlayViewParams();

    initPaints();
  }

  public OverlayView(@NonNull Context context, OverlayViewParams params) {
    super(context);

    boxPaint = new Paint();
    textBackgroundPaint = new Paint();
    textPaint = new Paint();

    this.params = params;

    initPaints();
  }

  public void clear() {
    textPaint.reset();
    textBackgroundPaint.reset();
    boxPaint.reset();
    invalidate();
    initPaints();
  }

  private void initPaints() {
    textBackgroundPaint.setColor(params.getTextBackgroundPaintColor());
    textBackgroundPaint.setStyle(Paint.Style.FILL);
    textBackgroundPaint.setTextSize(params.getTextSize());

    textPaint.setColor(params.getTextColor());
    textPaint.setStyle(Paint.Style.FILL);
    textPaint.setTextSize(params.getTextSize());

    boxPaint.setColor(params.getBoxColor());
    boxPaint.setStrokeWidth(params.getStrokeWidth());
    boxPaint.setStyle(Paint.Style.STROKE);
  }

  public void setTextBackgroundPaintColor(int color) {
    this.params.setTextBackgroundPaintColor(color);
    textBackgroundPaint.setColor(color);
  }

  public void setTextSize(float textSize) {
    this.params.setTextSize(textSize);
    textBackgroundPaint.setTextSize(textSize);
    textPaint.setTextSize(textSize);
  }

  public void setTextColor(int color) {
    params.setTextColor(color);
    textPaint.setColor(color);
  }

  public void setBoxColor(int color) {
    params.setBoxColor(color);
    boxPaint.setColor(color);
  }

  public void setStrokeWidth(float width) {
    params.setStrokeWidth(width);
    boxPaint.setStrokeWidth(width);
  }

  public void setParams(OverlayViewParams params) {
    this.params = params;
  }

  @Override
  public void draw(Canvas canvas) {
    super.draw(canvas);
    for (Detection result : results) {
      RectF boundingBox = result.getBoundingBox();

      float top = boundingBox.top * scaleFactor;
      float bottom = boundingBox.bottom * scaleFactor;
      float left = boundingBox.left * scaleFactor;
      float right = boundingBox.right * scaleFactor;

      // Draw bounding box around detected objects
      RectF drawableRect = new RectF(left, top, right, bottom);
      canvas.drawRect(drawableRect, boxPaint);

      // Create text to display alongside detected objects
      Category category = result.getCategories().get(0);
      String drawableText =
        category.getLabel() + " " +
          String.format("%.0f %%", 100f * category.getScore());

      // Draw rect behind display text
      textBackgroundPaint.getTextBounds(drawableText, 0, drawableText.length(), bounds);
      float textWidth = bounds.width();
      float textHeight = bounds.height();
      canvas.drawRect(
        left,
        top,
        left + textWidth + BOUNDING_RECT_TEXT_PADDING,
        top + textHeight + BOUNDING_RECT_TEXT_PADDING,
        textBackgroundPaint
      );

      // Draw text for detected object
      canvas.drawText(drawableText, left, top + bounds.height(), textPaint);
    }
  }

  public void setResults(
    List<Detection> detectionResults,
    int imageHeight,
    int imageWidth
  ) {
    results = detectionResults;

    scaleFactor = Math.max(getWidth() * 1f / imageWidth, getHeight() * 1f / imageHeight);
  }

}
