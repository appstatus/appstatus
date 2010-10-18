package net.sf.appstatus;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.Map;

import net.sf.appstatus.annotations.AppCheckMethod;
import net.sf.appstatus.check.impl.StatusResult;

import org.junit.Before;
import org.junit.Test;

public class StatusServiceTest extends StatusService {

  @Before
  public void setUp() {
    this.propertiesProviders.clear();
    this.probes.clear();
  }

  @Test
  public void testInit() throws Exception {
    this.init();
    assertTrue(probes.size() > 0);
  }

  @Test
  public void testMethodIsAChecker() throws Exception {
    Method[] allMethods = this.getClass().getMethods();
    for (Method method : allMethods) {
      // test only xxx method;
      if (!method.getName().startsWith("xxx")) {
        continue;
      }

      if (isACheckMethod(method)) {
        assertEquals("xxxmethod1", method.getName());
      } else {
        assertFalse("xxxmethod1".equals(method.getName()));
      }
    }
  }

  @Test
  public void testPropertiesProviderAnnotation() throws Exception {
    this.init();
    Map<String, Map<String, String>> allP = this.getProperties();

    assertEquals(1, allP.size());
    String category = allP.keySet().iterator().next();

    assertEquals("Unit Test", category);
    Map<String, String> properties = allP.get(category);
    assertEquals(1, properties.size());
    String property = properties.get("version");
    assertNotNull(property);
    assertEquals("1.0-unit test", property);
  }

  // @formatter:off -- disable eclipse formatter from here
  @AppCheckMethod
  public    StatusResult xxxmethod1() {return null;}
  public    StatusResult xxxmethod2() {return null;}
  protected StatusResult xxxmethod3() {return null;}
  public    String       xxxmethod4() {return null;}
  public    void         xxxmethod5() { }
  // @formatter:on

}
