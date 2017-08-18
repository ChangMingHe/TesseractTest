package tesstest2.tesstest2.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;

import tesstest2.tesstest2.utils.PictureUtils;

import static android.R.attr.path;


/*
 * Created by Administrator on 2017/7/28.
 *
 * AssetManager.open(String filename)，返回的是一个InputSteam类型的字节流，这里的filename必须是文件比如（aa.txt；img/semll.jpg），而不能是文件夹。
 *
 *
    获取assets目录下的所有文件及目录名，content（当前的上下文如Activity，Service等ContextWrapper的子类的都可以）
     String fileNames[] =context.getAssets().list(path);
 */

public class DoOCR {
    public  TessBaseAPI baseApiEng , baseApiChi;
    public  Bitmap nameBmp,sexBmp,nationBmp,numberBmp,addressBmp,birthBmp;
    public  String name,sex,nation,number,address,birth;
    PictureUtils pictureUtils = new PictureUtils();

    public  void init(Context context){
        baseApiEng = new TessBaseAPI();
        baseApiChi = new TessBaseAPI();

        String datapath = Environment.getExternalStorageDirectory() + "/tesseract1/tessdata/";

        File dir = new File(datapath + "tessdata");
        if (!dir.exists()){
            dir.mkdirs();
        }
        String languageEng = "eng";
        String  languageChi = "chi_sim";
        //Toast.makeText(context,"4",Toast.LENGTH_SHORT).show();
        baseApiEng.init(datapath, languageEng);
        //Toast.makeText(context,"5",Toast.LENGTH_SHORT).show();
        baseApiChi.init(datapath,languageChi);
        //Toast.makeText(context,"6",Toast.LENGTH_SHORT).show();
    }

    public Bundle doOCRGetResult(Bitmap imageBitmap,Context context) {
       // Toast.makeText(context,"1",Toast.LENGTH_SHORT).show();
        int imageWidth , imageHeight;
        imageWidth = imageBitmap.getWidth();
        imageHeight = imageBitmap.getHeight();
        JSONObject  result = new JSONObject();
        //性别截图
        //Toast.makeText(context,"2",Toast.LENGTH_SHORT).show();
        sexBmp = Bitmap.createBitmap(imageBitmap, (int)(imageWidth / 5.8), (int)(imageHeight / 4.6), imageWidth / 16, imageHeight / 8);
       // Toast.makeText(context,"3",Toast.LENGTH_SHORT).show();
        pictureUtils.saveImageToGallery(context,sexBmp);
        baseApiChi.setImage(sexBmp);
        sex = baseApiChi.getUTF8Text();

        if ("男".equals(sex) || "女".equals(sex)) {
            //民族截图
            nationBmp = Bitmap.createBitmap(imageBitmap, (int)(imageWidth / 2.6), (int)(imageHeight / 4.6), imageWidth / 16, imageHeight / 8);
            pictureUtils.saveImageToGallery(context,nationBmp);
            baseApiChi.setImage(nationBmp);
            nation = baseApiChi.getUTF8Text();

            if (nation != null) {
                //身份证号码截图
                numberBmp = Bitmap.createBitmap(imageBitmap, (int)(imageWidth / 3.1), (int)(imageHeight / 1.27), (int)(imageWidth / 1.67), imageHeight / 8);
                pictureUtils.saveImageToGallery(context,numberBmp);
                baseApiEng.setImage(numberBmp);
                number = baseApiEng.getUTF8Text();

                if (number != null) {
                    //地址截图
                    addressBmp = Bitmap.createBitmap(imageBitmap, imageWidth / 6, (int)(imageHeight / 2.1), (int)(imageWidth / 2.2), (int)(imageHeight / 5.5));
                    pictureUtils.saveImageToGallery(context,addressBmp);
                    baseApiChi.setImage(addressBmp);
                    address = baseApiChi.getUTF8Text();

                    if (address != null) {
                        //出生
                        birthBmp = Bitmap.createBitmap(imageBitmap,imageWidth / 6,(int)(imageHeight / 2.78),(int)(imageWidth / 2.7),imageHeight / 8);
                        pictureUtils.saveImageToGallery(context,birthBmp);
                        baseApiChi.setImage(birthBmp);
                        birth = baseApiChi.getUTF8Text();

                        if (birth != null){
                            //姓名截图
                            nameBmp = Bitmap.createBitmap(imageBitmap,imageWidth / 6, imageHeight / 10, imageWidth / 5, imageHeight / 8);
                            pictureUtils.saveImageToGallery(context,nameBmp);
                            baseApiChi.setImage(nameBmp);
                            name = baseApiChi.getUTF8Text();

                            if (name != null){
                                try {
                                    result.put("name",name);
                                    result.put("sex",sex);
                                    result.put("nation",nation);
                                    result.put("birth",birth);
                                    result.put("address",address);
                                    result.put("number",number);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
        }
        Bundle bundle = new Bundle();
        bundle.putString("result", String.valueOf(result));
        baseApiChi.clear();
        baseApiEng.clear();
        return bundle;
    }
    }
