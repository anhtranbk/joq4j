package org.joq4j.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigKey {

    String value();

    String defaultValue() default "";

    String datetimeFormat() default "yyyy-MM-dd'T'HH:mm:ssZ";

    String listDelimiter() default ",";
}
