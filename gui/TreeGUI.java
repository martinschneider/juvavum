package juvavum.gui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;

import juvavum.analyse.Board;
import juvavum.analyse.Position;
import juvavum.analyse.PositionTreeModel;

/**
 * GUI für die Ausgabe des Spielbaums
 * 
 * @author Martin Schneider
 */
public class TreeGUI extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	private JTree tree;

	private JTextField currentSelectionField;

	private BoardGUI boardGUI;

	private ResultsGUI resultsGUI;

	/**
	 * @param root
	 * 				Wurzel des Baums
	 * @param gui
	 * 				Verweis auf GUI-Objekt
	 */
	public TreeGUI(Position root, ResultsGUI gui) {
		setTitle("Juvavum Analyse Tool");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		Container content = getContentPane();

		tree = new JTree(new PositionTreeModel(root));
		tree.addTreeSelectionListener(tsl);
		tree.addMouseListener(ml);

		// Icons für die einzelnen Nodes
		DefaultTreeCellRenderer rend = new DefaultTreeCellRenderer();
		Icon customOpenIcon = new ImageIcon(this.getClass().getResource("/juvavum/images/domino.gif"));
		Icon customClosedIcon = new ImageIcon(this.getClass().getResource("/juvavum/images/domino.gif"));
		Icon customLeafIcon = new ImageIcon(this.getClass().getResource("/juvavum/images/domino2.gif"));
		rend.setOpenIcon(customOpenIcon);
		rend.setClosedIcon(customClosedIcon);
		rend.setLeafIcon(customLeafIcon);
		tree.setCellRenderer(rend);

		content.add(new JScrollPane(tree), BorderLayout.CENTER);
		currentSelectionField = new JTextField("");
		content.add(currentSelectionField, BorderLayout.SOUTH);

		boardGUI = new BoardGUI(root.getBoard());
		setSize(boardGUI.getWidth(), 300);
		setResizable(false);

		this.resultsGUI = gui;
		if (getWidth() < resultsGUI.getWidth())
			setSize(resultsGUI.getWidth(), 300);
		setLocation(0, 0);
		resultsGUI.setLocation(0, getHeight());

		boardGUI.setLocation(0, resultsGUI.getY() + resultsGUI.getHeight());

		boardGUI.setVisible(true);
		resultsGUI.setVisible(true);

		setVisible(true);

		addWindowListener(wa);
	}

	private TreeSelectionListener tsl = new TreeSelectionListener() {
		public void valueChanged(TreeSelectionEvent event) {
			if (tree.getLastSelectedPathComponent() != null)
				currentSelectionField.setText(tree
						.getLastSelectedPathComponent().toString());
		}
	};

	private WindowAdapter wa = new WindowAdapter() {
		public void windowClosing(WindowEvent e) {
			boardGUI.dispose();
			resultsGUI.dispose();
			e.getWindow().dispose();
		}
	};

	private MouseListener ml = new MouseListener() {
		public void mousePressed(MouseEvent e) {
		}

		public void mouseReleased(MouseEvent e) {
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}

		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() == 1) {
				Position pos = (Position) tree.getLastSelectedPathComponent();
				try {
					if (pos != null) {
						new Board(pos.getBoard()).output();
						boardGUI.setBoard(pos.getBoard());
						boardGUI.setVisible(true);
					}
				} catch (Exception e1) {
					System.out.println(e1.getMessage());
				}
			}
		}
	
	};
}