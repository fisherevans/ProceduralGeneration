package com.fisherevans.procedural_generation.old.test;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Gradient {
	private List<ColorPoint> _colors;
	
	public Gradient(Color startColor, Color endColor) {
		_colors = new ArrayList<ColorPoint>(2);
		addPoint(0f, startColor);
		addPoint(1f, endColor);
	}
	
	public void addPoint(float position, Color color) {
		if(position < 0 || position > 1)
			return;
		_colors.add(new ColorPoint(position, color));
		Collections.sort(_colors);
	}
	
	public Color getColor(float position) {
		if(position < 0 || position > 1)
			return null;
		ColorPoint cp1, cp2;
		for(int id = 1;id < _colors.size();id++) {
			cp1 = _colors.get(id-1);
			cp2 = _colors.get(id);
			if(cp1.position <= position && cp2.position >= position) {
				float scale = (position-cp1.position)/(cp2.position-cp1.position);
				return new Color(
						interpolate(cp1.color.getRed(), cp2.color.getRed(), scale),
						interpolate(cp1.color.getGreen(), cp2.color.getGreen(), scale),
						interpolate(cp1.color.getBlue(), cp2.color.getBlue(), scale),
						interpolate(cp1.color.getAlpha(), cp2.color.getAlpha(), scale)
						);
			}
		}
		return null;
	}
	
	public static int interpolate(int a, int b, float position) {
		return (int)(b*position + a*(1f-position));
	}
	
	private class ColorPoint implements Comparable<ColorPoint> {
		public float position;
		public Color color;
		
		public ColorPoint(float position, Color color) {
			this.position = position;
			this.color = color;
		}
		
		public int compareTo(ColorPoint other) {
			return (int) Math.signum(this.position - other.position);
		}
		
	}
}
