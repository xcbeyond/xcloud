package com.ctrip.framework.apollo.biz.service;

import com.ctrip.framework.apollo.biz.entity.AccessKey;
import com.ctrip.framework.apollo.biz.entity.Audit;
import com.ctrip.framework.apollo.biz.repository.AccessKeyRepository;
import com.ctrip.framework.apollo.common.exception.BadRequestException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author nisiyong
 */
@Service
public class AccessKeyService {

  private static final int ACCESSKEY_COUNT_LIMIT = 5;

  private final AccessKeyRepository accessKeyRepository;
  private final AuditService auditService;

  public AccessKeyService(
      AccessKeyRepository accessKeyRepository,
      AuditService auditService) {
    this.accessKeyRepository = accessKeyRepository;
    this.auditService = auditService;
  }

  public List<AccessKey> findByAppId(String appId) {
    return accessKeyRepository.findByAppId(appId);
  }

  @Transactional
  public AccessKey create(String appId, AccessKey entity) {
    long count = accessKeyRepository.countByAppId(appId);
    if (count >= ACCESSKEY_COUNT_LIMIT) {
      throw new BadRequestException("AccessKeys count limit exceeded");
    }

    entity.setId(0L);
    entity.setAppId(appId);
    entity.setDataChangeLastModifiedBy(entity.getDataChangeCreatedBy());
    AccessKey accessKey = accessKeyRepository.save(entity);

    auditService.audit(AccessKey.class.getSimpleName(), accessKey.getId(), Audit.OP.INSERT,
        accessKey.getDataChangeCreatedBy());

    return accessKey;
  }

  @Transactional
  public AccessKey update(String appId, AccessKey entity) {
    long id = entity.getId();
    String operator = entity.getDataChangeLastModifiedBy();

    AccessKey accessKey = accessKeyRepository.findOneByAppIdAndId(appId, id);
    if (accessKey == null) {
      throw new BadRequestException("AccessKey not exist");
    }

    accessKey.setEnabled(entity.isEnabled());
    accessKey.setDataChangeLastModifiedBy(operator);
    accessKeyRepository.save(accessKey);

    auditService.audit(AccessKey.class.getSimpleName(), id, Audit.OP.UPDATE, operator);
    return accessKey;
  }

  @Transactional
  public void delete(String appId, long id, String operator) {
    AccessKey accessKey = accessKeyRepository.findOneByAppIdAndId(appId, id);
    if (accessKey == null) {
      throw new BadRequestException("AccessKey not exist");
    }

    if (accessKey.isEnabled()) {
      throw new BadRequestException("AccessKey should disable first");
    }

    accessKey.setDeleted(Boolean.TRUE);
    accessKey.setDataChangeLastModifiedBy(operator);
    accessKeyRepository.save(accessKey);

    auditService.audit(AccessKey.class.getSimpleName(), id, Audit.OP.DELETE, operator);
  }
}
