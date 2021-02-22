import jdk.swing.interop.SwingInterOpUtils;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class GameSurface extends JPanel implements ActionListener, KeyListener {

    private Timer timer;
    private Rectangle birb;
    private List<Rectangle> obstacles;
    private int speed = -5;
    private boolean gameOver = false;
    private int score;
    private List<Player> highScore;
    private int gap;


    // När en Gamesurface skapas med en viss storlek, skapas obstacles
    public GameSurface(final int width, final int height, int gap, int delay) {

        highScore = SaveAndLoad.loadHighScore();
        score = 0;

        // hur ser avataren som du styr ut, här en rektangel  från JFrame?
        // och vilken x- och y-position börjar den på (två första)
        this.birb = new Rectangle(70, 100, 75, 85);
        this.obstacles = new ArrayList<>();
        this.gap = gap;

        addObstacles();

        // hur lång tid det tar för hela fönstret att röra sig
        // Fires one or more ActionEvents at specified intervals. Används alltså tillsammans med en ActionEvent
        // An example use is an animation object that uses a Timer as the trigger for drawing its frames.
        // this i (10, this) hänvisar till den här actionListenern
        this.timer = new Timer(delay, this);
        this.timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        repaint(g);
    }

    // obstacles skapas med en position och en storlek
    private void addObstacles() {

        int max = 600;
        int random = ThreadLocalRandom.current().nextInt(20, (max-gap));
        int top = random;
        int bot = max - gap - random;

        obstacles.add(new Rectangle(1000, max-bot, 150, bot));
        obstacles.add(new Rectangle(1000, 0, 150, top));
    }

    // Nighttime + tumbleweed
    Image backgroundImage = makeImage("images/desertLightningBlue.jpg");
    Image gameOverImage = makeImage("images/gameOverImageNight2.jpg");
    Image obstacleImage = makeImage("images/tumbleweed.png");
    Image birbImage = makeImage("images/redHawkWingsUpNight.png");

/*  Image backgroundImage = makeImage("images/mexicoDesert.jpg");
    Image gameOverImage = makeImage("images/gameOverImageDay.jpg");
    Image obstacleImage = makeImage(images/"tumbleweed.png");
    Image birbImage = makeImage("images/redHawkSunWingsUp.png");*/

    private void repaint(Graphics g) {
        final Dimension d = this.getSize();

        if (gameOver) {
            calHighScore(score);
            g.drawImage(gameOverImage, 0, 0, d.width, d.height, null);
            /*g.setColor(new Color(0x0000FF, false));
            g.fillRect(0, 0, d.width, d.height);*/
            g.setColor(Color.decode("#d3d5eb"));
            g.setFont(new Font("Arial", Font.BOLD, 32));


            int pos = 100;
            for (Player gamer : highScore) {
                g.drawString(gamer.toString(), 100, pos);
                pos += 42;
            }
            return;
        }

        // fill the background
        g.drawImage(backgroundImage, 0, 0, this.getWidth(), this.getHeight(), this);

        // fill the obstacles
        for (Rectangle obstacle : obstacles) {
            g.drawImage(obstacleImage, obstacle.x, obstacle.y, obstacle.width, obstacle.height, null);
            /*g.setColor(Color.pink);
            g.fillRect(obstacle.x, obstacle.y, obstacle.width, obstacle.height);*/
        }

        // fill the obstacles
        g.drawImage(birbImage, birb.x, birb.y, birb.width, birb.height, null);
        /*g.setColor(Color.black);
        g.fillRect(birb.x, birb.y, birb.width, birb.height);*/
    }

    public Image makeImage(String filename) {
        ImageIcon imageIcon = new ImageIcon(filename);
        Image image = imageIcon.getImage();
        return image;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // this will trigger on the timer event
        // if the game is not over yet it will
        // update the positions of all obstacles
        // and check for collision with the birb

        if (gameOver) {
            timer.stop();
            return;
        }

        // lista över obstacles som simmat ur bild
        final List<Rectangle> toRemove = new ArrayList<>();

        for (Rectangle obstacle : obstacles) {
            obstacle.translate(speed, 0);

            // när obstacle är utanför bild, lägg i tabort-listan
            if (obstacle.x < -150) {

                toRemove.add(obstacle);

                if (obstacle.y == 0) {
                    addObstacles();
                    obstacles.removeAll(toRemove);
                }

            }

            if (birb.intersects(obstacle) || birb.y > 600) {
                gameOver = true;
            }

            if (obstacle.y == 0 && (obstacle.x + 150) == 20 && !gameOver) {
                score++;
                System.out.println(score + "score");
                increaseSpeed();
                decreaseGap();
            }

        }


        // gör att fåglen faller.
        birb.translate(0, +2);

        // för att birben ska kunna dö när den når marken
        final int minHeight = 500;
        if (birb.y > minHeight) {
            gameOver = true;
        }

        // samma bakgrund osv som innan
        this.repaint();

    }

    public void increaseSpeed() {
        if (timer.getDelay() > 0) {
            timer.setDelay(timer.getDelay() - 1);
        }
        System.out.println(timer.getDelay());
    }

    public void decreaseGap() {
        if(this.gap > 50) {
            this.gap -= 5;
            System.out.println(this.gap);
        }
    }


    @Override
    public void keyReleased(KeyEvent e) {

        final int maxHeight = 20;
        /*final int minHeight = 200;*/


        final int kc = e.getKeyCode();

        if (kc == KeyEvent.VK_SPACE && birb.y > maxHeight) {
            //kolla hur mycket den flyger uppåt, ändrade från -100
            birb.translate(0, -75);
        }
    }


    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

        final int kc = e.getKeyCode();

        if (gameOver == true && kc == KeyEvent.VK_SPACE) {

            restart();
        }
    }

    public void restart() {
        gameOver = false;
        this.score = 0;

        this.birb = new Rectangle(20, 1000 / 2 - 15, 30, 20);
        this.obstacles = new ArrayList<>();

        addObstacles();
        repaint();

        this.gap = 200;
        this.timer.setDelay(20);
        this.timer.start();
    }

    public void createPlayer(int score) {
        String input = JOptionPane.showInputDialog("Skriv ditt namn");

        highScore.add(new Player(input, score));
        highScore.sort(new ScoreComparator().reversed());
        SaveAndLoad.saveHighScore(highScore);
    }

    public void calHighScore(int score) {
        if (highScore.size() == 10 && highScore.get(highScore.size() - 1).getScore() < score) {
            highScore.remove(highScore.size() - 1);
            this.createPlayer(score);
        } else if (highScore.size() < 10) {
            this.createPlayer(score);
        }
    }


}





