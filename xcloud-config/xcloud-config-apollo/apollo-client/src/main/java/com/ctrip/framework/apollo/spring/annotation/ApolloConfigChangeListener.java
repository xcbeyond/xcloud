package com.ctrip.framework.apollo.spring.annotation;

import com.ctrip.framework.apollo.core.ConfigConsts;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use this annotation to register Apollo ConfigChangeListener.
 *
 * <p>Usage example:</p>
 * <pre class="code">
 * //Listener on namespaces of "someNamespace" and "anotherNamespace", will be notified when any key is changed
 * &#064;ApolloConfigChangeListener({"someNamespace","anotherNamespace"})
 * private void onChange(ConfigChangeEvent changeEvent) {
 *     //handle change event
 * }
 * <br />
 * //Listener on namespaces of "someNamespace" and "anotherNamespace", will only be notified when "someKey" or "anotherKey" is changed
 * &#064;ApolloConfigChangeListener(value = {"someNamespace","anotherNamespace"}, interestedKeys = {"someKey", "anotherKey"})
 * private void onChange(ConfigChangeEvent changeEvent) {
 *     //handle change event
 * }
 * </pre>
 *
 * @author Jason Song(song_s@ctrip.com)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface ApolloConfigChangeListener {
  /**
   * Apollo namespace for the config, if not specified then default to application
   */
  String[] value() default {ConfigConsts.NAMESPACE_APPLICATION};

  /**
   * The keys interested by the listener, will only be notified if any of the interested keys is changed.
   * <br />
   * If neither of {@code interestedKeys} and {@code interestedKeyPrefixes} is specified then the {@code listener} will be notified when any key is changed.
   */
  String[] interestedKeys() default {};

  /**
   * The key prefixes that the listener is interested in, will be notified if and only if the changed keys start with anyone of the prefixes.
   * The prefixes will simply be used to determine whether the {@code listener} should be notified or not using {@code changedKey.startsWith(prefix)}.
   * e.g. "spring." means that {@code listener} is interested in keys that starts with "spring.", such as "spring.banner", "spring.jpa", etc.
   * and "application" means that {@code listener} is interested in keys that starts with "application", such as "applicationName", "application.port", etc.
   * <br />
   * If neither of {@code interestedKeys} and {@code interestedKeyPrefixes} is specified then the {@code listener} will be notified when whatever key is changed.
   */
  String[] interestedKeyPrefixes() default {};
}
