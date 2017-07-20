package com.sonatype.hello.dto;

import java.util.Date;

/**
 * Just some code to prevent a similar match with other bare projects
 */
public class FooDto
{
  private final String foo;
  private final String bar;
  private final Date date;

  public FooDto(final String foo, final String bar, final Date date) {
    this.foo = foo;
    this.bar = bar;
    this.date = date;
  }

  public String getFoo() {
    return foo;
  }

  public String getBar() {
    return bar;
  }

  public Date getDate() {
    return date;
  }
}
