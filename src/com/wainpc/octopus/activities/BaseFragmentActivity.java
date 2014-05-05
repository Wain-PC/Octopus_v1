package com.wainpc.octopus.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.wainpc.octopus.R;

public abstract class BaseFragmentActivity extends FragmentActivity {
    private enum ViewType {
        LIST, LOADING, ERROR
    };

    private ViewPager mPager = null; 
    private View mLoadingView = null;
    private View mErrorView = null;
    private ViewType mCurrentView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
    /** Returns the layout resource Id associated with this activity */
    protected abstract int getLayoutId();
    
    /** Reloads UI on the page */
    protected void resetUI() {

        // completely reload the root view, get loading and error views
        this.setContentView(this.getLayoutId());
        this.mPager = (ViewPager) this.findViewById(R.id.pager);
        this.mLoadingView = this.findViewById(R.id.loading);
        this.mErrorView = this.findViewById(R.id.error);
        this.switchToView(this.mCurrentView);
    }

    /** Shows the loading indicator */
    protected void switchToLoadingView() {
        this.switchToView(ViewType.LOADING);
    }

    /** Shows the normal list */
    protected void switchToListView() {
        this.switchToView(ViewType.LIST);
    }

    /** Shows the error message */
    protected void switchToErrorView(String message) {
        this.switchToView(ViewType.ERROR);

        TextView errorTextView = (TextView) this.mErrorView.findViewById(R.id.error_message);
        errorTextView.setText(message != null ? message : this.getString(R.string.error_unknown));
    }

    /** Switches the page between the list view, loading view and error view */
    private void switchToView(ViewType vt) {
        this.mCurrentView = vt;

        if (vt == null) {
            return;
        }

        switch (vt) {
            case LIST:
                this.mPager.setVisibility(View.VISIBLE);
                this.mLoadingView.setVisibility(View.GONE);
                this.mErrorView.setVisibility(View.GONE);
                break;
            case LOADING:
                this.mPager.setVisibility(View.GONE);
                this.mLoadingView.setVisibility(View.VISIBLE);
                this.mErrorView.setVisibility(View.GONE);
                break;
            case ERROR:
                this.mPager.setVisibility(View.GONE);
                this.mLoadingView.setVisibility(View.GONE);
                this.mErrorView.setVisibility(View.VISIBLE);
                break;
        }
    }
}
