package lava.bluepay.com.lavaapp.view.activity;

import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import lava.bluepay.com.lavaapp.R;
import lava.bluepay.com.lavaapp.view.widget.NewVPIndicator;

/**
 * Created by bluepay on 2017/10/9.
 */

public class BaseActivity extends AppCompatActivity {

    protected Toolbar toolbar;
    protected TextView tv_title;
    protected NewVPIndicator indicator;



    public void initToolbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tv_title = (TextView) findViewById(R.id.tv_toolbar_title);
        indicator = (NewVPIndicator) findViewById(R.id.indicator);

        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            if(TextUtils.isEmpty(toolbar.getTitle().toString())){//默认的toolbar标题
                setUITitle(toolbar.getTitle().toString());
            }
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }


    /**
     * toolbar中间设置标题
     * @param title
     */
    protected void setUITitle(String title){
        if(!TextUtils.isEmpty(title)){
            tv_title.setVisibility(View.VISIBLE);
            tv_title.setText(title);
            indicator.setVisibility(View.GONE);
        }
    }

    /**
     * toolbar中间设置viewPager的指示条
     * @param viewpager
     * @param titles
     */
    protected void setUiIndicator(ViewPager viewpager, String[] titles) {
        if(viewpager!=null && titles.length>0){
            tv_title.setVisibility(View.GONE);

            indicator.setmTitles(titles);
            indicator.setViewPager(viewpager);
            indicator.setVisibility(View.VISIBLE);
        }
    }



}
