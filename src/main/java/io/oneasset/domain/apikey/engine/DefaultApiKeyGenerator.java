package io.oneasset.domain.apikey.engine;

import static io.oneasset.domain.common.DomainValidator.requireText;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class DefaultApiKeyGenerator implements ApiKeyGenerator {

  private static final String KEY_PREFIX = "oa_live_";
  private static final int RANDOM_BYTES = 32;
  private static final int VISIBLE_PREFIX_LENGTH = 16;
  private static final String HMAC_ALGORITHM = "HmacSHA256";

  private final String serverSecret;
  private final SecureRandom secureRandom;

  public DefaultApiKeyGenerator(String serverSecret) {
    this(serverSecret, new SecureRandom());
  }

  DefaultApiKeyGenerator(String serverSecret, SecureRandom secureRandom) {
    this.serverSecret = requireText(serverSecret, "serverSecret");
    this.secureRandom = secureRandom;
  }

  @Override
  public GenerateApiKey generate() {
    String rawKey = KEY_PREFIX + randomToken();
    String prefix = rawKey.substring(0, Math.min(VISIBLE_PREFIX_LENGTH, rawKey.length()));

    return GenerateApiKey.from(rawKey, hash(rawKey), prefix);
  }

  @Override
  public String hash(String rawKey) {
    String value = requireText(rawKey, "rawKey");
    try {
      Mac mac = Mac.getInstance(HMAC_ALGORITHM);
      SecretKeySpec key =
          new SecretKeySpec(serverSecret.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM);
      mac.init(key);
      return HexFormat.of().formatHex(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
    } catch (NoSuchAlgorithmException | InvalidKeyException e) {
      throw new IllegalStateException("Failed to hash API key", e);
    }
  }

  private String randomToken() {
    byte[] bytes = new byte[RANDOM_BYTES];
    secureRandom.nextBytes(bytes);
    return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
  }
}
