package com.fisherevans.proc_gen.caves;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Author: Fisher Evans Date: 6/4/14
 */
public class OrganicCaves {
  private int _width, _height, _padding, _pointCount, _seed;

  private Random _random;
  private float[][] _map;
  private Point[] _points;

  public static void main(String[] args) throws IOException {
    OrganicCaves oc = new OrganicCaves(400, 400, 15, 50, 6393);
    oc.generateSkeleton();
    for(int i = 0;i < 100;i++)
      oc.grow(0.005f, 0.1f);
    oc.renderRawMap("C:/caves/cave.png");
  }

  public OrganicCaves(int width, int height, int padding, int pointCount, int seed) {
    _width = width;
    _height = height;
    _padding = padding;
    _pointCount = pointCount;
    _seed = seed;
  }

  public void generateSkeleton() {
    _map = new float[_height][_width];
    _points = new Point[_pointCount];
    _random = new Random(_seed);
    generatePoints();
    createRNG();
    drawEdges();
  }

  private void generatePoints() {
    int x, y;
    for (int pointId = 0; pointId<_pointCount; pointId++) {
      x = _random.nextInt(_width-_padding*2)+_padding;
      y = _random.nextInt(_height-_padding*2)+_padding;
      _points[pointId] = new Point(x, y);
    }
  }

  private void createRNG() {
    Point a, b, c;
    float abDist, acDist, bcDist;
    boolean isEdge;
    for (int aid = 0; aid<_pointCount; aid++) {
      for (int bid = aid+1; bid<_pointCount; bid++) {
        isEdge = true;
        a = _points[aid];
        b = _points[bid];
        abDist = a.squaredDistance(b);
        for (int cid = 0; cid<_pointCount; cid++) {
          if (cid==aid||cid==bid) continue;
          c = _points[cid];
          acDist = a.squaredDistance(c);
          bcDist = b.squaredDistance(c);
          if (acDist<abDist&&bcDist<abDist) {
            isEdge = false;
            break;
          }
        }
        if (isEdge) a.edges.add(b);
      }
    }
  }

  private void drawEdges() {
    for (Point a : _points)
      for (Point b : a.edges)
        drawEdge(a, b);
  }

  public void grow(float amount, float chance) {
    float[][] growth = new float[_height][_width];
    int tx, ty;
    for (int y = 0; y<_height; y++) {
      for (int x = 0; x<_width; x++) {
        if (_map[y][x] > 0) {
          for (int dy = -1; dy<=1; dy++) {
            for (int dx = -1; dx<=1; dx++) {
              tx = x+dx;
              ty = y+dy;
              if (tx<0||ty<0||tx>=_width||ty>=_height) continue;
              else if (tx==x&&ty==y) growth[y][x] = _map[y][x];
              else if (_random.nextFloat()<=chance) growth[ty][tx] += amount*_map[y][x];
            }
          }
        }
      }
    }
    for (int y = 0; y<_height; y++)
      for (int x = 0; x<_width; x++)
        _map[y][x] = clamp(_map[y][x]+growth[y][x]);
  }

  private void drawEdge(Point a, Point b) {
    int dx = b.x-a.x;
    int dy = b.y-a.y;
    float h = 1f/(float) Math.sqrt(a.squaredDistance(b));
    int x, y;
    for (float t = 0; t<=1; t += h) {
      x = (int) (a.x+(dx*t));
      y = (int) (a.y+(dy*t));
      _map[y][x] = 1f;
    }
  }

  public void renderMap(float threshold, String location) throws IOException {
    BufferedImage img = new BufferedImage(_width, _height, BufferedImage.TYPE_INT_RGB);
    int white = Color.WHITE.getRGB();
    for (int y = 0; y<_height; y++)
      for (int x = 0; x<_width; x++)
        if (_map[y][x]>=threshold) img.setRGB(x, y, white);
    ImageIO.write(img, "png", new File(location));
  }

  public void renderRawMap(String location) throws IOException {
    BufferedImage img = new BufferedImage(_width, _height, BufferedImage.TYPE_INT_RGB);
    for (int y = 0; y<_height; y++)
      for (int x = 0; x<_width; x++)
        img.setRGB(x, y, new Color(_map[y][x], _map[y][x], _map[y][x]).getRGB());
    ImageIO.write(img, "png", new File(location));
  }

  private static final float clamp(float x) {
    if (x>1) return 1f;
    if (x<0) return 0f;
    return x;
  }

  private class Point {
    public int x, y;
    public List<Point> edges = new ArrayList<Point>();

    private Point(int x, int y) {
      this.x = x;
      this.y = y;
    }

    private float squaredDistance(Point other) {
      float dx = x-other.x, dy = y-other.y;
      return dx*dx+dy*dy;
    }
  }
}
