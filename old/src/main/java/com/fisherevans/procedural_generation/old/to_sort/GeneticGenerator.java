package com.fisherevans.procedural_generation.old.to_sort;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;

public class GeneticGenerator {
    private List<Poly> _polygons;

    public static final double SCALE = 1;
    public static final int[] X_SIGNS = {1, 1, -1, -1};
    public static final int[] Y_SIGNS = {1, -1, -1, 1};

    private ImageComparator _comparer;
    private BufferedImage _baseImage, _smallBaseImage, _finalImage;
    private Graphics2D _finalGfx;

    private BufferedImage _lastGeneration, _tempGeneration;
    private Graphics2D _tempGfx;
    private int _smallWidth, _smallHeight;
    private long _lastFitness;
    private Random _random = new Random();

    public GeneticGenerator(BufferedImage base) {
        _baseImage = base;

        _smallWidth = (int) (base.getWidth() * SCALE);
        _smallHeight = (int) (base.getHeight() * SCALE);
        _smallBaseImage = new BufferedImage(_smallWidth, _smallHeight, BufferedImage.TYPE_INT_ARGB);
        _smallBaseImage.createGraphics().drawImage(base, 0, 0, _smallWidth, _smallHeight, null);

        _comparer = new ImageComparator(_smallBaseImage);

        _lastGeneration = new BufferedImage(_smallWidth, _smallHeight, BufferedImage.TYPE_INT_RGB);
        _tempGeneration = new BufferedImage(_smallWidth, _smallHeight, BufferedImage.TYPE_INT_RGB);
        _tempGfx = _tempGeneration.createGraphics();

        _finalImage = new BufferedImage(_baseImage.getWidth(), _baseImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        _finalGfx = _finalImage.createGraphics();
        _finalGfx.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        _lastFitness = _comparer.compare(_lastGeneration);

        System.out.println("Scaled to " + _smallWidth + "x" + _smallHeight);
    }


    public class Poly {
        public int[] xs, ys;
        public Poly(int[] xs, int[] ys) {
            this.xs = xs;
            this.ys = ys;
        }
    }
}
