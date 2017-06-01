package com.nnd.easygiftransform;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;

import com.glidebitmappool.GlideBitmapPool;
import com.nnd.easygiftransform.utils.GifTransformation;
import com.nnd.easygiftransform.utils.RoundedDrawable;

import pl.droidsonroids.gif.AnimationListener;
import pl.droidsonroids.gif.GifDrawable;
import timber.log.Timber;

/**
 * Created by Android dev on 5/31/17.
 */

public class EasyTransformGif extends AppCompatImageView {
    private static final int DEFAULT_COLOR = Color.BLACK;
    int cornerRadius;
    int borderWidth;
    int mMargin;
    int borderColor;
    int fillerColor;
    int alpha;
    boolean isOval;
    GifDrawable gifDrawable;
    Context context;
    private GifTransformation gifTransformation;

    /**
     * Transformable Image
     *
     * @param context current context
     */
    public EasyTransformGif(Context context) {
        super(context);
        init();
    }

    /**
     * @param context current context
     * @param attrs   set of attribute
     */
    public EasyTransformGif(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        Timber.plant(new Timber.DebugTree());
        GlideBitmapPool.initialize(10 * 1024 * 1024);
        this.fillerColor = 0;
        this.alpha = 100;
        this.borderColor = DEFAULT_COLOR;
        this.cornerRadius = 0;
        this.borderWidth = 0;

        gifTransformation = new GifTransformation(0, 0);
    }

    /**
     * Set width of border
     *
     * @param borderWidth width of border
     */
    public void setBorderWidth(int borderWidth) {
        this.borderWidth = borderWidth;
        invalidateGif();
    }

    /**
     * Get current corner radius
     *
     * @return current corner radius
     */
    public int getCornerRadius() {
        return this.cornerRadius;
    }

    /**
     * Set radius of corner <br/>
     * 0 for no corner
     *
     * @param cornerRadius radius for corner
     */
    public void setCornerRadius(int cornerRadius) {
        this.cornerRadius = cornerRadius;
        invalidateGif();
    }

    private void invalidateGif() {
        gifTransformation.setFillAlpha(alpha)
                .setCornerRadius(cornerRadius)
                .setBorderWidth(borderWidth)
                .setBorderColor(borderColor)
                .setFillerColor(fillerColor, PorterDuff.Mode.SRC_OVER);
    }

    /**
     * Set color of border
     *
     * @param color Hex color code
     */
    public void setBorderColor(int color) {
        this.borderColor = color;
        invalidateGif();
    }

    /**
     * Set color of border
     *
     * @param color String color that will be parsed
     */
    public void setBorderColor(String color) {
        this.borderColor = Color.parseColor(color);
        invalidateGif();
    }

    public int getFillerColor() {
        return fillerColor;
    }

    /**
     * Set the color on top of the view with Mode default DST_OVER
     *
     * @param fillerColor Color code
     */
    public void setFillerColor(int fillerColor) {
        setFillerColor(fillerColor, PorterDuff.Mode.SRC_OVER);
    }

    /**
     * Set the color on top of the view with mode preference
     *
     * @param fillerColor Color code
     * @param filterMode  PorterDuff mode
     */
    public void setFillerColor(int fillerColor, PorterDuff.Mode filterMode) {
        this.fillerColor = fillerColor;
        invalidateGif();
    }

    /**
     * Override setImageDrawable to apply transformation for drawable before put it in the view container.
     *
     * @param drawable GifDrawable for gif type and other drawable is acceptable.
     */
    @Override
    public void setImageDrawable(Drawable drawable) {
        if (drawable instanceof GifDrawable) {
            gifDrawable = (GifDrawable) drawable;
            transformGifDrawable((GifDrawable) drawable);
            startGif();
        } else {
            RoundedDrawable rd = RoundedDrawable.fromBitmap(RoundedDrawable.drawableToBitmap(drawable));
            rd.setCornerRadius(this.cornerRadius);

            drawable = RoundedDrawable.fromDrawable(rd);
            gifDrawable = null;
        }
        super.setImageDrawable(drawable);
    }

    /**
     * Transform gif drawable so it has rounded corner.
     *
     * @param drawable gif drawable
     */
    private void transformGifDrawable(GifDrawable drawable) {
        gifTransformation.setBorderWidth(borderWidth);
        gifTransformation.setCornerRadius(cornerRadius);
        drawable.setTransform(gifTransformation);
    }

    /**
     * Change the shape of view become oval or not
     *
     * @param isOval true for set view become oval
     */
    public void setOval(boolean isOval, Drawable d) {
        this.isOval = isOval;

        if (isOval) {
            RoundedDrawable rd = RoundedDrawable.fromBitmap(RoundedDrawable.drawableToBitmap(d));
            rd.setOval(isOval);
            rd.setColorFilter(fillerColor, PorterDuff.Mode.DST_OVER);
            setImageBitmap(RoundedDrawable.drawableToBitmap(rd));

            setBackgroundColor(Color.TRANSPARENT);
        } else {
            setImageDrawable(d);
        }

        invalidate();
    }

    /**
     * Set alpha of gif fill color.
     *
     * @param alpha 0 ~ 100 | 100 for full visibility
     */
    public void setFillerAlpha(int alpha) {
        this.alpha = alpha;
        invalidateGif();
    }

    private void setColor(int color) {
        GradientDrawable gd = (GradientDrawable) getDrawable();
        gd.setColor(color);
    }

    public void setGif(int resourceId) {
        setImageDrawable(GifDrawable.createFromResource(getResources(), resourceId));
    }

    /**
     * Play gif indefinitely
     */
    public void startGif() {
        startGif(0);
    }

    /**
     * Play gif with given loop count
     *
     * @param loopCount set the loop count of gif. Zero (0) for infinite.
     */
    public void startGif(int loopCount) {
        try {
            gifDrawable.setLoopCount(loopCount);
            gifDrawable.reset();
        } catch (NullPointerException e) {
            Log.e(getClass().getSimpleName(), "Gif Drawable is null in " + getClass().getSimpleName());
        }
    }

    boolean isGifLoaded() {
        return gifDrawable != null;
    }

    /**
     * Stop gif file loop
     */
    public void stopGif() {
        if (gifDrawable != null && gifDrawable.isRunning()) gifDrawable.stop();
    }

    /**
     * Set listener after single loop animation played
     *
     * @param gifListener Listener
     */
    public void setGifListener(AnimationListener gifListener) {
        if (gifDrawable == null) {
            return;
        }
        gifDrawable.addAnimationListener(gifListener);
    }
}
