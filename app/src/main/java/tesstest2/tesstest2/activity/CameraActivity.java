package tesstest2.tesstest2.activity;

import android.app.Activity;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import tesstest2.tesstest2.R;
import tesstest2.tesstest2.utils.PictureUtils;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;


public class CameraActivity extends Activity implements
        SurfaceHolder.Callback, Camera.PreviewCallback,Camera.AutoFocusCallback {

    private final static int F_DELAY = 200;
    private final static int F_PERIOD = 2000;
    private final static int D_DELAY = 400;
    private final static int D_PERIOD = 1000;


    private TextView hintText;
    private SurfaceView surfaceView;
    private Camera camera;
    private SurfaceHolder holder;
    private Camera.Parameters parameters;
    private MyRectView rectView;

    private Timer focusTimer;
    private TimerTask focusTask;
    private Timer detectTimer;
    private TimerTask detectTask;

    private int screenWidth, screenHeight;


    private int preWidth;
    private int preHeight;



    private boolean flag = false;
    private boolean isInvalidate = true;

    private Bitmap resultBitmap = null;
    private Bitmap surfaceBitmap = null;

    PictureUtils pictureUtils = new PictureUtils();

    Intent intent = new Intent();

    private String result = null;

    DoOCR doOCR = new DoOCR();

    int count1 = 0;
    int count2 = 0;
    int count3 = 0;
    int count4 = 0;
    int count5 = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去掉标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //去掉状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.scanning);
        createUI();
        holder = surfaceView.getHolder();
        holder.addCallback(this);
        doOCR.init(getApplicationContext());
    }

    public void createUI() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
        surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        FrameLayout layout = (FrameLayout) findViewById(R.id.root);
        rectView = new MyRectView(this, screenWidth, screenHeight);
        rectView.invalidate();
        layout.addView(rectView);
        hintText = (TextView) findViewById(R.id.hint_text);
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder,
                               int format, int width, int height) {
        initCamera();
        focusStart();
        detectStart();
    }

    private void initCamera() {
        parameters = camera.getParameters();

        boolean isSupport = getBestSize();

        parameters.setPreviewSize(preWidth, preHeight);

        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);

        camera.setParameters(parameters);
        //camera.setOneShotPreviewCallback(this);
        //camera.setPreviewCallback(this);
        try {
            camera.startPreview();
            camera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //camera.cancelAutoFocus();// 2如果要实现连续的自动对焦，这一句必须加上

        //表示分辨率不支持
        if (isSupport == false) {
            hintText.setText("摄像头分辨率不支持！");
        }
    }

    private void doFocus() {
        if (camera != null){
            count1++;
            camera.autoFocus(this);
        }
    }

    private void focusStart() {
        focusTimer = new Timer(false);
        focusTask = new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 2;
                uiHandler.sendMessage(message);
            }
        };
        focusTimer.schedule(focusTask, F_DELAY, F_PERIOD);
    }

    private void detectStart() {
        detectTimer = new Timer(false);
        detectTask = new TimerTask() {
            @Override
            public void run() {
                camera.setOneShotPreviewCallback(CameraActivity.this);
                if (flag == false && resultBitmap != null){
                    flag = true;
                    ProcessThread processThread =
                            new ProcessThread(uiHandler, resultBitmap);

                    processThread.setPriority(ProcessThread.MAX_PRIORITY);
                    processThread.start();
                }
            }
        };
        detectTimer.schedule(detectTask, D_DELAY, D_PERIOD);
    }


    private boolean getBestSize() {
        List<Size> preSize = parameters.getSupportedPreviewSizes();

        //打印所有支持的分辨率
		/*
		for(int i=0;i<preSize.size();i++)
			Log.i("CRTAG", "size"+i+":"+preSize.get(i).width + "*" + preSize.get(i).height);*/

        preWidth = preSize.get(0).width;
        preHeight = preSize.get(0).height;
        boolean isSupport = false;
        for (int i = 0; i < preSize.size(); i++) {
            if (preSize.get(i).width == 1280 && preSize.get(i).height == 720) {
                preWidth = preSize.get(i).width;
                preHeight = preSize.get(i).height;
                isSupport = true;
                break;
            }

            if (preSize.get(i).width == 1024 && preSize.get(i).height == 576
                    && preWidth * preHeight < 1024 * 576) {
                preWidth = preSize.get(i).width;
                preHeight = preSize.get(i).height;
                isSupport = true;
            }

            if (preSize.get(i).width == 960 && preSize.get(i).height == 540
                    && preWidth * preHeight < 960 * 540) {
                preWidth = preSize.get(i).width;
                preHeight = preSize.get(i).height;
                isSupport = true;
            }
        }

        return isSupport;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // start camera service
        if (camera == null) {
            camera = Camera.open();
        }

        try {
            // set the preview
            camera.setPreviewDisplay(holder);
        } catch (IOException e) {
            camera = null;
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        focusTimer.cancel();
        focusTimer = null;
        focusTask.cancel();
        focusTask = null;
        detectTimer.cancel();
        detectTimer = null;
        detectTask.cancel();
        detectTask = null;
        camera.setPreviewCallback(null);
        camera.stopPreview();
        camera.release();
        camera = null;
    }


    @Override
    public void onStop() {
        super.onStop();
    }


    @Override
    public void onPreviewFrame(byte[] bytes, Camera camera) {
        //Toast.makeText(CameraActivity.this,"bytes:"+bytes,Toast.LENGTH_SHORT).show();
        if (bytes != null) {
            //Bitmap bm = runInPreviewFrame(bytes,camera);
            //saveImageToGallery(getApplicationContext(),bm);
            Camera.Size previewSize = camera.getParameters().getPreviewSize();
            int width = previewSize.width;
            int hight = previewSize.height;
            /*bytes = pictureUtils.yuv2CanUse(bytes,camera);
            surfaceBitmap = pictureUtils.Bytes2Bitmap(bytes);*/
            surfaceBitmap = pictureUtils.rawByteArray2RGBABitmap2(bytes,width,hight);
            resultBitmap = pictureUtils.cutIDCardImage(surfaceBitmap);
            //bytes = bitmap2Bytes(resultBitmap);
            //previewData = bytes;

            //Toast.makeText(CameraActivity.this,"count1:"+count1,Toast.LENGTH_SHORT).show();
            //Toast.makeText(CameraActivity.this,"count2:"+count2,Toast.LENGTH_SHORT).show();
            //Toast.makeText(CameraActivity.this,"count3:"+count3,Toast.LENGTH_SHORT).show();
            /*Toast.makeText(CameraActivity.this,"count4:"+count4,Toast.LENGTH_SHORT).show();
            Toast.makeText(CameraActivity.this,"count5:"+count5,Toast.LENGTH_SHORT).show();*/
        }
    }


    @Override
    public void onAutoFocus(boolean b, Camera camera) {

    }

    @SuppressLint("HandlerLeak")
    private Handler uiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                            count3++;
                            flag = false;
                            Bundle bundle = doOCR.doOCRGetResult(resultBitmap,getApplicationContext());
                            result = bundle.getString("result");
                            Toast.makeText(CameraActivity.this,"result:"+result,Toast.LENGTH_SHORT).show();
                            /*Toast.makeText(CameraActivity.this,"count1:"+count1,Toast.LENGTH_SHORT).show();
                            Toast.makeText(CameraActivity.this,"count2:"+count2,Toast.LENGTH_SHORT).show();
                            Toast.makeText(CameraActivity.this,"count3:"+count3,Toast.LENGTH_SHORT).show();*/
                            checkResult(result,uiHandler);
                            break;
                case 2:
                    doFocus();
                    break;
               /* case 3:
                    flag = false;
                    checkResult(result,uiHandler);
                    break;*/
                case 3:
                    showResult(result);
                    break;
            }
        }
    };

    private void goCheck(String result, Handler uiHandler) {
        if (result != null && result != "{}"){
            Message msg = new Message();
            msg.what = 3;
            uiHandler.sendMessage(msg);
        }
    }

    private void checkResult(String result, Handler uiHandler) {
        pictureUtils.saveImageToGallery(getApplicationContext(),surfaceBitmap);
        pictureUtils.saveImageToGallery(getApplicationContext(),resultBitmap);
        JSONObject jsonObject = JSON.parseObject(result);
        if (jsonObject.size() != 0){
            Message msg = new Message();
            flag = true;
            msg.what = 3;
            uiHandler.sendMessage(msg);
        }/*else {
            Toast.makeText(CameraActivity.this,"i do",Toast.LENGTH_SHORT).show();
            showPicture();
           *//* msg.what = 1;
            msg.arg1 = 1;
            uiHandler.sendMessage(msg);*//*
        }*/
    }



    private void showPicture() {
        flag = true;
        //byte[] imagebyte = pictureUtils.bitmap2Bytes(resultBitmap);
        byte[] imagebyte2 = pictureUtils.bitmap2Bytes(surfaceBitmap);
        //intent.putExtra("key",imagebyte);
        //pictureUtils.saveImageToGallery(getApplicationContext(),resultBitmap);
        //pictureUtils.saveImageToGallery(getApplicationContext(),surfaceBitmap);
        intent.putExtra("key2",imagebyte2);
        setResult(RESULT_OK,intent);
        uiHandler.removeMessages(2);
        CameraActivity.this.finish();
    }
    private void showResult(String result) {
        JSONObject jsonObject = JSON.parseObject(result);
        intent.putExtra("name",jsonObject.getString("name"));
        intent.putExtra("sex",jsonObject.getString("sex"));
        intent.putExtra("nation",jsonObject.getString("nation"));
        intent.putExtra("birth",jsonObject.getString("birth"));
        intent.putExtra("address",jsonObject.getString("address"));
        intent.putExtra("number",jsonObject.getString("number"));
        //intent.putExtra("imageByte", pictureUtils.bitmap2Bytes(resultBitmap));
        setResult(RESULT_OK,intent);
        CameraActivity.this.finish();
    }

    private class ProcessThread extends Thread {
        public Handler uiHandler;
        public Bitmap raw_bitmap = null;

        public ProcessThread(Handler uiHandler, Bitmap bitmap) {
            this.uiHandler = uiHandler;
            this.raw_bitmap = bitmap;
        }

        @Override
        public void run() {
            android.os.Process.setThreadPriority(-20);
            if (raw_bitmap != null){
                Message msg = new Message();
                msg.what = 1;
                uiHandler.sendMessage(msg);
            }
        }



    }


}
