package juvavum.gui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import juvavum.analyse.*;

/**
 * Grafische Darstellung eines Spielbretts
 * 
 * @author Martin Schneider
 */
public class BoardGUI extends JWindow {
	
	private static final long serialVersionUID = 1L;
	
	private int w, h;

	private Board b;

	public BoardGUI() {
	}

	/**
	 * @param b
	 * 			Spielbrett
	 */
	public BoardGUI(Board b) {
		this.w = b.getWidth();
		this.h = b.getHeight();
		this.b = b;
		setSize(w * 50 + 20, h * 50 + 40);
		addMouseListener(ml);
		addWindowListener(wa);
	}

	/**
	 * @param b
	 * 			Spielbrett
	 */
	public void setBoard(Board b) {
		this.b = b;
		repaint();
	}

	/**
	 * @return
	 * 			Spielbrett
	 */
	public Board getBoard() {
		return b;
	}

	/* (non-Javadoc)
	 * @see java.awt.Container#paint(java.awt.Graphics)
	 */
	public void paint(Graphics g) {
		for (int i = 1; i <= w; i++) {
			for (int j = 1; j <= h; j++) {

				if (b.isSet(i, j)) {
					g.setColor(Color.black);
					g.fillRect((i - 1) * 50 + 10, (j - 1) * 50 + 30, 50, 50);
				}

				else {
					g.setColor(Color.white);
					g.fillRect((i - 1) * 50 + 10, (j - 1) * 50 + 30, 50, 50);
				}
				g.setColor(Color.black);
				g.drawRect((i - 1) * 50 + 10, (j - 1) * 50 + 30, 50, 50);
			}
		}
	}

	private WindowAdapter wa = new WindowAdapter() {
		/* (non-Javadoc)
		 * @see java.awt.event.WindowAdapter#windowClosing(java.awt.event.WindowEvent)
		 */
		public void windowClosing(WindowEvent e) {
			e.getWindow().dispose();
		}
	};

	private MouseListener ml = new MouseListener() {
		/* (non-Javadoc)
		 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
		 */
		public void mousePressed(MouseEvent e) {
			if (e.isAltDown()) dispose();
			int x = (e.getX() - 10) / 50;
			int y = (e.getY() - 30) / 50;
			if (b.isSet(x + 1, y + 1))
				b.clear(x + 1, y + 1);
			else
				b.set(x + 1, y + 1);
			repaint();
		}

		/* (non-Javadoc)
		 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
		 */
		public void mouseReleased(MouseEvent e) {
		}

		/* (non-Javadoc)
		 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
		 */
		public void mouseEntered(MouseEvent e) {
		}

		/* (non-Javadoc)
		 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
		 */
		public void mouseExited(MouseEvent e) {
		}

		/* (non-Javadoc)
		 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
		 */
		public void mouseClicked(MouseEvent e) {
		}
	}; 
}