package com.fisherevans.procedural_generation.old.worlds;

import com.fisherevans.procedural_generation.old.worlds.math.Edge;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

public class WorldRenderer {
  private World _world;
  private String _name;
  private Integer _size;

  private Integer _width, _height;
  private BufferedImage _image;
  private Graphics2D _gfx;


  public WorldRenderer(World world, String name, Integer size) {
    _world = world;
    _name = name;
    _size = size;

    _width = (int) (_world.getWidth()*_size);
    _height = (int) (_world.getHeight()*_size);
    _image = new BufferedImage(_width, _height, BufferedImage.TYPE_INT_RGB);
    _gfx = (Graphics2D) _image.createGraphics();

    RenderingHints rh = new RenderingHints(new HashMap<RenderingHints.Key, Object>());
    rh.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    rh.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    _gfx.setRenderingHints(rh);
  }

  public void render() {
    System.out.print("Rendering... ");
    renderEdges(_world.getEdges(), Color.WHITE, 4);
    //renderEdges(_world.getCleaveEdges(), Color.darkGray, 2);
    save();
    System.out.println("Done.");
  }

  private void renderEdges(Set<Edge> edges, Color color, int size) {
    _gfx.setColor(color);
    _gfx.setStroke(new BasicStroke(size));
    for(Edge edge:edges) {
      _gfx.drawLine((int)(edge.getA().getX()*_size), (int)(edge.getA().getY()*_size),
          (int)(edge.getB().getX()*_size), (int)(edge.getB().getY()*_size));
    }
  }

  private void save() {
    String filename = _name + ".png";
    try {
      ImageIO.write(_image, "png", new File("gen/world_" + filename));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
