package net.sf.appstatus.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicate that a method is a property provider for AppStatus.
 * 
 * Must annotate a public method
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface AppStatusProperties {

  /**
   * category of the properties.
   */
  String value();

}
