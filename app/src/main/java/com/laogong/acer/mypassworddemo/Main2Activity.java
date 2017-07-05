package com.laogong.acer.mypassworddemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.zhy.changeskin.SkinManager;

public class Main2Activity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main2 );
        SkinManager.getInstance().init(this);
        SkinManager.getInstance().register(this);
        final Button bt_theme=(Button)findViewById( R.id.button_theme );
        Button initcolorbt=(Button)findViewById( R.id.initcolor );
        initcolorbt.setOnClickListener( new View.OnClickListener( ) {
            @Override
            public void onClick(View v) {
                SkinManager.getInstance().changeSkin("black");
            }
        } );

        bt_theme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(bt_theme);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy( );
        SkinManager.getInstance().unregister(this);
    }

    private void showPopupMenu(View view) {
        // View当前PopupMenu显示的相对View的位置
        PopupMenu popupMenu = new PopupMenu(this, view);
        // menu布局
        popupMenu.getMenuInflater().inflate(R.menu.selecttheme, popupMenu.getMenu());
        // menu的item点击事件
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.red:
                        SkinManager.getInstance().changeSkin("red");
                        break;
                    case R.id.blue:
                        SkinManager.getInstance().changeSkin("blue");
                        break;
                    case R.id.black:
                        SkinManager.getInstance().changeSkin("black");
                        break;
                    case R.id.zi:
                        SkinManager.getInstance().changeSkin("zi");
                        break;
                    case R.id.green:
                        SkinManager.getInstance().changeSkin("green");
                        break;
                    case R.id.yellow:
                        SkinManager.getInstance().changeSkin("yellow");
                        break;
                }
                return false;
            }
        });
        popupMenu.show();
    }

}

