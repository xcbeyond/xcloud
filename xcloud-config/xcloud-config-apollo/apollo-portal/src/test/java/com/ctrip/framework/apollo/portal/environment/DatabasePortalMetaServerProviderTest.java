package com.ctrip.framework.apollo.portal.environment;

import static org.junit.Assert.*;

import com.ctrip.framework.apollo.portal.component.config.PortalConfig;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DatabasePortalMetaServerProviderTest {

  private DatabasePortalMetaServerProvider databasePortalMetaServerProvider;
  @Mock
  private PortalConfig portalConfig;

  private Map<String, String> metaServiceMap;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    metaServiceMap = new HashMap<>();
    metaServiceMap.put("nothing", "http://unknown.com");
    metaServiceMap.put("dev", "http://server.com:8080");
    Mockito.when(portalConfig.getMetaServers()).thenReturn(metaServiceMap);

    // use mocked object to construct
    databasePortalMetaServerProvider = new DatabasePortalMetaServerProvider(portalConfig);
  }

  @Test
  public void testGetMetaServerAddress() {
    String address = databasePortalMetaServerProvider.getMetaServerAddress(Env.DEV);
    assertEquals("http://server.com:8080", address);

    String newMetaServerAddress = "http://another-server.com:8080";
    metaServiceMap.put("dev", newMetaServerAddress);

    databasePortalMetaServerProvider.reload();

    assertEquals(newMetaServerAddress, databasePortalMetaServerProvider.getMetaServerAddress(Env.DEV));

  }

  @Test
  public void testExists() {
    assertTrue(databasePortalMetaServerProvider.exists(Env.DEV));
    assertFalse(databasePortalMetaServerProvider.exists(Env.PRO));
    assertTrue(databasePortalMetaServerProvider.exists(Env.addEnvironment("nothing")));
  }
}