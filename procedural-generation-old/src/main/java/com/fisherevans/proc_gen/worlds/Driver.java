package com.fisherevans.proc_gen.worlds;

public class Driver {
  public static void main(String[] args) {
    World world = new World((int)(Math.random()*9999999), 16f, 9f, 10000, 300);
    //World world = new World(9881137, 16f, 9f, 10);
    WorldRenderer renderer = new WorldRenderer(world, "world", 250);
    renderer.render();
  }
}
