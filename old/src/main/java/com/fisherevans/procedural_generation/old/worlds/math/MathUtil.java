package com.fisherevans.procedural_generation.old.worlds.math;

/**
 * http://www.geeksforgeeks.org/check-if-two-given-line-segments-intersect/
 */
public class MathUtil {

  public static boolean doIntersect(Edge a, Edge b) {
    return doIntersect(a.getA(), a.getB(), b.getA(), b.getB());
  }

  public static boolean doIntersect(Point p1, Point q1, Point p2, Point q2) {
    Orientation o1 = getOrientation(p1, q1, p2);
    Orientation o2 = getOrientation(p1, q1, q2);
    Orientation o3 = getOrientation(p2, q2, p1);
    Orientation o4 = getOrientation(p2, q2, q1);
    return (o1 != o2 && o3 != o4)
        || (o1 == Orientation.Colinear && onEdge(p1, p2, q1))
        || (o2 == Orientation.Colinear && onEdge(p1, q2, q1))
        || (o3 == Orientation.Colinear && onEdge(p2, p1, q2))
        || (o4 == Orientation.Colinear && onEdge(p2, q1, q2));
  }

  public static boolean onEdge(Edge edge, Point q) {
    return onEdge(edge.getA(), q, edge.getB());
  }
  public static boolean onEdge(Point p, Point q, Point r) {
    return q.getX() <= Math.max(p.getX(), r.getX())
        && q.getX() >= Math.min(p.getX(), r.getX())
        && q.getY() <= Math.max(p.getY(), r.getY())
        && q.getY() >= Math.min(p.getY(), r.getY());
  }

  public static Orientation getOrientation(Edge edge, Point q) {
    return getOrientation(edge.getA(), q, edge.getB());
  }

  public static Orientation getOrientation(Point p, Point q, Point r) {
    Float val = ((q.getY() - p.getY()) * (r.getX() - q.getX())) - ((q.getX() - p.getX()) * (r.getY() - q.getY()));
    if (val == 0)return Orientation.Colinear;
    return (val > 0) ? Orientation.Clockwise: Orientation.CounterClockwise;
  }

  public static enum Orientation {
    Colinear, Clockwise, CounterClockwise;
  }
}
