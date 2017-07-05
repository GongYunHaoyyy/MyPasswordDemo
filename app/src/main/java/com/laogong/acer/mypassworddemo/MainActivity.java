package com.laogong.acer.mypassworddemo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        Button button1=(Button)findViewById( R.id.button1 );
        Button button2=(Button)findViewById( R.id.button2 );
        Button button3=(Button)findViewById( R.id.button3 );
        Button button4=(Button)findViewById( R.id.button4 );

        button1.setOnClickListener( new View.OnClickListener( ) {//直接进入
            @Override
            public void onClick(View v) {
                Intent intent=new Intent( MainActivity.this,Main2Activity.class );
                startActivity( intent );
            }
        } );

        button2.setOnClickListener( new View.OnClickListener( ) {//密码进入
            @Override
            public void onClick(View v) {
                if (isDeadLock()){
                    Toast.makeText( MainActivity.this,"密码功能锁定中...",Toast.LENGTH_SHORT ).show();
                }else {
                    Intent intent=new Intent( MainActivity.this,LockActivity.class );
                    startActivity( intent );
                }
            }
        } );

        button3.setOnClickListener( new View.OnClickListener( ) {//密码进入
            @Override
            public void onClick(View v) {
                Intent intent=new Intent( MainActivity.this,LockActivity.class );
                startActivity( intent );
            }
        } );

        button4.setOnClickListener( new View.OnClickListener( ) {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent( MainActivity.this,ChangePasswordActivity.class );
                startActivity( intent );
            }
        } );
    }

    boolean isDeadLock(){
        SharedPreferences pref=getSharedPreferences( "time",MODE_PRIVATE );
        Long wt=pref.getLong( "wrongtime",0 );
        if (System.currentTimeMillis()-wt<=30000){
            return true;
        }else {
            return false;
        }
    }

}
