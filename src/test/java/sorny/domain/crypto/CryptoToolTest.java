package sorny.domain.crypto;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by Magnus on 2016-12-05.
 */
public class CryptoToolTest {
    @Test
    public void encryptDecrypt() throws Exception {
        String message = "На берегу пустынных волн";
        CryptoResult encrypted = CryptoTool.encrypt(message);
        String decrypted = CryptoTool.decrypt(encrypted);
        assertThat(decrypted, is(message));
    }
}
