package com.fisherevans.proc_gen.caves;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;

public class CaveRenderer {
	public static void main(String[] args) {
		//*
		for(File file:new File("C:/caves").listFiles()) {
			file.delete();
		}
		/**/
		for(int count = 0;count < 10;count++) {
			CaveGenerator.generate();
			int size = (CaveGenerator.mapSize)*3;
			int half = size/2;
			BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
			Graphics2D g = img.createGraphics();

            g.setColor(Color.YELLOW);
            for(Point p:CaveGenerator.points)
                g.fillRect(p.x+half, p.y+half, 1, 1);
            saveImage(count, img, "step1");

			drawLines(g, half, Color.WHITE);
            saveImage(count, img, "step2");

			g.fillRect(CaveGenerator.start.x+half-2, CaveGenerator.start.y+half-2, 5, 5);
			g.fillRect(CaveGenerator.end.x+half-2, CaveGenerator.end.y+half-2, 5, 5);
            saveImage(count, img, "step3");

			g.drawImage(expandWalls(img, 255/4 + 1, 1), 0, 0, null);
            saveImage(count, img, "step4");

			for(int id = 0;id < 100;id++)
				g.drawImage(expandWalls(img, 255/4 + 1, 0.5), 0, 0, null);
            saveImage(count, img, "step5");

			for(int id = 0;id < 10;id++)
				g.drawImage(expandWalls(img, 255/3 + 1, 0.5), 0, 0, null);
            saveImage(count, img, "step6");

			g.drawImage(expandWalls(img, 255/3 + 1, 1), 0, 0, null);
            saveImage(count, img, "step7");

            contrast(img, 100);

			drawLines(g, half, new Color(220, 220, 255));
			g.setColor(Color.GREEN);
			g.fillRect(CaveGenerator.start.x+half-1, CaveGenerator.start.y+half-1, 3, 3);
			g.setColor(Color.RED);
			g.fillRect(CaveGenerator.end.x+half-1, CaveGenerator.end.y+half-1, 3, 3);
            saveImage(count, img, "step8");
		}
		System.out.println("\nExit.");
	}

	public static BufferedImage expandWalls(BufferedImage base, int grayInt, double chance) {
		BufferedImage img = new BufferedImage(base.getWidth(), base.getHeight(), BufferedImage.TYPE_INT_RGB);
		int white = Color.WHITE.getRGB();
		Color gray = new Color(grayInt, grayInt, grayInt);
		for(int y = 0;y < img.getHeight();y++) {
			for(int x = 0;x < img.getWidth();x++) {
				if(base.getRGB(x, y) == white) {
					img.setRGB(x, y, white);
					if(CaveGenerator.random.nextDouble() <= chance) // 9
						addColor(img, x-1, y, gray);
					if(CaveGenerator.random.nextDouble() <= chance) // 10:30
						addColor(img, x-1, y+1, gray);
					if(CaveGenerator.random.nextDouble() <= chance) // 12
						addColor(img, x, y+1, gray);
					if(CaveGenerator.random.nextDouble() <= chance) // 1:30
						addColor(img, x+1, y+1, gray);
					if(CaveGenerator.random.nextDouble() <= chance) // 3
						addColor(img, x+1, y, gray);
					if(CaveGenerator.random.nextDouble() <= chance) // 4:30
						addColor(img, x+1, y-1, gray);
					if(CaveGenerator.random.nextDouble() <= chance) // 6
						addColor(img, x, y-1, gray);
					if(CaveGenerator.random.nextDouble() <= chance) // 7:30
						addColor(img, x-1, y-1, gray);
				}
			}
		}
		
		return img;
	}
	public static void contrast(BufferedImage img, int threshold) {
		int white = Color.white.getRGB();
		int black = Color.black.getRGB();
		for(int y = 0;y < img.getHeight();y++) {
			for(int x = 0;x < img.getWidth();x++) {
				if(new Color(img.getRGB(x, y)).getRed() >= threshold)
					img.setRGB(x, y, white);
				else
					img.setRGB(x, y, black);
			}
		}
	}
	
	private static void drawLines(Graphics2D g, int half,  Color c) {
		g.setColor(c);
		Point a, b;
        for(int j = 0;j < CaveGenerator.pointCount;j++) {
            for(int k = j+1;k < CaveGenerator.pointCount;k++) {
            	if(CaveGenerator.connections[j][k]) {
            		a = CaveGenerator.points.get(j);
            		b = CaveGenerator.points.get(k);
            		g.drawLine(a.x+half, a.y+half, b.x+half, b.y+half);
            	}
            }
        }
	}
	
	private static void addColor(BufferedImage img, int x, int y, Color c) {
		if(x < 0 || y < 0 || x == img.getWidth() || y == img.getHeight())
			return;
		Color add = new Color(img.getRGB(x, y));
		add = new Color(Math.min(add.getRed()+c.getRed(), 255),
				Math.min(add.getGreen()+c.getGreen(), 255),
				Math.min(add.getBlue()+c.getBlue(), 255));
		img.setRGB(x, y, add.getRGB());
	}
	
	public static void saveImage(int id, BufferedImage img, String suffix) {
		String filename = (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()))
				+ "_" + id + "_" + suffix + ".png";
		try {
            BufferedImage bigImg = new BufferedImage(img.getWidth()*4, img.getHeight()*4, BufferedImage.TYPE_INT_RGB);
            bigImg.createGraphics().drawImage(img, 0, 0, img.getWidth()*4, img.getHeight()*4, null);
			ImageIO.write(bigImg, "png", new File("C:/caves/" + filename));
			//ImageIO.write(img, "png", new File("C:/caves/" + filename));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
