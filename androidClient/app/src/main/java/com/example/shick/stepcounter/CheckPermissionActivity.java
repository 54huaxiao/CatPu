package com.example.shick.stepcounter;

/**
 * Created by shick on 2016/12/10.
 */
import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.MapView;
import com.tbruyelle.rxpermissions.RxPermissions;

import rx.functions.Action1;

//  Use RxPermission to request Permission
public class CheckPermissionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions
                .request(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean granted) {
                        if (granted) {
                            Toast.makeText(CheckPermissionActivity.this,
                                    "ACCESS_FINE_LOCATION Permission Granted", Toast.LENGTH_SHORT).show();
//                          enter BeginActivity
                            startActivity(new Intent(CheckPermissionActivity.this, BeginActivity.class));
                            finish();
                        } else {
                            Toast.makeText(CheckPermissionActivity.this,
                                    "Permission Not Guaranteed, App close in 3 secends...", Toast.LENGTH_SHORT).show();
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    finish();
                                }
                            }, 3000);
                        }
                    }

                });
    }
}

