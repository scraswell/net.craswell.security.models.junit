package net.craswell.security.models.junit;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Assert;
import org.junit.Test;

import net.craswell.common.BinarySerializerException;
import net.craswell.common.encryption.AesToolException;
import net.craswell.common.encryption.PassphraseProvider;
import net.craswell.common.persistence.SqLiteSessionManagerImpl;
import net.craswell.security.models.generated.ConfigurationItemSecured;

public class SecuredModelTesting {

  /**
   * Tests that the session manager can persist, retrieve and delete objects.
   * @throws BinarySerializerException 
   * @throws AesToolException 
   */
  @Test
  public void CanPerformStandardCrudOperations() throws AesToolException, BinarySerializerException {
    try (
        SqLiteSessionManagerImpl sm = new SqLiteSessionManagerImpl();
        Session s = sm.openSession()) {
      Transaction tx = s.beginTransaction();
      ConfigurationItemSecured c = new ConfigurationItemSecured();
      
      c.setPassphraseProvider(new TestPassphraseProviderImpl());

      c.setName("Test");
      c.setValue("Value");
      c.setNumber(10);

      s.saveOrUpdate(c);
      tx.commit();

      Assert.assertEquals(10, c.getNumber());
      Assert.assertEquals("Value", c.getValue());
      Assert.assertEquals("Test", c.getName());

      Assert.assertNotEquals(10, c.getNumberSecured());
      Assert.assertNotEquals("Value", c.getValueSecured());
      Assert.assertNotEquals("Test", c.getNameSecured());

      // Create CriteriaBuilder
      CriteriaBuilder builder = s.getCriteriaBuilder();

      // Create CriteriaQuery
      CriteriaQuery<ConfigurationItemSecured> criteria = builder
          .createQuery(ConfigurationItemSecured.class);
      Root<ConfigurationItemSecured> variableRoot = criteria
          .from(ConfigurationItemSecured.class);
      criteria.select(variableRoot);

      List<ConfigurationItemSecured> configurationItemList = s.createQuery(criteria)
          .list();

      Assert.assertTrue(configurationItemList.size() > 0);

      tx = s.beginTransaction();
      for (ConfigurationItemSecured i : configurationItemList) {
        s.delete(i);
      }
      tx.commit();

      configurationItemList = s.createQuery(criteria)
          .list();

      Assert.assertTrue(configurationItemList.size() == 0);
    }
  }
  
  private class TestPassphraseProviderImpl implements PassphraseProvider {

    @Override
    public String getPassphrase() {
      return "this is a bad passphrase";
    }
    
  }
}
