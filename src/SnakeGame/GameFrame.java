package SnakeGame;

import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class GameFrame extends JFrame {

	public GameFrame(){
		

		//añado gamePanel
		GamePanel gamePanel = new GamePanel();
		this.add(gamePanel);
	
		//titulo ventana
		this.setTitle("Snake Game");
		//tamaño fijo
		this.setResizable(false);
		//empaquetar componentes del jframe
		this.pack();
		//visible
		this.setVisible(true);
		//ventana centrada
		this.setLocationRelativeTo(null);
		


		
	}
}
