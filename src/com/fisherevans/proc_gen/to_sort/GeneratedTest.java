package com.fisherevans.aphotic.imgcomp;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.util.Random;
import java.util.Scanner;

import javax.imageio.ImageIO;

public class GeneratedTest {
    public static final double SCALE = 0.125;
    public static final int[] X_SIGNS = {1, 1, -1, -1};
    public static final int[] Y_SIGNS = {1, -1, -1, 1};

    private ImageComparer _comparer;
    private BufferedImage _baseImage, _smallBaseImage, _finalImage;
    private Graphics2D _finalGfx;

    private BufferedImage _lastGeneration, _tempGeneration;
    private Graphics2D _tempGfx;
    private int _smallWidth, _smallHeight;
    private long _lastFitness;
    private Random _random = new Random();

    private int[] _xs = new int[4], _ys = new int[4];

    public static void main(String[] args) {
        try {
            BufferedImage img = ImageIO.read(new File("C:/img/map.png"));
            GeneratedTest gt = new GeneratedTest(img);
            int count = 100000;
            int tick = Math.max(count / 50, 1);
            int barCount = count < 50 ? 50 / count : 1;
            String print = "";
            for (int x = 0; x < barCount; x++)
                print += "|";
            System.out.println("[0%                                          100%]");
            int total = 0;
            long start = System.currentTimeMillis();
            int i = 0;
            for (; i < count; i++) {
                total++;
                if (gt.mutate()) {
                    if (i % tick == 0)
                        System.out.print(print);
                } else
                    i--;
                if (System.in.available() > 0)
                    break;
            }
            long end = System.currentTimeMillis();
            ImageIO.write(gt.getFinalImage(), "png", new File("C:/img/out.png"));
            System.out.println("\n" + total + " total generations. " + i + " Polygons. Took " + ((end - start) / 1000.0) + " seconds.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public GeneratedTest(BufferedImage base) {
        _baseImage = base;

        _smallWidth = (int) (base.getWidth() * SCALE);
        _smallHeight = (int) (base.getHeight() * SCALE);
        _smallBaseImage = new BufferedImage(_smallWidth, _smallHeight, BufferedImage.TYPE_INT_ARGB);
        _smallBaseImage.createGraphics().drawImage(base, 0, 0, _smallWidth, _smallHeight, null);

        _comparer = new ImageComparer(_smallBaseImage);

        _lastGeneration = new BufferedImage(_smallWidth, _smallHeight, BufferedImage.TYPE_INT_RGB);
        _tempGeneration = new BufferedImage(_smallWidth, _smallHeight, BufferedImage.TYPE_INT_RGB);
        _tempGfx = _tempGeneration.createGraphics();

        _finalImage = new BufferedImage(_baseImage.getWidth(), _baseImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        _finalGfx = _finalImage.createGraphics();
        _finalGfx.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        _lastFitness = _comparer.compare(_lastGeneration);

        System.out.println("Scaled to " + _smallWidth + "x" + _smallHeight);
    }

    public BufferedImage getFinalImage() {
        return _finalImage;
    }

    public boolean mutate() {
        copyImage(_lastGeneration, _tempGeneration);
        int centerX = _random.nextInt(_smallWidth);
        int centerY = _random.nextInt(_smallHeight);
        for (int id = 0; id < 4; id++) {
            _xs[id] = (int) (centerX + _random.nextInt(_smallWidth) * X_SIGNS[id] * _random.nextDouble());
            _ys[id] = (int) (centerY + _random.nextInt(_smallWidth) * Y_SIGNS[id] * _random.nextDouble());
        }
        Color color = new Color(_random.nextInt(256), _random.nextInt(256), _random.nextInt(256), _random.nextInt(256));
        _tempGfx.setColor(color);
        _tempGfx.fillPolygon(_xs, _ys, 4);
        long fitness = _comparer.compare(_tempGeneration);
        if (fitness <= _lastFitness) {
            _lastFitness = fitness;
            copyImage(_tempGeneration, _lastGeneration);
            for (int i = 0; i < 4; i++) {
                _xs[i] /= SCALE;
                _ys[i] /= SCALE;
            }
            _finalGfx.setColor(color);
            _finalGfx.fillPolygon(_xs, _ys, 4);
            return true;
        } else
            return false;
    }

    public boolean mutate3() {
        copyImage(_lastGeneration, _tempGeneration);
        for (int id = 0; id < 4; id++) {
            _xs[id] = _random.nextInt(_smallWidth);
            _ys[id] = _random.nextInt(_smallHeight);
        }
        Color color = new Color(_random.nextInt(256), _random.nextInt(256), _random.nextInt(256), _random.nextInt(256));
        _tempGfx.setColor(color);
        _tempGfx.fillPolygon(_xs, _ys, 4);
        long fitness = _comparer.compare(_tempGeneration);
        if (fitness <= _lastFitness) {
            _lastFitness = fitness;
            copyImage(_tempGeneration, _lastGeneration);
            for (int i = 0; i < 4; i++) {
                _xs[i] /= SCALE;
                _ys[i] /= SCALE;
            }
            _finalGfx.setColor(color);
            _finalGfx.fillPolygon(_xs, _ys, 4);
            return true;
        } else
            return false;
    }

    public boolean mutate2() {
        copyImage(_lastGeneration, _tempGeneration);
        int centerX = _random.nextInt(_smallWidth);
        int centerY = _random.nextInt(_smallHeight);
        for (int id = 0; id < 4; id++) {
            _xs[id] = centerX + _random.nextInt(_smallWidth) * X_SIGNS[id];
            _ys[id] = centerY + _random.nextInt(_smallWidth) * Y_SIGNS[id];
        }
        Color color = new Color(_random.nextInt(256), _random.nextInt(256), _random.nextInt(256), _random.nextInt(256));
        _tempGfx.setColor(color);
        _tempGfx.fillPolygon(_xs, _ys, 4);
        long fitness = _comparer.compare(_tempGeneration);
        if (fitness <= _lastFitness) {
            _lastFitness = fitness;
            copyImage(_tempGeneration, _lastGeneration);
            for (int i = 0; i < 4; i++) {
                _xs[i] /= SCALE;
                _ys[i] /= SCALE;
            }
            _finalGfx.setColor(color);
            _finalGfx.fillPolygon(_xs, _ys, 4);
            return true;
        } else
            return false;
    }

    private void copyImage(BufferedImage from, BufferedImage to) {
        int[] fromData = ((DataBufferInt) from.getRaster().getDataBuffer()).getData();
        int[] toData = ((DataBufferInt) to.getRaster().getDataBuffer()).getData();
        System.arraycopy(fromData, 0, toData, 0, fromData.length);
    }
}
