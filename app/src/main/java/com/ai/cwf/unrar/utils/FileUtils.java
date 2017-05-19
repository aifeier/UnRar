package com.ai.cwf.unrar.utils;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created at 陈 on 2017/5/17.
 *
 * @author chenwanfeng
 * @email 237142681@qq.com
 */

public class FileUtils {


    /*打开系统文件管理器，选择文件*/
    public static void showFileChooser(AppCompatActivity activity, int FILE_SELECT_CODE) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            activity.startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(activity, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
        }
    }

/*    选择的结果

    protected void onActivityResult(int requestCode, int resultCode, Intent data)  {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    String path = FileUtils.getPath(this, uri);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }*/

    public static String getPath(Context context, Uri uri) {

        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {"_data"};
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                // Eat it
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /*copy asset file to loction storage*/
    public static void copyFileFromAsset(String outFilePath, String assetResName, AssetManager assetManager) throws Exception {
        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;

        try {
            File e = new File(outFilePath);
            if (!e.getParentFile().exists()) {
                e.getParentFile().mkdirs();
            }

            if (e.exists()) {
                e.delete();
            }

            inputStream = assetManager.open(assetResName);
            fileOutputStream = new FileOutputStream(outFilePath);
            byte[] b = new byte[1024];

            int l;
            while ((l = inputStream.read(b)) != -1) {
                fileOutputStream.write(b, 0, l);
            }

            inputStream.close();
            fileOutputStream.close();
            inputStream = null;
            fileOutputStream = null;
        } catch (IOException var11) {
            throw var11;
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }

            if (fileOutputStream != null) {
                fileOutputStream.close();
            }

        }

    }

    /**
     * 功能：Java读取txt文件的内容
     * 步骤：1：先获得文件句柄
     * 2：获得文件句柄当做是输入一个字节码流，需要对这个输入流进行读取
     * 3：读取到输入流后，需要读取生成字节流
     * 4：一行一行的输出。readline()。
     * 备注：需要考虑的是异常情况
     *
     * @param filePath
     */
    public static String readTxtFile(String filePath, String encoding) {
        StringBuilder sBuilder = new StringBuilder();
        try {
            File file = new File(filePath);
            if (file.isFile() && file.exists()) { //判断文件是否存在
                InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);//考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    //					System.out.println(lineTxt);
                    sBuilder.append(lineTxt);
                }
                read.close();
            } else {
                System.out.println("找不到指定的文件");
            }
        } catch (Exception e) {
            System.out.println("读取文件内容出错");
            e.printStackTrace();
        }
        return sBuilder.toString();
    }
}
