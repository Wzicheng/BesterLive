package com.neusoft.besterlive.control.activity;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.neusoft.besterlive.R;

import static com.tencent.qalsdk.base.a.S;
import static com.tencent.qalsdk.base.a.e;
import static com.tencent.qalsdk.base.a.s;

/**
 * Created by Wzich on 2017/10/29.
 */

public class EditProfileActivity extends AppCompatActivity {
    private Button mBtnFinish;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_edit_profile);
        initView();
    }

    private void initView() {
        mBtnFinish = (Button) findViewById(R.id.btn_finish);
        mBtnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getSharedPreferences("LoginInfo",MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isFirstLogin",false);
                editor.commit();
                Intent intent = new Intent(EditProfileActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
