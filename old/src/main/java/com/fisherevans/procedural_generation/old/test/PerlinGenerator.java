package com.fisherevans.procedural_generation.old.test;

public class PerlinGenerator extends TerrainGenerator {
	private double _persistance;
	private int _size, _octaveCount;
	private InterpType _octaveType;

	public PerlinGenerator(int seed, int size, int octaveCount, double persistance, InterpType octaveType) {
		super(seed);
		_size = size;
		_octaveCount = octaveCount;
		_persistance = persistance;
		_octaveType = octaveType;
	}

	@Override
	public TerrainGenerator generate() {
		double[][] noise = new double[_size][_size];
		for(int x = 0;x < _size;x++)
			for(int y = 0;y < _size;y++)
				noise[x][y] = getRandom().nextDouble();
		data = generatePerlinNoise(noise, _octaveCount, _persistance);
        return this;
	}
	
	private double[][] generatePerlinNoise(double[][] baseNoise, int octaveCount, double persistance) {
		double[][] smoothNoise, perlinNoise = new double[_size][_size];
		double amp = 1.0f;
		for (int octave = octaveCount - 1;octave >= 0;octave--) {
			amp *= persistance;
			smoothNoise = createNoise(baseNoise, octave);
			for(int x = 0;x < _size;x++)
				for(int y = 0;y < _size;y++)
					perlinNoise[x][y] += smoothNoise[x][y]*amp;
		}
		return perlinNoise;
	}
	
	private double[][] createNoise(double[][] baseNoise, int octave) {
		double[][] smoothNoise = new double[_size][_size];
		int period = 1 << octave;
		double dPeriod = period;
		double tlValue, trValue, blValue, brValue;
		double xPos, yPos;
		for(int x = 0;x < _size;x++) {
			int xLeft = (x/period)*period;
			int xRight = xLeft+period;
			for(int y = 0;y < _size;y++) {
		         int yTop = (y/period)*period;
		         int yBottom = yTop+period;
		         tlValue = baseNoise[getWrappedId(xLeft)][getWrappedId(yTop)];
		         trValue = baseNoise[getWrappedId(xRight)][getWrappedId(yTop)];
		         blValue = baseNoise[getWrappedId(xLeft)][getWrappedId(yBottom)];
		         brValue = baseNoise[getWrappedId(xRight)][getWrappedId(yBottom)];
		         xPos = (x-xLeft)/dPeriod;
		         yPos = (y-yTop)/dPeriod;
		         double i1 = interpolate(tlValue, trValue, xPos);
		         double i2 = interpolate(blValue, brValue, xPos);
		         smoothNoise[x][y] = interpolate(i1, i2, yPos);
			}
		}
		return smoothNoise;
	}

	private double interpolate(double a, double b, double x) {
		switch(_octaveType) {
		case Linear: return linearInterp(a, b, x);
		case Closest: return closestInterp(a, b, x);
		case Cosine: return cosineInterp(a, b, x);
		default: return closestInterp(a, b, x);
		}
	}
	
	private static final double cosineInterp(double a, double b, double x) {
		double result = (1.0 - Math.cos(x*Math.PI))*0.5;
		return linearInterp(a, b, result);
	}
	
	private static final double linearInterp(double a, double b, double x) {
		return a*(1.0-x) + b*x;
	}
	
	private static final double closestInterp(double a, double b, double x) {
		return x < 0.5 ? a : b;
	}
	
	private int getWrappedId(int position) {
		position %= _size;
		if(position < 0)
			position += _size;
		return position;
	}
	
	private double distance(double x1, double x2, double y1, double y2) {
		return Math.sqrt(Math.pow(x1-x2, 2) + Math.pow(y1-y2, 2));
	}
	
	public enum InterpType { Linear, Closest, Cosine }
}
