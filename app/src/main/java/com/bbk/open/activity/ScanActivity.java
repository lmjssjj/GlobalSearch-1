package com.bbk.open.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.bbk.open.globlesearch.R;
import com.example.captain_miao.grantap.CheckPermission;
import com.example.captain_miao.grantap.listeners.PermissionListener;

import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zbar.ZBarView;

/**
 * Created by Administrator on 2016/7/29.
 */
public class ScanActivity extends AppCompatActivity implements QRCodeView.Delegate {

    private static final String TAG = ScanActivity.class.getSimpleName();
    private QRCodeView mQRCodeView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        mQRCodeView = (ZBarView) findViewById(R.id.zbarview);
        mQRCodeView.setDelegate(this);
        mQRCodeView.startSpot();
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        Log.i(TAG, "result:" + result);
        Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
        if (result.contains("http")) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent .setData(Uri.parse(result));
            startActivity(intent);
        }
        vibrate();
        mQRCodeView.startSpot();
    }

    @Override
    public void onScanQRCodeOpenCameraError() {

    }

    @Override
    protected void onStart() {
        super.onStart();
        mQRCodeView.startCamera();
//        mQRCodeView.startCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
    }

    @Override
    protected void onStop() {
        mQRCodeView.stopCamera();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mQRCodeView.onDestroy();
        super.onDestroy();
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }

    public static void startScanActivity(final Context mContext) {
        String[] systemAlertWindowPermission = new String[]{Manifest.permission.CAMERA};
        CheckPermission
                .from(mContext)
                .setPermissions(systemAlertWindowPermission)
                .setRationaleConfirmText("Request SYSTEM_ALERT_WINDOW")
                .setDeniedMsg("The SYSTEM_ALERT_WINDOW Denied")
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void permissionGranted() {
                        mContext.startActivity(new Intent(mContext, ScanActivity.class));
                    }

                    @Override
                    public void permissionDenied() {

                    }
                }).check();
    }


}
