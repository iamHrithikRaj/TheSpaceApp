package com.project.space;
import android.animation.ArgbEvaluator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

public class TabsView extends FrameLayout implements ViewPager.OnPageChangeListener {
    private ImageView mCenterImage;
    private ImageView mStartImage;
    private ImageView mEndImage;
    private ImageView mBottomImage;
    private View mIndicator;
    private ArgbEvaluator mArgbEvaluator;
    private int mEndViewsTranslationX;
    private int mIndicatorTranslationX;
    private int mCenterTranslationY;

    private  int centreColor;
    private int sideColor;

    public TabsView(@NonNull Context context) {
        this(context, null);
    }

    public TabsView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabsView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setUpWithViewPager(final ViewPager viewPager){
        viewPager.addOnPageChangeListener(this);
        mStartImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewPager.getCurrentItem() != 0){
                    viewPager.setCurrentItem(0);
                }
            }
        });
        mEndImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewPager.getCurrentItem() != 2){
                    viewPager.setCurrentItem(2);
                }
            }
        });
        mCenterImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewPager.getCurrentItem() != 1){
                    viewPager.setCurrentItem(1);
                }
            }
        });
    }


    private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.view_tabs,this,true);
        mCenterImage = findViewById(R.id.center_image);
        mStartImage = findViewById(R.id.start_image);
        mEndImage = findViewById(R.id.end_image);
        mBottomImage = findViewById(R.id.reference);
        mIndicator = findViewById(R.id.indicator);

        /*centreColor = ContextCompat.getColor(getContext(),R.color.white);
        sideColor = ContextCompat.getColor(getContext(),R.color.dark_grey);*/

       mArgbEvaluator = new ArgbEvaluator();
        mIndicatorTranslationX = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,80,getResources().getDisplayMetrics());
        mBottomImage.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mEndViewsTranslationX =(int) (mBottomImage.getX() - mStartImage.getX()) - mIndicatorTranslationX;
                mBottomImage.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                mCenterTranslationY = getHeight() - mBottomImage.getBottom();
            }
        });
    }
    private void moveViews(float fractionFromCenter){
        mStartImage.setTranslationX(fractionFromCenter*mEndViewsTranslationX);
        mEndImage.setTranslationX(-fractionFromCenter*mEndViewsTranslationX);
        mIndicator.setAlpha(fractionFromCenter);
        mIndicator.setScaleX(fractionFromCenter);
    }
    private void moveAndScaleCenter(float fractionFromCenter){
        float scale = .7f + ((1 - fractionFromCenter) * .3f);
        mCenterImage.setScaleX(scale);
        mCenterImage.setScaleY(scale);

        int translation = (int) (fractionFromCenter *mCenterTranslationY);

        mCenterImage.setTranslationY(translation);
        mCenterImage.setTranslationY(translation);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if(position == 0){
            setColor(1-positionOffset);
            moveViews(1-positionOffset);
            moveAndScaleCenter(1-positionOffset);
            mIndicator.setTranslationX((positionOffset-1)*mIndicatorTranslationX);

        }else if(position == 1){
            setColor(positionOffset);
            moveViews(positionOffset);
            moveAndScaleCenter(positionOffset);
            mIndicator.setTranslationX(positionOffset*mIndicatorTranslationX);
        }
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
    private void setColor(float fractionFromCenter){
       // int color = (int) mArgbEvaluator.evaluate(fractionFromCenter,centreColor,sideColor);
        /*mCenterImage.setColorFilter(color);
        mStartImage.setColorFilter(color);
        mEndImage.setColorFilter(color);*/
    }
}
