package bg.bgx.security;

import java.security.Key;

import javax.crypto.spec.SecretKeySpec;

public class KeyGenerator {
	
	private static final String KEY = "secret";
	
    public static Key generateKey() {
        Key key = new SecretKeySpec(KEY.getBytes(), 0, KEY.getBytes().length, "DES");
        return key;
    }

}
