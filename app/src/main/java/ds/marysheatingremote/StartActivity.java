package ds.marysheatingremote;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.IOException;

import io.particle.android.sdk.cloud.ParticleCloud;
import io.particle.android.sdk.cloud.ParticleCloudException;
import io.particle.android.sdk.cloud.ParticleDevice;
import io.particle.android.sdk.utils.Async;
import io.particle.android.sdk.utils.Toaster;

/**
 * Created by Damian Smith on 03/01/2016.
 * Twitter @Javawocky
 */

public class StartActivity extends Activity {

    private static final String ARG_VALUE = "ARG_VALUE";
    private static final String ARG_DEVICEID = "ARG_DEVICEID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        findViewById(R.id.connect_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptDeviceConnect();
            }
        });
    }

    public void attemptDeviceConnect(){
        final ProgressDialog mProgressDialog = ProgressDialog.show(
                StartActivity.this, "One Sec Mary....", "Connecting to controller device...", true);
        mProgressDialog.setCancelable(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    login();
                    Thread.sleep(30000);
                }catch (Exception e){
                    Log.e("ERR", "Thread error");
                }
            }

        }).start();
        mProgressDialog.dismiss();
    }

    public void login(){
        Async.executeAsync(ParticleCloud.get(StartActivity.this), new Async.ApiWork<ParticleCloud, Object>() {

            private ParticleDevice mDevice;

            @Override
            public Object callApi(ParticleCloud sparkCloud) throws ParticleCloudException, IOException {
                sparkCloud.logIn(getString(R.string.un), getString(R.string.pw));
                sparkCloud.getDevices();
                mDevice = sparkCloud.getDevice("37001a000c47343432313031");
                Object obj;
                return -1;
            }

            @Override
            public void onSuccess(Object value) {

                Toaster.l(StartActivity.this, "Logged in");

                if(mDevice.isConnected()){
                    Intent intent = new Intent(StartActivity.this, RemoteControlActivity.class);
                    intent.putExtra(ARG_VALUE, value.toString());
                    intent.putExtra(ARG_DEVICEID, "37001a000c47343432313031");
                    startActivity(intent);
                }else{
                    Toaster.l(StartActivity.this, "Unable to connect device. Please try again.");
                }
            }

            @Override
            public void onFailure(ParticleCloudException e) {
                Toaster.l(StartActivity.this,"Something has gone horribly wrong!!! ");
                e.printStackTrace();
                Log.d("info", e.getBestMessage());
            }

        });
    }
}