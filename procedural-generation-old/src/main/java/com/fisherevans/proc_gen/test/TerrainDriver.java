package com.fisherevans.proc_gen.test;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;

import com.fisherevans.proc_gen.test.PerlinGenerator.InterpType;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class TerrainDriver {
    public static Color TRANSPARENT_WHITE = new Color(255, 255, 255, 190);
    public static Color TRANSPARENT = new Color(255, 255, 255, 0);

	public static Color LIGHT_GREEN = new Color(107, 181, 94);
	public static Color GREEN = new Color(76, 143, 64);
	public static Color DARK_GREEN = new Color(48, 99, 39);

	public static Color LIGHT_BLUE2 = new Color(81, 113, 158);
	public static Color LIGHT_BLUE = new Color(45, 79, 128);
	public static Color BLUE = new Color(40, 68, 107);
	public static Color DARK_BLUE = new Color(12, 41, 82);

    public static Color YELLOW = new Color(244, 245, 184);
	
	public static Color SKY_BLUE = new Color(105, 185, 255);
	
	public static Gradient MAP_GRAD, GRAY_SCALE_GRAD, SKY_GRAD, RAINBOW_GRAD;
	
	static {
    	MAP_GRAD = new Gradient(DARK_BLUE, LIGHT_GREEN);
        MAP_GRAD.addPoint(0.75f, GREEN);
        MAP_GRAD.addPoint(0.525f, DARK_GREEN);
        MAP_GRAD.addPoint(0.5f, YELLOW);
    	MAP_GRAD.addPoint(0.49f, LIGHT_BLUE);
    	MAP_GRAD.addPoint(0.35f, BLUE);
    	
    	GRAY_SCALE_GRAD = new Gradient(Color.BLACK, Color.WHITE);
    	
    	SKY_GRAD = new Gradient(TRANSPARENT, Color.WHITE);
    	SKY_GRAD.addPoint(0.7f,  TRANSPARENT_WHITE);
    	SKY_GRAD.addPoint(0.45f,  TRANSPARENT);
    	
    	RAINBOW_GRAD = new Gradient(new Color(255, 0, 0), new Color(0, 255, 0));
    	RAINBOW_GRAD.addPoint(0.25f,  new Color(255, 0, 255));
    	RAINBOW_GRAD.addPoint(0.5f,  new Color(0, 0, 255));
    	RAINBOW_GRAD.addPoint(0.75f,  new Color(0, 255, 255));
	}
	
    public static void main2(String[] args) throws IOException {
    	System.out.println("Generating 32x32x32x32 4D Noise");
    	long start = System.currentTimeMillis();
    	//float[][][][] noise = PerlinNoise.get4DNoise(32);
    	System.out.println((System.currentTimeMillis() - start) + "ms");
    	System.out.println("Done.");
    }
	
    public static void main(String[] args) throws IOException {
    	int power = 8;
    	int size = 1 << power;
    	int seed = (int)(Math.random()*Integer.MAX_VALUE);
    	long start;
    	//seed = 608643652;
    	
    	System.out.println("Generating " + size + "x" + size + " Height Maps");

    	//createMap("perlin.closest." + seed, new PerlinGenerator(seed, size, 9, 0.45f, InterpType.Closest), MAP_GRAD);
    	//createMap("perlin.linear." + seed, new PerlinGenerator(seed, size, 9, 0.45f, InterpType.Linear), MAP_GRAD);
    	//createMap("perlin.cosine." + seed, new PerlinGenerator(seed, size, 9, 0.45f, InterpType.Cosine), MAP_GRAD);
    	//createMap("ds." + seed, new DiamondSquareGenerator(seed, power, 0.5, true), MAP_GRAD);
    	//createMap("simplex." + seed, new SimplexGenerator(seed, size, 256, 12, 3), MAP_GRAD);

    	System.out.println("Generating Terrain");
    	start = System.currentTimeMillis();
    	double[][] ds = new DiamondSquareGenerator(seed, power, 0.5, true).quickGen().data;
    	System.out.println((System.currentTimeMillis() - start) + "ms");
    	
    	start = System.currentTimeMillis();
    	System.out.println("Generating Sky");
    	float[][][] clouds = new float[1][1][1];// PerlinNoise.getNoise();
    	System.out.println((System.currentTimeMillis() - start) + "ms");
    	
    	start = System.currentTimeMillis();
    	System.out.println("Generating GIF");
	    ImageOutputStream output = new FileImageOutputStream(new File("terrain/animClouds.gif"));
		GifSequenceWriter writer = new GifSequenceWriter(output, BufferedImage.TYPE_INT_ARGB, 75, true);
		for(int z = 0;z < clouds.length;z++)
			writer.writeToSequence(getLayeredMap(ds, clouds[z], z/7, z/5));
		writer.close();
		output.close();
    	System.out.println((System.currentTimeMillis() - start) + "ms");

    	System.out.println("Done.");
    }
    
    public static void createMap(String name, TerrainGenerator gen, Gradient grad) throws IOException {
    	System.out.println("Generating " + name + "...");
    	long start = System.currentTimeMillis();
    	gen.quickGen();
    	System.out.println((System.currentTimeMillis() - start));
    	System.out.println("Rendering " + name + "...");
    	saveMap(gen.data, grad, name);
    }
    
    public static void saveMap(double[][] terrain, Gradient g, String name) throws IOException {
        BufferedImage map = new BufferedImage(terrain.length, terrain[0].length, BufferedImage.TYPE_INT_ARGB);
        for(int x = 0;x < terrain.length;x++)
            for(int y = 0;y < terrain[x].length;y++)
                map.setRGB(x, y, g.getColor((float)terrain[x][y]).getRGB());
        ImageIO.write(map, "png", new File("gen/terrain_" + name + ".png"));
    }
    
    public static BufferedImage getImage(double[][] terrain, Gradient g) throws IOException {
        BufferedImage map = new BufferedImage(terrain.length, terrain[0].length, BufferedImage.TYPE_INT_ARGB);
        for(int x = 0;x < terrain.length;x++)
            for(int y = 0;y < terrain[x].length;y++)
                map.setRGB(x, y, g.getColor((float)terrain[x][y]).getRGB());
        return map;
    }
    
    public static void saveLayeredMap(double[][] terrain, double[][] sky, String name) throws IOException {
        BufferedImage map = new BufferedImage(terrain.length, terrain[0].length, BufferedImage.TYPE_INT_ARGB);
        Color terrainColor, skyColor;
        for(int x = 0;x < terrain.length;x++) {
            for(int y = 0;y < terrain[x].length;y++) {
            	terrainColor = MAP_GRAD.getColor((float)terrain[x][y]);
            	skyColor = SKY_GRAD.getColor((float)sky[x][y]);
            	map.setRGB(x, y, new Color(
            			Gradient.interpolate(terrainColor.getRed(), skyColor.getRed(), skyColor.getAlpha()/255f),
            			Gradient.interpolate(terrainColor.getGreen(), skyColor.getGreen(), skyColor.getAlpha()/255f),
            			Gradient.interpolate(terrainColor.getBlue(), skyColor.getBlue(), skyColor.getAlpha()/255f)
            			).getRGB());
            }
        }
        ImageIO.write(map, "png", new File("gen/terrain_" + name + ".png"));
    }
    
    public static BufferedImage getLayeredMap(double[][] terrain, float[][] sky, int sX, int sY) throws IOException {
        BufferedImage map = new BufferedImage(terrain.length, terrain[0].length, BufferedImage.TYPE_INT_ARGB);
        Color terrainColor, skyColor;
        for(int x = 0;x < terrain.length;x++) {
            for(int y = 0;y < terrain[x].length;y++) {
            	terrainColor = MAP_GRAD.getColor((float)terrain[x][y]);
            	skyColor = SKY_GRAD.getColor((float)sky[wrappedId((x+sX), sky.length)][wrappedId((y+sY), sky.length)]);
            	map.setRGB(x, y, new Color(
            			Gradient.interpolate(terrainColor.getRed(), skyColor.getRed(), skyColor.getAlpha()/255f),
            			Gradient.interpolate(terrainColor.getGreen(), skyColor.getGreen(), skyColor.getAlpha()/255f),
            			Gradient.interpolate(terrainColor.getBlue(), skyColor.getBlue(), skyColor.getAlpha()/255f)
            			).getRGB());
            }
        }
        return map;
    }
    
    public static final int wrappedId(int id, int size) {
    	id %= size;
    	if(id < 0)
    		id += size;
    	return id;
    }
}
