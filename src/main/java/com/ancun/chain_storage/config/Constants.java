package com.ancun.chain_storage.config;

public class Constants {
  public static byte[] String2SolidityBytes32(String value) {
    byte[] valueBytes = value.getBytes();
    byte[] targetBytes = new byte[32];

    for (int i = 0; i < valueBytes.length; i++) {
      targetBytes[i] = valueBytes[i];
    }
    return targetBytes;
  }

  public static final String CN_Resolver = "Resolver";
  public static final String CN_Setting = "Setting";
  public static final String CN_Blacklist = "Blacklist";
  public static final String CN_ChainStorage = "ChainStorage";
  public static final String CN_NodeManager = "NodeManager";
  public static final String CN_NodeStorage = "NodeStorage";
  public static final String CN_UserManager = "UserManager";
  public static final String CN_UserStorage = "UserStorage";
  public static final String CN_FileManager = "FileManager";
  public static final String CN_FileStorage = "FileStorage";
  public static final String Account_Admin = "Admin";

  public static final byte[] ResolverBytes32 = String2SolidityBytes32(CN_Resolver);
  public static final byte[] SettingBytes32 = String2SolidityBytes32(CN_Setting);
  public static final byte[] BlacklistBytes32 = String2SolidityBytes32(CN_Blacklist);
  public static final byte[] ChainStorageBytes32 = String2SolidityBytes32(CN_ChainStorage);
  public static final byte[] NodeManagerBytes32 = String2SolidityBytes32(CN_NodeManager);
  public static final byte[] NodeStorageBytes32 = String2SolidityBytes32(CN_NodeStorage);
  public static final byte[] UserManagerBytes32 = String2SolidityBytes32(CN_UserManager);
  public static final byte[] UserStorageBytes32 = String2SolidityBytes32(CN_UserStorage);
  public static final byte[] FileManagerBytes32 = String2SolidityBytes32(CN_FileManager);
  public static final byte[] FileStorageBytes32 = String2SolidityBytes32(CN_FileStorage);
}
