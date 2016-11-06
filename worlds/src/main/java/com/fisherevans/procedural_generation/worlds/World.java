package com.fisherevans.procedural_generation.worlds;

import com.fisherevans.procedural_generation.worlds.math.Edge;
import com.fisherevans.procedural_generation.worlds.math.Graphs;
import com.fisherevans.procedural_generation.worlds.math.Point;

import java.util.*;

public class World {
  private Integer _seed;
  private Float _width, _height;
  private Integer _basePointCount, _cleavePointCount;

  private Random _random;
  private List<Point> _points, _cleavePoints;
  private Set<Edge> _edges, _cleaveEdges;

  public World(Integer seed, Float width, Float height, Integer basePointCount, Integer cleavePointCount) {
    _seed = seed;
    _width = width;
    _height = height;
    _basePointCount = basePointCount;
    _cleavePointCount = cleavePointCount;
    System.out.println(toString());

    _random = new Random(_seed);

    _points = generatePoints(_basePointCount);
    _edges = Graphs.generateRelativeNeighborGraph(_points);

    _cleavePoints = generatePoints(_cleavePointCount);
    _cleaveEdges = Graphs.generateRelativeNeighborGraph(_cleavePoints);

    Graphs.cleave(_edges, _cleaveEdges);
    Graphs.trimTails(_edges, _points);
  }

  private List<Point> generatePoints(Integer count) {
    System.out.print("Generating points... ");
    List<Point> points = new ArrayList<Point>(count);
    for(Integer pointId = 0;pointId < count;pointId++) {
      points.add(new Point(_random.nextFloat()*_width, _random.nextFloat()*_height));
    }
    System.out.println(points.size() + " points generated.");
    return points;
  }

  public Integer getSeed() {
    return _seed;
  }

  public Float getWidth() {
    return _width;
  }

  public Float getHeight() {
    return _height;
  }

  public Integer getBasePointCount() {
    return _basePointCount;
  }

  public Random getRandom() {
    return _random;
  }

  public List<Point> getPoints() {
    return _points;
  }

  public Set<Edge> getEdges() {
    return _edges;
  }

  public Integer getCleavePointCount() {
    return _cleavePointCount;
  }

  public List<Point> getCleavePoints() {
    return _cleavePoints;
  }

  public Set<Edge> getCleaveEdges() {
    return _cleaveEdges;
  }

  @Override
  public String toString() {
    return "World[" +
        "Seed: " + _seed + ", " +
        "Width: " + _width + ", " +
        "Height: " + _height + ", " +
        "Base Point Count: " + _basePointCount + ", " +
        "Cleave Point Count: " + _cleavePointCount +
        "]";
  }
}
