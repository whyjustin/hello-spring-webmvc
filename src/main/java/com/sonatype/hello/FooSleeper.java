package com.sonatype.hello;

/**
 * Just some code to prevent a similar match with other bare projects
 */
public class FooSleeper
{
  private final int seed;

  public FooSleeper(final int seed) {
    this.seed = seed;
  }

  public void sleep(final int milliseconds) throws InterruptedException {
    Thread.sleep(calculateMillisecondsFromSeed(milliseconds));
  }

  protected int calculateMillisecondsFromSeed(final int milliseconds) {
    return milliseconds % seed;
  }
}
