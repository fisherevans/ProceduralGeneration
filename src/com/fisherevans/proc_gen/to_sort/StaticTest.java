package com.fisherevans.proc_gen.to_sort;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class StaticTest {
	public static void main(String[] args) {
		//testSmall();
		//testCar();
		testTime();
	}

	public static void testSmall() {
		try {
			BufferedImage test1 = ImageIO.read(new File("C:/img/test1.jpg"));
			BufferedImage test2 = ImageIO.read(new File("C:/img/test2.jpg"));
			BufferedImage test3 = ImageIO.read(new File("C:/img/test3.jpg"));
			BufferedImage test4 = ImageIO.read(new File("C:/img/test4.jpg"));
			ImageComparator ic = new ImageComparator(test1);
			System.out.println("1 vs 1: " + ic.compare(test1));
			System.out.println("1 vs 2: " + ic.compare(test2));
			System.out.println("1 vs 3: " + ic.compare(test3));
			System.out.println("1 vs 4: " + ic.compare(test4));
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void testCar() {
		try {
			BufferedImage car = ImageIO.read(new File("C:/img/car.jpg"));
			BufferedImage black = ImageIO.read(new File("C:/img/black.jpg"));
			ImageComparator ic = new ImageComparator(car);
			System.out.println("car vs car: " + ic.compare(car));
			System.out.println("car vs black: " + ic.compare(black));
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void testTime() {
		try {
			System.out.println("[0%                                          100%]");
			int ittr = 500;
			int tick = ittr/50;
			BufferedImage car = ImageIO.read(new File("C:/img/car.jpg"));
			ImageComparator ic = new ImageComparator(car);
			long start = System.currentTimeMillis();
			for(int i = 0;i < ittr;i++) {
				ic.compare(car);
				if(i%tick == 0)
					System.out.print("|");
			}
			long per = (System.currentTimeMillis()-start)/ittr;
			System.out.println("\n\n" + per + "ms per");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
