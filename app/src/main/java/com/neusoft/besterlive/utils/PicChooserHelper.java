package com.neusoft.besterlive.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;

import com.neusoft.besterlive.BesterApplication;
import com.neusoft.besterlive.control.widget.PicChooseDialog;
import com.qiniu.android.http.ResponseInfo;
import com.tencent.TIMUserProfile;

import java.io.File;
import java.io.IOException;


/**
 * Created by Wzich on 2017/10/30.
 */

public class PicChooserHelper {
    private static final int FROM_ALBUM = 1;
    private static final int FROM_CROP = 0;
    private Activity mActivity;
    private Uri cropUri;

    public PicChooserHelper(Activity activity) {
        mActivity = activity;
    }

    public void showPicChooserDialog() {
        PicChooseDialog dialog = new PicChooseDialog(mActivity);
        dialog.setOnDialogClickListener(new PicChooseDialog.OnDialogClickListener() {
            @Override
            public void onCamera() {
                //拍照
            }

            @Override
            public void onAlbum() {
                //相册
                choosePicFromAlbum();
            }
        });
        dialog.show();
    }

    private void choosePicFromAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        mActivity.startActivityForResult(intent, FROM_ALBUM);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FROM_ALBUM) {
            if (requestCode == Activity.RESULT_OK) {
                //选择成功
                Uri uri = data.getData();
                startCrop(uri);
            } else if(requestCode == FROM_CROP){
                if (resultCode == Activity.RESULT_OK){//上传到七牛云
                    upload7NiuCloud(cropUri.getPath());
                }
            }
        }
    }

    private void upload7NiuCloud(String path) {
        File file = new File(path);
        QnUploadHelper.uploadPic(path, file.getName(), new QnUploadHelper.UploadCallBack() {
            @Override
            public void success(String url) {
                //上传头像成功
                if (onUpdateListener != null){
                    onUpdateListener.onSuccess(url);
                }
            }

            @Override
            public void fail(String key, ResponseInfo info) {
                //上传头像失败！
                if (onUpdateListener != null){
                    onUpdateListener.onFail();
                }
            }
        });
    }

    //头像更新监听
    public interface OnUpdateListener{
        void onSuccess(String url);
        void onFail();
    }

    private OnUpdateListener onUpdateListener;
    public void setOnUpdateListener(OnUpdateListener listener){
        onUpdateListener = listener;
    }



    private void startCrop(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.putExtra("crop", true);
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat","JEPG");
        intent.setDataAndType(uri, "image/*");

        //设置输入大小和输出大小
        intent.putExtra("aspectX", 300);
        intent.putExtra("aspectX", 300);
        intent.putExtra("ourputX", 300);
        intent.putExtra("outputX", 300);

        cropUri = getCropUri();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cropUri);
        mActivity.startActivityForResult(intent,FROM_CROP);
    }

    private Uri getCropUri() {
        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
                + mActivity.getApplication().getPackageName();
        File dir = new File(dirPath);
        if (!dir.exists() || dir.isFile()){
            dir.mkdir();
        }
        TIMUserProfile selfProFile = BesterApplication.getApp().getSelfProfile();
        String fileName = "";
        if (selfProFile != null){
            fileName = selfProFile.getIdentifier() + "_";
        }
        fileName += SystemClock.currentThreadTimeMillis() + "_touxiang_crop.jpg";

        File jpgFile = new File(dir,fileName);
        if (jpgFile.exists()){
            jpgFile.delete();
        }
        try {
            jpgFile.createNewFile();
            return Uri.fromFile(jpgFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
