package juvavum.gui;

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;

import javax.swing.*;

import juvavum.game.*;
import juvavum.game.players.BetterSymmetricPlayer;
import juvavum.game.players.PerfectEndgamePlayer;
import juvavum.game.players.PerfectPlayer;
import juvavum.game.players.AllroundPlayer;
import juvavum.game.players.Player;
import juvavum.game.players.RandomPlayer;
import juvavum.game.players.SymmetricPlayer;
import juvavum.analyse.*;

/**
 * GUI für das Spielprogramm
 * 
 * @author Martin Schneider
 */
public class GameOptionsGUI extends JFrame {
	private static final long serialVersionUID = 1L;
	private JComboBox cmbHeight;
	private JComboBox cmbWidth;
	private JComboBox cmbPlayers;
	private JComboBox cmbGames;
	private JCheckBox chkHumanStarts = new JCheckBox("Ersten Zug machen");
	private JCheckBox chkMisere = new JCheckBox("Misere");
	private JCheckBox chkRandom = new JCheckBox("Mit leerem Spielfeld beginnen");
	private JLabel lblHeight = new JLabel("Spielfeldhöhe");
	private JLabel lblWidth = new JLabel("Spielfeldbreite");
	private JLabel lblOpponent = new JLabel("Computergegner");
	private JLabel lblGames = new JLabel("Spiel");
	private JButton btnPlay = new JButton("Spielstart");
	private JLabel lblName = new JLabel("Spielername:");
	private JLabel lblScore = new JLabel();
	private JTextField txtName = new JTextField();
	private boolean humanStarts = true;
	private boolean misere = false;
	private boolean random = false;
	final String cramRules = "<html><body><br><b>Spielregeln</b><br><i>Cram</i>: Jeder Zug besteht darin genau zwei (horizontal oder vertikal) benachbarte Felder zu belegen. Wer den letzten Zug macht, gewinnt.<br></body></html>";
	final String juvavumRules = "<html><body><br><b>Spielregeln</b><br><i>Juvavum</i>: Jeder Zug besteht darin beliebig viele Felder in einer Zeile oder Spalte zu belegen. Wer den letzten Zug macht, gewinnt.<br></body></html>";
	final String djRules = "<html><body><br><b>Spielregeln</b><br><i>Domino Juvavum</i>: Jeder Zug besteht darin beliebig viele Dominos in eine Zeile oder Spalte zu legen (um ein Domino zu setzen, werden zwei benachbarte Felder belegt). Wer den letzten Zug macht, gewinnt.<br></body></html>";
	final String tjRules = "<html><body><br><b>Spielregeln</b><br><i>3-Juvavum</i>: Jeder Zug besteht darin beliebig viele Tripel von benachbarten Feldern in einer Zeile oder Spalte zu besetzen. Wer den letzten Zug macht, gewinnt.<br></body></html>";
	final String holeRules = "<html><body><br><b>Spielregeln</b><br><i>Hole Cram</i>: Jeder Zug besteht darin zwei Felder in einer Zeile oder Spalte zu belegen, zwischen denen genau ein Feld liegt. Wer den letzten Zug macht, gewinnt.<br></body></html>";
	final String allroundPlayer = "<html><body><br><b>AllroundPlayer</b>kombiniert die Stärken aller anderen Spieler.</body></html>";
	final String randomPlayer = "<html><body><br><b>RandomPlayer</b> setzt zufällig.</body></html>";
	final String symmetricPlayer = "<html><body><br><b>SymmetricPlayer</b> und <b>BetterSymmetricPlayer</b> versuchen mit Hilfe einer Spiegelungsstrategie zu gewinnen (nicht für Misere-Spiele).</body></html>";
	final String perfectPlayer = "<html><body><br><b>PerfectPlayer</b> verwendet mit Hilfe von JAT erstellte Grundywerttabellen um <i>perfekt</i> zu spielen (nur für bestimmte Spielfeldgrößen).</body></html>";
	final String perfectEndgamePlayer = "<html><body><br><b>PerfectEndgamePlayer</b>spielt <i>perfekt</i> sobald nur noch eine bestimmte Anzahl von Feldern frei ist.</body></html>";
	private JLabel lblRules = new JLabel(juvavumRules);
	private JLabel lblPlayers = new JLabel(allroundPlayer);
	private JLabel lblHelp = new JLabel(
			"<html><body><br><b>Steuerung</b><br>F11 - Spielstart<br>Klick - Feld belgen<br>F12 <i>oder</i> Shift+Klick - Zug bestätigen<br>Esc <i>oder</i> Alt+Klick - Spielfeld schließen<br>F10 - Skin anpassen<br></body></html>");
	private ImageIcon icon = new ImageIcon(this.getClass().getResource(
			"/juvavum/images/thinking.gif"));
	private JLabel lblSmiley = new JLabel(icon);
	private Game game;
	private String playerName;
	private int humanWins = 0;
	private int computerWins = 0;

	/**
	 * Konstruktor 
	 */
	public GameOptionsGUI() {
		super("Juvavum spielen");
		setDefaultLookAndFeelDecorated(true);
		JDialog.setDefaultLookAndFeelDecorated(true);
		Vector<Integer> height = new Vector<Integer>();
		for (int i = 1; i <= 15; i++)
			height.add(i);
		cmbHeight = new JComboBox(height);
		cmbHeight.setSelectedIndex(4);
		cmbHeight.addActionListener(alHeight);

		Vector<Integer> width = new Vector<Integer>();
		for (int i = 1; i <= 15; i++)
			width.add(i);
		cmbWidth = new JComboBox(width);
		cmbWidth.setSelectedIndex(4);
		cmbWidth.addActionListener(alWidth);

		String[] players = { "RandomPlayer", "SymmetricPlayer",
				"BetterSymmetricPlayer", "PerfectPlayer",
				"PerfectEndgamePlayer", "AllroundPlayer" };
		cmbPlayers = new JComboBox(players);
		cmbPlayers.setSelectedIndex(5);
		cmbPlayers.addActionListener(alPlayers);

		String[] games = { "Juvavum", "Domino Juvavum", "Cram", "Hole Cram",
				"3-Juvavum" };
		cmbGames = new JComboBox(games);
		cmbGames.setSelectedIndex(0);
		cmbGames.addActionListener(alGames);

		btnPlay.addActionListener(alPlay);

		chkHumanStarts.addActionListener(alHumanStarts);
		chkMisere.addActionListener(alMisere);
		chkRandom.addActionListener(alRandom);

		chkHumanStarts.setSelected(true);
		chkRandom.setSelected(true);

		cmbHeight.addKeyListener(kl);
		cmbWidth.addKeyListener(kl);
		cmbPlayers.addKeyListener(kl);
		cmbGames.addKeyListener(kl);
		chkHumanStarts.addKeyListener(kl);
		chkMisere.addKeyListener(kl);
		chkRandom.addKeyListener(kl);
		lblHeight.addKeyListener(kl);
		lblWidth.addKeyListener(kl);
		lblOpponent.addKeyListener(kl);
		lblGames.addKeyListener(kl);
		lblRules.addKeyListener(kl);
		lblPlayers.addKeyListener(kl);
		btnPlay.addKeyListener(kl);
		lblHelp.addKeyListener(kl);
		lblSmiley.addKeyListener(kl);
		txtName.addKeyListener(kl);
		lblScore.addKeyListener(kl);
		addKeyListener(kl);

		btnPlay.setToolTipText("Hier klicken um ein Spiel zu starten.");
		cmbPlayers
				.setToolTipText("<html><body><b>RandomPlayer</b> setzt zufällig<br><b>SymmetricPlayer</b> und <b>BetterSymmetricPlayer</b> versuchen mit Hilfe einer Spiegelungsstrategie zu gewinnen (nicht für Misere-Spiele)<br><b>PerfectPlayer</b> verwendet mit Hilfe von JAT erstellte Grundywerttabellen um <i>perfekt</i> zu spielen (nur für bestimmte Spielfeldgrößen)<br><b>PerfectEndgamePlayer</b>spielt <i>perfekt</i> sobald nur noch eine bestimmte Anzahl von Feldern frei ist<br><b>AllroundPlayer</b>kombiniert die Stärken aller anderen Spieler");
		chkMisere
				.setToolTipText("<html><body>Im Misere-Spiel <i>verliert</i> der Spieler, der den letzten Zug macht. Es gewinnt also, wer zuerst nicht mehr ziehen kann.</body></html>");
		chkHumanStarts
				.setToolTipText("Auswählen um den 1. Zug im Spiel zu machen.");

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				e.getWindow().dispose();
				try {
					game.dispose();
				} catch (RuntimeException e1) {
				}
			}
		});

		// layout
		GridBagLayout gbl = new GridBagLayout();
		Container c = getContentPane();
		c.removeAll();
		c.setLayout(gbl);

		// x y w h wx wy
		addComponent(c, gbl, lblHeight, 0, 0, 1, 1, 1, 0);
		addComponent(c, gbl, cmbHeight, 1, 0, 1, 1, 1, 0);

		addComponent(c, gbl, lblWidth, 0, 1, 1, 1, 1, 0);
		addComponent(c, gbl, cmbWidth, 1, 1, 1, 1, 1, 0);

		addComponent(c, gbl, lblGames, 0, 2, 1, 1, 1, 0);
		addComponent(c, gbl, cmbGames, 1, 2, 1, 1, 1, 0);

		addComponent(c, gbl, lblOpponent, 0, 3, 1, 1, 1, 0);
		addComponent(c, gbl, cmbPlayers, 1, 3, 1, 1, 1, 0);

		addComponent(c, gbl, chkHumanStarts, 0, 4, 1, 1, 1, 0);
		addComponent(c, gbl, chkMisere, 1, 4, 1, 1, 1, 0);

		addComponent(c, gbl, chkRandom, 0, 5, 1, 1, 1, 0);

		addComponent(c, gbl, lblName, 0, 6, 1, 1, 1, 0);
		addComponent(c, gbl, txtName, 1, 6, 1, 1, 1, 0);

		addComponent(c, gbl, btnPlay, 0, 7, 2, 1, 1, 0);

		addComponent(c, gbl, lblRules, 0, 8, 2, 1, 1, 0);
		addComponent(c, gbl, lblPlayers, 0, 9, 2, 1, 1, 0);
		addComponent(c, gbl, lblHelp, 0, 10, 2, 1, 1, 0);
		addComponent(c, gbl, lblSmiley, 0, 11, 2, 1, 1, 1);
		addComponent(c, gbl, lblScore, 0, 12, 2, 1, 1, 0);

		// center frame
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = getSize();
		if (frameSize.height > screenSize.height) {
			frameSize.height = screenSize.height;
		}
		if (frameSize.width > screenSize.width) {
			frameSize.width = screenSize.width;
		}
		setLocation(0, 0);
		setSize(350, 550);
		setResizable(true);
		setVisible(true);
	}

	private ActionListener alPlayers = new ActionListener() {
		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			checkPossibility();
			if (cmbPlayers.getSelectedItem().equals("PerfectPlayer"))
				lblPlayers.setText(perfectPlayer);
			else if (cmbPlayers.getSelectedItem().equals("RandomPlayer"))
				lblPlayers.setText(randomPlayer);
			else if (cmbPlayers.getSelectedItem().equals("SymmetricPlayer")
					|| cmbPlayers.getSelectedItem().equals(
							"BetterSymmetricPlayer"))
				lblPlayers.setText(symmetricPlayer);
			else if (cmbPlayers.getSelectedItem()
					.equals("PerfectEndgamePlayer"))
				lblPlayers.setText(perfectEndgamePlayer);
			else if (cmbPlayers.getSelectedItem().equals("AllroundPlayer"))
				lblPlayers.setText(allroundPlayer);

		}
	};

	private ActionListener alMisere = new ActionListener() {
		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			misere = !misere;
			checkPossibility();
		}
	};

	private ActionListener alGames = new ActionListener() {
		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			checkPossibility();
		}
	};

	private ActionListener alHeight = new ActionListener() {
		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			checkPossibility();
		}
	};

	private ActionListener alWidth = new ActionListener() {
		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			checkPossibility();
		}
	};

	private ActionListener alHumanStarts = new ActionListener() {
		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			humanStarts = !humanStarts;
		}
	};

	private ActionListener alRandom = new ActionListener() {
		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			random = !random;
		}
	};

	private ActionListener alPlay = new ActionListener() {
		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			if (!random)
				startGame(new Board(cmbHeight.getSelectedIndex() + 1, cmbWidth
						.getSelectedIndex() + 1));
			else
				startRandomGame();
		}
	};

	/**
	 * Startet das Spiel
	 * 
	 * @param b
	 * 			Spielbrett
	 */
	private void startGame(Board b) {
		try {
			if (!game.getGameOver())
				declareWinner("Computer", false);
			game.dispose();
		} catch (RuntimeException e1) {
		}
		String tmp;
		Player player = null;
		playerName = txtName.getText();
		if (playerName.trim().equals(""))
			playerName = "Namenlos";
		GameAnalyse analyse = null;
		int h = cmbHeight.getSelectedIndex() + 1;
		int w = cmbWidth.getSelectedIndex() + 1;
		int gameType = cmbGames.getSelectedIndex() + 1;
		
		tmp = (String) cmbGames.getSelectedItem();
		if (tmp.equals("Juvavum"))
			analyse = new JGameAnalyse(b);
		else if (tmp.equals("Domino Juvavum"))
			analyse = new DJGameAnalyse(b);
		else if (tmp.equals("Cram"))
			analyse = new CramGameAnalyse(b);
		else if (tmp.equals("Hole Cram"))
			analyse = new HoleGameAnalyse(b);
		else if (tmp.equals("3-Juvavum"))
			analyse = new TJGameAnalyse(b);
		
		tmp = (String) cmbPlayers.getSelectedItem();
		if (tmp.equals("RandomPlayer"))
			player = new RandomPlayer(gameType);
		else if (tmp.equals("SymmetricPlayer"))
			player = new SymmetricPlayer(gameType);
		else if (tmp.equals("BetterSymmetricPlayer"))
			player = new BetterSymmetricPlayer(gameType);
		else if (tmp.equals("PerfectPlayer"))
			player = new PerfectPlayer(gameType, misere, analyse);
		else if (tmp.equals("PerfectEndgamePlayer"))
			player = new PerfectEndgamePlayer(gameType, misere);
		else if (tmp.equals("AllroundPlayer"))
			player = new AllroundPlayer(gameType, misere, humanStarts, analyse);

		if (h < 1 || w < 1 || player == null || analyse == null || gameType < 1) // Fehler
			showError("Ungültige Auswahl.");
		else
			game = new Game(b, player, gameType, analyse, humanStarts,
					playerName, misere, this);
		game.setLocation(getX() + getWidth(), getY());
		game.setVisible(true);
	}

	/**
	 * Startet das Spiel von einer zufällig belegten Startposition 
	 */
	private void startRandomGame() {
		int h = cmbHeight.getSelectedIndex() + 1;
		int w = cmbWidth.getSelectedIndex() + 1;
		Board b = new Board(h, w);
		int anz = (int) (Math.random() * h * w * 0.5);
		for (int i = 1; i <= anz; i++)
			b.set((int) (Math.random() * (w - 1)) + 1, (int) (Math.random()
					* (h - 1) + 1));
		startGame(b);
	}

	/**
	 * @param winner
	 * 				gewinner
	 * @param showDialog
	 * 				true, wennDialog anzeigen
	 */
	public void declareWinner(String winner, boolean showDialog) {
		int diff;
		if (!winner.equals("Computer")) {
			humanWins++;
			diff = humanWins - computerWins;
			if (diff <= -20)
				lblSmiley.setIcon(new ImageIcon("/juvavum/images/cry.gif"));
			else if (diff > -20 && diff <= -15)
				lblSmiley.setIcon(new ImageIcon(this.getClass().getResource(
						"/juvavum/images/argh.gif")));
			else if (diff > -15 && diff <= -10)
				lblSmiley.setIcon(new ImageIcon(this.getClass().getResource(
						"/juvavum/images/sad.gif")));
			else if (diff > -10 && diff <= -5)
				lblSmiley.setIcon(new ImageIcon(this.getClass().getResource(
						"/juvavum/images/thumbdown.gif")));
			else if (diff > -5 && diff <= -3)
				lblSmiley.setIcon(new ImageIcon(this.getClass().getResource(
						"/juvavum/images/headshake.gif")));
			else if (diff > -3 && diff < 0)
				lblSmiley.setIcon(new ImageIcon(this.getClass().getResource(
						"/juvavum/images/thinking.gif")));
			else if (diff == 0)
				lblSmiley.setIcon(new ImageIcon(this.getClass().getResource(
						"/juvavum/images/chewgum.gif")));
			else if (diff > 0 && diff < 5)
				lblSmiley.setIcon(new ImageIcon(this.getClass().getResource(
						"/juvavum/images/smile.gif")));
			else if (diff >= 5 && diff < 10)
				lblSmiley.setIcon(new ImageIcon(this.getClass().getResource(
						"/juvavum/images/thumbup.gif")));
			else if (diff >= 10 && diff < 15)
				lblSmiley.setIcon(new ImageIcon(this.getClass().getResource(
						"/juvavum/images/claphands.gif")));
			else if (diff >= 15 && diff < 20)
				lblSmiley.setIcon(new ImageIcon(this.getClass().getResource(
						"/juvavum/images/headbang.gif")));
			else if (diff >= 20)
				lblSmiley.setIcon(new ImageIcon(this.getClass().getResource(
						"/juvavum/images/sunglasses.gif")));
		} else {
			computerWins++;
			diff = humanWins - computerWins;
			if (diff <= -20)
				lblSmiley.setIcon(new ImageIcon("/juvavum/images/cry.gif"));
			else if (diff > -20 && diff <= -15)
				lblSmiley.setIcon(new ImageIcon(this.getClass().getResource(
						"/juvavum/images/argh.gif")));
			else if (diff > -15 && diff <= -10)
				lblSmiley.setIcon(new ImageIcon(this.getClass().getResource(
						"/juvavum/images/sad.gif")));
			else if (diff > -10 && diff <= -5)
				lblSmiley.setIcon(new ImageIcon(this.getClass().getResource(
						"/juvavum/images/thumbdown.gif")));
			else if (diff > -5 && diff <= -3)
				lblSmiley.setIcon(new ImageIcon(this.getClass().getResource(
						"/juvavum/images/headshake.gif")));
			else if (diff > -3 && diff < 0)
				lblSmiley.setIcon(new ImageIcon(this.getClass().getResource(
						"/juvavum/images/thinking.gif")));
			else if (diff == 0)
				lblSmiley.setIcon(new ImageIcon(this.getClass().getResource(
						"/juvavum/images/chewgum.gif")));
			else if (diff > 0 && diff < 5)
				lblSmiley.setIcon(new ImageIcon(this.getClass().getResource(
						"/juvavum/images/smile.gif")));
			else if (diff >= 5 && diff < 10)
				lblSmiley.setIcon(new ImageIcon(this.getClass().getResource(
						"/juvavum/images/thumbup.gif")));
			else if (diff >= 10 && diff < 15)
				lblSmiley.setIcon(new ImageIcon(this.getClass().getResource(
						"/juvavum/images/claphands.gif")));
			else if (diff >= 15 && diff < 20)
				lblSmiley.setIcon(new ImageIcon(this.getClass().getResource(
						"/juvavum/images/headbang.gif")));
			else if (diff >= 20)
				lblSmiley.setIcon(new ImageIcon(this.getClass().getResource(
						"/juvavum/images/sunglasses.gif")));
		}
		lblScore.setText("<html><body>" + playerName + " <b>" + humanWins
				+ "</b> - Computer <b>" + computerWins + "</b></body></html>");
		if (showDialog)
			JOptionPane.showMessageDialog(null, "<HTML><BODY><b><i>" + winner
					+ "</b></i> gewinnt.</BODY></HTML>", "Spiel beendet",
					JOptionPane.PLAIN_MESSAGE);
	}

	/**
	 * @param error
	 * 				Fehlermeldung
	 */
	public void showError(String error) {
		JOptionPane.showMessageDialog(null, error, "Fehler",
				JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Spiel mit diesen Einstellungen möglich?
	 */
	private void checkPossibility() {
		btnPlay.setEnabled(true);
		btnPlay.setText("Spielstart");
		String game = "";
		int h = cmbHeight.getSelectedIndex() + 1;
		int w = cmbWidth.getSelectedIndex() + 1;
		if (h * w >= 50 || h > 15 || w > 15) {
			btnPlay.setEnabled(false);
			btnPlay.setText("Spel mit diesen Einstellungen nicht möglich.");
		}
		if (cmbGames.getSelectedItem().equals("Juvavum")) {
			game = "JUV";
			lblRules.setText(juvavumRules);
		} else if (cmbGames.getSelectedItem().equals("Domino Juvavum")) {
			game = "DJUV";
			lblRules.setText(djRules);
		} else if (cmbGames.getSelectedItem().equals("Cram")) {
			game = "CRAM";
			lblRules.setText(cramRules);
		} else if (cmbGames.getSelectedItem().equals("Hole Cram")) {
			game = "HOLE";
			lblRules.setText(holeRules);
		} else if (cmbGames.getSelectedItem().equals("3-Juvavum")) {
			game = "TJUV";
			lblRules.setText(tjRules);
		}
		if ((game == "DJUV" || game == "CRAM") && (h == 1 && w == 1)) {
			btnPlay.setEnabled(false);
			btnPlay.setText("Spel mit diesen Einstellungen nicht möglich.");
		}
		if ((game == "HOLE") && (h < 3 && w < 3)) {
			btnPlay.setEnabled(false);
			btnPlay.setText("Spel mit diesen Einstellungen nicht möglich.");
		}
		if ((game == "TJUV") && (h < 3 && w < 3)) {
			btnPlay.setEnabled(false);
			btnPlay.setText("Spel mit diesen Einstellungen nicht möglich.");
		}
		if (misere
				&& (cmbPlayers.getSelectedItem().equals("SymmetricPlayer") || cmbPlayers
						.getSelectedItem().equals("BetterSymmetricPlayer"))) {
			btnPlay.setEnabled(false);
			btnPlay.setText("Spel mit diesen Einstellungen nicht möglich.");
		}
		if (cmbPlayers.getSelectedItem().equals("PerfectPlayer") && random) {
			btnPlay.setEnabled(false);
			btnPlay.setText("Spel mit diesen Einstellungen nicht möglich.");
		}
		if (cmbPlayers.getSelectedItem().equals("PerfectPlayer")) {
			if (h > w) {
				int help = h;
				h = w;
				w = help;
			}
			if (misere) {
				if (game == "JUV")
					game = "JUVM";
				else if (game == "DJUV")
					game = "DJUVM";
				else if (game == "CRAM")
					game = "MCRAM";
				else if (game == "HOLE")
					game = "HOLEM";
				else if (game == "TJUV")
					game = "TJUVM";
			}
			String file = "/juvavum/grundy/" + game + "[" + h + "x" + w
					+ "].txt";
			String fileSymm = "/juvavum/grundy/" + game + "[" + h + "x" + w
					+ ", symm].txt";
			try {
				InputStream is = this.getClass().getResourceAsStream(file);
				new BufferedReader(new InputStreamReader(is));
			} catch (Exception e1) {
				try {
					InputStream is = this.getClass().getResourceAsStream(fileSymm);
					new BufferedReader(new InputStreamReader(is));
				} catch (Exception e2) {
					btnPlay.setEnabled(false);
					btnPlay
							.setText("Spel mit diesen Einstellungen nicht möglich.");
				}
			}
		}
	}

	private KeyListener kl = new KeyListener() {
		/* (non-Javadoc)
		 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
		 */
		public void keyPressed(KeyEvent e) {
			try {
				if (e.getKeyCode() == 27)
					game.dispose();
				else if (e.getKeyCode() == 123)
					game.move();
				else if (e.getKeyCode() == 122)
					if (!random)
						startGame(new Board(cmbHeight.getSelectedIndex() + 1,
								cmbWidth.getSelectedIndex() + 1));
					else
						startRandomGame();
			} catch (RuntimeException e1) {
			}
			;
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