package src.main;

import java.awt.Color;

public class Square {
	public int x;
	public int y;
	public Type type = Type.BLACK_QUEEN;
	public boolean available;
	public Square takenOutBy = null;
	public boolean changePlayer = true;

	public Square() {
		this(0, 0, Type.EMPTY, false);
	}

	public Square(int x, int y, Type type, boolean avail) {
		this.x = x;
		this.y = y;
		this.type = type;
		available = avail;
	}

	public Square(Square toCopy) {
		this(toCopy.x, toCopy.y, toCopy.type, toCopy.available);
	}

	public enum Type {
		WHITE, EMPTY, RED_PLAYER, BLACK_PLAYER, RED_QUEEN, BLACK_QUEEN, TEST
	}

	public Color getBackgroundColor() {
		if (type == Type.WHITE) {
			return Color.white;
		}
		if (type == Type.TEST)
			return Color.orange;
		return Color.black;
	}

	public Color getPlayerColor() {
		if (type == Type.BLACK_QUEEN || type == Type.BLACK_PLAYER)
			return Color.gray;
		if (type == Type.RED_QUEEN || type == Type.RED_PLAYER)
			return Color.red;
		return Color.cyan;
	}

	public boolean isQueen() {
		if (type == Type.BLACK_QUEEN || type == Type.RED_QUEEN)
			return true;
		return false;
	}

}
