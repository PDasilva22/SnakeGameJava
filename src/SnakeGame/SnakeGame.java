package SnakeGame;

public class SnakeGame {
	
	private static GameFrame frame;

	public SnakeGame() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		//GameFrame frame = new GameFrame();
		//new GameFrame();
		frame = new GameFrame();

	}
	public static void restartGame() {
		frame.dispose();
		GamePanel.DELAY=100;
		frame=new GameFrame();
		
	}

}
