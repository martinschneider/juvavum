package juvavum.gui;

import javax.swing.UIManager;
import javax.swing.JFrame;

import juvavum.util.CommandLineOptions;

import org.jvnet.substance.skin.SubstanceBusinessBlackSteelLookAndFeel;
import org.jvnet.substance.utils.SubstanceConstants.ImageWatermarkKind;
import org.jvnet.substance.watermark.*;

/**
 * Starter für das Hauptprogramm
 * 
 * @author Martin Schneider
 */
public class Starter {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		CommandLineOptions options = new CommandLineOptions(args);
				
		if (!options.hasOption("-p")){
			try {
				UIManager.setLookAndFeel(new SubstanceBusinessBlackSteelLookAndFeel());
				SubstanceBusinessBlackSteelLookAndFeel.setImageWatermarkKind(ImageWatermarkKind.APP_TILE);
				SubstanceBusinessBlackSteelLookAndFeel.setCurrentWatermark(new SubstanceImageWatermark("juvavum/images/juvavum.jpg"));
			} catch (Exception e) {
			}
		}
				
		JFrame.setDefaultLookAndFeelDecorated(true);
		if (options.hasOption("-g")) new GameOptionsGUI();
		if (options.hasOption("-a")){
			new AnalyseGUI();
			new Console();
		}
		if (!options.hasOption("-a") && !options.hasOption("-g")) {
			new MainGUI();
			new Console();
		}
	}
}
