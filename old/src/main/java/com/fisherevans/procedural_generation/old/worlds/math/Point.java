package com.fisherevans.procedural_generation.old.worlds.math;

public class Point {
  private final Float _x, _y;

  public Point(Float x, Float y) {
    _x = x;
    _y = y;
  }

  public Float getY() {
    return _y;
  }

  public Float getX() {
    return _x;
  }

  public Float squaredDistance(Point other) {
    Float dx = getX()-other.getX(), dy = getY()-other.getY();
    return dx*dx + dy*dy;
  }

  @Override
  public boolean equals(Object obj) {
    if(obj == null || !(obj instanceof Point))
      return false;
    Point other = (Point) obj;
    return getX().equals(other.getX()) && getY().equals(other.getY());
  }

  @Override
  public int hashCode() {
    return _x.hashCode() + _y.hashCode()*31;
  }
}
