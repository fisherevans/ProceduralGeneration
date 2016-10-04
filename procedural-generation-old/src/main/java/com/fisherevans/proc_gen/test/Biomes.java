package com.fisherevans.proc_gen.test;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.fisherevans.proc_gen.noise.perlin.PerlinNoise;

public class Biomes {
  public static Color[] COLORS = new Color[] {
    Color.RED, Color.MAGENTA, Color.BLUE, Color.GREEN, Color.YELLOW, Color.ORANGE
  };
  public static void main(String[] args) throws IOException {
    int baseSeed = 123;//(int)(Math.random()*Integer.MAX_VALUE);
    int count = 4;
    int size = 512;
    PerlinNoise ps = new PerlinNoise(PerlinNoise.INTERP_TYPE_COSINE, 3, 0, 0.2f);
    ps.setSizes(size, size);
    ps.setScales(0.0125f, 0.0125f);
    float[][][] noise = new float[count][size][size];
    for(int id = 0;id < count;id++) {
      ps.setSeed(id + baseSeed);
      noise[id] = ps.get2DNoise();
    }
    int[][] biomes = new int[size][size];
    for(int x = 0;x < size;x++)
      for(int y = 0;y < size;y++)
        for(int id = 0;id < count;id++)
          if(noise[id][x][y] > noise[biomes[x][y]][x][y])
            biomes[x][y] = id;
    BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
    for(int x = 0;x < size;x++) {
      for(int y = 0;y < size;y++) {
        img.setRGB(x, y, COLORS[biomes[x][y]].getRGB());
      }
    }
    ImageIO.write(img, "png", new File("gen/terrain_biome.png"));
  }
}
