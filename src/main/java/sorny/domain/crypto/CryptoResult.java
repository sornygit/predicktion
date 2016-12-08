package sorny.domain.crypto;

/**
 * Created by Magnus on 2016-12-05.
 */
public class CryptoResult {
    public String aesKey;
    public byte[] encryptedText;

    public CryptoResult(String aesKey, byte[] encryptedText) {
        this.aesKey = aesKey;
        this.encryptedText = encryptedText;
    }
}
