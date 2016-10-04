package com.fisherevans.proc_gen.noise.perlin;

public class PerlinNoise {
  public static final int INTERP_TYPE_CLOSEST = 1;
  public static final int INTERP_TYPE_LINEAR = 2;
  public static final int INTERP_TYPE_COSINE = 3;

  public static final int DEFAULT_OCTAVES = 4;
  public static final float DEFAULT_PERSISTANCE = 0.5f;

  public static final int DEFAULT_SIZE = 16;
  public static final float DEFAULT_OFFSET = 0f;
  public static final float DEFAULT_SCALE = 1f;

  private int _interpolationType;
  private int _octaves, _seed;
  private float _persistance;

  private NoiseFunction _noiseFunction;

  private int _xSize, _ySize, _zSize, _wSize;
  private float _xOffset, _yOffset, _zOffset, _wOffset;
  private float _xScale, _yScale, _zScale, _wScale;

  public PerlinNoise(int interpolationType, int seed) {
    this(interpolationType, DEFAULT_OCTAVES, seed, DEFAULT_PERSISTANCE);
  }

  public PerlinNoise(int interpolationType, int octaves, int seed, float persistance) {
    _interpolationType = interpolationType;
    _octaves = octaves;
    _seed = seed;
    _persistance = persistance;

    _noiseFunction = new BasicNoiseFunction();

    _xSize = _ySize = _zSize = _wSize = DEFAULT_SIZE;
    _xOffset = _yOffset = _zOffset = _wOffset = DEFAULT_OFFSET;
    _xScale = _yScale = _zScale = _wScale = DEFAULT_SCALE;
  }

  public void setSizes(int... sizes) {
    if (sizes.length>=1) _xSize = sizes[0];
    if (sizes.length>=2) _ySize = sizes[1];
    if (sizes.length>=3) _zSize = sizes[2];
    if (sizes.length>=4) _wSize = sizes[3];
  }

  public void setOffsets(float... offsets) {
    if (offsets.length>=1) _xOffset = offsets[0];
    if (offsets.length>=2) _yOffset = offsets[1];
    if (offsets.length>=3) _zOffset = offsets[2];
    if (offsets.length>=4) _wOffset = offsets[3];
  }

  public void setScales(float... scales) {
    if (scales.length>=1) _xScale = scales[0];
    if (scales.length>=2) _yScale = scales[1];
    if (scales.length>=3) _zScale = scales[2];
    if (scales.length>=4) _wScale = scales[3];
  }

  private static final float closestInterpolation(float a, float b, float x) {
    if (x<0.5) return a;
    else return b;
  }

  private static final float linearInterpolation(float a, float b, float x) {
    return a+(b-a)*x;
  }

  private static final float cosineInterpolation(float a, float b, float x) {
    float result = (float) ((1.0-Math.cos(x*Math.PI))*0.5);
    return linearInterpolation(a, b, result);
  }

  private final float interpolate(float a, float b, float x) {
    switch (_interpolationType) {
    case INTERP_TYPE_CLOSEST:
      return closestInterpolation(a, b, x);
    case INTERP_TYPE_LINEAR:
      return linearInterpolation(a, b, x);
    case INTERP_TYPE_COSINE:
      return cosineInterpolation(a, b, x);
    default:
      return linearInterpolation(a, b, x);
    }
  }

  private final float biInterpolate(float v00, float v10, float v01, float v11, float x, float y) {
    float u = interpolate(v00, v10, x);
    float v = interpolate(v01, v11, x);
    return interpolate(u, v, y);
  }

  private final float triInterpolate(float v000, float v100, float v010, float v110, float v001, float v101, float v011, float v111, float x, float y, float z) {
    float u = biInterpolate(v000, v100, v010, v110, x, y);
    float v = biInterpolate(v001, v101, v011, v111, x, y);
    return interpolate(u, v, z);
  }

  private final float quadInterpolate(float v0000, float v1000, float v0100, float v1100, float v0010, float v1010, float v0110, float v1110, float v0001, float v1001, float v0101, float v1101, float v0011, float v1011, float v0111, float v1111, float x, float y, float z, float w) {
    float u = triInterpolate(v0000, v1000, v0100, v1100, v0010, v1010, v0110, v1110, x, y, z);
    float v = triInterpolate(v0001, v1001, v0101, v1101, v0011, v1011, v0111, v1111, x, y, z);
    return interpolate(u, v, w);
  }

  private final float smoothNoise1D(float x, int seed) {
    int x0 = (int) x;
    float xl = x-(float) x0;
    float v0 = _noiseFunction.noise1D(x0, seed);
    float v1 = _noiseFunction.noise1D(x0+1, seed);
    return interpolate(v0, v1, xl);
  }

  private final float smoothNoise2D(float x, float y, int seed) {
    int x0 = (int) x;
    int y0 = (int) y;
    float xl = x-(float) x0;
    float yl = y-(float) y0;
    float v00 = _noiseFunction.noise2D(x0, y0, seed);
    float v10 = _noiseFunction.noise2D(x0+1, y0, seed);
    float v01 = _noiseFunction.noise2D(x0, y0+1, seed);
    float v11 = _noiseFunction.noise2D(x0+1, y0+1, seed);
    return biInterpolate(v00, v10, v01, v11, xl, yl);
  }

  private final float smoothNoise3D(float x, float y, float z, int seed) {
    int x0 = (int) x;
    int y0 = (int) y;
    int z0 = (int) z;
    float xl = x-(float) x0;
    float yl = y-(float) y0;
    float zl = z-(float) z0;
    float v000 = _noiseFunction.noise3D(x0, y0, z0, seed);
    float v100 = _noiseFunction.noise3D(x0+1, y0, z0, seed);
    float v010 = _noiseFunction.noise3D(x0, y0+1, z0, seed);
    float v110 = _noiseFunction.noise3D(x0+1, y0+1, z0, seed);
    float v001 = _noiseFunction.noise3D(x0, y0, z0+1, seed);
    float v101 = _noiseFunction.noise3D(x0+1, y0, z0+1, seed);
    float v011 = _noiseFunction.noise3D(x0, y0+1, z0+1, seed);
    float v111 = _noiseFunction.noise3D(x0+1, y0+1, z0+1, seed);
    return triInterpolate(v000, v100, v010, v110, v001, v101, v011, v111, xl, yl, zl);
  }

  private final float smoothNoise4D(float x, float y, float z, float w, int seed) {
    int x0 = (int) x;
    int y0 = (int) y;
    int z0 = (int) z;
    int w0 = (int) w;
    float xl = x-(float) x0;
    float yl = y-(float) y0;
    float zl = z-(float) z0;
    float wl = w-(float) w0;
    float v0000 = _noiseFunction.noise4D(x0, y0, z0, w0, seed);
    float v1000 = _noiseFunction.noise4D(x0+1, y0, z0, w0, seed);
    float v0100 = _noiseFunction.noise4D(x0, y0+1, z0, w0, seed);
    float v1100 = _noiseFunction.noise4D(x0+1, y0+1, z0, w0, seed);
    float v0010 = _noiseFunction.noise4D(x0, y0, z0+1, w0, seed);
    float v1010 = _noiseFunction.noise4D(x0+1, y0, z0+1, w0, seed);
    float v0110 = _noiseFunction.noise4D(x0, y0+1, z0+1, w0, seed);
    float v1110 = _noiseFunction.noise4D(x0+1, y0+1, z0+1, w0, seed);
    float v0001 = _noiseFunction.noise4D(x0, y0, z0, w0+1, seed);
    float v1001 = _noiseFunction.noise4D(x0+1, y0, z0, w0+1, seed);
    float v0101 = _noiseFunction.noise4D(x0, y0+1, z0, w0+1, seed);
    float v1101 = _noiseFunction.noise4D(x0+1, y0+1, z0, w0+1, seed);
    float v0011 = _noiseFunction.noise4D(x0, y0, z0+1, w0+1, seed);
    float v1011 = _noiseFunction.noise4D(x0+1, y0, z0+1, w0+1, seed);
    float v0111 = _noiseFunction.noise4D(x0, y0+1, z0+1, w0+1, seed);
    float v1111 = _noiseFunction.noise4D(x0+1, y0+1, z0+1, w0+1, seed);
    return quadInterpolate(v0000, v1000, v0100, v1100, v0010, v1010, v0110, v1110, v0001, v1001, v0101, v1101, v0011, v1011, v0111, v1111, xl, yl, zl, wl);
  }

  private final float getNoise1D(float x) {
    float a = 0;
    float f = 1.0f;
    float g = 1.0f;
    for (int i = 0; i<_octaves; i++) {
      a += g*smoothNoise1D(x*f, _seed+i);
      f *= 2.0;
      g *= _persistance;
    }
    return a;
  }

  private final float getNoise2D(float x, float y) {
    float a = 0;
    float f = 1.0f;
    float g = 1.0f;
    for (int i = 0; i<_octaves; i++) {
      a += g*smoothNoise2D(x*f, y*f, _seed+i);
      f *= 2.0;
      g *= _persistance;
    }
    return a;
  }

  private final float getNoise3D(float x, float y, float z) {
    float a = 0;
    float f = 1.0f;
    float g = 1.0f;
    for (int i = 0; i<_octaves; i++) {
      a += g*smoothNoise3D(x*f, y*f, z*f, _seed+i);
      f *= 2.0;
      g *= _persistance;
    }
    return a;
  }

  private final float getNoise4D(float x, float y, float z, float w) {
    float a = 0;
    float f = 1.0f;
    float g = 1.0f;
    for (int i = 0; i<_octaves; i++) {
      a += g*smoothNoise4D(x*f, y*f, z*f, w*f, _seed+i);
      f *= 2.0;
      g *= _persistance;
    }
    return a;
  }

  public final float[] get1DNoise() {
    float[] noise = new float[_xSize];
    float value, min = Float.MAX_VALUE, max = -1f*Float.MAX_VALUE;
    for (int x = 0; x<_xSize; x++) {
      value = getNoise1D(x*_xScale+_xOffset);
      if (value<min) min = value;
      if (value>max) max = value;
      noise[x] = value;
    }
    float scale = 1f/(max-min);
    for (int x = 0; x<_xSize; x++)
      noise[x] = (noise[x]-min)*scale;
    return noise;
  }

  public final float[][] get2DNoise() {
    float[][] noise = new float[_xSize][_ySize];
    float value, min = Float.MAX_VALUE, max = -1f*Float.MAX_VALUE;
    for (int x = 0; x<_xSize; x++) {
      for (int y = 0; y<_ySize; y++) {
        value = getNoise2D(x*_xScale+_xOffset, y*_yScale+_yOffset);
        if (value<min) min = value;
        if (value>max) max = value;
        noise[x][y] = value;
      }
    }
    float scale = 1f/(max-min);
    for (int x = 0; x<_xSize; x++)
      for (int y = 0; y<_ySize; y++)
        noise[x][y] = (noise[x][y]-min)*scale;
    return noise;
  }

  public final float[][][] get3DNoise() {
    float[][][] noise = new float[_xSize][_ySize][_zSize];
    float value, min = Float.MAX_VALUE, max = -1f*Float.MAX_VALUE;
    for (int x = 0; x<_xSize; x++) {
      for (int y = 0; y<_ySize; y++) {
        for (int z = 0; z<_zSize; z++) {
          value = getNoise3D(x*_xScale+_xOffset, y*_yScale+_yOffset, z*_zScale+_zOffset);
          if (value<min) min = value;
          if (value>max) max = value;
          noise[x][y][z] = value;
        }
      }
    }
    float scale = 1f/(max-min);
    for (int x = 0; x<_xSize; x++)
      for (int y = 0; y<_ySize; y++)
        for (int z = 0; z<_zSize; z++)
          noise[x][y][z] = (noise[x][y][z]-min)*scale;
    return noise;
  }

  public final float[][][][] get4DNoise() {
    float[][][][] noise = new float[_xSize][_ySize][_zSize][_wSize];
    float value, min = Float.MAX_VALUE, max = -1f*Float.MAX_VALUE;
    for (int x = 0; x<_xSize; x++) {
      for (int y = 0; y<_ySize; y++) {
        for (int z = 0; z<_zSize; z++) {
          for (int w = 0; w<_wSize; w++) {
            value = getNoise4D(x*_xScale+_xOffset, y*_yScale+_yOffset, z*_zScale+_zOffset, w*_wScale+_wOffset);
            if (value<min) min = value;
            if (value>max) max = value;
            noise[x][y][z][w] = value;
          }
        }
      }
    }
    float scale = 1f/(max-min);
    for (int x = 0; x<_xSize; x++)
      for (int y = 0; y<_ySize; y++)
        for (int z = 0; z<_zSize; z++)
          for (int w = 0; w<_wSize; w++)
            noise[x][y][z][w] = (noise[x][y][z][w]-min)*scale;
    return noise;
  }

  public int getInterpolationType() {
    return _interpolationType;
  }

  public void setInterpolationType(int interpolationType) {
    _interpolationType = interpolationType;
  }

  public int getOctaves() {
    return _octaves;
  }

  public void setOctaves(int octaves) {
    _octaves = octaves;
  }

  public int getSeed() {
    return _seed;
  }

  public void setSeed(int seed) {
    _seed = seed;
  }

  public float getPersistance() {
    return _persistance;
  }

  public void setPersistance(float persistance) {
    _persistance = persistance;
  }

  public NoiseFunction getNoiseFunction() {
    return _noiseFunction;
  }

  public void setNoiseFunction(NoiseFunction noiseFunction) {
    _noiseFunction = noiseFunction;
  }

  public int getXSize() {
    return _xSize;
  }

  public void setXSize(int xSize) {
    _xSize = xSize;
  }

  public int getYSize() {
    return _ySize;
  }

  public void setYSize(int ySize) {
    _ySize = ySize;
  }

  public int getZSize() {
    return _zSize;
  }

  public void setZSize(int zSize) {
    _zSize = zSize;
  }

  public int getWSize() {
    return _wSize;
  }

  public void setWSize(int wSize) {
    _wSize = wSize;
  }

  public float getXOffset() {
    return _xOffset;
  }

  public void setXOffset(float xOffset) {
    _xOffset = xOffset;
  }

  public float getYOffset() {
    return _yOffset;
  }

  public void setYOffset(float yOffset) {
    _yOffset = yOffset;
  }

  public float getZOffset() {
    return _zOffset;
  }

  public void setZOffset(float zOffset) {
    _zOffset = zOffset;
  }

  public float getWOffset() {
    return _wOffset;
  }

  public void setWOffset(float wOffset) {
    _wOffset = wOffset;
  }

  public float getXScale() {
    return _xScale;
  }

  public void setXScale(float xScale) {
    _xScale = xScale;
  }

  public float getYScale() {
    return _yScale;
  }

  public void setYScale(float yScale) {
    _yScale = yScale;
  }

  public float getZScale() {
    return _zScale;
  }

  public void setZScale(float zScale) {
    _zScale = zScale;
  }

  public float getWScale() {
    return _wScale;
  }

  public void setWScale(float wScale) {
    _wScale = wScale;
  }
}
