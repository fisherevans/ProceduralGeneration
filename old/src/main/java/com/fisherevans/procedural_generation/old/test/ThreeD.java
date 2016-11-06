package com.fisherevans.procedural_generation.old.test;

import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;

import javax.swing.JFrame;

import com.fisherevans.procedural_generation.caves.noise.perlin.PerlinNoise;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.glu.GLU;

public class ThreeD extends GLJPanel implements GLEventListener, KeyListener {
  public static int screenSize = 256;

  private float rotateX, rotateY, rotateZ, zoom;
                         
  private PerlinNoise _ps;
  private float[][][] noise;

  private GifSequenceWriter writer;

  public static void main(String[] args) throws FileNotFoundException, IOException {
    JFrame window = new JFrame("JOGL Scene");
    GLCapabilities caps = new GLCapabilities(null);
    ThreeD panel = new ThreeD(caps);
    window.setContentPane(panel);
    window.pack();
    window.setLocation(100, 0);
    window.setResizable(false);
    window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    window.setVisible(true);
    panel.requestFocusInWindow();
  }

  public ThreeD(GLCapabilities capabilities) throws FileNotFoundException, IOException {
    super(capabilities);
    setPreferredSize(new Dimension(screenSize, screenSize));
    addGLEventListener(this);
    addKeyListener(this);
    rotateX = 45;
    rotateY = 45;
    rotateZ = 45;
    zoom = 50;
    System.out.println("Generating x16 4D Noise");
    long start = System.currentTimeMillis();
    _ps = new PerlinNoise(PerlinNoise.INTERP_TYPE_LINEAR, 4, (int)(Math.random()*Integer.MAX_VALUE), 0.5f);
    float scale = 0.05f;
    _ps.setScales(scale, scale, scale, scale*0.125f);
    _ps.setSizes(64, 32, 64);
    noise = _ps.get3DNoise();
    System.out.println((System.currentTimeMillis()-start)+"ms");
    ImageOutputStream output = new FileImageOutputStream(new File("gen/4d_perlin.gif"));
    writer = new GifSequenceWriter(output, BufferedImage.TYPE_INT_RGB, 50, true);
  }

  public void keyPressed(KeyEvent e) {
    int key = e.getKeyCode();
    float degrees = 1f;
    if (key==KeyEvent.VK_LEFT) rotateY -= degrees;
    else if (key==KeyEvent.VK_RIGHT) rotateY += degrees;
    else if (key==KeyEvent.VK_DOWN) rotateX += degrees;
    else if (key==KeyEvent.VK_UP) rotateX -= degrees;
    else if (key==KeyEvent.VK_PAGE_UP) rotateZ += degrees;
    else if (key==KeyEvent.VK_PAGE_DOWN) rotateZ -= degrees;
    else if (key==KeyEvent.VK_Q) zoom += 0.5f;
    else if (key==KeyEvent.VK_E) zoom -= 0.5f;
    else if (key==KeyEvent.VK_HOME) {
      rotateX = rotateY = rotateZ = 0;
      zoom = 15;
    }
    else if (key==KeyEvent.VK_ESCAPE) {
      try {
        writer.close();
      } catch (IOException e1) {
        e1.printStackTrace();
      }
      System.exit(0);
    }
    repaint();
  }

  public void keyTyped(KeyEvent e) {}

  public void keyReleased(KeyEvent e) {}

  private void cube(GL2 gl, float size, float x, float y, float z) {
    gl.glPushMatrix();
    gl.glTranslatef(x, y, z);
    gl.glColor3f(1f, 1f, 1f);
    gl.glBegin(GL2.GL_POLYGON);/* f1: front */
    gl.glNormal3f(-1f, 0.0f, 0.0f);
    gl.glVertex3f(0.0f, 0.0f, 0.0f);
    gl.glVertex3f(0.0f, 0.0f, size);
    gl.glVertex3f(size, 0.0f, size);
    gl.glVertex3f(size, 0.0f, 0.0f);
    gl.glEnd();
    gl.glColor3f(0.6667f, 0.6667f, 0.6667f);
    gl.glBegin(GL2.GL_POLYGON);/* f2: bottom */
    gl.glNormal3f(0.0f, 0.0f, -1f);
    gl.glVertex3f(0.0f, 0.0f, 0.0f);
    gl.glVertex3f(size, 0.0f, 0.0f);
    gl.glVertex3f(size, size, 0.0f);
    gl.glVertex3f(0.0f, size, 0.0f);
    gl.glEnd();
    gl.glColor3f(1f, 1f, 1f);
    gl.glBegin(GL2.GL_POLYGON);/* f3:back */
    gl.glNormal3f(1f, 0.0f, 0.0f);
    gl.glVertex3f(size, size, 0.0f);
    gl.glVertex3f(size, size, size);
    gl.glVertex3f(0.0f, size, size);
    gl.glVertex3f(0.0f, size, 0.0f);
    gl.glEnd();
    gl.glColor3f(0.6667f, 0.6667f, 0.6667f);
    gl.glBegin(GL2.GL_POLYGON);/* f4: top */
    gl.glNormal3f(0.0f, 0.0f, 1f);
    gl.glVertex3f(size, size, size);
    gl.glVertex3f(size, 0.0f, size);
    gl.glVertex3f(0.0f, 0.0f, size);
    gl.glVertex3f(0.0f, size, size);
    gl.glEnd();
    gl.glColor3f(0.3333f, 0.3333f, 0.3333f);
    gl.glBegin(GL2.GL_POLYGON);/* f5: left */
    gl.glNormal3f(0.0f, 1f, 0.0f);
    gl.glVertex3f(0.0f, 0.0f, 0.0f);
    gl.glVertex3f(0.0f, size, 0.0f);
    gl.glVertex3f(0.0f, size, size);
    gl.glVertex3f(0.0f, 0.0f, size);
    gl.glEnd();
    gl.glColor3f(0.3333f, 0.3333f, 0.3333f);
    gl.glBegin(GL2.GL_POLYGON);/* f6: right */
    gl.glNormal3f(0.0f, -1f, 0.0f);
    gl.glVertex3f(size, 0.0f, 0.0f);
    gl.glVertex3f(size, 0.0f, size);
    gl.glVertex3f(size, size, size);
    gl.glVertex3f(size, size, 0.0f);
    gl.glEnd();
    gl.glPopMatrix();
  }

  public void display(GLAutoDrawable drawable) {
    GL2 gl = drawable.getGL().getGL2();
    GLU glu = new GLU();
    gl.glClearColor(0f, 0f, 0f, 1F);
    gl.glClear(GL.GL_COLOR_BUFFER_BIT|GL.GL_DEPTH_BUFFER_BIT);

    gl.glMatrixMode(GL2.GL_PROJECTION); // Set up the projection.
    gl.glLoadIdentity();
    gl.glOrtho(-zoom, zoom, -zoom, zoom, -200, 200);
    gl.glMatrixMode(GL2.GL_MODELVIEW);
    glu.gluPerspective(90, 1, 0, 100);

    gl.glLoadIdentity(); // Set up modelview transform.
    gl.glRotatef(rotateZ, 0, 0, 1);
    gl.glRotatef(rotateY, 0, 1, 0);
    gl.glRotatef(rotateX, 1, 0, 0);

    for (int x = 0;x < _ps.getXSize(); x++) {
      for (int y = 0; y<_ps.getYSize(); y++) {
        for (int z = 0; z<_ps.getZSize(); z++) {
          if (noise[x][y][z]>0.5) cube(gl, 1f, x-_ps.getXSize()/2f, y-_ps.getYSize()/2f, z-_ps.getZSize()/2f);
        }
      }
    }
    try {
      // This used to write the current render into a gif buffer
      // writer.writeToSequence(Screenshot.readToBufferedImage(0, 0, screenSize, screenSize, false));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void init(GLAutoDrawable drawable) {
    GL2 gl = drawable.getGL().getGL2();
    gl.glEnable(GL.GL_DEPTH_TEST);
    // gl.glEnable(GL2.GL_LIGHTING);
    // gl.glEnable(GL2.GL_LIGHT0);
    // gl.glEnable(GL2.GL_COLOR_MATERIAL);
    // gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, new float[] { 0.6f, 0.6f,
    // 0.6f }, 0);
    gl.glEnable(GL2.GL_POLYGON_SMOOTH);
    gl.glHint(GL2.GL_LINE_SMOOTH_HINT, GL2.GL_NICEST);
    gl.glHint(GL2.GL_POLYGON_SMOOTH_HINT, GL2.GL_NICEST);
  }

  public void dispose(GLAutoDrawable arg0) {}

  public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3, int arg4) {}
}
