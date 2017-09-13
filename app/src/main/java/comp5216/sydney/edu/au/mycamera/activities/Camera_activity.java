package comp5216.sydney.edu.au.mycamera.activities;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import comp5216.sydney.edu.au.mycamera.R;

/**
 * Created by WZZ on 10/09/2017.
 */

public class Camera_activity extends Activity {

    SurfaceView surfaceView = null;
    Button cameraBack=null;
    Button cameraTake=null;
    ImageView photoPreview=null;
    SurfaceHolder holder=null;
    Camera camera=null;
    Camera.Parameters parameters=null;
    private boolean safeToTakePicture=false;
    MySurfaceCallBack surfaceCB=null;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_layout);

        surfaceView= (SurfaceView)findViewById(R.id.surfaceView);
        cameraBack=(Button)findViewById(R.id.cameraBack);
        photoPreview=(ImageView)findViewById(R.id.photoPreView);
        cameraTake=(Button)findViewById(R.id.cameraTakePhoto);

        holder=surfaceView.getHolder();
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        holder.setFixedSize(368,450);
        holder.setKeepScreenOn(true);
        surfaceCB= new MySurfaceCallBack();
        holder.addCallback(surfaceCB);

        cameraTake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                camera.takePicture(null,null,new TakePic());

//                if(!safeToTakePicture) {
//                    camera=Camera.open();
//                    camera.startPreview();
//                    try {
//                        camera.setPreviewDisplay(holder);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    safeToTakePicture=true;
//                    camera.takePicture(null,null,new TakePic());
//                    Toast.makeText(Camera_activity.this,"click1",Toast.LENGTH_SHORT).show();
//                    safeToTakePicture=false;
//                    camera.startPreview();
//
//                }
//
//                if (safeToTakePicture){
//                    camera.takePicture(null,null,new TakePic());
//                    Toast.makeText(Camera_activity.this,"click2",Toast.LENGTH_SHORT).show();
//                    safeToTakePicture=false;
//                    camera.startPreview();
//                }
            }
        });

    }

    class TakePic implements Camera.PictureCallback{

        @Override
        public void onPictureTaken(byte[] bytes, Camera camera) {
            if (bytes.length>0){
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                photoPreview.setImageBitmap(bitmap);
            }
        }
    }

    class MySurfaceCallBack implements SurfaceHolder.Callback{

        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            camera= Camera.open();
            Toast.makeText(Camera_activity.this,"camera open",Toast.LENGTH_SHORT).show();
            camera.setDisplayOrientation(getRotation(Camera_activity.this));
            try {
                camera.setPreviewDisplay(holder);
            } catch (IOException e) {
                e.printStackTrace();
            }
            camera.startPreview();
            safeToTakePicture=true;
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
//            Toast.makeText(Camera_activity.this,"1",Toast.LENGTH_SHORT).show();
            parameters=camera.getParameters();
//            Toast.makeText(Camera_activity.this,"2",Toast.LENGTH_SHORT).show();

            List<Camera.Size> supportedPictureSizes = parameters.getSupportedPictureSizes();
            if (supportedPictureSizes.isEmpty()){
                parameters.setPreviewSize(i1,i2);
//                Toast.makeText(Camera_activity.this,"3",Toast.LENGTH_SHORT).show();


            }else{
                Camera.Size size = supportedPictureSizes.get(0);
                parameters.setPreviewSize(size.width,size.height);
            }

            parameters.setPictureFormat(PixelFormat.JPEG);
            parameters.setPictureSize(i1,i2);
//            Toast.makeText(Camera_activity.this,"4",Toast.LENGTH_SHORT).show();


            parameters.setJpegQuality(80);
//            Toast.makeText(Camera_activity.this,"5",Toast.LENGTH_SHORT).show();

            parameters.setPreviewFrameRate(5);
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            Toast.makeText(Camera_activity.this,"6",Toast.LENGTH_SHORT).show();

            if (camera!=null){
                camera.release();
                camera=null;
                Toast.makeText(Camera_activity.this,"camera close",Toast.LENGTH_SHORT).show();
            }
        }
    }



    private int getRotation(Activity activity){
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degree=0;

        switch (rotation){
            case Surface.ROTATION_0:
                degree=90;
                break;
            case Surface.ROTATION_90:
                degree=0;
                break;
            case Surface.ROTATION_180:
                degree=270;
                break;
            case Surface.ROTATION_270:
                degree=180;
                break;


        }
        return degree;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (camera!=null){
            camera.stopPreview();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (camera!=null){
            camera.startPreview();
        }else {
            try {
                camera= Camera.open();
                camera.setDisplayOrientation(getRotation(Camera_activity.this));
                camera.setPreviewDisplay(holder);
                camera.startPreview();
                safeToTakePicture=true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
