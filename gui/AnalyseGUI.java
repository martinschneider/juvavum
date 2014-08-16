package juvavum.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

import juvavum.analyse.CramAnalyse;
import juvavum.analyse.DJAnalyse;
import juvavum.analyse.HoleAnalyse;
import juvavum.analyse.TJAnalyse;
import juvavum.analyse.JAnalyse;
import juvavum.analyse.JAnalyseNormalForm;
import juvavum.analyse.Board;

/**
 * GUI für das Analyseprogramm
 * 
 * @author Martin Schneider
 */
public class AnalyseGUI extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	private JButton btAnalyse = new JButton("Spiel analysieren");

	private JButton btAnalyse2 = new JButton("Position analysieren");
	
	private JComboBox cmbGames;

	private JTextField txtHeight = new JTextField("3");

	private JTextField txtWidth = new JTextField("3");

	private JLabel lblWidth = new JLabel("Breite:");

	private JLabel lblHeight = new JLabel("Höhe:");

	private JCheckBox chkMisere = new JCheckBox("Misere");

	private JCheckBox chkTextOnly = new JCheckBox("Baumansicht ausgeben");

	private JCheckBox chkSymmetrien = new JCheckBox("Symmetrien");
		private JCheckBox chkNormalformen = new JCheckBox("Normalformen verwenden");;
	
	private JCheckBox chkPositionen = new JCheckBox("Nur Positionen zählen");
	
	private JCheckBox chkSpielegraph = new JCheckBox("Spielegraph anzeigen");
	
	private boolean misere = false;

	private boolean symmetrien = true;

	private boolean positionen = false;

	private boolean textOnly = false;
	
	private boolean graph = false;
	
	private boolean normalformen = false;
	
	private int gameType = 2; // JUV=1, DJUV=2, CRAM=3, HOLE=4, TJUV=5
	
	private BoardGUI boardGUI;

	/**
	 * Konstruktor 
	 */
	public AnalyseGUI() {
		super("Juvavum Analyse Tool");
		chkSpielegraph.setToolTipText("Achtung: nur für kleine Spielfeldgrößen verwenden!");
		chkSymmetrien.doClick();
		chkNormalformen.setEnabled(false);
		btAnalyse.addActionListener(alAnalyse);
		btAnalyse2.addActionListener(alAnalyse2);
		chkMisere.addActionListener(alMisere);
		chkSymmetrien.addActionListener(alSymmetrien);
		chkTextOnly.addActionListener(alTextOnly);
		String[] games = { "Juvavum", "Domino Juvavum", "Triomino Juvavum","Cram" , "Hole Cram",};
		cmbGames = new JComboBox(games);
		cmbGames.setSelectedIndex(1);
		cmbGames.addActionListener(alGames);
		chkSpielegraph.addActionListener(alGraph);
		chkNormalformen.addActionListener(alNormal);
		cmbGames.addKeyListener(kl);
		btAnalyse.addKeyListener(kl);
		btAnalyse2.addKeyListener(kl);
		txtHeight.addKeyListener(kl);
		txtWidth.addKeyListener(kl);
		lblWidth.addKeyListener(kl);
		lblHeight.addKeyListener(kl);
		chkMisere.addKeyListener(kl);
		chkTextOnly.addKeyListener(kl);
		chkSymmetrien.addKeyListener(kl);
		chkPositionen.addKeyListener(kl);
		chkSpielegraph.addKeyListener(kl);
		chkNormalformen.addKeyListener(kl);
		addKeyListener(kl);

		setSize(350, 185);
		
		addWindowListener
	       (new WindowAdapter() {
	           public void windowClosing(WindowEvent e) {
	              e.getWindow().dispose();
	              try {
						boardGUI.dispose();
					} catch (RuntimeException e1) {
					}
	              }
	           }
	       );

		// layout
		GridBagLayout gbl = new GridBagLayout();
		Container c = getContentPane();
		c.removeAll();
		c.setLayout(gbl);
		// x y w h wx wy
		addComponent(c, gbl, lblHeight, 0, 0, 1, 1, 1, 0);
		addComponent(c, gbl, txtHeight, 1, 0, 1, 1, 1, 0);

		addComponent(c, gbl, lblWidth, 0, 1, 1, 1, 1, 0);
		addComponent(c, gbl, txtWidth, 1, 1, 1, 1, 1, 0);

		addComponent(c, gbl, cmbGames, 0, 2, 1, 1, 1, 0);
		addComponent(c, gbl, chkMisere, 1, 2, 1, 1, 1, 0);
		
		addComponent(c, gbl, chkSymmetrien, 0, 3, 1, 1, 1, 0);
		addComponent(c, gbl, chkNormalformen, 1, 3, 1, 1, 1, 0);
		
		addComponent(c, gbl, chkTextOnly, 0, 4, 1, 1, 1, 0);
		addComponent(c, gbl, chkSpielegraph, 1, 4, 2, 1, 1, 0);
		
		addComponent(c, gbl, btAnalyse, 0, 5, 1, 1, 1, 1);
		addComponent(c, gbl, btAnalyse2, 1, 5, 1, 1, 1, 1);

		setResizable(true);

		// Fenster positionieren
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = getSize();
		if (frameSize.height > screenSize.height) {
			frameSize.height = screenSize.height;
		}
		if (frameSize.width > screenSize.width) {
			frameSize.width = screenSize.width;
		}
		setLocation(screenSize.width - frameSize.width, 0);

		boardGUI = new BoardGUI(new Board(3, 3));

		setVisible(true);
	}

	private ActionListener alAnalyse = new ActionListener() {
		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			int fileOutput=2;
			if (!positionen) fileOutput = showDialog();
			int h = new Integer(txtHeight.getText()).intValue();
			int w = new Integer(txtWidth.getText()).intValue();
			if (gameType==1)
				if (normalformen)
					new JAnalyseNormalForm(h, w, misere, true, !textOnly, fileOutput, graph);
				else
					new JAnalyse(h, w, misere, symmetrien, !textOnly, fileOutput, graph);
			else if (gameType==2)
				new DJAnalyse(h, w, misere, symmetrien, !textOnly, fileOutput, graph);
			else if (gameType==3)
				new CramAnalyse(h, w, misere, symmetrien, !textOnly, fileOutput, graph);
			else if (gameType==4)
				new HoleAnalyse(h, w, misere, symmetrien, !textOnly, fileOutput, graph);
			else if (gameType==5)
				new TJAnalyse(h, w, misere, symmetrien, !textOnly, fileOutput, graph);
		}
	};

	private ActionListener alAnalyse2 = new ActionListener() {
		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			int h = new Integer(txtHeight.getText()).intValue();
			int w = new Integer(txtWidth.getText()).intValue();
			if (!boardGUI.isVisible() || boardGUI.getBoard().getHeight() != h
					|| boardGUI.getBoard().getWidth() != w) {
				boardGUI.dispose();
				boardGUI = new BoardGUI(new Board(h, w));
				boardGUI.setLocation(getX()-boardGUI.getWidth(), getY());
				boardGUI.setVisible(true);
			} else {
				int fileOutput=0;
				fileOutput = showDialog();
				if (gameType==1)
					if (normalformen)
						new JAnalyseNormalForm(boardGUI.getBoard(), misere, true, !textOnly, fileOutput, graph);
					else
						new JAnalyse(boardGUI.getBoard(), misere, symmetrien, !textOnly, fileOutput, graph);
				else if (gameType==2)
					new DJAnalyse(boardGUI.getBoard(), misere, symmetrien, !textOnly, fileOutput, graph);
				else if (gameType==3)
					new CramAnalyse(boardGUI.getBoard(), misere, symmetrien, !textOnly, fileOutput, graph);
				else if (gameType==4)
					new HoleAnalyse(boardGUI.getBoard(), misere, symmetrien, !textOnly, fileOutput, graph);
				else if (gameType==5)
					new TJAnalyse(boardGUI.getBoard(), misere, symmetrien, !textOnly, fileOutput, graph);
			}
		}
	};

	private ActionListener alGames = new ActionListener() {
		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			if (cmbGames.getSelectedItem().equals("Juvavum")){
				gameType = 1;
				chkNormalformen.setEnabled(true);
			}
			else if (cmbGames.getSelectedItem().equals("Domino Juvavum")){
				gameType = 2;
				chkSymmetrien.setEnabled(true);
				if (normalformen) chkNormalformen.doClick();
				chkNormalformen.setEnabled(false);
			}
			else if (cmbGames.getSelectedItem().equals("Cram")){
				gameType = 3;
				chkSymmetrien.setEnabled(true);
				if (normalformen) chkNormalformen.doClick();
				chkNormalformen.setEnabled(false);
			}
			else if (cmbGames.getSelectedItem().equals("Hole Cram")){
				gameType = 4;
				chkSymmetrien.setEnabled(true);
				if (normalformen) chkNormalformen.doClick();
				chkNormalformen.setEnabled(false);
			}
			else if (cmbGames.getSelectedItem().equals("Triomino Juvavum")){
				gameType = 5;
				chkSymmetrien.setEnabled(true);
				if (normalformen) chkNormalformen.doClick();
				chkNormalformen.setEnabled(false);
			}
		}
	};
	
	private ActionListener alMisere = new ActionListener() {
		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			misere = !misere;
		}
	};

	private ActionListener alSymmetrien = new ActionListener() {
		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			symmetrien = !symmetrien;
		}
	};

	private ActionListener alTextOnly = new ActionListener() {
		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			textOnly = !textOnly;
		}
	};
	
	private ActionListener alGraph = new ActionListener() {
		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			graph = !graph;
		}
	};
	
	private ActionListener alNormal = new ActionListener() {
		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			normalformen = !normalformen;
			if (normalformen){
				if (!symmetrien) chkSymmetrien.doClick();
				chkSymmetrien.setEnabled(false);
			}
			else chkSymmetrien.setEnabled(true);
		}
	};
	
	private KeyListener kl = new KeyListener() {
		/* (non-Javadoc)
		 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
		 */
		public void keyPressed(KeyEvent e) {
			try {
			if (e.getKeyCode()==27)	boardGUI.dispose();
			} catch (RuntimeException e1) {
			};
		}

		/* (non-Javadoc)
		 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
		 */
		public void keyReleased(KeyEvent e) {
		}

		/* (non-Javadoc)
		 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
		 */
		public void keyTyped(KeyEvent e) {

		}

	};

	
	/**
	 * Welche Positionen sollen in einer Textdatei gespeichert werden?
	 * 
	 * @return
	 * 			0 - alle Positionen
	 * 			1 - Positionen mit Grundywert 0
	 * 			2 - keine
	 */
	public int showDialog() {
		Object[] options = { "Alle", "Positionen mit Grundywert 0", "Nichts speichern" };
		return  JOptionPane.showOptionDialog(this,
				"Welche Positionen sollen mit ihren Grundywerten in eine Textdatei gespeichert werden?",
				"Ausgabe in Textdatei", JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, options, options[2]);
	}
	
	static void addComponent(Container cont, GridBagLayout gbl, Component c,
			int x, int y, int width, int height, double weightx, double weighty) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.gridwidth = width;
		gbc.gridheight = height;
		gbc.weightx = weightx;
		gbc.weighty = weighty;
		gbl.setConstraints(c, gbc);
		cont.add(c);
	}
}