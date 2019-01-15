package com.xson.common.app;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.umeng.analytics.MobclickAgent;
import com.xson.common.R;

import butterknife.ButterKnife;


/**
 * @author Milk <249828165@qq.com>
 */
public abstract class BaseFragment extends Fragment {

    protected Context context;
    public abstract int getContentViewId();
    protected abstract void initAllMembersView(Bundle savedInstanceState);
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.context = getActivity();
        View mRootView = inflater.inflate(getContentViewId(),null);
        ButterKnife.inject(this,mRootView);
        initAllMembersView(savedInstanceState);
        return mRootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        ((BaseApp)getContext().getApplicationContext()).watch(this);
        ButterKnife.reset(this);
    }
    /**
     * 设置导航图标为返回按钮
     * @param toolbar
     */
    protected void setUpButton(Toolbar toolbar) {
//        ((ActionBarActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!getFragmentManager().popBackStackImmediate())
                    getActivity().finish();
            }
        });
    }

    /**
     * 在当前Fragment开启另外一个Fragment
     * @param fragment
     */
    protected void startFragment(Fragment fragment) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left);
        ft.replace(getId(), fragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(getClass().getName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(getClass().getName());
    }
}
