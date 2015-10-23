package com.fisherevans.proc_gen.test;

import com.fisherevans.proc_gen.MathUtil;
import com.fisherevans.proc_gen.noise.perlin.PerlinNoise;
/*
 Elev | Temp | Perc | Climate
------+------+------+------------------
 Low  | Cold | Dry  | Snow Deseet
 Low  | Cold | Damp | Tundra
 Low  | Cold | Wet  | Snowy
 Low  | Warm | Dry  | Plains
 Low  | Warm | Damp | Lightly Wooded
 Low  | Warm | Wet  | Heavely Wooded
 Low  | Hot  | Dry  | Desert
 Low  | Hot  | Damp | Plains
 Low  | Hot  | Wet  | Rain Forest
 Med  | Cold | Dry  | Tundra
 Med  | Cold | Damp | Tundra
 Med  | Cold | Wet  | Snowy
 Med  | Warm | Dry  | Rocky
 Med  | Warm | Damp | Light Foliage
 Med  | Warm | Wet  | Lightly Wooded
 Med  | Hot  | Dry  | Rocky
 Med  | Hot  | Damp | Light Foliage
 Med  | Hot  | Wet  | Rainforest
 High | Cold | Dry  | Rocky Snow
 High | Cold | Damp | Tundra
 High | Cold | Wet  | Tundra
 High | Warm | Dry  | Rocky
 High | Warm | Damp | Light Folage
 High | Warm | Wet  | Lightly Wooded
 High | Hot  | Dry  | Rocky
 High | Hot  | Damp | Lush Brush
 High | Hot  | Wet  | Rain Forest
 */
public class World {
  private static int WIDTH = 10000;
  private static int HEIGHT = WIDTH/16*9;
  private static float NOISE_SCALE = (2048f/WIDTH)*0.0025f;

  private int SEED_ELEVATION = 123123;
  private int SEED_TEMP = 23432;
  private int SEED_PERCIP = 987987;
  
  private static float WATER_LEVEL = 0.5f;
  
  private PerlinNoise _gen;
  private float[][] _elevation, _avgTemp, _avgPercip;
  
  public static void main(String[] args) { new World().generateScales().generate(); }
  
  public World() {
    _gen = new PerlinNoise(PerlinNoise.INTERP_TYPE_COSINE, 0);
    _gen.setSizes(WIDTH, HEIGHT);

    SEED_ELEVATION = (int)(Math.random()*Integer.MAX_VALUE);
    SEED_TEMP = (int)(Math.random()*Integer.MAX_VALUE);
    SEED_PERCIP = (int)(Math.random()*Integer.MAX_VALUE);
  }

  public World generateScales() {
    float[][] temp, percip;
    temp = new float[100][100];
    percip = new float[100][100];
    float xv, yv;
    for(int x = 0;x < 100;x++) {
      for(int y = 0;y < 100;y++) {
        xv = x/100f;
        yv = y/100f;
        temp[x][y] = getTemp(50, y, xv, 0);
        percip[x][y] = getPercip(yv, xv, 0);
      }
    }
    TerrainRenderer.saveMap(temp, TerrainRenderer.GRAY_SCALE_GRAD, "scale-temp");
    TerrainRenderer.saveMap(percip, TerrainRenderer.GRAY_SCALE_GRAD, "scale-percip");
      return this;
  }
  
  public void generate() {
    generateElevation();
    generateAvgTemp();
    generateAvgPercip();
  }
  
  private void generateElevation() {
    _gen.setSeed(SEED_ELEVATION);
    _gen.setScales(NOISE_SCALE, NOISE_SCALE);
    _gen.setOctaves(10);
    _gen.setPersistance(0.5f);
    _elevation = _gen.get2DNoise();
    TerrainRenderer.saveMap(_elevation, TerrainRenderer.GRAY_MAP_GRAD, "map-elevation");
  }
  
  private void generateAvgTemp() {
    _gen.setSeed(SEED_TEMP);
    _gen.setScales(NOISE_SCALE, NOISE_SCALE);
    _gen.setOctaves(10);
    _gen.setPersistance(0.75f);
    _avgTemp = _gen.get2DNoise();
    for(int x = 0;x < WIDTH;x++)
      for(int y = 0;y < HEIGHT;y++)
        _avgTemp[x][y] = getTemp(HEIGHT/2f, y, _elevation[x][y], (_avgTemp[x][y]-0.5f)*0.25f);
    TerrainRenderer.saveMap(_avgTemp, TerrainRenderer.TEMP_GRAD, "map-temp");
  }
  
  private float getTemp(float halfHeight, int y, float elevation, float noise) {
    float temp = 1f - Math.abs(y-halfHeight)/halfHeight;
    temp = (float) Math.pow(temp, 1)/1.75f;
    return MathUtil.clamp(0, -(elevation - 0.85f) / 2f + temp + noise, 1f);
  }
  
  private void generateAvgPercip() {
    _gen.setSeed(SEED_PERCIP);
    _gen.setScales(NOISE_SCALE*2f, NOISE_SCALE*2f);
    _gen.setOctaves(10);
    _gen.setPersistance(0.4f);
    _avgPercip = _gen.get2DNoise();
    for(int x = 0;x < WIDTH;x++)
      for(int y = 0;y < HEIGHT;y++)
        _avgPercip[x][y] = getPercip(_avgTemp[x][y], _elevation[x][y], (_avgPercip[x][y])*0.3f);
    TerrainRenderer.saveMap(_avgPercip, TerrainRenderer.GRAY_SCALE_GRAD, "map-percip");
  }
  
  private float getPercip(float temp, float elevation, float noise) {
    elevation = (MathUtil.clamp(0.5f, elevation, 1f)-0.5f)*1.5f;
    float value = (1f-(float)Math.pow(elevation, 1.25))*(temp+0.1f);
    return MathUtil.clamp(0f, value + noise, 1f);
  }
}
