package com.neusoft.besterlive.control;
import android.hardware.Camera;

import com.tencent.ilivesdk.ILiveConstants;
import com.tencent.ilivesdk.core.ILiveLoginManager;
import com.tencent.ilivesdk.core.ILiveRoomManager;

/**
 * Created by Wzich on 2017/11/28.
 */

public class HostOperateStatus {
    private boolean isBeauty = false;
    private boolean isFlashLight = false;
    private boolean isVoice = true;
    private int cameraId = ILiveConstants.FRONT_CAMERA;

    public void switchBeauty(){
        isBeauty = !isBeauty;
        if (isBeauty){
            ILiveRoomManager.getInstance().enableBeauty(2.0f);
        } else {
            ILiveRoomManager.getInstance().enableBeauty(0);
        }

    }
    public boolean isBeauty(){
        return isBeauty;
    }

    public void switchFlashLight(){
        if (cameraId == ILiveConstants.FRONT_CAMERA){
            isFlashLight = false;
            return;
        }
        Object camera = ILiveLoginManager.getInstance().getAVConext().getVideoCtrl().getCamera();
        if (camera == null){
            isFlashLight = false;
            return;
        }
        Camera finalCamera = (Camera) camera;
        Camera.Parameters cameraParam = finalCamera.getParameters();
        if (cameraParam == null){
            isFlashLight = false;
            return;
        }
        if (isFlashLight){
            cameraParam.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        } else {
            cameraParam.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        }
        try{
            finalCamera.setParameters(cameraParam);
            isFlashLight = !isFlashLight;
        } catch (Exception e){
            isFlashLight = false;
        }
    }
    public boolean isFlashLight(){
        return isFlashLight;
    }

    public void switchVoice(){
        isVoice = !isVoice;
        ILiveRoomManager.getInstance().enableMic(isVoice);
    }
    public boolean isVoice(){
        return isVoice;
    }

    public void switchCamera(){
        switch (cameraId){
            case ILiveConstants.FRONT_CAMERA:
                cameraId = ILiveConstants.BACK_CAMERA;
                break;
            case ILiveConstants.BACK_CAMERA:
                cameraId = ILiveConstants.FRONT_CAMERA;
                break;
        }
        ILiveRoomManager.getInstance().switchCamera(cameraId);
    }
}
