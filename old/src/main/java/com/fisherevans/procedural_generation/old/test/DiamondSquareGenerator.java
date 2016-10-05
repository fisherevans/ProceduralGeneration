package com.fisherevans.procedural_generation.old.test;

public class DiamondSquareGenerator extends TerrainGenerator {
	private double _roughness;
	private boolean _tile;
	private int _power, _divisions;

	public DiamondSquareGenerator(int seed, int power, double roughness, boolean tile) {
		super(seed);
		_power = power;
		_roughness = roughness;
		_divisions = 1 << power;
		_tile = tile;
	}
	
	public TerrainGenerator generate() {
		data = new double[_divisions][_divisions];
		if(_tile)
			data[0][0] = data[0][_divisions-1]
					= data[_divisions-1][_divisions-1] = data[_divisions-1][0] = getRandomInRange();
		else {
			data[0][0] = getRandomInRange();
			data[0][_divisions] = getRandomInRange();
			data[_divisions][_divisions] = getRandomInRange();
			data[_divisions][0] = getRandomInRange();
		}
		double rough = _roughness;
		for (int step = 0; step < _power;step++) {
			int side = 1 << (_power - step), s = side >> 1;
			for (int x = 0; x < _divisions; x += side)
				for (int y = 0; y < _divisions; y += side)
					if(side > 1)
						diamond(x, y, side, rough);
			if (s > 0)
				for (int x = 0; x <= _divisions; x += s)
					for (int y = (x + s) % side; y <= _divisions; y += side)
						square(x - s, y - s, side, rough);
			rough *= _roughness;
		}
        return this;
	}

	private void diamond(int x, int y, int side, double scale) {
		int half = side/2;
		double total = 0.0;
		total += data[getId(x)][getId(y)];
		total += data[getId(x+side)][getId(y)];
		total += data[getId(x+side)][getId(y+side)];
		total += data[getId(x)][getId(y+side)];
		data[getId(x+half)][getId(y+half)] = total/4.0 + getRandomInRange()*scale;
	}

	private void square(int x, int y, int side, double scale) {
		int half = side / 2;
		double total = 0.0;
		total += data[getId(x)][getId(y+half)];
		total += data[getId(x+half)][getId(y)];
		total += data[getId(x+side)][getId(y+half)];
		total += data[getId(x+half)][getId(y+side)];
		data[getId(x + half)][getId(y + half)] = total/4.0 + getRandomInRange()*scale;
	}

	private double getRandomInRange() {
		return 2.0*getRandom().nextDouble()-1.0;
	}
	
	private int getId(int position) {
		position %= _divisions;
		if(position < 0)
			position += _divisions;
		return position;
	}
}
