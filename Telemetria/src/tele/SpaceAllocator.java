package tele;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

import common.Point;

public class SpaceAllocator {
	private List<Point> setp = new ArrayList<>();

	public Point calcPosition(GC gc, String s, Point p) {
		
		org.eclipse.swt.graphics.Point stringSpace = gc.stringExtent(s);
		int xSpace = stringSpace.x;
		int ySpace = stringSpace.y;

		Point spacePoint = new Point(xSpace, ySpace);

		if (check(p, spacePoint)) {
			setp.add(p);

			return p;
		}
		Point qq = Point.copy(p);
		while (true) {
			qq.setY(qq.getY() - 5);

			if (check(qq, spacePoint)) {
				setp.add(qq);

				return qq;
			}
		}
	}

	private boolean check(Point p, Point spacePoint) {
		for (Point q : setp) {
			if (overlap(p, q, spacePoint))
				return false;

		}
		return true;
	}

	private static boolean overlap(Point p1, Point p2, Point extend) {
	
		Rectangle r1 = new Rectangle(p1.getX(), p1.getY(),  extend.getX(), extend.getY());
		Rectangle r2 = new Rectangle(p2.getX(), p2.getY(),  extend.getX(), extend.getY());

		return r1.intersects(r2);
	}
}
