package tesstest2.tesstest2.utils;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import tesstest2.tesstest2.R;

/**
 * Created by Administrator on 2017/7/31.
 */

public class TraineddataUtils {

    public void copyFilesFromRaw(String newPath , Context context){
        try {
            InputStream eng = context.getResources().openRawResource(R.raw.eng);
            //InputStream chi = context.getResources().openRawResource(R.raw.chi_sim);
            InputStream chi = context.getResources().openRawResource(R.raw.chi);
            FileOutputStream fosEng = new FileOutputStream(new File(newPath+"eng.traineddata"));
            //FileOutputStream fosChi = new FileOutputStream(new File(newPath+"chi_sim.traineddata"));
            FileOutputStream fosChi = new FileOutputStream(new File(newPath+"chi.traineddata"));
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

    public  void deleteDirWihtFile(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;
        for (File file : dir.listFiles()) {
            if (file.isFile())
                file.delete(); // 删除所有文件
            else if (file.isDirectory())
                deleteDirWihtFile(file); // 递规的方式删除文件夹
        }
        dir.delete();// 删除目录本身
    }
}
