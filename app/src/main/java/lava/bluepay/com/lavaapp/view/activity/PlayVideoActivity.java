package lava.bluepay.com.lavaapp.view.activity;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import lava.bluepay.com.lavaapp.R;
import lava.bluepay.com.lavaapp.common.FormatUtils;
import lava.bluepay.com.lavaapp.common.Logger;
import lava.bluepay.com.lavaapp.view.bean.VideoBean;
import lava.bluepay.com.lavaapp.view.widget.video.FullScreenVideoView;

/**
 * Created by bluepay on 2017/10/16.
 */

public class PlayVideoActivity extends BaseActivity {



    LinearLayout header_view;
    TextView tv_video_title;
    ImageView iv_back;

    FullScreenVideoView video_view;
    ProgressBar pb_video;
    LinearLayout ll_video_bottom_layout;
    TextView tv_video_bottom_played;
    SeekBar seekbar_video_bottom;
    TextView tv_video_bottom_total_size;

    RelativeLayout error_layout;
    ImageView video_play_state;

    //是否显示加载图层（progressBar）
    private boolean bCanShow = true;
    //是否显示布局图层（顶部、底部、播放状态）
    private boolean bControllerHide = true;
    //视屏总时长
    private static int videoLength;
    //进度百分比
    private static int progressValue;

    private VideoBean video;


    private AudioManager mAudioManager;

    /**
     * 最大声音
     */
    private int mMaxVolume;
    /**
     * 当前声音
     */
    private int mVolume = -1;

    private SeekBar.OnSeekBarChangeListener videoProgressListener ;

    private MediaPlayer.OnErrorListener videoErrorListener;
    private MediaPlayer.OnCompletionListener videoCompleteListener;
    private MediaPlayer.OnPreparedListener videoPreparedListerner;
    private MediaPlayer.OnInfoListener videoInfoListener;

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
        initPlayer();
        initData();
    }

    /**
     * 初始化界面
     */
    private void initViews(){
        header_view = (LinearLayout) findViewById(R.id.header_view);
        tv_video_title = (TextView) findViewById(R.id.tv_video_title);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        video_view = (FullScreenVideoView) findViewById(R.id.video_view);
        pb_video = (ProgressBar) findViewById(R.id.pb_video);
        ll_video_bottom_layout = (LinearLayout) findViewById(R.id.ll_video_bottom_layout);
        tv_video_bottom_played = (TextView) findViewById(R.id.tv_video_bottom_played);
        seekbar_video_bottom = (SeekBar) findViewById(R.id.seekbar_video_bottom);
        tv_video_bottom_total_size = (TextView) findViewById(R.id.tv_video_bottom_total_size);

        error_layout = (RelativeLayout) findViewById(R.id.error_layout);
        video_play_state = (ImageView) findViewById(R.id.video_play_state);
        video_play_state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showVideoControllerLayout();
                if (video_view.isPlaying()) {
                    pause();
                    video_play_state.setImageResource(R.drawable.play);
                } else {
                    if (progressValue > 0) {
                        Logger.i(Logger.DEBUG_TAG, "playVideoActivity --- > doClick : progressValue :" + progressValue);
                        video_view.start();
                        updateVideoProgress();
                    } else {
                        playVideo();
                    }
                    video_play_state.setImageResource(R.drawable.stop);
                }
            }
        });
        //声音
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        videoProgressListener = new SeekBar.OnSeekBarChangeListener(){

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                video_view.removeCallbacks(hideLayoutTask);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                progressValue = seekBar.getProgress();
                video_view.seekTo(progressValue);
                video_view.start();
                updateVideoProgress();
                hideVideoController();
            }
        };

        seekbar_video_bottom.setOnSeekBarChangeListener(videoProgressListener);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        resume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        pause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            video_view.setOnInfoListener(null);
        }
        mGestureDetector = null;
        myGestureListener = null;
        if(seekbar_video_bottom != null){
            seekbar_video_bottom.setOnSeekBarChangeListener(null);
            seekbar_video_bottom.removeCallbacks(mUpdateProgress);
        }
        if(video_view!=null){
            video_view.setOnCompletionListener(null);
            video_view.setOnPreparedListener(null);
            video_view.setOnErrorListener(null);
            video_view.removeCallbacks(hideLayoutTask);
            video_view.stopPlayback();
            ((ViewGroup)(video_view.getParent())).removeView(video_view);
            video_view = null;
        }
        Logger.e("TT","onDestroy");
        super.onDestroy();
    }

    //region=====================VideoView相关=================

    /**
     * 初始化MediaPlayer
     */
    private void initPlayer(){
        videoErrorListener = new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                cancelProgressBar();
                showVideoControllerLayout();
                Logger.e("TT","视频播放错误");
                return true;
            }
        };
        videoPreparedListerner = new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                full(true);
                cancelProgressBar();
                video_view.start();
                videoLength = video_view.getDuration();
                seekbar_video_bottom.setMax(videoLength);
                seekbar_video_bottom.setProgress(progressValue);
                updateVideoProgress();
            }
        };
        videoCompleteListener = new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stop();
            }
        };
        video_view.setOnErrorListener(videoErrorListener);
        video_view.setOnPreparedListener(videoPreparedListerner);
        video_view.setOnCompletionListener(videoCompleteListener);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            videoInfoListener = new MediaPlayer.OnInfoListener() {
                @Override
                public boolean onInfo(MediaPlayer mp, int what, int extra) {//特殊警告出现时
                    if(what == MediaPlayer.MEDIA_INFO_BUFFERING_START){
                        showProgressBar();
                    }else if(what == MediaPlayer.MEDIA_INFO_BUFFERING_END){
                        if(mp.isPlaying()){
                            cancelProgressBar();
                        }
                    }
                    return true;
                }
            };
            video_view.setOnInfoListener(videoInfoListener);
        }
    }


    private void stop(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_video_bottom_played.setText(tv_video_bottom_total_size.getText().toString());
                seekbar_video_bottom.setProgress(100);
            }
        });

        progressValue = 0;
        video_view.pause();
        showVideoControllerLayout();
    }

    private void playVideo(){
        if(TextUtils.isEmpty(video.getVideoUrl())){
            return;
        }
        video_view.setVideoURI(Uri.parse(video.getVideoUrl()));
        showProgressBar();
        video_view.seekTo(0);
        video_view.start();
        video_view.requestFocus();
    }

    private void pause(){
        progressValue = video_view.getCurrentPosition();
        video_view.pause();
    }
    private void resume(){
        if(progressValue>0){
            video_view.seekTo(progressValue);
            video_view.start();
        }
    }

    //endregion=====================VideoView相关=================

    //region============手势控制===============================

    private GestureDetector mGestureDetector;
    private MyGestureListener myGestureListener;
    public class MyGestureListener extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if(bCanShow && bControllerHide){
                showVideoControllerLayout();
            }else if(!bControllerHide){
                hideLayout();
            }
            return super.onSingleTapUp(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    }

    private GestureDetector getmGestureDetector(){
        if(mGestureDetector == null){
            myGestureListener = new MyGestureListener();
            mGestureDetector = new GestureDetector(this,myGestureListener);
        }
        return mGestureDetector;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(getmGestureDetector().onTouchEvent(event)){
            return true;
        }
        return super.onTouchEvent(event);
    }


    //endregion============手势控制===============================

    private void initData(){
        video = new VideoBean("视频Title","http://192.168.4.210:8168/resources/video/10.mp4");
        if(tv_video_title!=null) {
            tv_video_title.setText(video.getVideoTitle());
        }
        playVideo();
    }


    //region===================任务相关==================================================
    /**
     * 进度条更新开启任务
     */
    private void updateVideoProgress(){
        seekbar_video_bottom.removeCallbacks(mUpdateProgress);
        seekbar_video_bottom.post(mUpdateProgress);
    }

    /**
     * 隐藏video控制布局任务开启
     */
    private void hideVideoController(){
        video_view.removeCallbacks(hideLayoutTask);
        video_view.postDelayed(hideLayoutTask,3000);
    }
    /**
     * 隐藏布局任务
     */
    public Runnable hideLayoutTask = new Runnable() {
        @Override
        public void run() {
            hideLayout();
        }
    };

    /**
     * 进度条更新任务
     */
    public Runnable mUpdateProgress = new Runnable() {
        @Override
        public void run() {
            if(video_view.isPlaying()){
                seekbar_video_bottom.postDelayed(mUpdateProgress,1000);
            }
            int progressValue = video_view.getCurrentPosition();
            seekbar_video_bottom.setProgress(progressValue);
            tv_video_bottom_played.setText(FormatUtils.formatToVideoPlayTime(progressValue/1000));
            tv_video_bottom_total_size.setText(FormatUtils.formatToVideoPlayTime(videoLength/1000));
        }
    };

    //endregion===================任务相关==================================================




    //region===================布局显示与隐藏相关==================================================

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

    /**
     * 取消加载图层
     */
    private void cancelProgressBar(){
        bCanShow = true;
        video_view.setEnabled(true);
        pb_video.setVisibility(View.GONE);
    }

    /**
     * 显示加载图层
     */
    private void showProgressBar(){
        bCanShow = false;
        hideLayout();
        video_view.setEnabled(false);
        pb_video.setVisibility(View.VISIBLE);
    }

    /**
     * 显示布局
     */
    private void showVideoControllerLayout(){
        bControllerHide = false;
        full(false);
        error_layout.setVisibility(View.VISIBLE);
        ll_video_bottom_layout.setVisibility(View.VISIBLE);
        header_view.setVisibility(View.VISIBLE);
        if(video_view.isPlaying()){
            showPause();
        }else{
            showPlay();
        }
        hideVideoController();
    }

    private void hideLayout(){
        bControllerHide = true;
        full(true);
        error_layout.setVisibility(View.GONE);
        header_view.setVisibility(View.GONE);
        ll_video_bottom_layout.setVisibility(View.GONE);
    }

    /**
     * 显示播放状态
     */
    private void showPlay(){
        video_play_state.setImageResource(R.drawable.play);
    }

    /**
     * 显示暂停状态
     */
    private void showPause(){
        video_play_state.setImageResource(R.drawable.stop);
    }

    //endregion===================布局显示与隐藏相关==================================================
}
