package sorny.domain.crypto;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.util.Base64;

/**
 * Created by Magnus on 2016-12-05.
 */
public class CryptoTool {

    private static final Cipher aesCipher = getCipherInstance();

    private static Cipher getCipherInstance() {
        try {
            return Cipher.getInstance("AES/ECB/PKCS5Padding");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static CryptoResult encrypt(String toBeEncrypted) {
        try {
            KeyGenerator keygen = KeyGenerator.getInstance("AES");
            SecretKey aesKey = keygen.generateKey();
            String aesKeyForFutureUse = Base64.getEncoder().encodeToString(
                    aesKey.getEncoded()
            );
            aesCipher.init(Cipher.ENCRYPT_MODE, aesKey);
            byte[] clearTextBuff = toBeEncrypted.getBytes("UTF-8");
            byte[] cipherTextBuff = aesCipher.doFinal(clearTextBuff);
            return new CryptoResult(aesKeyForFutureUse, cipherTextBuff);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String decrypt(CryptoResult cryptoResult) {
        try {
            byte[] aesKeyBuff = Base64.getDecoder().decode(cryptoResult.aesKey);
            SecretKey aesDecryptKey = new SecretKeySpec(aesKeyBuff, "AES");
            aesCipher.init(Cipher.DECRYPT_MODE, aesDecryptKey);
            byte[] decipheredBuff = aesCipher.doFinal(cryptoResult.encryptedText);
            return new String(decipheredBuff, "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
