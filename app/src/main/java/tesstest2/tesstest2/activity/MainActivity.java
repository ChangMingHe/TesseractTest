package tesstest2.tesstest2.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import tesstest2.tesstest2.R;
import tesstest2.tesstest2.utils.PictureUtils;
import tesstest2.tesstest2.utils.TraineddataUtils;


public class MainActivity extends Activity {

    private Button btn_scanchi, btn_scaneng, btn_test, btn_openScan ;
    private ImageView iv_img,iv_img2;
    private TextView tv_result;
    private final int PHOTO_REQUEST = 100;
    private final int TEST_OK = 200;
    private final int CAMERA_TEST_OK = 300;
    private TextView tv_name, tv_nation, tv_sex, tv_birth, tv_address, tv_number;
    PictureUtils pictureUtils = new PictureUtils();
    TraineddataUtils traineddataUtils = new TraineddataUtils();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //setContentView(R.layout.activity_main);
        ClickEvent event = new ClickEvent();

        /*btn_test = (Button)this.findViewById(R.id.btn_test);
        btn_test.setOnClickListener(event);*/
        /*btn_openScan = (Button) this.findViewById(R.id.btn_openScan);
        btn_openScan.setOnClickListener(event);*/
        btn_scanchi = (Button)this.findViewById(R.id.btn_scanchi);
        btn_scanchi.setOnClickListener(event);
        btn_scaneng = (Button)this.findViewById(R.id.btn_scaneng);
        btn_scaneng.setOnClickListener(event);
       /* btn_test2 = (Button)this.findViewById(R.id.btn_test2);
        btn_test2.setOnClickListener(event);*/




        iv_img = (ImageView) this.findViewById(R.id.iv_pic);
        tv_result = (TextView) this.findViewById(R.id.tv_result);

        tv_name = (TextView) this.findViewById(R.id.tv_name);
        tv_nation = (TextView) this.findViewById(R.id.tv_nation);
        tv_sex = (TextView) this.findViewById(R.id.tv_sex);
        tv_birth = (TextView) this.findViewById(R.id.tv_birth);
        tv_address = (TextView) this.findViewById(R.id.tv_address);
        tv_number = (TextView) this.findViewById(R.id.tv_number);

        File file = new File(Environment.getExternalStorageDirectory() + "/tesseract1");
        traineddataUtils.deleteDirWihtFile(file);
        String datapath = Environment.getExternalStorageDirectory() + "/tesseract1/tessdata/";
        Toast.makeText(MainActivity.this,datapath,Toast.LENGTH_LONG).show();
        File dir = new File(datapath + "tessdata");
        if (!dir.exists()){
            dir.mkdirs();
        }
        File[] fileArray = dir.listFiles();
        if (fileArray.length < 2){
            String newPath = datapath + "tessdata/";
            traineddataUtils.copyFilesFromRaw(newPath , getApplicationContext());
            Toast.makeText(MainActivity.this,"OK",Toast.LENGTH_LONG).show();
        }
        traineddataUtils = null;
    }


    class ClickEvent implements View.OnClickListener {
        public void onClick(View v) {
            if (v == btn_scaneng || v == btn_scanchi) {
                Intent intent = new Intent(MainActivity.this, AlbumActivity.class);
                if (v == btn_scanchi) {
                    // 激活系统图库，选择一张图片
                    intent.putExtra("key", "1");
                }
                startActivityForResult(intent, PHOTO_REQUEST);
            } else if (v == btn_test) {
                Intent intent = new Intent(MainActivity.this, IDCacdActivity.class);
                startActivityForResult(intent, TEST_OK);
            } else if (v == btn_openScan) {
                Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                startActivityForResult(intent, CAMERA_TEST_OK);
            }/*else if (v == btn_test2){
                Intent intent = new Intent(MainActivity.this, TestActivity.class);
                startActivity(intent);
            }*/
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PHOTO_REQUEST && resultCode == RESULT_OK) {
            String result = data.getStringExtra("result");
            byte[] imgByte = data.getByteArrayExtra("imgByte");
            Bitmap bitmap = pictureUtils.Bytes2Bimap(imgByte,new BitmapFactory.Options());
            //bitmap = pictureUtils.bmpOnViewScale(iv_img,bitmap);
            tv_result.setText(result);
            iv_img.setImageBitmap(bitmap);
            iv_img.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        } else if (requestCode == TEST_OK && resultCode == RESULT_OK) {
            byte[] imgByte = data.getByteArrayExtra("imgByte");
            Bitmap bitmap = pictureUtils.Bytes2Bimap(imgByte,new BitmapFactory.Options());
            //bitmap = pictureUtils.bmpOnViewScale(iv_img,bitmap);
            iv_img.setImageBitmap(bitmap);
            iv_img.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

            tv_name.setText(data.getStringExtra("name"));
            tv_nation.setText(data.getStringExtra("nation"));
            tv_sex.setText(data.getStringExtra("sex"));
            tv_address.setText(data.getStringExtra("address"));
            tv_number.setText(data.getStringExtra("number"));
            tv_birth.setText(data.getStringExtra("birth"));
        } else if (requestCode == CAMERA_TEST_OK && resultCode == RESULT_OK) {
//            byte[] imgByte = data.getByteArrayExtra("key");
//            byte[] imgByte2 = data.getByteArrayExtra("key2");
//            Bitmap bitmap = pictureUtils.Bytes2Bimap(imgByte,new BitmapFactory.Options());
//            Bitmap bitmap2 = pictureUtils.Bytes2Bimap(imgByte2,new BitmapFactory.Options());
//            //bitmap = pictureUtils.bmpOnViewScale(iv_img,bitmap);
//            iv_img.setImageBitmap(bitmap);
//            iv_img.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
//            iv_img2.setImageBitmap(bitmap2);
//            iv_img2.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            /*byte[] imgByte = data.getByteArrayExtra("imgByte");
            Bitmap bitmap = pictureUtils.Bytes2Bimap(imgByte,new BitmapFactory.Options());
            //bitmap = pictureUtils.bmpOnViewScale(iv_img,bitmap);
            iv_img.setImageBitmap(bitmap);
            iv_img.setScaleType(ImageView.ScaleType.CENTER_INSIDE);*/

            tv_name.setText(data.getStringExtra("name"));
            tv_nation.setText(data.getStringExtra("nation"));
            tv_sex.setText(data.getStringExtra("sex"));
            tv_address.setText(data.getStringExtra("address"));
            tv_number.setText(data.getStringExtra("number"));
            tv_birth.setText(data.getStringExtra("birth"));
        }
    }
}
