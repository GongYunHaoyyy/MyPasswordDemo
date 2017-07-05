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

public class SetPasswordActivity extends AppCompatActivity {
    private PatternLockView mPatternLockView;
    private PatternLockViewListener mPatternLockViewListener = new PatternLockViewListener() {
        @Override
        public void onStarted() {}
        @Override
        public void onProgress(List<PatternLockView.Dot> progressPattern) {}
        @Override
        public void onComplete(List<PatternLockView.Dot> pattern) {}
        @Override
        public void onCleared() {}
    };

    //--------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_set_password );

        //记录新密码次数
        final int[] i = {0};

        //记录输入错误次数
        final int[] wrongpw = {5};
        final TextView textView=(TextView)findViewById( R.id.hint_tv );
        final TextView test=(TextView)findViewById( R.id.hint_tv_next );
        textView.setText( "输入新密码");

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


        RxPatternLockView.patternChanges(mPatternLockView)
                .subscribe(new Consumer<PatternLockCompoundEvent>() {
                    @Override
                    public void accept(PatternLockCompoundEvent event) throws Exception {
                        if (event.getEventType() == PatternLockCompoundEvent.EventType.PATTERN_STARTED) {
                            test.setText( "松手后完成" );
                        } else if (event.getEventType() == PatternLockCompoundEvent.EventType.PATTERN_PROGRESS) {
                            test.setText( "松手后完成" );
                        } else if (event.getEventType() == PatternLockCompoundEvent.EventType.PATTERN_COMPLETE) {
                            //获取得到当前输入的string形式的密码
                            String password= PatternLockUtils.patternToString(mPatternLockView, event.getPattern());
                            int passwordlength=password.length();
                            if (passwordlength<=3){
                                test.setText( "至少连接4个点,请重试" );
                            }else {
                                    if (i[0] ==0){
                                        SharedPreferences.Editor editor=getSharedPreferences( "newpwfirst",MODE_PRIVATE ).edit();
                                        editor.putString( "passwordfirst", password);
                                        editor.apply();
                                        test.setText( "绘制成功，再次绘制已确认" );
                                        i[0]++;
                                    }else if (i[0] ==1){
                                        SharedPreferences pref=getSharedPreferences( "newpwfirst",MODE_PRIVATE );
                                        String pwfirst=pref.getString( "passwordfirst","" );
                                        if (password.equals( pwfirst )){
                                            SharedPreferences.Editor editor=getSharedPreferences( "data",MODE_PRIVATE ).edit();
                                            editor.putString( "oldpassword", password);
                                            editor.apply();
                                            Toast.makeText( SetPasswordActivity.this,"密码设置成功",Toast.LENGTH_SHORT ).show();
                                            finish();
                                            i[0]=0;
                                        }else {
                                            test.setText( "密码不一致，请再次绘制" );
                                        }
                                }

                            }
                        } else if (event.getEventType() == PatternLockCompoundEvent.EventType.PATTERN_CLEARED) {
                            test.setText( "密码清空" );
                        }
                    }
                });
    }
}
