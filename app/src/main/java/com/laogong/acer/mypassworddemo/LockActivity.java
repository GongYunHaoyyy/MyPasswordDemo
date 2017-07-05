package com.laogong.acer.mypassworddemo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;
import com.andrognito.patternlockview.utils.ResourceUtils;
import com.andrognito.rxpatternlockview.RxPatternLockView;
import com.andrognito.rxpatternlockview.events.PatternLockCompleteEvent;
import com.andrognito.rxpatternlockview.events.PatternLockCompoundEvent;

import java.util.List;

import io.reactivex.functions.Consumer;

public class LockActivity extends AppCompatActivity {

    private PatternLockView mPatternLockView;
    private PatternLockViewListener mPatternLockViewListener = new PatternLockViewListener() {
        @Override
        public void onStarted() {
            Log.d(getClass().getName(), "Pattern drawing started");
        }

        @Override
        public void onProgress(List<PatternLockView.Dot> progressPattern) {
            Log.d(getClass().getName(), "Pattern progress: " +
                    PatternLockUtils.patternToString(mPatternLockView, progressPattern));
        }

        @Override
        public void onComplete(List<PatternLockView.Dot> pattern) {
            Log.d(getClass().getName(), "Pattern complete: " +
                    PatternLockUtils.patternToString(mPatternLockView, pattern));
        }

        @Override
        public void onCleared() {
            Log.d(getClass().getName(), "Pattern has been cleared");
        }
    };
    //--------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_lock );
        TextView textView=(TextView)findViewById( R.id.test_tv );
        final TextView test=(TextView)findViewById( R.id.profile_name );
        //记录输入错误次数
        final int[] wrongpw = {5};


        //判断是否时第一次进入
        SharedPreferences sharedPreferences = this.getSharedPreferences("share", MODE_PRIVATE);
        final boolean isFirstRun = sharedPreferences.getBoolean("isFirstRun", true);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (isFirstRun)
        {
            textView.setText( "第一次使用，录入新密码" );
            editor.putBoolean("isFirstRun", false);
            editor.commit();
        } else
        {
            textView.setText( "绘制密码以进入" );
        }

        //-----------------------------------------------------------

        mPatternLockView = (PatternLockView) findViewById(R.id.patter_lock_view);
        mPatternLockView.setDotCount(3);
        mPatternLockView.setDotNormalSize((int) ResourceUtils.getDimensionInPx(this, R.dimen.pattern_lock_dot_size));
        mPatternLockView.setDotSelectedSize((int) ResourceUtils.getDimensionInPx(this, R.dimen.pattern_lock_dot_selected_size));
        mPatternLockView.setPathWidth((int) ResourceUtils.getDimensionInPx(this, R.dimen.pattern_lock_path_width));
        mPatternLockView.setAspectRatioEnabled(true);
        mPatternLockView.setAspectRatio(PatternLockView.AspectRatio.ASPECT_RATIO_HEIGHT_BIAS);
        mPatternLockView.setViewMode(PatternLockView.PatternViewMode.CORRECT);
        mPatternLockView.setDotAnimationDuration(150);
        mPatternLockView.setPathEndAnimationDuration(100);
        mPatternLockView.setCorrectStateColor(ResourceUtils.getColor(this, R.color.white));
        mPatternLockView.setInStealthMode(false);
        mPatternLockView.setTactileFeedbackEnabled(true);
        mPatternLockView.setInputEnabled(true);
        mPatternLockView.addPatternLockListener(mPatternLockViewListener);

        RxPatternLockView.patternComplete(mPatternLockView)
                .subscribe(new Consumer<PatternLockCompleteEvent>() {
                    @Override
                    public void accept(PatternLockCompleteEvent patternLockCompleteEvent) throws Exception {
                        Log.d(getClass().getName(), "Complete: " + patternLockCompleteEvent.getPattern().toString());
                    }
                });

        RxPatternLockView.patternChanges(mPatternLockView)
                .subscribe(new Consumer<PatternLockCompoundEvent>() {
                    @Override
                    public void accept(PatternLockCompoundEvent event) throws Exception {
                        if (event.getEventType() == PatternLockCompoundEvent.EventType.PATTERN_STARTED) {
                            test.setText( "开始绘制" );
                        } else if (event.getEventType() == PatternLockCompoundEvent.EventType.PATTERN_PROGRESS) {
                            test.setText( "绘制中..." );
                        } else if (event.getEventType() == PatternLockCompoundEvent.EventType.PATTERN_COMPLETE) {
                            //获取得到string形式的密码
                            String password=PatternLockUtils.patternToString(mPatternLockView, event.getPattern());
                            int passwordlength=password.length();
                            if (passwordlength<=3){
                                test.setText( "至少连接4个点,请重试" );
                            }else {
                                test.setText( "绘制完成" );
                                Intent intent=new Intent( LockActivity.this,Main2Activity.class );
                                if (isFirstRun){
                                    SharedPreferences.Editor editor=getSharedPreferences( "data",MODE_PRIVATE ).edit();
                                    editor.putString( "oldpassword", password);
                                    editor.apply();
                                    Toast.makeText( LockActivity.this,"密码设置成功",Toast.LENGTH_SHORT ).show();
                                    startActivity( intent );
                                    finish();
                                }else {
                                    SharedPreferences pref=getSharedPreferences( "data",MODE_PRIVATE );
                                    String opassword=pref.getString( "oldpassword","" );
                                    if (opassword.equals( password )){
                                        Toast.makeText( LockActivity.this,"密码正确",Toast.LENGTH_SHORT ).show();
                                        startActivity( intent );
                                        finish();
                                    }else {
                                        wrongpw[0]-=1;
                                        if (wrongpw[0]>0){
                                            test.setText( "密码错误,还有"+ wrongpw[0] +"次机会！" );
                                        }else {
                                            test.setText( "警告！" );
                                            Toast.makeText( LockActivity.this,"密码功能锁定,请30秒后重试",Toast.LENGTH_LONG ).show();
                                            SharedPreferences.Editor timeeditor=getSharedPreferences( "time",MODE_PRIVATE ).edit();
                                            timeeditor.putLong( "wrongtime", System.currentTimeMillis());
                                            timeeditor.apply();
                                            finish();
                                        }


                                    }
                                }
                            }
//                            SharedPreferences.Editor lteditor=getSharedPreferences( "length",MODE_PRIVATE ).edit();
//                            lteditor.putInt( "pwlt", passwordlength);
//                            lteditor.apply();//存放密码的长度
                        } else if (event.getEventType() == PatternLockCompoundEvent.EventType.PATTERN_CLEARED) {
                            test.setText( "Pattern has been cleared" );
                        }
                    }
                });

    }
}
