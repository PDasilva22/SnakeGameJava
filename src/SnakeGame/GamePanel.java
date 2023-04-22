package SnakeGame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

public class GamePanel extends JPanel implements ActionListener {
	// Logica del juego

	static final int SCREEN_WIDTH = 600;
	static final int SCREEN_HEIGHT = 600;
	// mulplicador de nivel (cada cuanto sube de nivel el juego, manzana extra)
	static final int LEVEL_MULTIPLIER = 2;
	//no puede ser estatico si quiero se sea nuevo para cada instancia del juego
	int LEVEL = 1;

	// tam of objects
	static final int UNIT_SIZE = 25;
	// cuantos objetos caben en mi panel
	static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
	// speed of games
	static final int DELAY = 75;

	// snake body
	// final int x[] = new int[GAME_UNITS];
	// final int y[] = new int[GAME_UNITS];
	LinkedList<SnakeBodyPart> snake = new LinkedList<SnakeBodyPart>();

	// tamanio inicial snake
	int bodyParts = 6;
	int applesEaten;

	// lista de manzanas
	ArrayList<Apple> appleList = new ArrayList<Apple>();

	// lista de obstaculos
	LinkedList<Obstacle> obsList = new LinkedList<Obstacle>();

	// R,L,U,D
	// Direccion de inicio de serpiente
	char direction = 'R';
	boolean running = false;
	Timer timer;
	Random random;

	/// boton restart
	JButton boton = new JButton("Try Again.");

	public GamePanel() {
		// TODO Auto-generated constructor stub
		this.random = new Random();
		// cambiar el tamaño de la ventana
		this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
		 this.setBackground(Color.black);
		this.setFocusable(true);
		// aniadimos nuestro adaptador para entrado
		this.addKeyListener(new MyKeyAdapter());

		// boton restart

		boton.setBackground(Color.red);
		boton.setVisible(false);
		//aniadimos listener
		boton.addActionListener(new restartListener());
		

		this.setLayout(new FlowLayout(FlowLayout.CENTER,10,(this.SCREEN_HEIGHT/2)+this.SCREEN_HEIGHT/8));
		//this.boton.setLocation(200, 200);
		this.add(boton);

		// iniciamos cuerpo serpiente
		for (int i = 0; i < this.GAME_UNITS; i++) {
			snake.add(new SnakeBodyPart(0, 0));
		}

		// iniciamos el juego al iniciar el panel
		startGame();

	}

	public void startGame() {
		newApple();

		// empieza el juego
		running = true;
		timer = new Timer(DELAY, this);
		timer.start();

	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		draw(g);

	}

	public void draw(Graphics g) {

		if (running) {

			// creamos cuadriculas
			for (int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) {
				// dibujamos lineas verticales
				g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
				// diobujamos lineas horizontales
				g.drawLine(0, i * UNIT_SIZE, SCREEN_HEIGHT, i * UNIT_SIZE);
			}

			// Dibujar manzanas
			for (Location apple : this.appleList) {

				g.setColor(Color.red);
				g.fillOval(apple.getX(), apple.getY(), (int) (UNIT_SIZE - 1.7), UNIT_SIZE - 1);
				g.setColor(Color.green);
				g.fillOval(apple.getX(), apple.getY(), (int) (UNIT_SIZE / 8), UNIT_SIZE / 8);

			}

			// dibujamos obstaculos
			for (Obstacle obs : this.obsList) {

				g.setColor(Color.ORANGE);
				//g.fillOval(obs.getX(), obs.getY(), (int) (UNIT_SIZE), UNIT_SIZE);
				g.fill3DRect(obs.getX(), obs.getY(), UNIT_SIZE, UNIT_SIZE, true);
			}

			// dibujar serpiente

			for (int i = 0; i < bodyParts; i++) {
				// pintando cabeza
				if (i == 0) {
					g.setColor(Color.BLUE);
					g.fillRect(this.snake.getFirst().getX(), this.snake.getFirst().getY(), UNIT_SIZE, UNIT_SIZE);
				}
				// pintando cuerpo
				else {
					g.setColor(new Color(45, 180, 0));
					g.fillRect(this.snake.get(i).getX(), this.snake.get(i).getY(), UNIT_SIZE, UNIT_SIZE);
				}

				// pintando puntuacion

				g.setColor(Color.red);
				g.setFont(new Font("Ink Free", Font.BOLD, 50));
				// centramos el texto
				FontMetrics metrics = getFontMetrics(g.getFont());
				String frase = "Score: " + this.applesEaten;
				g.drawString(frase, (this.SCREEN_WIDTH - metrics.stringWidth(frase)) / 2, g.getFont().getSize());

				// pintar level

				g.setColor(Color.red);
				g.setFont(new Font("Ink Free", Font.BOLD, 30));
				// centramos el texto

				String frase2 = "Level: " + this.LEVEL;
				g.drawString(frase2, (this.SCREEN_WIDTH - g.getFont().getSize() * 4), g.getFont().getSize());

			}
		} else {

			gameOver(g);
		}

	}

	// este metodo crea una manzana en una posicion random
	public void newApple() {
		// la ponemos en una posicion random
		// divimos por unitsize para asegurar que queda en una casilla de la cuadricula

		for (int i = this.appleList.size(); i < this.LEVEL; i++) {
			Apple apple = new Apple(random.nextInt(GamePanel.SCREEN_WIDTH / GamePanel.UNIT_SIZE) * UNIT_SIZE,
					random.nextInt(GamePanel.SCREEN_HEIGHT / GamePanel.UNIT_SIZE) * UNIT_SIZE);
			while (this.appleList.contains(apple)) {
				apple = new Apple(random.nextInt(GamePanel.SCREEN_WIDTH / GamePanel.UNIT_SIZE) * UNIT_SIZE,
						random.nextInt(GamePanel.SCREEN_HEIGHT / GamePanel.UNIT_SIZE) * UNIT_SIZE);
			}

			this.appleList.add(apple);

		}
		//crea obstaculos
		if(this.applesEaten%2==0 && this.applesEaten>5) {
			newObstacle();
		}

	}

	public void newObstacle() {
		// uso manzanas porque la estructura es la misma una x e una y
		// el obstaculo no puede ser un obstaculo donde este la manza o la serpiente
		Obstacle obstacle = new Obstacle(random.nextInt(GamePanel.SCREEN_WIDTH / GamePanel.UNIT_SIZE) * UNIT_SIZE,
				random.nextInt(GamePanel.SCREEN_HEIGHT / GamePanel.UNIT_SIZE) * UNIT_SIZE);
		// compruebo que sea un obstaculo correcto

		while (this.appleList.contains(obstacle) || this.snake.contains(obstacle)) {
			obstacle = new Obstacle(random.nextInt(GamePanel.SCREEN_WIDTH / GamePanel.UNIT_SIZE) * UNIT_SIZE,
					random.nextInt(GamePanel.SCREEN_HEIGHT / GamePanel.UNIT_SIZE) * UNIT_SIZE);
		}

		this.obsList.add(obstacle);

	}

	// mover snake
	public void move() {

		for (int i = bodyParts; i > 0; i--) {

			this.snake.get(i).setX(this.snake.get(i - 1).getX());
			this.snake.get(i).setY(this.snake.get(i - 1).getY());
			/*
			 * this.x[i] = this.x[i - 1]; this.y[i] = this.y[i - 1];
			 */
		}

		// change directions
		switch (direction) {

		case 'U':
			this.snake.getFirst().setY(this.snake.getFirst().getY() - this.UNIT_SIZE);
			// this.y[0] = this.y[0] - UNIT_SIZE;
			break;

		case 'D':
			this.snake.getFirst().setY(this.snake.getFirst().getY() + this.UNIT_SIZE);
			// this.y[0] = this.y[0] + UNIT_SIZE;
			break;
		case 'L':
			this.snake.getFirst().setX(this.snake.getFirst().getX() - this.UNIT_SIZE);
			// this.x[0] = this.x[0] - UNIT_SIZE;
			break;
		case 'R':
			this.snake.getFirst().setX(this.snake.getFirst().getX() + this.UNIT_SIZE);
			// this.x[0] = this.x[0] + UNIT_SIZE;
			break;

		}

	}

	// devuelve las manzas que sahi guardadas
	/*
	 * private int countApplesGen() {
	 * 
	 * int res = 0; for (int i = 0; i < this.appleX.length; i++) { if
	 * (this.appleX[i] != 0 && this.appleY[i] != 0) { res++; } } return res; }
	 */

	public void checkApple() {
		// checkear si he golpeado una manzana

		Iterator it = this.appleList.iterator();

		while (it.hasNext()) {

			Location manzana = (Location) it.next();

			if (manzana.getX() == this.snake.getFirst().getX() && manzana.getY() == this.snake.getFirst().getY()) {

				this.bodyParts++;
				this.applesEaten++;
				// quito la manzana comida
				it.remove();
				// aparece nueva manzana
				newApple();
				// me salgo para evitar concurrent exeption ya que estare modificando la lista
				// que recorro
				break;
			}

		}

	}

	public void activateWallCollisions() {

		// checkear si la serpiente golpea las paredes
		// left border
		if (this.snake.getFirst().getX() < 0) {
			running = false;
		}
		// right border
		if (this.snake.getFirst().getX() > this.SCREEN_WIDTH) {
			running = false;
		}
		// top border
		if (this.snake.getFirst().getY() < 0) {
			running = false;
		}
		// bottom border
		running = false;

	}

	public void checkCollisions() {

		// checkear si la serpiente se golpea asi misma
		for (int i = this.bodyParts; i > 0; i--) {
			if ((this.snake.getFirst().getX() == this.snake.get(i).getX())
					&& (this.snake.getFirst().getY() == this.snake.get(i).getY())) {
				running = false;

			}
		}
		avoidWallCollisions();
		
		//obstacle colisions
		obstacleCollisions();

		// checkea level
		if (this.applesEaten > this.LEVEL * this.LEVEL_MULTIPLIER) {
			this.LEVEL++;

		}
		
	

		// stop timer
		if (!running) {
			timer.stop();
		}

	}

	// checkea/activa conlision contra obstaculos
	public void obstacleCollisions() {

	

		Iterator it = this.obsList.iterator();

		while (it.hasNext()) {

			Location obs = (Location) it.next();

			if (obs.getX() == this.snake.getFirst().getX() && obs.getY() == this.snake.getFirst().getY()) {

			
				running=false;
				break;
			}

		}

	}

	// este metodo activa que se puedan tocar las paredes
	public void avoidWallCollisions() {
		// checkear si la serpiente golpea las paredes
		// left border
		if (this.snake.getFirst().getX() < 0) {

			this.snake.getFirst().setX(this.SCREEN_WIDTH - UNIT_SIZE);

		}

		// right border
		if (this.snake.getFirst().getX() > this.SCREEN_WIDTH) {
			// this.x[0] = 0;// (this.SCREEN_WIDTH - (this.SCREEN_WIDTH+UNIT_SIZE));
			this.snake.getFirst().setX(0);

		}
		// top border
		if (this.snake.getFirst().getY() < 0) {

			this.snake.getFirst().setY(this.SCREEN_HEIGHT - UNIT_SIZE);
		}
		// bottom border
		if (this.snake.getFirst().getY() > this.SCREEN_HEIGHT) {
			this.snake.getFirst().setY(0);
		}

	}

	public void gameOver(Graphics g) {
		// Texto game over

		g.setColor(Color.red);
		g.setFont(new Font("Ink Free", Font.BOLD, 75));
		// centramos el texto
		FontMetrics metrics = getFontMetrics(g.getFont());
		String frase = "Game Over";
		g.drawString(frase, (this.SCREEN_WIDTH - metrics.stringWidth(frase)) / 2, this.SCREEN_HEIGHT / 2);

		// pintando puntuacion

		g.setColor(Color.red);
		g.setFont(new Font("Ink Free", Font.BOLD, 50));
		// centramos el texto
		FontMetrics metrics2 = getFontMetrics(g.getFont());
		String frase2 = "Score: " + this.applesEaten;
		g.drawString(frase2, (this.SCREEN_WIDTH - metrics2.stringWidth(frase2)) / 2, g.getFont().getSize());

		// restar button
		this.boton.setVisible(true);

	}

	// moverse
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		

		if (this.running) {
			move();
			checkApple();
			checkCollisions();

		}
		repaint();

	}
	//clase para boton restart
	
	public class restartListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			
			
			SnakeGame.restartGame();
			
			//System.out.println("out");
		}
		
		
	}

	// classe para pulsacion
	public class MyKeyAdapter extends KeyAdapter {
		public void keyPressed(KeyEvent e) {
			switch (e.getKeyCode()) {
			case KeyEvent.VK_LEFT:
				if (direction != 'R') {
					direction = 'L';
				}
				break;
			case KeyEvent.VK_RIGHT:
				if (direction != 'L') {
					direction = 'R';
				}
				break;
			case KeyEvent.VK_UP:
				if (direction != 'D') {
					direction = 'U';
				}
				break;
			case KeyEvent.VK_DOWN:
				if (direction != 'U') {
					direction = 'D';
				}
				break;

			}
		}
	}

}
