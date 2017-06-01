package com.nnd.easygiftransform.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.FloatRange;

import pl.droidsonroids.gif.transforms.Transform;
import timber.log.Timber;

/**
 * Created by Android dev on 6/1/17.
 */

public class GifTransformation implements Transform {
    private final RectF gifDstRect = new RectF();
    private final RectF borderRect = new RectF();
    private float cornerRadius;
    private float borderWidth;
    private Shader shader;
    private Paint borderPaint;
    private Paint fillerPaint;
    private int borderColor;
    private int fillerColor = 0;
    private int alpha;
    private Path borderPath;
    private PorterDuff.Mode filterMode;


    /**
     * Custom gif transformation
     *
     * @param cornerRadius corner radius for gif
     * @param borderWidth  border width for gif
     */
    public GifTransformation(@FloatRange(from = 0) float cornerRadius, @FloatRange(from = 0) float borderWidth) {
        setCornerRadius(cornerRadius);
        setBorderWidth(borderWidth);
        init();
    }

    private void init() {
        borderPath = new Path();
        alpha = 100;

        borderPaint = new Paint();
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeCap(Paint.Cap.ROUND);
        borderPaint.setStrokeJoin(Paint.Join.ROUND);
        borderPaint.setColor(Color.BLACK);
        borderPaint.setDither(true);
        borderPaint.setPathEffect(new CornerPathEffect(cornerRadius));
        borderPaint.setStrokeWidth(borderWidth);
        borderPaint.setAntiAlias(true);

        fillerPaint = new Paint();
        fillerPaint.setAntiAlias(true);
        fillerPaint.setStyle(Paint.Style.FILL);
        fillerPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));

        Timber.i("init_done_here");
    }

    /**
     * @return The corner radius applied when drawing this drawable. 0 when drawable is not rounded.
     */
    @FloatRange(from = 0)
    public float getCornerRadius() {
        return cornerRadius;
    }

    /**
     * Sets the corner radius to be applied when drawing the bitmap.
     *
     * @param cornerRadius corner radius or 0 to remove rounding
     */
    public GifTransformation setCornerRadius(@FloatRange(from = 0) float cornerRadius) {
        cornerRadius = Math.max(0, cornerRadius);
        if (cornerRadius == this.cornerRadius) {
            return this;
        }
        this.cornerRadius = cornerRadius;
        shader = null;
        return this;
    }

    /**
     * Set border width for gif
     * @param borderWidth
     * @return
     */
    public GifTransformation setBorderWidth(@FloatRange(from = 0) float borderWidth) {
        borderWidth = Math.max(0, borderWidth);
        if (borderWidth == this.borderWidth) {
            return this;
        }
        this.borderWidth = borderWidth;
        shader = null;

        return this;
    }

    /**
     * Set border color for gif
     * @param borderColor Color code
     * @return
     */
    public GifTransformation setBorderColor(int borderColor) {
        if (borderColor == this.borderColor) {
            return this;
        }
        this.borderColor = borderColor;
        shader = null;
        return this;
    }

    /**
     * Set fill color of the image with default PorterDuff mode (SRC_OVER)
     * @param fillerColor Color code
     */
    private void setFillerColor(int fillerColor) {
        if (fillerColor == this.fillerColor) {
            return;
        }
        this.fillerColor = fillerColor;
        shader = null;
    }

    /**
     * Set fill color of the image with selected PorterDuff mode
     * @param fillerColor Color code
     * @param filterMode Filter mode
     * @return
     */
    public GifTransformation setFillerColor(int fillerColor, PorterDuff.Mode filterMode) {
        setFillerColor(fillerColor);
        setFilterMode(filterMode);

        return this;
    }

    private void setFilterMode(PorterDuff.Mode filterMode) {
        if (filterMode == this.filterMode) {
            return;
        }
        this.filterMode = filterMode;
        shader = null;
    }

    /**
     * Set alpha of the fill
     * @param alpha
     * @return
     */
    public GifTransformation setFillAlpha(int alpha) {
        if (alpha == this.alpha) {
            return this;
        }
        this.alpha = alpha;
        shader = null;
        return this;
    }

    @Override
    public void onBoundsChange(Rect bounds) {
        gifDstRect.set(bounds);
        borderRect.set(bounds);
        shader = null;
    }

    @Override
    public void onDraw(Canvas canvas, Paint paint, Bitmap buffer) {
        float newB = borderWidth / 2;

        if (cornerRadius == 0 & borderWidth == 0 & fillerColor == 0) {
            canvas.drawBitmap(buffer, null, gifDstRect, paint);
            return;
        }

        if (shader == null) {
            shader = new BitmapShader(buffer, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            final Matrix shaderMatrix = new Matrix();
            shaderMatrix.setTranslate(gifDstRect.left, gifDstRect.top);
            shaderMatrix.preScale(gifDstRect.width() / buffer.getWidth(), gifDstRect.height() / buffer
                    .getHeight());
            shader.setLocalMatrix(shaderMatrix);
        }

        paint.setPathEffect(new CornerPathEffect(0));
        paint.setShader(shader);
        canvas.drawRoundRect(gifDstRect, cornerRadius, cornerRadius, paint);

        if (fillerColor != 0) {
            fillerPaint.setColor(fillerColor);
            fillerPaint.setAlpha(255 * alpha / 100);
            fillerPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
            canvas.drawRoundRect(gifDstRect, cornerRadius, cornerRadius, fillerPaint);
        }

        if (borderWidth > 0) {
            updateBorderPaint(newB);

            borderPath.moveTo(newB, 0 - newB);
            borderPath.lineTo(gifDstRect.width() - newB, 0 - newB);
            borderPath.lineTo(gifDstRect.width() - newB, gifDstRect.height());
            borderPath.lineTo(newB, gifDstRect.height());
            borderPath.close();

            canvas.drawPath(borderPath, borderPaint);
        }
    }

    private void updateBorderPaint(float newB) {
        float pathEffect = cornerRadius;

        if (cornerRadius > 0) pathEffect = cornerRadius - newB;

        Timber.i(cornerRadius + "current_path_eff_rad");

        borderPaint.setPathEffect(new CornerPathEffect(pathEffect));
        borderPaint.setStrokeWidth(borderWidth);
        borderPaint.setColor(borderColor);
    }
}
