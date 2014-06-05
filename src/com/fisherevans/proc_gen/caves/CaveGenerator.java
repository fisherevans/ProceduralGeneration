package com.fisherevans.proc_gen.caves;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.fisherevans.proc_gen.dungeons.Room;

public class CaveGenerator {
	public static int mapSize = 50;
	public static int pointCount = 50;
	
	public static Random random;
	public static Point start, end;
	public static List<Point> points;
	public static boolean[][] connections;
	
	public static void generate() {
		random = new Random((long) (Math.random()*Long.MAX_VALUE));
		//random = new Random(123);
		connections = new boolean[pointCount][pointCount];
		createPoints();
		connectPoints();
		findStartAndEnd();
	}
	
	private static void createPoints() {
		points = new ArrayList<Point>();
		for(int pointId = 0;pointId < pointCount;pointId++)
			points.add(new Point(getRandomPosition(), getRandomPosition(), pointId));
	}

    private static void connectPoints() {
        Point a, b, c;
        double abDist, acDist, bcDist;
        boolean skip;
        for(int i = 0;i < points.size();i++) {
            a = points.get(i);
            for(int j = i+1;j < points.size();j++) {
                skip = false;
                b = points.get(j);
                abDist = a.distanceTo(b);
                for(int k = 0;k < points.size();k++) {
                    if(k == i || k == j)
                        continue;
                    c = points.get(k);
                    acDist = a.distanceTo(c);;
                    bcDist = b.distanceTo(c);
                    if(acDist < abDist && bcDist < abDist)
                        skip = true;
                    if(skip)
                        break;
                }
                if(!skip) {
                	connections[a.id][b.id] = true;
                	connections[b.id][a.id] = true;
                }
            }
        }
    }

    private static void findStartAndEnd() {
        Point a, b;
        double maxDist = Double.MIN_VALUE, dist;
        for(int i = 0;i < points.size();i++) {
            a = points.get(i);
            for(int j = i+1;j < points.size();j++) {
                b = points.get(j);
                dist = a.distanceTo(b);
                if(dist > maxDist) {
                    maxDist = dist;
                    if(random.nextBoolean()) {
                        start = a;
                        end = b;
                    } else {
                        start = b;
                        end = a;
                    }
                }
            }
        }
    }
	
	private static int getRandomPosition() {
		return random.nextInt(mapSize*2)-mapSize;
	}
}
