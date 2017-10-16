package lava.bluepay.com.lavaapp.view.activity;

import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import lava.bluepay.com.lavaapp.R;
import lava.bluepay.com.lavaapp.view.widget.video.FullScreenVideoView;

/**
 * Created by bluepay on 2017/10/16.
 */

public class PlayVideoActivity extends BaseActivity {

    FullScreenVideoView video_view;
    ProgressBar pb_video;
    LinearLayout ll_video_bottom_layout;
    TextView tv_video_bottom_played;
    SeekBar seekbar_video_bottom;
    TextView tv_video_bottom_not_played;


    private SeekBar.OnSeekBarChangeListener videoProgressListener ;
    private AudioManager mAudioManager;
    /**
     * 最大声音
     */
    private int mMaxVolume;
    /**
     * 当前声音
     */
    private int mVolume = -1;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//常量
        full(false);
        setContentView(R.layout.activity_play_video);
        initViews();

    }
    private void initViews(){
        video_view = (FullScreenVideoView) findViewById(R.id.video_view);
        pb_video = (ProgressBar) findViewById(R.id.pb_video);
        ll_video_bottom_layout = (LinearLayout) findViewById(R.id.ll_video_bottom_layout);
        tv_video_bottom_played = (TextView) findViewById(R.id.tv_video_bottom_played);
        seekbar_video_bottom = (SeekBar) findViewById(R.id.seekbar_video_bottom);
        tv_video_bottom_not_played = (TextView) findViewById(R.id.tv_video_bottom_not_played);

        //声音
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        videoProgressListener = new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        };

        seekbar_video_bottom.setOnSeekBarChangeListener(videoProgressListener);

    }

    /**
     * 动态设置状态栏是否显示
     *
     * @param enable 1、true 显示  2、false 不显示
     */
    private void full(boolean enable) {
        if (!enable) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getWindow().setAttributes(lp);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else {
            WindowManager.LayoutParams attr = getWindow().getAttributes();
            attr.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setAttributes(attr);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

}
