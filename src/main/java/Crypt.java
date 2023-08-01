import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

public class Crypt {
    public static AsymmetricCrypt createAsymmetricCryptObject() {
        Crypt.AsymmetricCrypt crypt = new AsymmetricCrypt();
        crypt.init();

        return crypt;
    }

    public static SymmetricCrypt createSymmetricCryptObject() {
        Crypt.SymmetricCrypt crypt = new SymmetricCrypt();
        crypt.init();

        return crypt;
    }

    public static class AsymmetricCrypt {
        private PrivateKey _privateKey = null;
        private PublicKey _publicKey = null;

        private Cipher _cipher = null;


        public void setPublicKey(PublicKey key) { _publicKey = key; }

        public PublicKey init() {
            createCipher();
            _publicKey = generateKeyPair();

            return _publicKey;
        }

        private void createCipher() {
            try {
                _cipher = Cipher.getInstance("RSA");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        private PublicKey generateKeyPair() {
            try {
                KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
                generator.initialize(2048);
                KeyPair keyPair = generator.generateKeyPair();
                _privateKey = keyPair.getPrivate();

                return keyPair.getPublic();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        public String encryptKey(SecretKey key) {
            byte[] keyBytes = keyToBytes(key);
            try {
                _cipher.init(Cipher.ENCRYPT_MODE, _publicKey);
                return Base64.getEncoder().encodeToString(_cipher.doFinal(keyBytes));
            } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
                throw new RuntimeException(e);
            }
        }

        public SecretKey decryptKey(String str) {
            byte[] keyBytes = Base64.getDecoder().decode(str);
            try {
                _cipher.init(Cipher.DECRYPT_MODE, _privateKey);
                return bytesToKey(_cipher.doFinal(Base64.getDecoder().decode(keyBytes)));
            } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
                throw new RuntimeException(e);
            }
        }

        private byte[] keyToBytes(SecretKey key) {
            return key.getEncoded();
        }

        private SecretKey bytesToKey(byte[] data) {
            return new SecretKeySpec(data, 0, data.length, "AES");
        }
    }

    public static class SymmetricCrypt {
        private SecretKey _key = null;

        private Cipher _cipher = null;


        public void setKey(SecretKey key) { _key = key; }

        public SecretKey init() {
            createCipher();
            return generateKey();
        }

        private void createCipher() {
            try {
                _cipher = Cipher.getInstance("AES");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        private SecretKey generateKey() {
            try {
                KeyGenerator generator = KeyGenerator.getInstance("AES");
                generator.init(256, new SecureRandom());
                _key = generator.generateKey();
                return _key;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public String encryptString(String text) {
            try {
                _cipher.init(Cipher.ENCRYPT_MODE, _key);
                byte[] result = _cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));
                return Base64.getEncoder().encodeToString(result);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public String decryptString(String text) {
            try {
                byte[] base64decrypted = Base64.getDecoder().decode(text);
                _cipher.init(Cipher.DECRYPT_MODE, _key);
                return new String(_cipher.doFinal(base64decrypted));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}