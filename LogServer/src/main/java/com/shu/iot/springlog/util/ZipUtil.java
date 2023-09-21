package com.shu.iot.springlog.util;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


public class ZipUtil {

    public static void zip(String sourceFileName, String dstFileName) {
        ZipOutputStream out = null;
        BufferedOutputStream bos = null;
        FileOutputStream outputStream;
        File file = new File(dstFileName);
        File fileParent = file.getParentFile();
        if (!fileParent.exists()){
            fileParent.mkdirs();
        }
        if (!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            outputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        try {
            //创建zip输出流
            out = new ZipOutputStream(outputStream);
            //创建缓冲输出流
            bos = new BufferedOutputStream(out);
            File sourceFile = new File(sourceFileName);
            //调用压缩函数
            compress(out, bos, sourceFile, sourceFile.getName());
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                bos.close();
                out.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void compress(ZipOutputStream out, BufferedOutputStream bos, File sourceFile, String base){
        FileInputStream fos = null;
        BufferedInputStream bis = null;
        try {
            //如果路径为目录（文件夹）
            if (sourceFile.isDirectory()) {
                //取出文件夹中的文件（或子文件夹）
                File[] flist = sourceFile.listFiles();
                if (flist.length == 0) {//如果文件夹为空，则只需在目的地zip文件中写入一个目录进入点
                    out.putNextEntry(new ZipEntry(base + "/"));
                } else {//如果文件夹不为空，则递归调用compress，文件夹中的每一个文件（或文件夹）进行压缩
                    for (int i = 0; i < flist.length; i++) {
                        compress(out, bos, flist[i], base + "/" + flist[i].getName());
                    }
                }
            } else if (sourceFile.isFile()){//如果不是目录（文件夹），即为文件，则先写入目录进入点，之后将文件写入zip文件中
                out.putNextEntry(new ZipEntry(base));
                fos = new FileInputStream(sourceFile);
                bis = new BufferedInputStream(fos);
                int tag;
                //将源文件写入到zip文件中
                while ((tag = bis.read()) != -1) {
                    out.write(tag);
                }
                bis.close();
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            // do nothing
        }
    }

    public static void main(String[] args) throws Exception {
//        /** 测试压缩方法1  */
//        FileOutputStream fos1 = new FileOutputStream(new File("c:/mytest01.zip"));
//        ZipUtil.toZip("D:/log", fos1,true);

        /** 测试压缩方法2  */

        ZipUtil.zip("./log/jm", "./log/zips/jm.zip");
    }
}

