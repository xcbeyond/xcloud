package com.ctrip.framework.apollo.adminservice.controller;

import com.ctrip.framework.apollo.biz.entity.AccessKey;
import com.ctrip.framework.apollo.biz.service.AccessKeyService;
import com.ctrip.framework.apollo.common.dto.AccessKeyDTO;
import com.ctrip.framework.apollo.common.utils.BeanUtils;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author nisiyong
 */
@RestController
public class AccessKeyController {

  private final AccessKeyService accessKeyService;

  public AccessKeyController(
      AccessKeyService accessKeyService) {
    this.accessKeyService = accessKeyService;
  }

  @PostMapping(value = "/apps/{appId}/accesskeys")
  public AccessKeyDTO create(@PathVariable String appId, @RequestBody AccessKeyDTO dto) {
    AccessKey entity = BeanUtils.transform(AccessKey.class, dto);
    entity = accessKeyService.create(appId, entity);
    return BeanUtils.transform(AccessKeyDTO.class, entity);
  }

  @GetMapping(value = "/apps/{appId}/accesskeys")
  public List<AccessKeyDTO> findByAppId(@PathVariable String appId) {
    List<AccessKey> accessKeyList = accessKeyService.findByAppId(appId);
    return BeanUtils.batchTransform(AccessKeyDTO.class, accessKeyList);
  }

  @DeleteMapping(value = "/apps/{appId}/accesskeys/{id}")
  public void delete(@PathVariable String appId, @PathVariable long id, String operator) {
    accessKeyService.delete(appId, id, operator);
  }

  @PutMapping(value = "/apps/{appId}/accesskeys/{id}/enable")
  public void enable(@PathVariable String appId, @PathVariable long id, String operator) {
    AccessKey entity = new AccessKey();
    entity.setId(id);
    entity.setEnabled(true);
    entity.setDataChangeLastModifiedBy(operator);

    accessKeyService.update(appId, entity);
  }

  @PutMapping(value = "/apps/{appId}/accesskeys/{id}/disable")
  public void disable(@PathVariable String appId, @PathVariable long id, String operator) {
    AccessKey entity = new AccessKey();
    entity.setId(id);
    entity.setEnabled(false);
    entity.setDataChangeLastModifiedBy(operator);

    accessKeyService.update(appId, entity);
  }
}
