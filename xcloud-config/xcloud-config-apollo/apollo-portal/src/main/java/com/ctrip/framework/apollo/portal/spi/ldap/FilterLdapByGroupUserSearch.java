

package com.ctrip.framework.apollo.portal.spi.ldap;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

import javax.naming.Name;
import javax.naming.directory.SearchControls;
import javax.naming.ldap.LdapName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.support.BaseLdapPathContextSource;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.ldap.SpringSecurityLdapTemplate;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;

/**
 * the FilterLdapByGroupUserSearch description.
 *
 * @author wuzishu
 */
public class FilterLdapByGroupUserSearch extends FilterBasedLdapUserSearch {

  private static final Logger logger = LoggerFactory.getLogger(FilterLdapByGroupUserSearch.class);
  private static final String MEMBER_UID_ATTR_NAME = "memberUid";
  private String searchBase;
  private String groupBase;
  private String groupSearch;
  private String rdnKey;
  private String groupMembershipAttrName;
  private String loginIdAttrName;

  private final SearchControls searchControls = new SearchControls();

  private BaseLdapPathContextSource contextSource;


  public FilterLdapByGroupUserSearch(String searchBase, String searchFilter,
      String groupBase, BaseLdapPathContextSource contextSource, String groupSearch,
      String rdnKey, String groupMembershipAttrName, String loginIdAttrName) {
    super(searchBase, searchFilter, contextSource);
    this.searchBase = searchBase;
    this.groupBase = groupBase;
    this.groupSearch = groupSearch;
    this.contextSource = contextSource;
    this.rdnKey = rdnKey;
    this.groupMembershipAttrName = groupMembershipAttrName;
    this.loginIdAttrName = loginIdAttrName;
  }

  private Name searchUserById(String userId) {
    SpringSecurityLdapTemplate template = new SpringSecurityLdapTemplate(this.contextSource);
    template.setSearchControls(searchControls);
    return template.searchForObject(query().where(this.loginIdAttrName).is(userId),
        ctx -> ((DirContextAdapter) ctx).getDn());
  }


  @Override
  public DirContextOperations searchForUser(String username) {
    if (logger.isDebugEnabled()) {
      logger.debug("Searching for user '" + username + "', with user search " + this);
    }
    SpringSecurityLdapTemplate template = new SpringSecurityLdapTemplate(this.contextSource);
    template.setSearchControls(searchControls);
    return template
        .searchForObject(groupBase, groupSearch, ctx -> {
          if (!MEMBER_UID_ATTR_NAME.equals(groupMembershipAttrName)) {
            String[] members = ((DirContextAdapter) ctx)
                .getStringAttributes(groupMembershipAttrName);
            for (String item : members) {
              LdapName memberDn = LdapUtils.newLdapName(item);
              LdapName memberRdn = LdapUtils
                  .removeFirst(memberDn, LdapUtils.newLdapName(searchBase));
              String rdnValue = LdapUtils.getValue(memberRdn, rdnKey).toString();
              if (rdnValue.equalsIgnoreCase(username)) {
                return new DirContextAdapter(memberRdn.toString());
              }
            }
            throw new UsernameNotFoundException("User " + username + " not found in directory.");
          }
          String[] memberUids = ((DirContextAdapter) ctx)
              .getStringAttributes(groupMembershipAttrName);
          for (String memberUid : memberUids) {
            if (memberUid.equalsIgnoreCase(username)) {
              Name name = searchUserById(memberUid);
              LdapName ldapName = LdapUtils.newLdapName(name);
              LdapName ldapRdn = LdapUtils
                  .removeFirst(ldapName, LdapUtils.newLdapName(searchBase));
              return new DirContextAdapter(ldapRdn);
            }
          }
          throw new UsernameNotFoundException("User " + username + " not found in directory.");
        });
  }
}
