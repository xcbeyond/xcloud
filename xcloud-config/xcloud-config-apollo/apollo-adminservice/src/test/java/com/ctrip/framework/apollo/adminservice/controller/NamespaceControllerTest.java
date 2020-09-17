package com.ctrip.framework.apollo.adminservice.controller;

import com.ctrip.framework.apollo.common.dto.NamespaceDTO;
import com.ctrip.framework.apollo.common.utils.InputValidator;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.web.client.HttpClientErrorException;
import static org.hamcrest.Matchers.containsString;

/**
 * Created by kezhenxu at 2019/1/8 16:27.
 *
 * @author kezhenxu (kezhenxu94@163.com)
 */
public class NamespaceControllerTest extends AbstractControllerTest {
  @Test
  public void create() {
    try {
      NamespaceDTO namespaceDTO = new NamespaceDTO();
      namespaceDTO.setClusterName("cluster");
      namespaceDTO.setNamespaceName("invalid name");
      namespaceDTO.setAppId("whatever");
      restTemplate.postForEntity(
          url("/apps/{appId}/clusters/{clusterName}/namespaces"),
          namespaceDTO, NamespaceDTO.class, namespaceDTO.getAppId(), namespaceDTO.getClusterName());
      Assert.fail("Should throw");
    } catch (HttpClientErrorException e) {
      Assert.assertThat(new String(e.getResponseBodyAsByteArray()), containsString(InputValidator.INVALID_CLUSTER_NAMESPACE_MESSAGE));
    }
  }
}
