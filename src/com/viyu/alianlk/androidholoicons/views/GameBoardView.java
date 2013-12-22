package com.viyu.alianlk.androidholoicons.views;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/**
 * 
 * @author Viyu_Lu
 * 
 */
public class GameBoardView extends View {
	// count of x way
	protected int X_COUNT = 0;
	// count of y way
	protected int Y_COUNT = 0;
	// bitmap icons will be used
	protected Bitmap[] icons = null;
	// matrix
	protected int[][] xYMatrix = null;
	// size of icon
	protected int iconSize = 0;
	protected int expanWhenSelected = 0;
	// path to lian
	private Point[] path = null;
	// the point of icons selected
	protected List<Point> selectedIconsPointList = new ArrayList<Point>();
	//
	protected Paint paint = new Paint();
	//
	protected Rect rect = new Rect();

	public GameBoardView(Context context, AttributeSet atts) {
		super(context, atts);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (xYMatrix == null)
			return;

		// draw path
		if (path != null && path.length >= 2) {
			for (int i = 0; i < path.length - 1; i++) {
				Paint paint = new Paint();
				paint.setColor(Color.LTGRAY);
				paint.setStyle(Paint.Style.STROKE);
				paint.setStrokeWidth(3);
				Point p1 = indextoScreen(path[i].x, path[i].y);
				Point p2 = indextoScreen(path[i + 1].x, path[i + 1].y);
				canvas.drawLine(p1.x + iconSize / 2, p1.y + iconSize / 2, p2.x + iconSize / 2, p2.y + iconSize / 2, paint);
			}
			Point p = path[0];
			xYMatrix[p.x][p.y] = 0;
			p = path[path.length - 1];
			xYMatrix[p.x][p.y] = 0;
			selectedIconsPointList.clear();
			path = null;
		}
		// draw icons
		for (int x = 0; x < xYMatrix.length; x += 1) {
			for (int y = 0; y < xYMatrix[x].length; y += 1) {
				if (xYMatrix[x][y] > 0) {
					Point p = indextoScreen(x, y);
					canvas.drawBitmap(icons[xYMatrix[x][y]], p.x, p.y, null);
				}
			}
		}

		// draw selected icons
		for (Point position : selectedIconsPointList) {
			Point p = indextoScreen(position.x, position.y);
			if (xYMatrix[position.x][position.y] >= 1) {
				paint.setColor(Color.GREEN);
				paint.setStyle(Paint.Style.STROKE);
				paint.setStrokeWidth(3);
				rect.set(p.x - expanWhenSelected, p.y - expanWhenSelected, p.x + iconSize + expanWhenSelected, p.y + iconSize + expanWhenSelected);
				canvas.drawRect(rect, paint);
				canvas.drawBitmap(icons[xYMatrix[position.x][position.y]], null, rect, null);
			}
		}
	}

	public void drawLine(Point[] path) {
		this.path = path;
		this.invalidate();
	}

	public Point indextoScreen(int x, int y) {
		return new Point(x * iconSize, y * iconSize);
	}

	public Point screenToindex(int x, int y) {
		int ix = x / iconSize;
		int iy = y / iconSize;
		if (ix < X_COUNT && iy < Y_COUNT) {
			return new Point(ix, iy);
		} else {
			return new Point(0, 0);
		}
	}
}
