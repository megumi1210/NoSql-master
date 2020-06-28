package org.example.util;

import java.io.*;

/** @author chenj */
public final class FileUtil {

  /**
   * 把文件转换为二进制数组
   *
   * @param file 文件
   * @return 二进制数组
   */
  public static byte[] getFileToByte(File file) {
    byte[] result = new byte[0];
    try {
      InputStream is = new FileInputStream(file);
      result = getInputStreamToByte(is);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    return result;
  }

  /**
   * 把输入流转换为字节数组
   *
   * @param is 输入流
   * @return 字节数组
   */
  public static byte[] getInputStreamToByte(InputStream is) {
    byte[] result = new byte[0];
    try {
      result = new byte[is.available()];
      ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
      byte[] bytes = new byte[2048];
      int len = is.read(bytes);
      while (len != -1) {
        byteStream.write(bytes, 0, len);
        len = is.read(bytes);
      }
      result = byteStream.toByteArray();
    } catch (IOException ex) {
      ex.printStackTrace();
    }
    return result;
  }
}
