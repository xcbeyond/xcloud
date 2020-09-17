package com.ctrip.framework.apollo.adminservice.controller;

import com.ctrip.framework.apollo.biz.service.AdminService;
import com.ctrip.framework.apollo.biz.service.AppService;
import com.ctrip.framework.apollo.common.dto.AppDTO;
import com.ctrip.framework.apollo.common.entity.App;
import com.ctrip.framework.apollo.common.exception.BadRequestException;
import com.ctrip.framework.apollo.common.exception.NotFoundException;
import com.ctrip.framework.apollo.common.utils.BeanUtils;
import com.ctrip.framework.apollo.core.utils.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

@RestController
public class AppController {

  private final AppService appService;
  private final AdminService adminService;

  public AppController(final AppService appService, final AdminService adminService) {
    this.appService = appService;
    this.adminService = adminService;
  }

  @PostMapping("/apps")
  public AppDTO create(@Valid @RequestBody AppDTO dto) {
    App entity = BeanUtils.transform(App.class, dto);
    App managedEntity = appService.findOne(entity.getAppId());
    if (managedEntity != null) {
      throw new BadRequestException("app already exist.");
    }

    entity = adminService.createNewApp(entity);

    return BeanUtils.transform(AppDTO.class, entity);
  }

  @DeleteMapping("/apps/{appId:.+}")
  public void delete(@PathVariable("appId") String appId, @RequestParam String operator) {
    App entity = appService.findOne(appId);
    if (entity == null) {
      throw new NotFoundException("app not found for appId " + appId);
    }
    adminService.deleteApp(entity, operator);
  }

  @PutMapping("/apps/{appId:.+}")
  public void update(@PathVariable String appId, @RequestBody App app) {
    if (!Objects.equals(appId, app.getAppId())) {
      throw new BadRequestException("The App Id of path variable and request body is different");
    }

    appService.update(app);
  }

  @GetMapping("/apps")
  public List<AppDTO> find(@RequestParam(value = "name", required = false) String name,
                           Pageable pageable) {
    List<App> app = null;
    if (StringUtils.isBlank(name)) {
      app = appService.findAll(pageable);
    } else {
      app = appService.findByName(name);
    }
    return BeanUtils.batchTransform(AppDTO.class, app);
  }

  @GetMapping("/apps/{appId:.+}")
  public AppDTO get(@PathVariable("appId") String appId) {
    App app = appService.findOne(appId);
    if (app == null) {
      throw new NotFoundException("app not found for appId " + appId);
    }
    return BeanUtils.transform(AppDTO.class, app);
  }

  @GetMapping("/apps/{appId}/unique")
  public boolean isAppIdUnique(@PathVariable("appId") String appId) {
    return appService.isAppIdUnique(appId);
  }
}
