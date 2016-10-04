package com.fisherevans.proc_gen.test;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;


public class TerrainRenderer {
  private static Color TRANSPARENT_WHITE = new Color(255, 255, 255, 190);
  private static Color TRANSPARENT = new Color(255, 255, 255, 0);

  private static Color LIGHT_GREEN = new Color(107, 181, 94);
  private static Color GREEN = new Color(76, 143, 64);
  private static Color DARK_GREEN = new Color(48, 99, 39);
  
  private static Color LIGHT_BLUE2 = new Color(81, 113, 158);
  private static Color LIGHT_BLUE = new Color(45, 79, 128);
  private static Color BLUE = new Color(40, 68, 107);
  private static Color DARK_BLUE = new Color(12, 41, 82);
  
  private static Color YELLOW = new Color(244, 245, 184);
  
  private static Color SKY_BLUE = new Color(105, 185, 255);
  
  public static Gradient TEMP_GRAD, MAP_GRAD, GRAY_MAP_GRAD, GRAY_SCALE_GRAD, SKY_GRAD, RAINBOW_GRAD;
  
  static {
      MAP_GRAD = new Gradient(DARK_BLUE, LIGHT_GREEN);
      MAP_GRAD.addPoint(0.75f, GREEN);
      MAP_GRAD.addPoint(0.505f, DARK_GREEN);
      //MAP_GRAD.addPoint(0.5f, YELLOW);
      MAP_GRAD.addPoint(0.495f, LIGHT_BLUE);
      MAP_GRAD.addPoint(0.35f, BLUE);
    
      GRAY_MAP_GRAD = new Gradient(Color.BLACK, Color.WHITE);
      GRAY_MAP_GRAD.addPoint(0.495f, new Color(0.3f, 0.3f, 0.3f));
      GRAY_MAP_GRAD.addPoint(0.505f, new Color(0.6f, 0.6f, 0.6f));
      
      GRAY_SCALE_GRAD = new Gradient(Color.BLACK, Color.WHITE);
      
      SKY_GRAD = new Gradient(TRANSPARENT, Color.WHITE);
      SKY_GRAD.addPoint(0.7f,  TRANSPARENT_WHITE);
      SKY_GRAD.addPoint(0.45f,  TRANSPARENT);
      
      RAINBOW_GRAD = new Gradient(new Color(255, 0, 0), new Color(0, 255, 0));
      RAINBOW_GRAD.addPoint(0.25f,  new Color(255, 0, 255));
      RAINBOW_GRAD.addPoint(0.5f,  new Color(0, 0, 255));
      RAINBOW_GRAD.addPoint(0.75f,  new Color(0, 255, 255));
      
      TEMP_GRAD = new Gradient(Color.BLUE, Color.RED);
      TEMP_GRAD.addPoint(0.3333f, Color.GREEN);
      TEMP_GRAD.addPoint(0.5f, Color.YELLOW);
      TEMP_GRAD.addPoint(0.6667f, Color.ORANGE);
  }

  public static void saveMap(float[][] terrain, Gradient g, String name) {
    try {
      BufferedImage map = new BufferedImage(terrain.length, terrain[0].length, BufferedImage.TYPE_INT_ARGB);
      for(int x = 0;x < terrain.length;x++)
          for(int y = 0;y < terrain[x].length;y++)
              map.setRGB(x, y, g.getColor((float)terrain[x][y]).getRGB());
      ImageIO.write(map, "png", new File("terrain/" + name + ".png"));
    } catch(Exception e) {
      e.printStackTrace();
    }
  }
}
