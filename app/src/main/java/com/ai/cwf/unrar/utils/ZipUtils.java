package com.ai.cwf.unrar.utils;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import de.innosystec.unrar.Archive;
import de.innosystec.unrar.rarfile.FileHeader;

/**
 * Created at 陈 on 2017/5/17.
 * 文件压缩和解压缩
 *
 * @author chenwanfeng
 * @email 237142681@qq.com
 */

public class ZipUtils {
    private static final String TAG = "ZipUtils";

    /**
     * DeCompress the ZIP to the path
     *
     * @param zipFileString name of ZIP
     * @param outPathString path to be unZIP
     * @throws Exception
     */
    public static String UnZipFolder(String zipFileString, String outPathString) throws Exception {
        // check file exists
        File zipFile = new File(zipFileString);
        if (zipFileString.endsWith(".rar")) {
            return UnRarFolder(zipFileString, outPathString);
        }
        if (!zipFile.exists())
            throw new IOException("file not exists");
//        outPathString += File.separator + zipFile.getName().substring(0, zipFile.getName().lastIndexOf("."));
        // create zip input
        ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile));
        ZipEntry zipEntry;
        String zipPathName = "";
        Log.d(TAG, "start unZip: " + zipFileString);
        // zip
        while ((zipEntry = zipInputStream.getNextEntry()) != null) {
            zipPathName = zipEntry.getName();
            Log.d(TAG, "zipPath: " + zipPathName);
            if (zipEntry.isDirectory()) {
                File folder = new File(outPathString + File.separator + zipPathName);
                if (!folder.exists())
                    folder.mkdirs();
            } else {
                File file = new File(outPathString + File.separator + zipPathName);
                File pFile = file.getParentFile();
                if (!pFile.exists()) {
                    pFile.mkdirs();
                }
                file.createNewFile();
                // outPut file
                FileOutputStream out = new FileOutputStream(file);
                int len;
                byte[] buffer = new byte[1024];
                while ((len = zipInputStream.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                    out.flush();
                }
                out.close();
            }
        }
        // finish
        Log.d(TAG, "end unZip");
        zipInputStream.close();
        return outPathString;
    }

    public static String UnRarFolder(String zipFileString, String outPathString) {
        Log.d("start unRar", System.currentTimeMillis() + "");
        File srcFile = new File(zipFileString);
        if (null == outPathString || "".equals(outPathString)) {
            outPathString = srcFile.getParentFile().getPath();
        }
        // 保证文件夹路径最后是"/"或者"\"
        char lastChar = outPathString.charAt(outPathString.length() - 1);
//        if (lastChar != '/' && lastChar != '\\') {
//            outPathString += File.separator;
//        }
//        Log.d(TAG, "unrar file to :" + outPathString);


        FileOutputStream fileOut = null;
        Archive rarfile = null;

        try {
            rarfile = new Archive(srcFile);
            FileHeader fh = null;
            final int total = rarfile.getFileHeaders().size();
            for (int i = 0; i < rarfile.getFileHeaders().size(); i++) {
                fh = rarfile.getFileHeaders().get(i);
                String entrypath = "";
                if (fh.isUnicode()) {//解決中文乱码
                    entrypath = fh.getFileNameW().trim();
                } else {
                    entrypath = fh.getFileNameString().trim();
                }
                entrypath = entrypath.replaceAll("\\\\", "/");

                File file = new File(outPathString, entrypath);
                Log.d(TAG, "unrar entry file :" + file.getPath());

                if (fh.isDirectory()) {
                    file.mkdirs();
                } else {
                    File parent = file.getParentFile();
                    if (parent != null && !parent.exists()) {
                        parent.mkdirs();
                    }
                    fileOut = new FileOutputStream(file);
                    rarfile.extractFile(fh, fileOut);
                    fileOut.close();
                }
            }
            rarfile.close();


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileOut != null) {
                try {
                    fileOut.close();
                    fileOut = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (rarfile != null) {
                try {
                    rarfile.close();
                    rarfile = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        Log.d("finish unRar", System.currentTimeMillis() + "");
        return outPathString;
    }

    public static String ZipFolder(String zipFilePath, String outZipString) throws Exception {
        //create ZIP
        ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(outZipString));
        //create the file
        File file = new File(zipFilePath);
        //compress
        ZipFiles(file.getParent() + File.separator, file.getName(), zipOutputStream);
        //finish and close
        zipOutputStream.finish();
        zipOutputStream.close();
        return outZipString;
    }

    /**
     * compress files
     *
     * @param folderString
     * @param fileString
     * @param zipOutputSteam
     * @throws Exception
     */
    private static void ZipFiles(String folderString, String fileString, ZipOutputStream zipOutputSteam) throws Exception {
        if (zipOutputSteam == null)
            return;
        File file = new File(folderString + fileString);
        if (file.isFile()) {
            ZipEntry zipEntry = new ZipEntry(fileString);
            FileInputStream inputStream = new FileInputStream(file);
            zipOutputSteam.putNextEntry(zipEntry);
            int len;
            byte[] buffer = new byte[4096];
            while ((len = inputStream.read(buffer)) != -1) {
                zipOutputSteam.write(buffer, 0, len);
            }
            zipOutputSteam.closeEntry();
        } else {
            //folder
            String fileList[] = file.list();
            //no child file and compress
            if (fileList.length <= 0) {
                ZipEntry zipEntry = new ZipEntry(fileString + File.separator);
                zipOutputSteam.putNextEntry(zipEntry);
                zipOutputSteam.closeEntry();
            }
            //child files and recursion
            for (int i = 0; i < fileList.length; i++) {
                ZipFiles(folderString, fileString + File.separator + fileList[i], zipOutputSteam);
            }//end of for
        }
    }
}
