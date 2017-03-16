package net.craswell.security.models.junit;

import org.junit.Assert;
import org.junit.Test;

import net.craswell.security.annotations.EncryptIfPossible;
import net.craswell.security.annotations.EncryptionProviderException;
import net.craswell.security.annotations.EncryptionProviderImpl;

/**
 * EncryptionProviderImpl tests.
 * 
 * @author scraswell@gmail.com
 *
 */
public class EncryptionProviderImplTests {
  /**
   * Testing passphrase.
   */
  static final String TEST_PASSPHRASE = "This is a bad passphrase.";

  /**
   * Testing passphrase.
   */
  static final String TEST_BAD_PASSPHRASE = "This is a bad passphrase!";

  /**
   * Test value for encryption.
   */
  static final String TEST_VALUE = "Hello World!";

  /**
   * Tests that annotated fields can be encrypted and decrypted.
   * 
   * @throws EncryptionProviderException
   */
  @Test
  public void AnnotatedFieldsCanBeSecured()
      throws EncryptionProviderException {
    TestClass tc = new TestClass();
    tc.setMySecuredField1(TEST_VALUE);

    EncryptionProviderImpl ep = new EncryptionProviderImpl(TEST_PASSPHRASE);
    ep.encryptObject(tc);

    Assert.assertNotEquals(TEST_VALUE, tc.getMySecuredField1());

    ep.decryptObject(tc);
    Assert.assertEquals(TEST_VALUE, tc.getMySecuredField1());
  }

  /**
   * Tests that an exception is properly thrown when an attempt to decrypt data with an invalid
   * passphrase fails.
   * 
   * @throws EncryptionProviderException
   */
  @Test(expected = EncryptionProviderException.class)
  public void AttemptToDecryptWithInvalidKeyThrowsExpectedException()
      throws EncryptionProviderException {
    TestClass tc = new TestClass();
    tc.setMySecuredField1(TEST_VALUE);

    EncryptionProviderImpl ep1 = new EncryptionProviderImpl(TEST_PASSPHRASE);
    EncryptionProviderImpl ep2 = new EncryptionProviderImpl(TEST_BAD_PASSPHRASE);

    ep1.encryptObject(tc);
    ep2.decryptObject(tc);
  }

  /**
   * A test model with an annotated field.
   * 
   * @author scraswell@gmail.com
   *
   */
  public class TestClass {
    @EncryptIfPossible
    private String mySecuredField1;

    public String getMySecuredField1() {
      return mySecuredField1;
    }

    public void setMySecuredField1(String mySecuredField1) {
      this.mySecuredField1 = mySecuredField1;
    }
  }

}
