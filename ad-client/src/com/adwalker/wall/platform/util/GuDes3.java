package com.adwalker.wall.platform.util;
import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec; 
/**
 * 3DES加密工具类
 */
public class GuDes3 {
	
		private final static String secretKey = "adwalkerwifi@lx100$#365#";  // 密钥
		private final static String iv = "76540123"; // 向量
		public final static String iv_android = "12345678";
		private final static String encoding = "utf-8"; // 加解密统一使用的编码方式
		
        public static void main(String[] args) {
        	
        	
        }
        /**
         * 3DES加密
         * 
         * @param plainText 普通文本
         * @return
         * @throws Exception 
         */
        public static String encode(String plainText) throws Exception {
        	
                Key deskey = null;
                DESedeKeySpec spec = new DESedeKeySpec(secretKey.getBytes());
                SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
                deskey = keyfactory.generateSecret(spec);
                Cipher cipher = Cipher.getInstance("desede/CBC/PKCS5Padding");
                IvParameterSpec ips = new IvParameterSpec(iv.getBytes());
                cipher.init(Cipher.ENCRYPT_MODE, deskey, ips);
                byte[] encryptData = cipher.doFinal(plainText.getBytes(encoding));
                return AdBase64.encode(encryptData);
        }
        
        
      /**
      * 3DES解密
      * 
      * @param encryptText 解密文本
      * @return
      * @throws Exception
      */
     public static String decode(String encryptText) throws Exception {
             Key deskey = null;
             DESedeKeySpec spec = new DESedeKeySpec(secretKey.getBytes());
             SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
             deskey = keyfactory.generateSecret(spec);
             Cipher cipher = Cipher.getInstance("desede/CBC/PKCS5Padding");
             IvParameterSpec ips = new IvParameterSpec(iv.getBytes());
             cipher.init(Cipher.DECRYPT_MODE, deskey, ips);

             byte[] decryptData = cipher.doFinal(AdBase64.decode(encryptText));

             return new String(decryptData, encoding);
     }
}
