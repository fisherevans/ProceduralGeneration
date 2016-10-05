package com.fisherevans.procedural_generation.old.test;

import java.awt.image.BufferedImage;

public class ImageComparer {
	private final static int RED_OS = 16;
	private final static int GREEN_OS = 8;
	private final static int BLUE_OS = 0;
	
	private BufferedImage _base;
	private int _width, _height;
	
	public ImageComparer(BufferedImage base) {
		_base = base;
		_width = base.getWidth();
		_height = base.getHeight();
	}
	
	public long compare(BufferedImage compare) {
		long fitness = 0;
		int baseRGB, compareRGB, dr, dg, db;
		for(int y = 0;y < _height;y++) {
			for(int x = 0;x < _width;x++) {
				baseRGB = _base.getRGB(x, y);
				compareRGB = compare.getRGB(x, y);
				dr = ((baseRGB >> RED_OS) & 0xFF)   - ((compareRGB >> RED_OS) & 0xFF);
				dg = ((baseRGB >> GREEN_OS) & 0xFF) - ((compareRGB >> GREEN_OS) & 0xFF);
				db = ((baseRGB >> BLUE_OS) & 0xFF)  - ((compareRGB >> BLUE_OS) & 0xFF);
				fitness += dr*dr + dg*dg + db*db;
			}
		}
		return fitness;
	}
}
