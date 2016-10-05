package com.fisherevans.proc_gen.dungeons;

import java.util.List;

public class Room implements Comparable<Room> {
	private int _left, _right, _top, _bottom;

	public Room(int x1, int y1, int width, int height) {
        _left = x1;
        _right = x1;
        if(width < 0)
            _left +=  width;
        else
            _right +=  width;

		_bottom = y1;
		_top = y1;
        if(height < 0)
            _bottom +=  height;
        else
            _top +=  height;
	}
	
	public boolean touches(Room b) {
		Room a = this;
		return !(b.getLeft() >= a.getRight() ||
				b.getRight() <= a.getLeft() ||
				b.getTop() <= a.getBottom() ||
				b.getBottom() >= a.getTop());
	}
	
	public boolean touches(Room b, int padding) {
		Room a = this;
		return !(b.getLeft()-padding >= a.getRight() ||
				b.getRight() <= a.getLeft()-padding ||
				b.getTop() <= a.getBottom()-padding ||
				b.getBottom()-padding >= a.getTop());
	}
	
	public void expand(int by) {
		_left -= by;
		_right += by;
		_top += by;
		_bottom -= by;
	}

    public int getArea() {
        return getWidth()*getHeight();
    }
	
	public int getWidth() {
		return _right - _left;
	}
	
	public int getHeight() {
		return _top - _bottom;
	}
	
	public int getLeft() {
		return _left;
	}
	
	public int getRight() {
		return _right;
	}
	
	public int getTop() {
		return _top;
	}
	
	public int getBottom() {
		return _bottom;
	}
	
	public double getCenterX() {
		return (_left+_right)/2.0;
	}
	
	public double getCenterY() {
		return (_top+_bottom)/2.0;
	}
	
	public void shift(int x, int y) {
		_left += x;
		_right += x;
		
		_top += y;
		_bottom += y;
	}

    public double getRatio() {
        return getRatio(getWidth(), getHeight());
    }

    public static double getRatio(int width, int height) {
        if(width > height)
            return width/((double)height);
        else
            return height/((double)width);
    }
    
    public static void expandAll(List<Room> rooms, int by) {
    	for(Room room:rooms)
    		room.expand(by);
    }
	
	public String toString() {
		return String.format("[L:%d,  R:%d, T:%d, B:%d]", _left, _right, _top, _bottom);
	}

    public int compareTo(Room room) {
        int d = room.getArea() - getArea();
        if(d == 0) {
            return (int) Math.signum((room.getCenterX()*room.getCenterX()+room.getCenterY()*room.getCenterY())
                    -(getCenterX()*getCenterX()+getCenterY()*getCenterY()));
        } else
            return d;
    }
}
