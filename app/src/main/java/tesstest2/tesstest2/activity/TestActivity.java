package tesstest2.tesstest2.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import tesstest2.tesstest2.R;

/**
 * Created by Administrator on 2017/7/30.
 */

public class TestActivity extends Activity{

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        String datapath = Environment.getExternalStorageDirectory() + "/tesseract/tessdata/";
        File dir = new File(datapath + "tessdata");
        if (!dir.exists()){
            dir.mkdirs();
        }

        String newPath = datapath + "tessdata/";
        copyFilesFromAssets(newPath);
    }

    public void copyFilesFromAssets(String newPath){
        try {
            InputStream eng = getApplicationContext().getResources().openRawResource(R.raw.eng);
            InputStream chi = getResources().openRawResource(R.raw.chi_sim);
            FileOutputStream fosEng = new FileOutputStream(new File(newPath+"eng.traineddata"));
            FileOutputStream fosChi = new FileOutputStream(new File(newPath+"chi_sim.traineddata"));
            byte[] buffer=new byte[1024];
            int byteCount=0;
            while((byteCount=eng.read(buffer))!=-1)//循环从输入流读取 buffer字节
            {
                fosEng.write(buffer,0,byteCount);//将读取的输入流写入到输出流
            }
            while((byteCount=chi.read(buffer))!=-1)//循环从输入流读取 buffer字节
            {
                fosChi.write(buffer,0,byteCount);//将读取的输入流写入到输出流
            }

            eng.close();
            chi.close();
            fosChi.flush();
            fosChi.close();
            fosEng.flush();
            fosEng.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
