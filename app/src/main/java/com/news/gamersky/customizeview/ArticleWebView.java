package com.news.gamersky.customizeview;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

public class ArticleWebView extends WebView {

    public ArticleWebView(Context context) {
        super(context);
        init();
    }

    public ArticleWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ArticleWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public ArticleWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }


    @Override
    public boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        return super.overScrollBy(0, deltaY, 0, scrollY, 0, scrollRangeY, 0, maxOverScrollY, isTouchEvent);
    }

    public void init() {
    }

}
