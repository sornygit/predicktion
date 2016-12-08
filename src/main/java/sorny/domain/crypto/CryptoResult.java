package sorny.domain.crypto;

/**
 * Crypto result transport object
 */
public class CryptoResult {
    public String aesKey;
    public byte[] encryptedText;

    public CryptoResult(String aesKey, byte[] encryptedText) {
        this.aesKey = aesKey;
        this.encryptedText = encryptedText;
    }
}
