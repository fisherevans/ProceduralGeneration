package com.fisherevans.proc_gen.worlds.math;

/**
 * Created by h13730 on 10/22/2015.
 */
public class Pair<T> {
  private final T _a, _b;

  public Pair(T a, T b) {
    _a = a;
    _b = b;
    if(_a == null || _b == null)
      throw new IllegalArgumentException("Cannot have a null in a Edge");
  }

  public T getA() {
    return _a;
  }

  public T getB() {
    return _b;
  }

  public boolean contains(Point other) {
    return getA().equals(other) || getB().equals(other);
  }

  @Override
  public int hashCode() {
    return _a.hashCode() * _b.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if(obj == null || !(obj instanceof Edge))
      return false;
    Edge other = (Edge) obj;
    return (getA().equals(other.getA()) && getB().equals(other.getB()))
        || (getA().equals(other.getB()) && getB().equals(other.getA()));
  }
}
