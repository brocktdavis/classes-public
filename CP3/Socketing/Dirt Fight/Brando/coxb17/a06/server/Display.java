package coxb17.a06.server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Display extends JFrame implements KeyListener {

	private char[][] world;
	private boolean next;

	public Display(char[][] world) {
		this.world = world;
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		addKeyListener(this);
		setPreferredSize(new Dimension(1024, 768));
		pack();
		setVisible(true);
	}

	public void update(char[][] world) {
		this.world = world;
		repaint();
	}

	public boolean isReady() {
		if (next) {
			next = false;
			return true;
		}
		return false;
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		double cellWidth = getWidth() / 100.0 - 1;
		double cellHeight = getHeight() / 30.0;
		double cellDim = Math.min(cellHeight, cellWidth);
		double worldWidth = cellDim * 100;
		double worldHeight = cellDim * 30;
		double xOffset = (getWidth() - worldWidth) / 2;
		double yOffset = getHeight() - worldHeight;
		for (int i = 0; i < world.length; i++) {
			for (int j = 0; j < world[i].length; j++) {
				double x = cellDim * j;
				double y = cellDim * i;
				if (world[i][j] == Game.DIRT) {
					g.setColor(new Color(0x8B4513));
				} else if (world[i][j] == Game.LEFT_CHAR) {
					g.setColor(new Color(0xFF0000));
				} else if (world[i][j] == Game.LEFT_FLAG) {
					g.setColor(new Color(0x8B0000));
				} else if (world[i][j] == Game.RIGHT_CHAR) {
					g.setColor(new Color(0x0000FF));
				} else if (world[i][j] == Game.RIGHT_FLAG) {
					g.setColor(new Color(0x00008B));
				} else {
					continue;
				}
				g.fillRect((int) (x + xOffset), (int) (y + yOffset), (int) cellDim, (int) cellDim);
			}
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void keyPressed(KeyEvent e) {
		next = true;
	}

	@Override
	public void keyReleased(KeyEvent e) {

	}
}
