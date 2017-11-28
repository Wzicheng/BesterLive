package com.neusoft.besterlive.control.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.neusoft.besterlive.BesterApplication;
import com.neusoft.besterlive.R;
import com.tencent.TIMFriendshipManager;
import com.tencent.TIMUserProfile;
import com.tencent.TIMValueCallBack;
import com.tencent.ilivesdk.ILiveCallBack;
import com.tencent.ilivesdk.core.ILiveLoginManager;

public class LoginActivity extends Activity {
    private EditText mEtAccount;
    private EditText mEtPassword;
    private Button mBtnLogin;
    private TextView mBtnRegister;

    private String account;
    private String password;
    private boolean isFromRegister;
    private boolean isFirstLogin = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        initData();
    }

    private void initData() {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginInfo",MODE_PRIVATE);
        isFirstLogin = sharedPreferences.getBoolean("isFirstLogin",true);
        isFromRegister = getIntent().getBooleanExtra("isFromRegister",false);
        if(isFromRegister){
            account = getIntent().getStringExtra("account");
            password = getIntent().getStringExtra("password");
            if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(password)){
                mEtAccount.setText(account);
                mEtPassword.setText(password);
                Login(account,password);
            }
        }else {
            account = sharedPreferences.getString("account","");
            password = sharedPreferences.getString("password","");
            if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(password)){
                mEtAccount.setText(account);
                mEtPassword.setText(password);
                Login(account,password);
            }
        }

    }


    private void initView() {
        mEtAccount = (EditText) findViewById(R.id.et_account);
        mEtPassword = (EditText) findViewById(R.id.et_password);
        mBtnLogin = (Button) findViewById(R.id.btn_login);
        mBtnRegister = (TextView) findViewById(R.id.btn_register);

        mBtnLogin.setOnClickListener(new MyOnClickListener());
        mBtnRegister.setOnClickListener(new MyOnClickListener());
    }

    private void toLogin() {
        account = mEtAccount.getText().toString();
        // validate
        if (TextUtils.isEmpty(account)) {
            Toast.makeText(this, "account不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        password = mEtPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "password不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        // 基本校验完成

        Login(account,password);

    }

    private void Login(final String account, String password) {
        ILiveLoginManager.getInstance().tlsLogin(account, password, new ILiveCallBack<String>() {
            @Override
            public void onSuccess(String data) {
                ILiveLoginManager.getInstance().iLiveLogin(account, data, new ILiveCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        //最终登录成功，获取用户信息字段
                        getSelfProfile();
                        //登陆成功将登录信息保存
                        saveLoginInfo();
                        Toast.makeText(LoginActivity.this, "登录成功！", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        if (isFirstLogin){
                            intent.setClass(LoginActivity.this,EditProfileActivity.class);
                        } else {
                            intent.setClass(LoginActivity.this, MainActivity.class);
                        }
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onError(String module, int errCode, String errMsg) {
                        //登录失败
                        Toast.makeText(LoginActivity.this, "iLive登录失败：" + errMsg, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                Toast.makeText(LoginActivity.this, "tls登录失败：错误码：" + errCode, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveLoginInfo() {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginInfo",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
        if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(password)){
            editor.putString("account",account);
            editor.putString("password",password);
            editor.commit();
        }
    }


    /**
     * 获取用户字段信息
     */
    private void getSelfProfile() {
        Log.e("TAG","getSelfProfile");
        TIMFriendshipManager.getInstance().getSelfProfile(new TIMValueCallBack<TIMUserProfile>() {
            @Override
            public void onError(int i, String s) {
                Log.e("TAG","onError");
                Toast.makeText(LoginActivity.this, "获取用户字段信息失败！错误码：" + i, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(TIMUserProfile timUserProfile) {
                Log.e("TAG","onSuccess");
                //获取用户字段信息成功，将其保存
                BesterApplication.getApp().saveSelfProfile(timUserProfile);

            }
        });
    }


    private class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_login:
                    toLogin();
                    break;
                case R.id.btn_register:
                    Intent toRegister = new Intent(LoginActivity.this,RegisterActivity.class);
                    startActivity(toRegister);
                    break;
            }
        }
    }
}
