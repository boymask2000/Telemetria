package tele;

import java.util.HashSet;
import java.util.Set;

import common.Point;

public class SpaceAllocator {
	private Set<Point> set = new HashSet<>();

	public Point calcPosition(Point p) {
		if (check(p)) {
			set.add(p);
			return p;
		}
		while (true) {
			p.setY(p.getY() - 5);
			p.setX(p.getX() + 5);
			if (check(p)) {
				set.add(p);
				return p;
			}
		}
//		return null;
	}

	private int dist(Point p1, Point p2) {
		return (p1.getX() - p2.getX()) * (p1.getX() - p2.getX()) + (p1.getY() - p2.getY()) * (p1.getY() - p2.getY());
	}

	private boolean check(Point p) {
		for (Point q : set) {
			if (dist(p, q) < 500)
				return false;

		}
		return true;
	}
}
