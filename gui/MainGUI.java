package juvavum.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

/**
 * GUI des Hauptprogramms
 * 
 * @author Martin Schneider
 */
public class MainGUI extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	private JButton btAnalyse = new JButton("JAT - Juvavum Analyse Tool");

	private JButton btPlay = new JButton("Juvavum spielen");
	
	private JLabel lblInfo = new JLabel("(c) Martin Schneider, 2004-2009");
	
	/**
	 * Konstruktor 
	 */
	public MainGUI() {
		super("Juvavum");
		setDefaultLookAndFeelDecorated(true);

		btAnalyse.addActionListener(alAnalyse);
		btPlay.addActionListener(alPlay);
		setSize(250, 250);
		setResizable(false);

		// layout
		GridBagLayout gbl = new GridBagLayout();
		Container c = getContentPane();
		c.removeAll();
		c.setLayout(gbl);
		// x y w h wx wy
		addComponent(c, gbl, btPlay, 0, 0, 1, 1, 1, 1);
		addComponent(c, gbl, btAnalyse, 0, 1, 1, 1, 1, 1);
		addComponent(c, gbl, lblInfo, 0, 2, 1, 1, 0, 0);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// center frame
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = getSize();
		if (frameSize.height > screenSize.height) {
			frameSize.height = screenSize.height;
		}
		if (frameSize.width > screenSize.width) {
			frameSize.width = screenSize.width;
		}
		setLocation((screenSize.width - frameSize.width) / 2,
				(screenSize.height - frameSize.height) / 2);
		
		setVisible(true);
	}

	private ActionListener alAnalyse = new ActionListener() {
		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			new AnalyseGUI();
		}
	};

	private ActionListener alPlay = new ActionListener() {
		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			new GameOptionsGUI();
		}
	};
	
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