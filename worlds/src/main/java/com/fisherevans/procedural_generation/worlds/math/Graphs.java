package com.fisherevans.procedural_generation.worlds.math;

import java.util.*;

public class Graphs {
  public static Set<Edge> generateRelativeNeighborGraph(List<Point> points) {
    System.out.print("Generating edges... ");
    Set<Edge> edges = new HashSet<Edge>();
    Point a, b, c;
    Float abSqDist;
    Boolean skip;
    for(Integer i = 0;i < points.size();i++) {
      a = points.get(i);
      for(Integer j = i+1;j < points.size();j++) {
        skip = false;
        b = points.get(j);
        abSqDist = a.squaredDistance(b);
        for(Integer k = 0;k < points.size();k++) {
          if(k == i || k == j)
            continue;
          c = points.get(k);
          if(a.squaredDistance(c) < abSqDist && b.squaredDistance(c) < abSqDist) {
            skip = true;
            break;
          }
        }
        if(!skip)
          edges.add(new Edge(a, b));
      }
    }
    System.out.println(edges.size() + " edges created.");
    return edges;
  }

  public static void trimTails(Set<Edge> edges, List<Point> points) {
    System.out.print("Trimming tails... ");
    List<Point> tails = new ArrayList<Point>();
    do {
      tails.clear();
      for(Point point:points) {
        int edgeCount = 0;
        for(Edge edge:edges) {
          if(edge.contains(point))
            edgeCount++;
        }
        if(edgeCount <= 1)
          tails.add(point);
      }
      for(Point tailPoint:tails) {
        points.remove(tailPoint);
        Iterator<Edge> edgeIttr = edges.iterator();
        while(edgeIttr.hasNext()) {
          Edge edge = edgeIttr.next();
          if(edge.contains(tailPoint))
            edgeIttr.remove();
        }
      }
    } while(tails.size() > 0);
    System.out.println(edges.size() + " edges remain.");
  }

  public static void cleave(Set<Edge> base, Set<Edge> knives) {
    for(Edge knife:knives) {
      Iterator<Edge> edgeIttr = base.iterator();
      while(edgeIttr.hasNext()) {
        Edge edge = edgeIttr.next();
        if(MathUtil.doIntersect(knife, edge))
          edgeIttr.remove();
      }
    }
  }
}
