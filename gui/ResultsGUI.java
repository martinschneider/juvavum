package juvavum.gui;

import java.awt.*;
import javax.swing.*;

/**
 * GUI für die Ergebnisausgabe
 * 
 * @author Martin Schneider
 */
public class ResultsGUI extends JFrame {

	private static final long serialVersionUID = 1L;
	private JLabel lblGrundy=new JLabel("Grundywert: ");
	private JLabel lblPos=new JLabel("Anzahl Spielpositionen: ");
	private JLabel lblBranching=new JLabel("Zeit: ");
	private JLabel lblTime=new JLabel("Zeit: ");

	/**
	 * @param h
	 * 						Höhe des Spielbretts
	 * @param w
	 * 						Breite des Spielbretts
	 * @param misere
	 * 						true, wenn Misere-Form
	 * @param symmetrien
	 * 						true, wenn Symmetrien berücksichtigt wurden
	 * @param normalformen
	 * 						true, wenn Normalformen berücksichtigt wurden
	 * @param grundy
	 * 						Grundywert der untersuchten Position
	 * @param pos
	 * 						Anzahl der Spielpositionen
	 * @param avgBranching
	 * 						durchschnittlicher Verzweigungsfaktor
	 * @param time
	 * 						Dauer der Berechnung
	 */
	public ResultsGUI(int h, int w, boolean misere, boolean symmetrien, boolean normalformen, int grundy, int pos, double avgBranching, String time) {
		String title="Ergebnisse: "+h+"x"+w;
		if (symmetrien) title+=" (Symmetrien berücksichtigt)";
		if (normalformen) title+=" (Normalformen berücksichtigt)";
		setTitle(title);
		if (misere) title+=" misere";
		this.setName(title);
		lblGrundy.setText("Grundywert der Startposition: "+grundy);
		lblPos.setText("Anzahl Spielpositionen: "+pos);
		lblBranching.setText("Durchschnittliche Verzweigung: "+Math.round(avgBranching*100.)/100.);
		lblTime.setText("Dauer der Berechnung: "+time);
		
		setSize(400, 100);
		setResizable(false);

		// layout
		GridBagLayout gbl = new GridBagLayout();
		Container c = getContentPane();
		c.removeAll();
		c.setLayout(gbl);

		//                              x y w h wx wy
		addComponent(c, gbl, lblGrundy, 0, 0, 1, 1, 1, 0);
		addComponent(c, gbl, lblPos, 0, 1, 1, 1, 1, 0);
		addComponent(c, gbl, lblBranching, 0, 2, 1, 1, 1, 0);
		addComponent(c, gbl, lblTime, 0, 3, 1, 1, 1, 0);
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