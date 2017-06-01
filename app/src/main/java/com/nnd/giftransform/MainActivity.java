package com.nnd.giftransform;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.RelativeLayout;

import com.nnd.easygiftransform.EasyTransformGif;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.droidsonroids.gif.AnimationListener;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.container) RelativeLayout relativeLayout;
    @BindView(R.id.gifView) EasyTransformGif easyTransformGif;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        //transformableImage.setImageDrawable(getResources().getDrawable(R.drawable.image_128));
        easyTransformGif.setGif(R.drawable.cat);
        easyTransformGif.setBorderWidth(12);
        easyTransformGif.setBorderColor(Color.GREEN);
        easyTransformGif.setCornerRadius(67);
        easyTransformGif.setFillerColor(Color.BLACK);
        easyTransformGif.setFillerAlpha(25);
        easyTransformGif.stopGif();

        easyTransformGif.startGif(5);
        easyTransformGif.setGifListener(new AnimationListener() {
            @Override
            public void onAnimationCompleted(int loopNumber) {
                Timber.i("loop_number = " + loopNumber);
            }
        });
    }
}
