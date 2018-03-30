package com.eng.cber.na.renderer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

/**
 * A class that knows how to render icons for nodes in a network.
 * Symptoms are rendered in blue, vaccines are rendered in red.
 * This is independent of the usual methods of drawing nodes 
 * according to transformers.
 */
public class NodeIcon {

	@SuppressWarnings("serial")
	public static class SYMIcon extends JPanel {		
		private boolean selected;
		private Color paint;
		public SYMIcon(boolean selected) {
			this(selected,Color.BLUE);
		}
		public SYMIcon(boolean selected, Color paint) {
			this.selected = selected;
			this.paint = paint;
		}
		@Override
		public void paint(Graphics g) {
			int wOff = getWidth()/2;
			int hOff = getHeight()/2;
			int h = (int)(0.9*(Math.min(getWidth(), getHeight())/2));
			g.setColor(selected ? Color.LIGHT_GRAY : Color.WHITE);
			g.fillRect(0, 0, getWidth(), getHeight());
			g.setColor(paint); 
			g.fillOval(wOff - h, hOff - h, 2 * h, 2 * h);
		}
	}
	
	@SuppressWarnings("serial")
	public static class VAXIcon extends JPanel {
		private boolean selected;
		private Color paint;
		public VAXIcon(boolean selected) {
			this(selected, Color.RED);
		}
		public VAXIcon(boolean selected, Color paint) {
			this.selected = selected;
			this.paint = paint;
		}
		@Override
		public void paint(Graphics g) {
			int wOff = getWidth()/2;
			int hOff = getHeight()/2;
			int h = (int)(0.9*(Math.min(getWidth(), getHeight())/2));
			g.setColor(selected ? Color.LIGHT_GRAY : Color.WHITE);
			g.fillRect(0, 0, getWidth(), getHeight());
			g.setColor(paint);
			g.fillRect(wOff - h, hOff - h, 2 * h, 2 * h);
		}
	}

	@SuppressWarnings("serial")
	public static class SETIcon extends JPanel {
		private boolean selected;
		private Color paint;
		public SETIcon(boolean selected, Color paint) {
			this.selected = selected;
			this.paint = paint;
		}
		@Override
		public void paint(Graphics g) {
			int wOff = getWidth()/2;
			int hOff = getHeight()/2;
			int h = (int)(0.9*(Math.min(getWidth(), getHeight())/2));
			
			int[] x = {wOff - h, wOff, wOff + h, wOff};
			int[] y = {hOff, hOff - h, hOff, hOff + h};
			
			g.setColor(selected ? Color.LIGHT_GRAY : Color.WHITE);
			g.fillRect(0, 0, getWidth(), getHeight());
			g.setColor(paint);
			g.fillPolygon(x, y, x.length);
		}
		
		@Override
		public Dimension getPreferredSize() {
			return new Dimension(16,16);
		}
	}
}
