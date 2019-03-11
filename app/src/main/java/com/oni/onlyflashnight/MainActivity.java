package com.oni.onlyflashnight;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import at.markushi.ui.CircleButton;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int CAMERA_REQUEST = 1111;
    private TextView mTvSos;
    private CircleButton mButtonOnOf;
    private boolean mHasCameraFlash;
    private boolean mFlashStatus;
    private boolean mSos;
    private MyThread mMyThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewByIds();
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST);
        mHasCameraFlash = getPackageManager().
                hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        boolean isEnabled = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
        mMyThread = new MyThread();

    }

    private void findViewByIds() {
        mButtonOnOf = findViewById(R.id.btn_on);
        mButtonOnOf.setOnClickListener(this);
        mTvSos = findViewById(R.id.tv_sos);
        mTvSos.setOnClickListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View v) {
        switch ((v.getId())) {
            case R.id.btn_on:
                if (mHasCameraFlash) {
                    if (mFlashStatus) {
                        flashLightOff();
                        mButtonOnOf.setImageResource(R.drawable.ic_power_button_off);
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        flashLightOn();
                        mButtonOnOf.setImageResource(R.drawable.ic_power_button_on);
                    }
                } else {
                    Toast.makeText(MainActivity.this, "No flash available on your device",
                            Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.tv_sos:
                setSosFlash();
                mSos = !mSos;
                break;

            default:
                break;
        }
    }

    private void setSosFlash() {
        try {
            if (mSos) {
                mMyThread.interrupt();
                flashLightOff();
            } else {
                mMyThread.start();
            }
        } catch (Exception e) {
        }
    }

    private void flashLightOn() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            try {
                String cameraId = cameraManager.getCameraIdList()[0];
                cameraManager.setTorchMode(cameraId, true);
                mFlashStatus = true;

            } catch (CameraAccessException e) {
                Log.d("ERROR", e.toString());
            }
        }


    }

    private void flashLightOff() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            try {
                String cameraId = cameraManager.getCameraIdList()[0];
                cameraManager.setTorchMode(cameraId, false);
                mFlashStatus = false;
            } catch (CameraAccessException e) {
            }
        }
    }

    class MyThread extends Thread {
        public void run() {
            long blinkDelay = 1000; //Delay in ms
            boolean isFlashOn = false;
            for (int i = 0; ; i++) {
                if (!mSos) {
                    if (!isFlashOn) {
                        flashLightOn();
                        isFlashOn = true;
                    } else {
                        flashLightOff();
                        isFlashOn = false;
                    }
                    try {
                        Thread.sleep(blinkDelay);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
