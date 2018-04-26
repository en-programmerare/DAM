package src.main;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class Frame extends JFrame {
	
	Game game = new Game(this);
	JLabel labelScoreboardTitle = new JLabel("Poäng", JLabel.CENTER);
	
	public Frame() {
		setTitle("Dam");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		setVisible(true);
		add(game);
		setSize(500, 500);
		setResizable(true);
	}
	
	public Game getGame() {
		return game;
	}
}
