package src.main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JOptionPane;
import javax.swing.JPanel;


public class Game extends JPanel {

	private int tableWidth = 8;
	private int tableHeight = 8;
	Frame frame;
	Square[][] squares = new Square[tableWidth][tableHeight];
	/**
	 * false = take true = place
	 */
	boolean mode = false;
	boolean movingQueen = false;
	Player player = Player.BLACK;

	private MouseAdapter click = new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent event) {
			if (event.getComponent().equals(frame.getGame())) {
				if (!mode) {
					if (getSquareAt(event.getX(), event.getY()).available) {
						System.out.println("Du klickade på en " + getSquareAt(event.getX(), event.getY()).type);
						if (getSquareAt(event.getX(), event.getY()).isQueen())
							movingQueen = true;
						getSquareAt(event.getX(), event.getY()).type = Square.Type.EMPTY;
						findAvailableSquares(getSquareAt(event.getX(), event.getY()).x,
								getSquareAt(event.getX(), event.getY()).y);
						Toolkit toolkit = Toolkit.getDefaultToolkit();
						Image image;
						if (player == Player.BLACK)
							image = toolkit.getImage("black.png");
						else
							image = toolkit.getImage("red.png");
						Cursor c = toolkit.createCustomCursor(image, new Point(0, 0), "img");
						setCursor(c);
						repaint();
						mode = true;
					}
				} else {
					if (getSquareAt(event.getX(), event.getY()).available) {
						if (player == Player.BLACK) {
							if (movingQueen)
								getSquareAt(event.getX(), event.getY()).type = Square.Type.BLACK_QUEEN;
							else
								getSquareAt(event.getX(), event.getY()).type = Square.Type.BLACK_PLAYER;
							setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
							repaint();
							if (getSquareAt(event.getX(), event.getY()).changePlayer)
								player = Player.RED;
						} else {
							if (player == Player.RED) {
								if (movingQueen) {
									getSquareAt(event.getX(), event.getY()).type = Square.Type.RED_QUEEN;
								} else {
									getSquareAt(event.getX(), event.getY()).type = Square.Type.RED_PLAYER;
								}
								setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
								repaint();
								if (getSquareAt(event.getX(), event.getY()).changePlayer)
									player = Player.BLACK;
							}
						}
						for (Square[] allSquares : squares) {
							for (Square square : allSquares) {
								if (square.takenOutBy != null
										&& square.takenOutBy.equals(getSquareAt(event.getX(), event.getY()))) {
									square.type = Square.Type.EMPTY;
								}
							}
						}
						mode = false;
						movingQueen = false;
						findAvailableSquares(-1, -1);
						for (Square[] allSquares : squares) {
							for (Square square : allSquares) {
								square.takenOutBy = null;
								square.changePlayer = true;
							}
						}
						for (Square[] allSquares : squares) {
							for (Square square : allSquares) {
								if (square.y == 0 && square.type == Square.Type.RED_PLAYER)
									square.type = Square.Type.RED_QUEEN;
								if (square.y == 7 && square.type == Square.Type.BLACK_PLAYER) {
									square.type = Square.Type.BLACK_QUEEN;
									System.out.println("Jag är nu en dam på " + square.x + ", " + square.y);
								}
							}
						}
						repaint();
						boolean blackWon = true;
						boolean redWon = true;
						for (Square[] allSquares : squares) {
							for (Square square : allSquares) {
								if (square.type == Square.Type.BLACK_PLAYER || square.type == Square.Type.BLACK_QUEEN)
									redWon = false;
								if (square.type == Square.Type.RED_PLAYER || square.type == Square.Type.RED_QUEEN)
									blackWon = false;
							}
						}
						String[] options = { "Spela igen!", "Stäng", "Ha roligt!" };
						int result = 1;
						if (blackWon) {
							result = JOptionPane.showOptionDialog(frame.getGame(), "Svart spelare vann!", "Resultat",
									JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options,
									options[0]);
						}
						if (redWon) {
							result = JOptionPane.showOptionDialog(frame.getGame(), "Röd spelare vann!", "Resultat",
									JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options,
									options[0]);
						}
						if (redWon || blackWon) {
							switch (result) {
							case 0:
								removeMouseListener(this);
								tableWidth = 8;
								tableHeight = 8;
								squares = new Square[tableWidth][tableHeight];
								mode = false;
								movingQueen = false;
								player = Player.BLACK;
								initGame(frame);
								repaint();
								break;
							case 1:
								System.exit(0);
								break;
							case 2:
								Desktop deskt = Desktop.getDesktop();
								try {
									deskt.browse(new URI("https://www.cybercom.com/innovationzone"));
								} catch (Exception e) {
									JOptionPane.showMessageDialog(frame.getGame(), "Ett fel har uppstått: " + e, "FEL",
											JOptionPane.ERROR_MESSAGE);
								}
								frame.dispose();
								break;
							default:
								JOptionPane.showMessageDialog(frame.getGame(), "Ett fel har uppstått: ogiltlig knapp",
										"FEL", JOptionPane.ERROR_MESSAGE);
							}
						}
					}
				}
			}
		}
	};

	public Game(Game copy) {
		this(copy.frame);
		tableWidth = copy.tableWidth;
		tableHeight = copy.tableHeight;
		squares = copy.squares;
		mode = copy.mode;
		movingQueen = copy.movingQueen;
		player = copy.player;
	}

	public Game(Frame frame) {
		initGame(frame);
	}

	public void initGame(Frame frame) {
		addMouseListener(click);
		this.frame = frame;
		Square.Type color = Square.Type.WHITE;
		for (int x = 0; x <= 7; x++) {
			for (int y = 0; y <= 7; y++) {
				if (x % 2 == 0 && y % 2 == 0) {
					color = Square.Type.EMPTY;
					if (y <= 2) {
						color = Square.Type.BLACK_PLAYER;
					}
					if (y >= 5) {
						color = Square.Type.RED_PLAYER;
					}
				}
				if (x % 2 == 1 && y % 2 == 0) {
					color = Square.Type.WHITE;
				}
				if (x % 2 == 0 && y % 2 == 1) {
					color = Square.Type.WHITE;
				}
				if (x % 2 == 1 && y % 2 == 1) {
					color = Square.Type.EMPTY;
					if (y <= 2) {
						color = Square.Type.BLACK_PLAYER;
					}
					if (y >= 5) {
						color = Square.Type.RED_PLAYER;
					}
				}
				System.out.println(color);
				squares[x][y] = new Square(x, y, color, false);
			}
		}
		findAvailableSquares(-1, -1);
		findAvailableSquares(-1, -1);
		/*for (Square[] allSquares : squares) {
			for (Square asquare : allSquares) {
				if (asquare.type == Square.Type.BLACK_PLAYER)
					asquare.type = Square.Type.EMPTY;
				if (asquare.type == Square.Type.RED_PLAYER)
					asquare.type = Square.Type.EMPTY;
				squares[3][7].type = Square.Type.RED_QUEEN;
				squares[3][5].type = Square.Type.RED_QUEEN;
				squares[2][6].type = Square.Type.BLACK_QUEEN;
			}
		}*/
	}

	private void findAvailableSquares(int x, int y) {
		for (Square[] allSquares : squares) {
			for (Square asquare : allSquares) {
				if (asquare == null)
					continue;
				asquare.available = false;
			}
		}
		if (x == -1 || y == -1) {
			for (Square[] allSquares : squares) {
				for (Square asquare : allSquares) {
					if (asquare == null)
						continue;
					if (player == Player.BLACK)
						if (asquare.type == Square.Type.BLACK_PLAYER || asquare.type == Square.Type.BLACK_QUEEN)
							asquare.available = true;
					if (player == Player.RED)
						if (asquare.type == Square.Type.RED_PLAYER || asquare.type == Square.Type.RED_QUEEN)
							asquare.available = true;
				}
			}
		} else {
			if (player == Player.BLACK) {
				if (!movingQueen) {
					if (x + 2 < tableWidth && y + 2 < tableHeight
							&& (squares[x + 1][y + 1].type == Square.Type.RED_PLAYER
									|| squares[x + 1][y + 1].type == Square.Type.RED_QUEEN)
							&& squares[x + 2][y + 2].type == Square.Type.EMPTY) {
						squares[x + 2][y + 2].available = true;
						squares[x + 1][y + 1].takenOutBy = squares[x + 2][y + 2];
					}
					if (x - 2 >= 0 && y + 2 < tableHeight
							&& (squares[x - 1][y + 1].type == Square.Type.RED_PLAYER
									|| squares[x - 1][y + 1].type == Square.Type.RED_QUEEN)
							&& squares[x - 2][y + 2].type == Square.Type.EMPTY) {
						squares[x - 2][y + 2].available = true;
						squares[x - 1][y + 1].takenOutBy = squares[x - 2][y + 2];
					}
					if (x + 1 < tableWidth && y + 1 < tableHeight && squares[x + 1][y + 1].type == Square.Type.EMPTY)
						squares[x + 1][y + 1].available = true;
					if (x - 1 >= 0 && y + 1 < tableHeight && squares[x - 1][y + 1].type == Square.Type.EMPTY)
						squares[x - 1][y + 1].available = true;
				} else {
					System.out.println("Nu ska flyttas damen!");
					if (x + 2 < tableWidth && y - 2 >= 0
							&& (squares[x + 1][y - 1].type == Square.Type.RED_PLAYER
									|| squares[x + 1][y - 1].type == Square.Type.RED_QUEEN)
							&& squares[x + 2][y - 2].type == Square.Type.EMPTY) {
						squares[x + 2][y - 2].available = true;
						squares[x + 1][y - 1].takenOutBy = squares[x + 2][y - 2];
					}
					if (x - 2 >= 0 && y - 2 >= 0
							&& (squares[x - 1][y - 1].type == Square.Type.RED_PLAYER
									|| squares[x - 1][y - 1].type == Square.Type.RED_QUEEN)
							&& squares[x - 2][y - 2].type == Square.Type.EMPTY) {
						squares[x - 2][y - 2].available = true;
						squares[x - 1][y - 1].takenOutBy = squares[x - 2][y - 2];
					}
					if (x + 1 < tableWidth && y - 1 >= 0 && squares[x + 1][y - 1].type == Square.Type.EMPTY) {
						squares[x + 1][y - 1].available = true;
					}
					if (x - 1 >= 0 && y - 1 >= 0 && squares[x - 1][y - 1].type == Square.Type.EMPTY) {
						squares[x - 1][y - 1].available = true;
					}
					if (x + 2 < tableWidth && y + 2 < tableHeight
							&& squares[x + 1][y + 1].type == Square.Type.RED_PLAYER
							&& squares[x + 2][y + 2].type == Square.Type.EMPTY) {
						squares[x + 2][y + 2].available = true;
						squares[x + 1][y + 1].takenOutBy = squares[x + 2][y + 2];
					}
					if (x - 2 >= 0 && y + 2 < tableHeight
							&& (squares[x - 1][y + 1].type == Square.Type.RED_PLAYER
									|| squares[x - 1][y + 1].type == Square.Type.RED_QUEEN)
							&& squares[x - 2][y + 2].type == Square.Type.EMPTY) {
						squares[x - 2][y + 2].available = true;
						squares[x - 1][y + 1].takenOutBy = squares[x - 2][y + 2];
					}
					if (x + 1 < tableWidth && y + 1 < tableWidth && squares[x + 1][y + 1].type == Square.Type.EMPTY)
						squares[x + 1][y + 1].available = true;
					if (x - 1 >= 0 && y + 1 < tableWidth && squares[x - 1][y + 1].type == Square.Type.EMPTY)
						squares[x - 1][y + 1].available = true;
				}
			}
			if (player == Player.RED) {
				if (!movingQueen) {
					if (x + 2 < tableWidth && y - 2 >= 0
							&& (squares[x + 1][y - 1].type == Square.Type.BLACK_PLAYER
									|| squares[x + 1][y - 1].type == Square.Type.BLACK_QUEEN)
							&& squares[x + 2][y - 2].type == Square.Type.EMPTY) {
						squares[x + 2][y - 2].available = true;
						squares[x + 1][y - 1].takenOutBy = squares[x + 2][y - 2];
					}
					if (x - 2 >= 0 && y - 2 >= 0
							&& (squares[x - 1][y - 1].type == Square.Type.BLACK_PLAYER
									|| squares[x - 1][y - 1].type == Square.Type.BLACK_QUEEN)
							&& squares[x - 2][y - 2].type == Square.Type.EMPTY) {
						squares[x - 2][y - 2].available = true;
						squares[x - 1][y - 1].takenOutBy = squares[x - 2][y - 2];
					}
					if (x + 1 < tableWidth && y - 1 >= 0 && squares[x + 1][y - 1].type == Square.Type.EMPTY) {
						squares[x + 1][y - 1].available = true;
					}
					if (x - 1 >= 0 && y - 1 >= 0 && squares[x - 1][y - 1].type == Square.Type.EMPTY) {
						squares[x - 1][y - 1].available = true;
					}
				} else {
					if (x + 2 < tableWidth && y - 2 >= 0
							&& (squares[x + 1][y - 1].type == Square.Type.BLACK_PLAYER
									|| squares[x + 1][y - 1].type == Square.Type.BLACK_QUEEN)
							&& squares[x + 2][y - 2].type == Square.Type.EMPTY) {
						squares[x + 2][y - 2].available = true;
						squares[x + 1][y - 1].takenOutBy = squares[x + 2][y - 2];
					}
					if (x - 2 >= 0 && y - 2 >= 0
							&& (squares[x - 1][y - 1].type == Square.Type.BLACK_PLAYER
									|| squares[x - 1][y - 1].type == Square.Type.BLACK_QUEEN)
							&& squares[x - 2][y - 2].type == Square.Type.EMPTY) {
						squares[x - 2][y - 2].available = true;
						squares[x - 1][y - 1].takenOutBy = squares[x - 2][y - 2];
					}
					if (x + 1 < tableWidth && y - 1 >= 0 && squares[x + 1][y - 1].type == Square.Type.EMPTY) {
						squares[x + 1][y - 1].available = true;
					}
					if (x - 1 >= 0 && y - 1 >= 0 && squares[x - 1][y - 1].type == Square.Type.EMPTY) {
						squares[x - 1][y - 1].available = true;
					}
					if (x + 2 < tableWidth && y + 2 < tableHeight
							&& (squares[x + 1][y + 1].type == Square.Type.BLACK_PLAYER
									|| squares[x + 1][y + 1].type == Square.Type.BLACK_QUEEN)
							&& squares[x + 2][y + 2].type == Square.Type.EMPTY) {
						squares[x + 2][y + 2].available = true;
						squares[x + 1][y + 1].takenOutBy = squares[x + 2][y + 2];
					}
					if (x - 2 >= 0 && y + 2 < tableHeight
							&& (squares[x - 1][y + 1].type == Square.Type.BLACK_PLAYER
									|| squares[x - 1][y + 1].type == Square.Type.BLACK_QUEEN)
							&& squares[x - 2][y + 2].type == Square.Type.EMPTY) {
						squares[x - 2][y + 2].available = true;
						squares[x - 1][y + 1].takenOutBy = squares[x - 2][y + 2];
					}
					if (x + 1 < tableWidth && y + 1 < tableWidth && squares[x + 1][y + 1].type == Square.Type.EMPTY)
						squares[x + 1][y + 1].available = true;
					if (x - 1 >= 0 && y + 1 < tableWidth && squares[x - 1][y + 1].type == Square.Type.EMPTY)
						squares[x - 1][y + 1].available = true;
				}
			}
			squares[x][y].available = true;
			squares[x][y].changePlayer = false;
		}
	}

	private Square getSquareAt(int x, int y) {
		int squareWidth = getWidth() / 8;
		int squareHeight = getHeight() / 8;
		int xPosition = x / squareWidth;
		int yPosition = y / squareHeight;
		return squares[xPosition][yPosition];
	}

	@Override
	public void paintComponent(Graphics gra) {
		super.paintComponent(gra);
		for (Square[] allSquares : squares) {
			for (Square square : allSquares) {
				gra.setColor(square.getBackgroundColor());
				gra.fillRect(getWidth() / 8 * square.x, getHeight() / 8 * square.y, getWidth() / 8, getHeight() / 8);
				gra.setColor(Color.cyan);
				if (!square.getPlayerColor().equals(Color.cyan)) {
					gra.setColor(square.getPlayerColor());
					gra.fillOval(getWidth() / 8 * square.x + getWidth() / 8 / 2 + getWidth() / 16 / 2 - getWidth() / 16,
							getHeight() / 8 * square.y + getHeight() / 8 / 2 + getHeight() / 16 / 2 - getHeight() / 16,
							getWidth() / 16, getHeight() / 16);
					if (square.isQueen()) {
						gra.setColor(Color.orange);
						gra.fillOval(
								getWidth() / 8 * square.x + getWidth() / 8 / 2 + getWidth() / 32 / 2 - getWidth() / 32,
								getHeight() / 8 * square.y + getHeight() / 8 / 2 + getHeight() / 32 / 2
										- getHeight() / 32,
								getWidth() / 32, getHeight() / 32);
					}
				}
				if (square.available) {
					Graphics2D gra2 = (Graphics2D) gra;
					gra2.setColor(Color.yellow);
					gra2.setStroke(new BasicStroke(4));
					gra2.drawRect(getWidth() / 8 * square.x + 2, getHeight() / 8 * square.y + 2, getWidth() / 8 - 2,
							getHeight() / 8 - 2);
				}
			}
		}
	}
}
