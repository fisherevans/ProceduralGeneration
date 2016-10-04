package com.fisherevans.proc_gen.noise.perlin;

public abstract class NoiseFunction {
  public abstract float noise(int n);

  public abstract float noise1D(int x, int seed);
  
  public abstract float noise2D(int x, int y, int seed);

  public abstract float noise3D(int x, int y, int z, int seed);

  public abstract float noise4D(int x, int y, int z, int w, int seed);
}
