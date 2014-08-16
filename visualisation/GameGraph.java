package juvavum.visualisation;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import prefuse.Constants;
import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.assignment.DataColorAction;
import prefuse.action.assignment.FontAction;
import prefuse.action.layout.*;
import prefuse.action.layout.graph.*;
import prefuse.activity.Activity;
import prefuse.controls.*;
import prefuse.data.Graph;
import prefuse.data.io.DataIOException;
import prefuse.data.io.GraphMLReader;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.LabelRenderer;
import prefuse.render.EdgeRenderer;
import prefuse.util.ColorLib;
import prefuse.util.FontLib;
import prefuse.visual.VisualItem;

/**
 * GUI zur Darstellung eines Spielegraphen
 * verwendet das prefuse-Paket
 *  
 * @author Martin Schneider
 */
public class GameGraph extends JFrame {

	private JComboBox cmbLayouts;

	private JComboBox cmbColors;
	
	private JLabel lblLayouts=new JLabel("Layout: ");
	
	private JLabel lblColors=new JLabel("Färbung: ");

	private Visualization vis;

	/**
	 * @param filename
	 * 					Dateiname der XML-Datei, die den Graph repräsentiert
	 * @param maxGrundy
	 * 					maximaler Grundywert
	 */
	public GameGraph(String filename, int maxGrundy) {

		super("Spielegraph");

		// Daten laden
		Graph graph = null;
		try {
			graph = new GraphMLReader().readGraph(filename);
		} catch (DataIOException e) {
			e.printStackTrace();
			System.err.println("Fehler beim Laden des Graphen.");
			System.exit(1);
		}

		vis = new Visualization();
		vis.add("graph", graph);
		vis.setInteractive("graph.edges", null, false);

		LabelRenderer r = new LabelRenderer("description");
		r.setRoundedCorner(8, 8);

		DefaultRendererFactory drf = new DefaultRendererFactory(r);
		drf.setDefaultEdgeRenderer(new EdgeRenderer(Constants.EDGE_TYPE_CURVE,
				Constants.EDGE_ARROW_FORWARD));
		vis.setRendererFactory(drf);

		// Färbung des Graphen
		
		// nach Grundywerten
		int max = maxGrundy;
		int[] paletteGrundy = new int[max + 1];
		for (int i = 0; i <= max; i++) {
			paletteGrundy[i] = ColorLib.rgb(255, (i * 200) / max, (i * 200)
					/ max);
		}
		DataColorAction fillGrundy = new DataColorAction("graph.nodes",
				"grundy", Constants.NUMERICAL, VisualItem.FILLCOLOR,
				paletteGrundy);
		
		// nach Art der Spielpositionen (Startecke, Endecken, übrige Ecken)
		int[] palettePostype = { ColorLib.rgb(255, 0, 0),
				ColorLib.rgb(255, 127, 127), ColorLib.rgb(0, 0, 255) };
		DataColorAction fillPostype = new DataColorAction("graph.nodes",
				"postype", Constants.NOMINAL, VisualItem.FILLCOLOR,
				palettePostype);
		
		// nach Art der Spielpositionen (Gewinnecken, Verlustecken)
		int[] paletteWinedge = { ColorLib.rgb(0, 0, 255),
				ColorLib.rgb(255, 0, 0)};
		DataColorAction fillWinedge = new DataColorAction("graph.nodes",
				"winedge", Constants.NOMINAL, VisualItem.FILLCOLOR,
				paletteWinedge);

		// Text schwarz, Kanten und Pfeile grau
		ColorAction text = new ColorAction("graph.nodes", VisualItem.TEXTCOLOR,
				ColorLib.gray(0));
		ColorAction edges = new ColorAction("graph.edges",
				VisualItem.STROKECOLOR, ColorLib.gray(100));
		ColorAction arrows = new ColorAction("graph.edges",
				VisualItem.FILLCOLOR, ColorLib.gray(100));
		
		// Courier New als Schriftart
		// dadurch werden die Spielpositionen gut dargestellt
		FontAction fonts = new FontAction();
		fonts.setDefaultFont(FontLib.getFont("Courier New", 10));

		ActionList color = new ActionList();
		color.add(text);
		color.add(edges);
		color.add(arrows);
		color.add(fonts);

		// Färbung nach Grundywerten
		ActionList color1 = new ActionList();
		color1.add(fillGrundy);
		color1.add(new RepaintAction());

		// Färbung nach Eckentyp
		ActionList color2 = new ActionList();
		color2.add(fillPostype);
		color2.add(new RepaintAction());
		
		// Färbung nach Eckentyp (Gewinn-, Verlustecken)
		ActionList color3 = new ActionList();
		color3.add(fillWinedge);
		color3.add(new RepaintAction());

		ActionList repaint = new ActionList();
		repaint.add(new RepaintAction());

		vis.putAction("color", color);
		vis.putAction("color1", color1);
		vis.putAction("color2", color2);
		vis.putAction("color3", color3);
		vis.putAction("repaint", repaint);

		// verschiedene Graphlayouts
		ActionList layout1 = new ActionList(Activity.DEFAULT_STEP_TIME);
		layout1.add(new FruchtermanReingoldLayout("graph"));
		layout1.add(new RepaintAction());
		vis.putAction("layout1", layout1);

		ActionList layout2 = new ActionList(Activity.DEFAULT_STEP_TIME);
		layout2.add(new NodeLinkTreeLayout("graph"));
		layout2.add(new RepaintAction());
		vis.putAction("layout2", layout2);

		ActionList layout3 = new ActionList(Activity.DEFAULT_STEP_TIME);
		layout3.add(new RadialTreeLayout("graph"));
		layout3.add(new RepaintAction());
		vis.putAction("layout3", layout3);

		ActionList layout4 = new ActionList(Activity.DEFAULT_STEP_TIME);
		layout4.add(new RandomLayout("graph"));
		layout4.add(new RepaintAction());
		vis.putAction("layout4", layout4);

		String[] layouts = { "Fruchterman Reingold Layout",
				"Node Link Tree Layout", "Radial Tree Layout", "Random Layout" };
		cmbLayouts = new JComboBox(layouts);
		cmbLayouts.setSelectedIndex(2);

		String[] coloring = { "Grundywerte",
				"Eckentyp (Startecke [rot], Zielecken [blau], übrige Ecken [rosa])", "Eckentyp (Gewinnecken [rot], Verlustecken [blau])" };
		cmbColors = new JComboBox(coloring);
		cmbColors.setSelectedIndex(1);

		// ActionListener
		cmbLayouts.addActionListener(alLayout);
		cmbColors.addActionListener(alColor);
		
		Display d = new Display(vis);
		d.addControlListener(new DragControl());
		d.addControlListener(new ZoomControl());
		d.addControlListener(new PanControl());
		//d.addControlListener(new ToolTipControl("grundy"));
		d.addControlListener(new NeighborHighlightControl());
		
		d.setSize(800,600);

		// Frame layout
		GridBagLayout gbl = new GridBagLayout();
		Container c = getContentPane();
		c.removeAll();
		c.setLayout(gbl);
		// x y w h wx wy
		addComponent(c, gbl, d, 0, 0, 2, 1, 1, 1);
		addComponent(c, gbl, lblLayouts, 0, 1, 1, 1, 0, 0);
		addComponent(c, gbl, cmbLayouts, 1, 1, 1, 1, 0, 0);
		addComponent(c, gbl, lblColors, 0, 2, 1, 1, 0, 0);
		addComponent(c, gbl, cmbColors, 1, 2, 1, 1, 0, 0);

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		pack();
		
		vis.run("color");

		vis.run("color2"); // Färbung nach Eckentyp (Startecke, Zielecke, übrige Ecken)
		vis.run("layout3"); // Radial Tree Layout
		vis.run("font"); // Courier
		
		// Frame am Bildschirm zentrieren
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

	private ActionListener alLayout = new ActionListener() {
		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			int layoutIndex = cmbLayouts.getSelectedIndex() + 1;
			vis.run("layout" + layoutIndex);
		}
	};

	private ActionListener alColor = new ActionListener() {
		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			int colorIndex = cmbColors.getSelectedIndex() + 1;
			vis.run("color" + colorIndex);
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