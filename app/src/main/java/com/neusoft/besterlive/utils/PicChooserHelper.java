package com.neusoft.besterlive.utils;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;

import com.neusoft.besterlive.BesterApplication;
import com.neusoft.besterlive.view.Dialog.PicChooseDialog;
import com.qiniu.android.http.ResponseInfo;
import com.tencent.TIMUserProfile;

import java.io.File;
import java.io.IOException;


/**
 * Created by Wzich on 2017/10/30.
 */

public class PicChooserHelper {
    private static final int FROM_CROP = 0;
    private static final int FROM_ALBUM = 1;
    private static final int FROM_CAMERA = 2;
    private Activity mActivity;
    private Fragment mFragment;
    private PicType mPicType;

    public enum PicType{
        Avatar,Cover
    }

    public PicChooserHelper(Activity activity,PicType picType) {
        mActivity = activity;
        mPicType = picType;
    }
    public PicChooserHelper(Fragment fragment,PicType picType) {
        mFragment = fragment;
        mPicType = picType;
    }


    public void showPicChooserDialog() {
        Activity activity = mActivity;
        if (activity == null){
            activity = mFragment.getActivity();
        }
        PicChooseDialog dialog = new PicChooseDialog(activity);
        dialog.setOnDialogClickListener(new PicChooseDialog.OnDialogClickListener() {
            @Override
            public void onCamera() {
                //拍照
                choosePicFromCamera();
            }

            @Override
            public void onAlbum() {
                //相册
                choosePicFromAlbum();
            }
        });
        dialog.show();
    }
    private Uri cameraPicUri;
    private void choosePicFromCamera() {
        cameraPicUri = createCameraPicUri();
        if (cameraPicUri == null){
            return;
        } else {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            int currentApiVersion = Build.VERSION.SDK_INT;
            if (currentApiVersion <24){
                //7.0系统以下
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,cameraPicUri);
            } else {
                Uri uri = convertUri(cameraPicUri);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,uri);
            }
            if (mFragment == null){
                mActivity.startActivityForResult(cameraIntent,FROM_CAMERA);
            } else {
                mFragment.startActivityForResult(cameraIntent,FROM_CAMERA);
            }

        }
    }

    private Uri convertUri(Uri cameraPicUri) {
        if(cameraPicUri.getScheme().equals("content")){
            return cameraPicUri;
        } else {
            String filePath = cameraPicUri.getPath();
            Activity activity = mActivity;
            if (activity == null){
                activity = mFragment.getActivity();
            }
            Cursor cursor = activity.getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    new String[]{MediaStore.Images.Media._ID},
                    MediaStore.Images.Media.DATA + "=?",
                    new String[]{filePath}, null);
            if (cursor != null && cursor.moveToFirst()) {
                int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
                Uri baseUri = Uri.parse("content://media/external/images/media");
                return Uri.withAppendedPath(baseUri, "" + id);
            } else {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            }
        }
    }

    private Uri createCameraPicUri() {
        Activity activity  = mActivity;
        if (activity == null){
            activity = mFragment.getActivity();
        }
        String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
                + activity.getApplication().getApplicationInfo().packageName;
        File file = new File(dir);
        if (!file.exists() || file.isFile()){
            file.mkdirs();
        }
        TIMUserProfile userProfile = BesterApplication.getApp().getSelfProfile();
        if (userProfile != null){
            String picName = userProfile.getIdentifier();
            if (mPicType == PicType.Avatar){
                picName += "_avatar.jpg";
            } else if (mPicType == PicType.Cover){
                picName += "_cover.jpg";
            }
            File picFile = new File(dir,picName);
            if (picFile.exists()){
                picFile.delete();
            }
            return Uri.fromFile(picFile);
        }
        return null;
    }

    private void choosePicFromAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        if (mFragment == null){
            mActivity.startActivityForResult(intent, FROM_ALBUM);
        } else {
            mFragment.startActivityForResult(intent,FROM_ALBUM);
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FROM_ALBUM) {
            if (resultCode == Activity.RESULT_OK) {
                //选择成功
                Uri uri = data.getData();
                startCrop(uri);
            }
        } else if(requestCode == FROM_CROP){
            if (resultCode == Activity.RESULT_OK){//上传到七牛云
                upload7NiuCloud(cropUri.getPath());
            }
        } else if (requestCode == FROM_CAMERA){
            if (resultCode == Activity.RESULT_OK){
                startCrop(cameraPicUri);
            }
        }
    }
    private Uri cropUri;
    private void startCrop(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.putExtra("crop", true);
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat","JEPG");
        intent.setDataAndType(uri, "image/*");

        //设置输入大小和输出大小
        if (mPicType == PicType.Avatar){
            intent.putExtra("aspectX", 300);
            intent.putExtra("aspectY", 300);
            intent.putExtra("ourputX", 300);
            intent.putExtra("outputY", 300);
        } else if (mPicType == PicType.Cover){
            intent.putExtra("aspectX", 500);
            intent.putExtra("aspectY", 300);
            intent.putExtra("ourputX", 500);
            intent.putExtra("outputY", 300);
        }
        int currentApiVersion = Build.VERSION.SDK_INT;
        if (currentApiVersion < 24){
            //小于7.0版本
            intent.setDataAndType(uri,"image/*");
        } else {
            //大于7.0版本
            Uri contentUri = convertUri(uri);
            intent.setDataAndType(contentUri,"image/*");
        }
        cropUri = getCropUri();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cropUri);
        if (mFragment == null){
            mActivity.startActivityForResult(intent,FROM_CROP);
        } else {
            mFragment.startActivityForResult(intent,FROM_CROP);
        }
    }

    private Uri getCropUri() {
        Activity activity  = mActivity;
        if (activity == null){
            activity = mFragment.getActivity();
        }
        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/"
                + activity.getApplication().getPackageName();
        File dir = new File(dirPath);
        if (!dir.exists() || dir.isFile()){
            dir.mkdir();
        }

        TIMUserProfile selfProFile = BesterApplication.getApp().getSelfProfile();
        String fileName = "";
        if (selfProFile != null){
            fileName = selfProFile.getIdentifier() + "_" ;
            if (mPicType == PicType.Avatar){
                fileName += System.currentTimeMillis() + "_avatar_crop.jpg";
            } else if (mPicType == PicType.Cover){
                fileName += System.currentTimeMillis() + "_cover_crop.jpg";
            }
        }
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

    private void  upload7NiuCloud(String path) {
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


}
