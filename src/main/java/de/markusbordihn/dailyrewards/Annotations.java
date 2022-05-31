package de.markusbordihn.dailyrewards;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class Annotations {

  @Retention(RetentionPolicy.SOURCE)
  @Repeatable(TemplateEntryPointContainer.class)
  public @interface TemplateEntryPoint {
    String value() default "";
  }

  @Retention(RetentionPolicy.SOURCE)
  public @interface TemplateEntryPointContainer {
    public TemplateEntryPoint[] value();
  }

}
