package com.gracecode.android.presentation.helper;

import com.gracecode.android.presentation.dao.Pin;
import org.apache.commons.io.FileUtils;

import java.io.*;

public class FileHelper {
    public static final String DEFAULT_CHARSET = "utf-8";

    public static boolean putFileContent(File file, InputStream inputStream) throws IOException {
        if (!file.exists() && !file.createNewFile()) {
            return false;
        }

        byte[] buffer = new byte[1024];
        FileOutputStream fileOutputStream = new FileOutputStream(file);

        for (int len; (len = inputStream.read(buffer)) != -1; ) {
            fileOutputStream.write(buffer, 0, len);
        }

        inputStream.close();
        fileOutputStream.close();
        return true;
    }


    /**
     * 两个文件的相互拷贝
     *
     * @param source File
     * @param dest   File
     * @throws IOException
     */
    public static void copyFile(File source, File dest) throws IOException {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = new FileInputStream(source);
            outputStream = new FileOutputStream(dest);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }

            if (outputStream != null) {
                outputStream.close();
            }
        }
    }


    public static String getFileContent(InputStream fis) throws IOException {
        InputStreamReader isr = new InputStreamReader(fis, DEFAULT_CHARSET);
        BufferedReader br = new BufferedReader(isr);
        StringBuffer sbContent = new StringBuffer();

        String sLine;
        while ((sLine = br.readLine()) != null) {
            String s = sLine + "\n";
            sbContent = sbContent.append(s);
        }

        isr.close();
        br.close();
        return sbContent.toString();
    }


    public static String getFileContent(String filePath) throws IOException {
        FileInputStream fis = new FileInputStream(filePath);
        String content = getFileContent(fis);
        fis.close();
        return content;
    }

    public static long getSizeOfDirectory(File folder) {
        return FileUtils.sizeOfDirectory(folder);
    }

    public static String getSavedFileName(Pin mPin) {
        return "_" + mPin.getKey() + ".jpeg";
    }
}
