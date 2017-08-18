package tesstest2.tesstest2.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;

import tesstest2.tesstest2.utils.PictureUtils;


/**
 * Created by Administrator on 2017/7/18.
 */

public class AlbumActivity extends Activity {

    private final int PHOTO_REQUEST_GALLERY = 333;
    private Bitmap imageBitmap;
    private TessBaseAPI baseApi;
    private byte[] imageByte;
    PictureUtils pictureUtils = new PictureUtils();

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // 激活系统图库，选择一张图片
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_GALLERY
        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
        baseApi = new TessBaseAPI();
        Intent keyIntent = getIntent();
        String key = keyIntent.getStringExtra("key");
        String language = null;
        if ("1".equals(key)){
            //language = "chi_sim";
            language = "chi";
        }else {
            language = "eng";
        }
        String datapath = Environment.getExternalStorageDirectory() + "/tesseract1/tessdata/";
        // String language = "eng";

        File dir = new File(datapath + "tessdata/");
        if (!dir.exists())
            dir.mkdirs();
        baseApi.init(datapath, language);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PHOTO_REQUEST_GALLERY && resultCode == RESULT_OK) {
            // 从相册返回的数据
            if (data != null) {
                String result = null;

                // 得到图片的全路径
                Uri uri = data.getData();
                imageBitmap = pictureUtils.getBitmapFromUri(getApplicationContext(),uri);
                baseApi.setImage(imageBitmap);
               //根据Init的语言，获得ocr后的字符串
                String text1= baseApi.getUTF8Text();
                imageByte = pictureUtils.bitmap2Bytes(imageBitmap);
                Intent intent = new Intent();
                intent.putExtra("result",text1);
                intent.putExtra("imgByte",imageByte);
                setResult(RESULT_OK,intent);

                //释放bitmap
               baseApi.clear();
                }
            }
        AlbumActivity.this.finish();
    }




}
