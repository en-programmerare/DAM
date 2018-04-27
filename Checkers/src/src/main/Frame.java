package src.main;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Frame extends JFrame {
	
	Game game = new Game(this);
	private JLabel labelScoreboardTitle = new JLabel("Poäng", JLabel.CENTER);
	private JLabel labelScoreboardText = new JLabel("Svart: 0\nRöd: 0", JLabel.LEFT);
	private JPanel scoreboard = new JPanel();
	public Frame() {
		setTitle("Dam");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		setVisible(true);
		setLayout(new BorderLayout());
		add(game, BorderLayout.CENTER);
		//add(scoreboard, BorderLayout.EAST);
		setSize(500, 500);
		setLocation(new Point( (Toolkit.getDefaultToolkit().getScreenSize().width - 500 ) / 2, ( Toolkit.getDefaultToolkit().getScreenSize().height - 500 ) / 2));
		setResizable(true);
	}
	
	public Game getGame() {
		return game;
	}
	
	public void setScores(int black, int red) {
		
	}
}
