package com.neusoft.besterlive.control.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.neusoft.besterlive.R;
import com.tencent.ilivesdk.ILiveCallBack;
import com.tencent.ilivesdk.core.ILiveLoginManager;

import static com.tencent.qalsdk.base.a.S;

public class RegisterActivity extends AppCompatActivity {

    private Toolbar mTitlebar;
    private EditText mEtAccountRegister;
    private EditText mEtPasswordRegister;
    private EditText mEtConfirmPassword;
    private Button mBtnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_register);
        initView();
        setTitleBar();
    }

    private void setTitleBar() {
        mTitlebar.setTitle("注册新用户");
        mTitlebar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(mTitlebar);
    }

    private void initView() {
        mTitlebar = (Toolbar) findViewById(R.id.titlebar);
        mEtAccountRegister = (EditText) findViewById(R.id.et_account_register);
        mEtPasswordRegister = (EditText) findViewById(R.id.et_password_register);
        mEtConfirmPassword = (EditText) findViewById(R.id.et_confirm_password);
        mBtnRegister = (Button) findViewById(R.id.btn_register);

        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }

    private void register() {
        // account
        final String account = mEtAccountRegister.getText().toString();
        if (TextUtils.isEmpty(account)) {
            Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        // password
        final String password = mEtPasswordRegister.getText().toString();
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return;
       }
       // 判断输入账号是否附和账号注册规则
       if (account.length() < 8 || password.length() < 8){
           Toast.makeText(this, "用户名或密码长度不够！", Toast.LENGTH_SHORT).show();
           return;
       }
        // confirm password
        if (!password.equals(mEtConfirmPassword.getText().toString())){
            Toast.makeText(this, "两次输入密码不一致", Toast.LENGTH_SHORT).show();
            return;
        }

        // validate success, do something
        ILiveLoginManager.getInstance().tlsRegister(account, password, new ILiveCallBack() {
            @Override
            public void onSuccess(Object data) {
                //注册成功
                Toast.makeText(RegisterActivity.this, "创建用户成功，请登录！", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                intent.putExtra("account",account);
                intent.putExtra("password",password);
                intent.putExtra("isFromRegister",true);startActivity(intent);
                finish();
            }
            @Override
            public void onError(String module, int errCode, String errMsg) {
                Toast.makeText(RegisterActivity.this, "创建用户失败，错误码：" + errCode, Toast.LENGTH_SHORT).show();
            }
        });

    }
}
