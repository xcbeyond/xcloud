package com.ctrip.framework.apollo.portal.spi.defaultimpl;

import com.ctrip.framework.apollo.common.entity.App;
import com.ctrip.framework.apollo.common.entity.BaseEntity;
import com.ctrip.framework.apollo.core.ConfigConsts;
import com.ctrip.framework.apollo.portal.environment.Env;
import com.ctrip.framework.apollo.portal.component.config.PortalConfig;
import com.ctrip.framework.apollo.portal.constant.PermissionType;
import com.ctrip.framework.apollo.portal.constant.RoleType;
import com.ctrip.framework.apollo.portal.entity.po.Permission;
import com.ctrip.framework.apollo.portal.entity.po.Role;
import com.ctrip.framework.apollo.portal.repository.PermissionRepository;
import com.ctrip.framework.apollo.portal.service.RoleInitializationService;
import com.ctrip.framework.apollo.portal.service.RolePermissionService;
import com.ctrip.framework.apollo.portal.service.SystemRoleManagerService;
import com.ctrip.framework.apollo.portal.util.RoleUtils;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by timothy on 2017/4/26.
 */
public class DefaultRoleInitializationService implements RoleInitializationService {

  @Autowired
  private RolePermissionService rolePermissionService;
  @Autowired
  private PortalConfig portalConfig;
  @Autowired
  private PermissionRepository permissionRepository;

  @Transactional
  public void initAppRoles(App app) {
    String appId = app.getAppId();

    String appMasterRoleName = RoleUtils.buildAppMasterRoleName(appId);

    //has created before
    if (rolePermissionService.findRoleByRoleName(appMasterRoleName) != null) {
      return;
    }
    String operator = app.getDataChangeCreatedBy();
    //create app permissions
    createAppMasterRole(appId, operator);
    //create manageAppMaster permission
    createManageAppMasterRole(appId, operator);

    //assign master role to user
    rolePermissionService
        .assignRoleToUsers(RoleUtils.buildAppMasterRoleName(appId), Sets.newHashSet(app.getOwnerName()),
            operator);

    initNamespaceRoles(appId, ConfigConsts.NAMESPACE_APPLICATION, operator);
    initNamespaceEnvRoles(appId, ConfigConsts.NAMESPACE_APPLICATION, operator);

    //assign modify、release namespace role to user
    rolePermissionService.assignRoleToUsers(
        RoleUtils.buildNamespaceRoleName(appId, ConfigConsts.NAMESPACE_APPLICATION, RoleType.MODIFY_NAMESPACE),
        Sets.newHashSet(operator), operator);
    rolePermissionService.assignRoleToUsers(
        RoleUtils.buildNamespaceRoleName(appId, ConfigConsts.NAMESPACE_APPLICATION, RoleType.RELEASE_NAMESPACE),
        Sets.newHashSet(operator), operator);

  }

  @Transactional
  public void initNamespaceRoles(String appId, String namespaceName, String operator) {

    String modifyNamespaceRoleName = RoleUtils.buildModifyNamespaceRoleName(appId, namespaceName);
    if (rolePermissionService.findRoleByRoleName(modifyNamespaceRoleName) == null) {
      createNamespaceRole(appId, namespaceName, PermissionType.MODIFY_NAMESPACE,
          modifyNamespaceRoleName, operator);
    }

    String releaseNamespaceRoleName = RoleUtils.buildReleaseNamespaceRoleName(appId, namespaceName);
    if (rolePermissionService.findRoleByRoleName(releaseNamespaceRoleName) == null) {
      createNamespaceRole(appId, namespaceName, PermissionType.RELEASE_NAMESPACE,
          releaseNamespaceRoleName, operator);
    }
  }

  @Transactional
  public void initNamespaceEnvRoles(String appId, String namespaceName, String operator) {
    List<Env> portalEnvs = portalConfig.portalSupportedEnvs();

    for (Env env : portalEnvs) {
      initNamespaceSpecificEnvRoles(appId, namespaceName, env.toString(), operator);
    }
  }

  @Transactional
  public void initNamespaceSpecificEnvRoles(String appId, String namespaceName, String env, String operator) {
    String modifyNamespaceEnvRoleName = RoleUtils.buildModifyNamespaceRoleName(appId, namespaceName, env);
    if (rolePermissionService.findRoleByRoleName(modifyNamespaceEnvRoleName) == null) {
      createNamespaceEnvRole(appId, namespaceName, PermissionType.MODIFY_NAMESPACE, env,
          modifyNamespaceEnvRoleName, operator);
    }

    String releaseNamespaceEnvRoleName = RoleUtils.buildReleaseNamespaceRoleName(appId, namespaceName, env);
    if (rolePermissionService.findRoleByRoleName(releaseNamespaceEnvRoleName) == null) {
      createNamespaceEnvRole(appId, namespaceName, PermissionType.RELEASE_NAMESPACE, env,
          releaseNamespaceEnvRoleName, operator);
    }
  }

  @Transactional
  public void initCreateAppRole() {
    if (rolePermissionService.findRoleByRoleName(SystemRoleManagerService.CREATE_APPLICATION_ROLE_NAME) != null) {
      return;
    }
    Permission createAppPermission = permissionRepository.findTopByPermissionTypeAndTargetId(PermissionType.CREATE_APPLICATION, SystemRoleManagerService.SYSTEM_PERMISSION_TARGET_ID);
    if (createAppPermission == null) {
      // create application permission init
      createAppPermission = createPermission(SystemRoleManagerService.SYSTEM_PERMISSION_TARGET_ID, PermissionType.CREATE_APPLICATION, "apollo");
      rolePermissionService.createPermission(createAppPermission);
    }
    //  create application role init
    Role createAppRole = createRole(SystemRoleManagerService.CREATE_APPLICATION_ROLE_NAME, "apollo");
    rolePermissionService.createRoleWithPermissions(createAppRole, Sets.newHashSet(createAppPermission.getId()));
  }

  @Transactional
  private void createManageAppMasterRole(String appId, String operator) {
    Permission permission = createPermission(appId, PermissionType.MANAGE_APP_MASTER, operator);
    rolePermissionService.createPermission(permission);
    Role role = createRole(RoleUtils.buildAppRoleName(appId, PermissionType.MANAGE_APP_MASTER), operator);
    Set<Long> permissionIds = new HashSet<>();
    permissionIds.add(permission.getId());
    rolePermissionService.createRoleWithPermissions(role, permissionIds);
  }

  // fix historical data
  @Transactional
  public void initManageAppMasterRole(String appId, String operator) {
    String manageAppMasterRoleName = RoleUtils.buildAppRoleName(appId, PermissionType.MANAGE_APP_MASTER);
    if (rolePermissionService.findRoleByRoleName(manageAppMasterRoleName) != null) {
      return;
    }
    synchronized (DefaultRoleInitializationService.class) {
      createManageAppMasterRole(appId, operator);
    }
  }

  private void createAppMasterRole(String appId, String operator) {
    Set<Permission> appPermissions =
        Stream.of(PermissionType.CREATE_CLUSTER, PermissionType.CREATE_NAMESPACE, PermissionType.ASSIGN_ROLE)
            .map(permissionType -> createPermission(appId, permissionType, operator)).collect(Collectors.toSet());
    Set<Permission> createdAppPermissions = rolePermissionService.createPermissions(appPermissions);
    Set<Long>
        appPermissionIds =
        createdAppPermissions.stream().map(BaseEntity::getId).collect(Collectors.toSet());

    //create app master role
    Role appMasterRole = createRole(RoleUtils.buildAppMasterRoleName(appId), operator);

    rolePermissionService.createRoleWithPermissions(appMasterRole, appPermissionIds);
  }

  private Permission createPermission(String targetId, String permissionType, String operator) {
    Permission permission = new Permission();
    permission.setPermissionType(permissionType);
    permission.setTargetId(targetId);
    permission.setDataChangeCreatedBy(operator);
    permission.setDataChangeLastModifiedBy(operator);
    return permission;
  }

  private Role createRole(String roleName, String operator) {
    Role role = new Role();
    role.setRoleName(roleName);
    role.setDataChangeCreatedBy(operator);
    role.setDataChangeLastModifiedBy(operator);
    return role;
  }

  private void createNamespaceRole(String appId, String namespaceName, String permissionType,
                                   String roleName, String operator) {

    Permission permission =
        createPermission(RoleUtils.buildNamespaceTargetId(appId, namespaceName), permissionType, operator);
    Permission createdPermission = rolePermissionService.createPermission(permission);

    Role role = createRole(roleName, operator);
    rolePermissionService
        .createRoleWithPermissions(role, Sets.newHashSet(createdPermission.getId()));
  }

  private void createNamespaceEnvRole(String appId, String namespaceName, String permissionType, String env,
                                      String roleName, String operator) {
    Permission permission =
        createPermission(RoleUtils.buildNamespaceTargetId(appId, namespaceName, env), permissionType, operator);
    Permission createdPermission = rolePermissionService.createPermission(permission);

    Role role = createRole(roleName, operator);
    rolePermissionService
        .createRoleWithPermissions(role, Sets.newHashSet(createdPermission.getId()));
  }
}
