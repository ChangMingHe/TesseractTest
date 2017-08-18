package tesstest2.tesstest2.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;

import tesstest2.tesstest2.utils.PictureUtils;


/**
 * Created by Administrator on 2017/7/21.
 */

public class IDCacdActivity extends Activity {
    private final int PHOTO_REQUEST_GALLERY = 100;
    private Bitmap imageBitmap;
    TessBaseAPI baseApiEng , baseApiChi;
    PictureUtils pictureUtils = new PictureUtils();

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 激活系统图库，选择一张图片
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_GALLERY
        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);

        baseApiEng = new TessBaseAPI();
        baseApiChi = new TessBaseAPI();

        String datapath = Environment.getExternalStorageDirectory() + "/tesseract/tessdata/";
         String languageEng = "eng";
         String  languageChi = "chi_sim";


        File dir = new File(datapath + "tessdata/");
        if (!dir.exists())
            dir.mkdirs();
        baseApiEng.init(datapath, languageEng);
        baseApiChi.init(datapath,languageChi);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PHOTO_REQUEST_GALLERY && resultCode == RESULT_OK) {
            if (data != null) {

                // 得到图片的全路径
                Uri uri = data.getData();
                imageBitmap = pictureUtils.getBitmapFromUri(getApplicationContext(),uri);
                //返回某个坐标的颜色值  -15988213,-13947855,-13486012
                //int i = imageBitmap.getPixel(770,150);
              /*  int i = imageBitmap.getPixel(154,103);*/

                DoOCR doOCR = new DoOCR();
                doOCR.init(getApplicationContext());
                Bundle bundle = doOCR.doOCRGetResult(imageBitmap,getApplicationContext());
                Bitmap newImageBitMap;
                //newImageBitMap = doOCR.numberBmp;
                newImageBitMap = pictureUtils.zoomImage(imageBitmap,550,350);
                byte[] imgByte = pictureUtils.bitmap2Bytes(newImageBitMap);
                Intent intent = new Intent();
                String result = bundle.getString("result");
                JSONObject jsonObject = JSON.parseObject(result);
                intent.putExtra("imgByte",imgByte);
                intent.putExtra("name", jsonObject.getString("name"));
                intent.putExtra("sex", jsonObject.getString("sex"));
                intent.putExtra("nation", jsonObject.getString("nation"));
                intent.putExtra("number", jsonObject.getString("number"));
                intent.putExtra("address", jsonObject.getString("address"));
                intent.putExtra("birth", jsonObject.getString("birth"));

                setResult(RESULT_OK,intent);

            }

        }
        IDCacdActivity.this.finish();
    }

}
