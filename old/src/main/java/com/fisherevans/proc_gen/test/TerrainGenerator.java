package com.fisherevans.proc_gen.test;

import java.util.Random;

public abstract class TerrainGenerator {
	public static final int RANDOM_SEED = -1;
	
	public double[][] data;
	public int seed, size;
	
	private Random _random;

    public TerrainGenerator(int seed) {
        this(seed, 0);
    }

    public TerrainGenerator(int seed, int size) {
        this.seed = seed == RANDOM_SEED ? (int)(Math.random()*Integer.MAX_VALUE) : seed;
        _random = new Random(this.seed);
        this.size = size;
    }
	
	public abstract TerrainGenerator generate();
	
	public TerrainGenerator quickGen() {
		generate();
		normalize(1.0);
		return this;
	}
	
	public Random getRandom() {
		return _random;
	}

    public TerrainGenerator normalize(double size) {
        double min = Double.MAX_VALUE, max = -1.0*Double.MAX_VALUE;
        for(int x = 0;x < data.length;x++) {
            for(int y = 0;y < data[x].length;y++) {
                min = Math.min(min, data[x][y]);
                max = Math.max(max, data[x][y]);
            }
        }
        double scale = size/(max-min);
        for(int x = 0;x < data.length;x++)
            for(int y = 0;y < data[x].length;y++)
            	data[x][y] = (data[x][y]-min)*scale;
        return this;
    }

    public TerrainGenerator powerAll(double power) {
        for(int x = 0;x < data.length;x++)
            for(int y = 0;y < data[x].length;y++)
            	data[x][y] = Math.pow(data[x][y], power);
        return this;
    }

    public void print() {
        for(int x = 0;x < data.length;x++) {
            for(int y = 0;y < data[x].length;y++) {
                System.out.printf(" %4.2f ", data[x][y]);
            }
            System.out.println();
        }
    }
}
