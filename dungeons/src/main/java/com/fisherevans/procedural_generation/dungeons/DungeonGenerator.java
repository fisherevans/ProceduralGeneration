package com.fisherevans.procedural_generation.dungeons;

import java.util.*;

public class DungeonGenerator {
	public static long staticSeed = 123321;
	public static boolean useStaticSeed = false;
	public static long lastSeed = -1;

	public static int padding = -1;

    public static int mapSize = 35;
	public static int minSideLength = 5;
	public static int maxSideLength = 15;
    public static int corridorCount = 10;
	public static int roomCount = 80;
	public static double minRatio = 1;
	public static double maxRatio = 1.5;
	public static double touchedRoomChance = 1;

    public static Random random;
    public static List<Room> rooms, corridors, halls, untouched;
    public static Map<Room, List<Room>> graph;

    public static Room start, end;
	
	public static void generate() {
		lastSeed = useStaticSeed ? staticSeed : (long)(Math.random()*Long.MAX_VALUE);
		random = new Random(lastSeed);
        
        rooms = new ArrayList<Room>();
        corridors = new ArrayList<Room>();
        halls = new ArrayList<Room>();
        untouched = new ArrayList<Room>();
        
        graph = new HashMap<Room, List<Room>>();

		createRooms();
        separateRooms();
        findCorridors();
        centerCorridors();
        connectRooms();
        createHalls();
        Room.expandAll(halls, 1);
        removeUntouched();
        findStartAndEnd();
	}

    private static void createRooms() {
		int width, height, x, y;
		double ratio;
		Room room;
		for(int roomId = 0;roomId < roomCount;) {
			do {
				width = getRandomSide();
				height = getRandomSide();
				ratio = Room.getRatio(width, height);
			} while(ratio < minRatio || ratio > maxRatio);
			x = getRandomGausInt(mapSize * 2) - mapSize - width/2;
			y = getRandomGausInt(mapSize * 2) - mapSize - height/2;
			room = new Room(x, y, width, height);
			System.out.println((roomId+1) + "/" + roomCount + " - Adding room " + room.toString());
			rooms.add(room);
			roomId++;
		}
	}
	
	private static void separateRooms() {
		Room a, b;
		int dx, dxa, dxb, dy, dya, dyb;
		boolean touching;
		int step = 1;
		do {
			System.out.println("Seperating Rooms. Itteration: " + (step++));
			touching = false;
			for(int i = 0;i < rooms.size();i++) {
				a = rooms.get(i);
				for(int j = i+1;j < rooms.size();j++) {
					b = rooms.get(j);
					if(a.touches(b, padding)) {
						touching = true;
						dx = Math.min(a.getRight()-b.getLeft()+padding, a.getLeft()-b.getRight()-padding);
						dy = Math.min(a.getBottom()-b.getTop()+padding, a.getTop()-b.getBottom()-padding);
						if(Math.abs(dx) < Math.abs(dy)) dy = 0;
						else dx = 0;
						
						dxa = -dx/2;
						dxb = dx+dxa;
						
						dya = -dy/2;
						dyb = dy+dya;

						a.shift(dxa,  dya);
						b.shift(dxb,  dyb);
					}
				}
			}
		} while(touching);
	}

    private static void centerCorridors() {
        int left = Integer.MAX_VALUE, right = Integer.MIN_VALUE;
        int top = Integer.MIN_VALUE, bottom = Integer.MAX_VALUE;
        for(Room corridor:corridors) {
            left = Math.min(left, corridor.getLeft());
            right = Math.max(right, corridor.getRight());
            top = Math.max(top, corridor.getTop());
            bottom = Math.min(bottom, corridor.getBottom());
        }
        int shiftX = (right+left)/2;
        int shiftY = (top+bottom)/2;
        for(Room corridor:corridors)
            corridor.shift(-shiftX, -shiftY);
        for(Room room:rooms)
            room.shift(-shiftX, -shiftY);
    }

    private static void findCorridors() {
    	Collections.sort(rooms);
        for(int count = 0;count < corridorCount;count++)
            corridors.add(rooms.remove(0));
    }

    private static void connectRooms() {
        Room a, b, c;
        double abDist, acDist, bcDist;
        boolean skip;
        for(int i = 0;i < corridors.size();i++) {
            a = corridors.get(i);
            for(int j = i+1;j < corridors.size();j++) {
                skip = false;
                b = corridors.get(j);
                abDist = Math.pow(a.getCenterX()-b.getCenterX(), 2) + Math.pow(a.getCenterY()-b.getCenterY(), 2);
                for(int k = 0;k < corridors.size();k++) {
                    if(k == i || k == j)
                        continue;
                    c = corridors.get(k);
                    acDist = Math.pow(a.getCenterX()-c.getCenterX(), 2) + Math.pow(a.getCenterY()-c.getCenterY(), 2);
                    bcDist = Math.pow(b.getCenterX()-c.getCenterX(), 2) + Math.pow(b.getCenterY()-c.getCenterY(), 2);
                    if(acDist < abDist && bcDist < abDist)
                        skip = true;
                    if(skip)
                        break;
                }
                if(!skip) {
                    if(graph.get(a) == null)
                        graph.put(a, new LinkedList<Room>());
                    graph.get(a).add(b);
                }
            }
        }
    }

    private static void createHalls() {
        Random random = new Random(lastSeed);
        int dx, dy, x, y;
        Room a, b;
        List<Room> keys = new ArrayList<Room>();
        keys.addAll(graph.keySet());
        Collections.sort(keys);
        for(Room outer:keys) {
            for(Room inner:graph.get(outer)) {
            	// make sure starting point is to the left
            	if(outer.getCenterX() < inner.getCenterX()) {
                    a = outer;
                    b = inner;
                } else {
                    a = inner;
                    b = outer;
                }
                x = (int) a.getCenterX();
                y = (int) a.getCenterY();
                dx = (int) b.getCenterX()-x;
                dy = (int) b.getCenterY()-y;
                
                if(random.nextInt(1) == 1) {
                    halls.add(new Room(x, y, dx+1, 1));
                    halls.add(new Room(x+dx, y, 1, dy));
                } else {
                    halls.add(new Room(x, y+dy, dx+1, 1));
                    halls.add(new Room(x, y, 1, dy));
                }
            }
        }
    }
    
    private static void removeUntouched() {
    	Room room;
    	boolean touched;
    	for(int roomId = 0;roomId < rooms.size();) {
    		room = rooms.get(roomId);
    		touched = false;
    		for(Room hall:halls) {
    			if(room.touches(hall) && random.nextDouble() <= touchedRoomChance) {
    				touched = true;
    				break;
    			}
    		}
    		if(!touched)
    			untouched.add(rooms.remove(roomId));
    		else
    			roomId++;
    	}
    }

    private static void findStartAndEnd() {
        Room a, b;
        double maxDist = Double.MIN_VALUE, dist;
        for(int i = 0;i < corridors.size();i++) {
            a = corridors.get(i);
            for(int j = i+1;j < corridors.size();j++) {
                b = corridors.get(j);
                dist = Math.pow(a.getCenterX()-b.getCenterX(), 2) + Math.pow(a.getCenterY()-b.getCenterY(), 2);
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

    private static int getRandomGausInt(int size) {
        double r = random.nextGaussian();
        r *= size/5;
        r += size/2;
        if(r < 0 || r > size)
            return getRandomGausInt(size);
        else
            return (int)r;
    }

    private static int getRandomGausSmallInt(int size) {
        double r = random.nextGaussian();
        r *= size/1.5;
        if(r < 0)
            r *= -1;
        if(r > size)
            return getRandomGausSmallInt(size);
        else
            return (int)r;
    }
	
	private static int getRandomSide() {
		return getRandomGausSmallInt(maxSideLength - minSideLength) + minSideLength;
	}
}
