package com.fisherevans.proc_gen.test;
/*
 * From: http://webstaff.itn.liu.se/~stegu/simplexnoise/SimplexNoise.java
 * Trimmed for my uses
 */
public class SimplexGenerator extends TerrainGenerator {
	private static Grad GRADS[] = { new Grad(1, 1, 0), new Grad(-1, 1, 0),
			new Grad(1, -1, 0), new Grad(-1, -1, 0), new Grad(1, 0, 1),
			new Grad(-1, 0, 1), new Grad(1, 0, -1), new Grad(-1, 0, -1),
			new Grad(0, 1, 1), new Grad(0, -1, 1), new Grad(0, 1, -1),
			new Grad(0, -1, -1) };
	private static final double F2 = 0.5 * (Math.sqrt(3.0) - 1.0);
	private static final double G2 = (3.0 - Math.sqrt(3.0)) / 6.0;

	private static short[] _p , _perm, _permMod;
	private int _permSize, _permModValue ,_size;
	private double _scale;

	public SimplexGenerator(int seed, int size, int permSize, int permModValue, double scale) {
		super(seed);
		_permSize = permSize; // 256
		_permModValue = permModValue; // 12
		_size = size;
		_scale = scale;
	}

	@Override
	public TerrainGenerator generate() {
		generatPerms();
		data = new double[_size][_size];
		for(int x = 0;x < _size;x++)
			for(int y = 0;y < _size;y++)
				data[x][y] = noise(x/(double)_size*_scale, y/(double)_size*_scale);
		return this;
	}
	
	private void generatPerms() {
		_p  = new short[_permSize];
		_perm  = new short[_permSize*2];
		_permMod  = new short[_permSize*2];
		
		for(short i = 0;i < _permSize;i++)
			_p[i] = i;
		shuffleArray(_p);
		
		for (int i = 0; i < _permSize*2; i++) {
			_perm[i] = _p[i & (_permSize-1)];
			_permMod[i] = (short) (_perm[i] % _permModValue);
		}
	}
	
	private void shuffleArray(short[] arr) {
	    for (int i = arr.length - 1; i > 0; i--) {
	      int index = getRandom().nextInt(i + 1);
	      short a = arr[index];
	      arr[index] = arr[i];
	      arr[i] = a;
	    }
	}

	private static int fastFloor(double x) {
		int xi = (int) x;
		return x < xi ? xi - 1 : xi;
	}

	private static double dot(Grad g, double x, double y) {
		return g.x * x + g.y * y;
	}

	// 2D simplex noise
	public static double noise(double xin, double yin) {
		double n0, n1, n2;
		double s = (xin + yin) * F2;
		int i = fastFloor(xin + s);
		int j = fastFloor(yin + s);
		double t = (i + j) * G2;
		double X0 = i - t;
		double Y0 = j - t;
		double x0 = xin - X0;
		double y0 = yin - Y0;
		int i1, j1;
		if (x0 > y0) {
			i1 = 1;
			j1 = 0;
		} else {
			i1 = 0;
			j1 = 1;
		}
		double x1 = x0 - i1 + G2;
		double y1 = y0 - j1 + G2;
		double x2 = x0 - 1.0 + 2.0 * G2;
		double y2 = y0 - 1.0 + 2.0 * G2;

		int ii = i & 255;
		int jj = j & 255;
		int gi0 = _permMod[ii + _perm[jj]];
		int gi1 = _permMod[ii + i1 + _perm[jj + j1]];
		int gi2 = _permMod[ii + 1 + _perm[jj + 1]];

		double t0 = 0.5 - x0 * x0 - y0 * y0;
		if (t0 < 0)
			n0 = 0.0;
		else {
			t0 *= t0;
			n0 = t0 * t0 * dot(GRADS[gi0], x0, y0);
		}
		double t1 = 0.5 - x1 * x1 - y1 * y1;
		if (t1 < 0)
			n1 = 0.0;
		else {
			t1 *= t1;
			n1 = t1 * t1 * dot(GRADS[gi1], x1, y1);
		}
		double t2 = 0.5 - x2 * x2 - y2 * y2;
		if (t2 < 0)
			n2 = 0.0;
		else {
			t2 *= t2;
			n2 = t2 * t2 * dot(GRADS[gi2], x2, y2);
		}
		return 70.0 * (n0 + n1 + n2);
	}

	private static class Grad {
		double x, y, z;
		Grad(double x, double y, double z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
	}

}
