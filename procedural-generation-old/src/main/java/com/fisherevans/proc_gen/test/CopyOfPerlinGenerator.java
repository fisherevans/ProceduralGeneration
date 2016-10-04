package com.fisherevans.proc_gen.test;

public class CopyOfPerlinGenerator extends TerrainGenerator {
	private double _persistance;
	private int _size, _octaveCount;
	private OctaveType _octaveType;

	public CopyOfPerlinGenerator(int seed, int size, int octaveCount, double persistance, OctaveType octaveType) {
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
			smoothNoise = createSmoothNoise(baseNoise, octave);
			for(int x = 0;x < _size;x++)
				for(int y = 0;y < _size;y++)
					perlinNoise[x][y] += smoothNoise[x][y]*amp;
		}
		return perlinNoise;
	}

	private double[][] createSmoothNoise(double[][] baseNoise, int octave) {
		switch(_octaveType) {
			case Radial: return createRadialNoise(baseNoise, octave);
			case Linear: return createLinearNoise(baseNoise, octave);
			case Closest: return createClosestNoise(baseNoise, octave);
			case Bicubic: return createBicubicNoise(baseNoise, octave);
			default: return new double[][] {{}};
		}
	}
	
	private double[][] createRadialNoise(double[][] baseNoise, int octave) {
		double[][] smoothNoise = new double[_size][_size];
		int period = 1 << octave;
		double dPeriod = period, dPeriodSq = dPeriod*dPeriod;
		double d1, d2, d3, d4, vf;
		int dx1sq, dx2sq, dy1sq, dy2sq;
		for (int x = 0; x < _size; x++) {
			int x1 = (x / period) * period;
			int x2 = x1+period;
			for (int y = 0; y < _size; y++) {
				int y1 = (y / period) * period;
				int y2 = y1+period;
				if(x1 == x2 && y1 == y2)
					smoothNoise[x][y] = baseNoise[x1][y1];
				else {
					dx1sq = x - x1; dx1sq *= dx1sq;
					dx2sq = x - x2; dx2sq *= dx2sq;
					dy1sq = y - y1; dy1sq *= dy1sq;
					dy2sq = y - y2; dy2sq *= dy2sq;
					vf = 0;
					d1 = dx1sq + dy1sq;
					if(d1 < dPeriodSq) 
						vf += (1.0-(Math.sqrt(d1)/dPeriod))*baseNoise[x1][y1];
					d2 = dx1sq + dy2sq;
					if(d2 < dPeriodSq) 
						vf += (1.0-(Math.sqrt(d2)/dPeriod))*baseNoise[x1][getWrappedId(y2)];
					d3 = dx2sq + dy1sq;
					if(d3 < dPeriodSq) 
						vf += (1.0-(Math.sqrt(d3)/dPeriod))*baseNoise[getWrappedId(x2)][y1];
					d4 = dx2sq + dy2sq;
					if(d4 < dPeriodSq) 
						vf += (1.0-(Math.sqrt(d4)/dPeriod))*baseNoise[getWrappedId(x2)][getWrappedId(y2)];
					smoothNoise[x][y] = vf;
				}
			}
		}
		return smoothNoise;
	}
	
	private double[][] createClosestNoise(double[][] baseNoise, int octave) {
		double[][] smoothNoise = new double[_size][_size];
		int period = 1 << octave;
		for(int x = 0;x < _size;x++) {
			int sampleX = (x/period)*period;
			for(int y = 0;y < _size;y++) {
		         int sampleY = (y/period)*period;
		         smoothNoise[x][y] = baseNoise[sampleX][sampleY];
			}
		}
		return smoothNoise;
	}
	
	private double[][] createLinearNoise(double[][] baseNoise, int octave) {
		double[][] smoothNoise = new double[_size][_size];
		int period = 1 << octave;
		double freq = 1.0/period;
		for(int x = 0;x < _size;x++) {
			int x1 = (x/period)*period;
			int x2 = (x1+period)%_size;
			double horzBlend = (x-x1)*freq;
			for(int y = 0;y < _size;y++) {
		         int y1 = (y/period)*period;
		         int y2 = (y1+period)%_size;
		         double vertBlend = (y-y1)*freq;
		         double top = interpolate(baseNoise[x1][y1], baseNoise[x2][y1], horzBlend);
		         double bottom = interpolate(baseNoise[x1][y2], baseNoise[x2][y2], horzBlend);
		         smoothNoise[x][y] = interpolate(top, bottom, vertBlend);
			}
		}
		return smoothNoise;
	}
	
	private double[][] createBicubicNoise(double[][] baseNoise, int octave) {
		double[][] smoothNoise = new double[_size][_size];
		int period = 1 << octave;
		double value, dPeriod = period;
		for(int x = 0;x < _size;x++) {
			int x1 = (x/period)*period;
			for(int y = 0;y < _size;y++) {
		         int y1 = (y/period)*period;
		         value = 0;
		         for(int dx = -1;dx < 3;dx++)
			         for(int dy = -1;dy < 3;dy++)
				         value += cosineInterp(distance(x, x1+dx*dPeriod, y, y1+dy*dPeriod)/dPeriod)*baseNoise[getWrappedId(x1+dx*period)][getWrappedId(y1+dy*period)];
		         smoothNoise[x][y] = value;
			}
		}
		return smoothNoise;
	}
	
	public static final double A = 0.25;
	private static final double bicubic(double x) {
		double ax = Math.abs(x);
		double ax2 = ax*ax;
		double ax3 = ax2*ax;
		if(ax <= 1)
			return (A+2)*ax3 - (A+3)*ax2 + 1;
		else if(x > 1 && x < 2)
			return A*ax3 + 5*A*ax2 + 8*A*ax - 4*A;
		else
			return 0;
	}
	private static final double cosineInterp(double x) {
		double result = (1.0 - Math.cos(x*Math.PI))*0.5;
		//System.out.println(x + " - " + result);
		return result;
	}
	
	private double interpolate(double x, double y, double pos) {
		return x*(1.0-pos) + pos*y;
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
	
	public enum OctaveType { Linear, Radial, Closest, Bicubic }
}
