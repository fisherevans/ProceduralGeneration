package com.fisherevans.procedural_generation.old.noise.ds;

import java.util.Random;

public class DiamondSquareNoise {
  private float[][] _data;
  private Random _random;
  private float _roughness;
  private boolean _tile;
  private int _power, _divisions;

  public DiamondSquareNoise(int seed, int power, float roughness, boolean tile) {
    _random = new Random(seed);
    _power = power;
    _roughness = roughness;
    _divisions = 1 << power;
    _tile = tile;
  }
  
  public void generate() {
    _data = new float[_divisions][_divisions];
    if(_tile)
      _data[0][0] = _data[0][_divisions-1]
          = _data[_divisions-1][_divisions-1] = _data[_divisions-1][0] = getRandomInRange();
    else {
      _data[0][0] = getRandomInRange();
      _data[0][_divisions] = getRandomInRange();
      _data[_divisions][_divisions] = getRandomInRange();
      _data[_divisions][0] = getRandomInRange();
    }
    float rough = _roughness;
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
  }

  private void diamond(int x, int y, int side, float scale) {
    int half = side/2;
    float total = 0f;
    total += _data[getId(x)][getId(y)];
    total += _data[getId(x+side)][getId(y)];
    total += _data[getId(x+side)][getId(y+side)];
    total += _data[getId(x)][getId(y+side)];
    _data[getId(x+half)][getId(y+half)] = total/4f + getRandomInRange()*scale;
  }

  private void square(int x, int y, int side, float scale) {
    int half = side / 2;
    float total = 0f;
    total += _data[getId(x)][getId(y+half)];
    total += _data[getId(x+half)][getId(y)];
    total += _data[getId(x+side)][getId(y+half)];
    total += _data[getId(x+half)][getId(y+side)];
    _data[getId(x + half)][getId(y + half)] = total/4f + getRandomInRange()*scale;
  }

  private float getRandomInRange() {
    return 2f*_random.nextFloat()-1f;
  }
  
  private int getId(int position) {
    position %= _divisions;
    if(position < 0)
      position += _divisions;
    return position;
  }
  
  public float[][] getData() {
    return _data;
  }
}
