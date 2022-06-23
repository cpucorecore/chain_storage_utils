package com.ancun.chain_storage.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommonUtils {
  public static String bytesToHexString(byte[] data) {
    StringBuffer sb = new StringBuffer(data.length * 2);
    for (int i = 0; i < data.length; i++) {
      String hex = Integer.toHexString(0xFF & data[i]);
      if (hex.length() < 2) {
        sb.append("0");
      }
      sb.append(hex);
    }
    return sb.toString().toLowerCase();
  }
}
