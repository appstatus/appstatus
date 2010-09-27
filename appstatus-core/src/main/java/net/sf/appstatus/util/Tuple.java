package net.sf.appstatus.util;

/**
 * un tuple immutable.
 */
public final class Tuple<F, S> {

  private static int prime = 67;

  private final F first;

  private final S second;

  public Tuple(F first, S second) {
    this.first = first;
    this.second = second;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Tuple)) {
      return false;
    }

    @SuppressWarnings("rawtypes")
    Object oFirst = ((Tuple) o).getFirst();
    @SuppressWarnings("rawtypes")
    Object oSecond = ((Tuple) o).getSecond();

    boolean areFirstsEqual = first == null ? oFirst == null : first.equals(oFirst);
    if (!areFirstsEqual) {
      return false;
    }

    boolean areSecondsEqual = second == null ? oSecond == null : second.equals(oSecond);
    return areSecondsEqual;
  }

  public F getFirst() {
    return first;
  }

  public S getSecond() {
    return second;
  }

  @Override
  public int hashCode() {
    int hash = //
    prime ^ (first == null ? 0 : first.hashCode()) //
        ^ (second == null ? 0 : second.hashCode());
    return hash;
  }

  @Override
  public String toString() {
    return "<" + String.valueOf(first) + "," + String.valueOf(second) + ">";
  }
}
