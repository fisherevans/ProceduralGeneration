package com.fisherevans.procedural_generation.old.noise.perlin;

public class BasicNoiseFunction extends NoiseFunction {
  private static final int MAGIC_SEED = 2017;
  
  private static final int MAGIC_X = 5051;
  private static final int MAGIC_Y = 6653;
  private static final int MAGIC_Z = 11299;
  private static final int MAGIC_W = 19661;

  public float noise(int x) {
    x = (x<<13)^x;
    x = (x*(x*x*15731+789221)+1376312589);
    x &= 0x7fffffff;
    return (((float) x)/1073741824.0f)/2f;
  }

  public float noise1D(int x, int seed) {
    return noise(MAGIC_X*x+MAGIC_SEED*seed);
  }

  public float noise2D(int x, int y, int seed) {
    return noise(MAGIC_X*x+MAGIC_Y*y+MAGIC_SEED*seed);
  }

  public float noise3D(int x, int y, int z, int seed) {
    return noise(MAGIC_X*x+MAGIC_Y*y+MAGIC_Z*z+MAGIC_SEED*seed);
  }

  public float noise4D(int x, int y, int z, int w, int seed) {
    return noise(MAGIC_X*x+MAGIC_Y*y+MAGIC_Z*z+MAGIC_W*w+MAGIC_SEED*seed);
  }
  
  public static void main(String[] args) {
    int size = 10000000;
    BasicNoiseFunction nf = new BasicNoiseFunction();
    float[] noise = new float[size];
    float value, min = Float.MAX_VALUE, max = -1f*Float.MAX_VALUE, total = 0;
    for(int i = 0;i < size;i++) {
      value = noise[i] = nf.noise(i);
      if(value < min)
        min = value;
      if(value > max)
        max = value;
      total += value;
    }
    System.out.println("Min: " + min);
    System.out.println("Max: " + max);
    System.out.println("Avg: " + (total/((float)size)));
  }
}
