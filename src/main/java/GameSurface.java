import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class GameSurface extends JPanel implements ActionListener, KeyListener {


    private final int speed = -5;
    private final Timer timer;
    private final List<Player> highScore;
    private Image background;
    private Image birbWingsUp;
    private Image birbWingsDown;
    private Image tumbleweed;
    private Image gameOverScreen;
    private boolean gameOver = false;
    private Rectangle birb;
    private List<Rectangle> obstacles;
    private int score;
    private int gap;
    private Enum<Difficulty> difficulty;
    private int delay;

    public GameSurface(final int width, final int height, final Enum<Difficulty> difficulty) {

        this.highScore = SaveAndLoad.loadHighScore();
        this.score = 0;
        this.birb = new Rectangle(70, 100, 60, 70);
        this.obstacles = new ArrayList<>();
        this.difficulty = difficulty;

        setupDifficulty();
        addObstacles(1500);
        addObstacles();


        this.timer = new Timer(delay, this);
        this.timer.start();

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        repaint(g);
    }

    private void addObstacles() {
        addObstacles(1000);
    }

    private void addObstacles(int x) {

        int minHeight = 75;
        int maxHeight = 600;
        int topObstacleHeight = ThreadLocalRandom.current().nextInt(minHeight, (maxHeight - gap - minHeight));
        int bottomObstacleHeight = maxHeight - gap - topObstacleHeight;

        obstacles.add(new Rectangle(x, maxHeight - bottomObstacleHeight, 150, bottomObstacleHeight));
        obstacles.add(new Rectangle(x, 0, 150, topObstacleHeight));
    }


    private void repaint(Graphics g) {
        final Dimension d = this.getSize();

        if (gameOver) {
            addToHighScoreList(score);
            g.drawImage(gameOverScreen, 0, 0, d.width, d.height, null);
            g.setColor(Color.decode("#d3d5eb"));
            g.setFont(new Font("Arial", Font.BOLD, 21));


            int highscoreTextPosition = 100;
            for (Player gamer : highScore) {
                g.drawString(gamer.toString(), 100, highscoreTextPosition);
                highscoreTextPosition += 42;
            }
            return;
        }

        g.drawImage(background, 0, 0, this.getWidth(), this.getHeight(), this);

        for (Rectangle obstacle : obstacles) {
            g.drawImage(tumbleweed, obstacle.x, obstacle.y, obstacle.width, obstacle.height, null);

        }

        makeBirdFlap(g);

    }

    public void makeBirdFlap(Graphics g) {
        if (birb.y % 2 == 0) {
            g.drawImage(birbWingsDown, birb.x, birb.y, birb.width, birb.height, null);
        } else {
            g.drawImage(birbWingsUp, birb.x, birb.y, birb.width, birb.height, null);
        }
    }

    public Image makeImage(String filename) {
        ImageIcon imageIcon = new ImageIcon(filename);
        Image image = imageIcon.getImage();
        return image;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (gameOver) {
            timer.stop();
            return;
        }

        List<Rectangle> toRemove = new ArrayList<>();

        for (Rectangle obstacle : obstacles) {
            obstacle.translate(speed, 0);

            findObstaclesToRecycle(obstacle, toRemove);

            // collisionDetector(obstacle);

            addScore(obstacle);

        }

        recycleObstacles(toRemove);

        birb.translate(0, +2);

        final int minHeight = 500;
        if (birb.y > minHeight) {
            gameOver = true;
        }

        this.repaint();

    }

    private void collisionDetector(Rectangle obstacle) {
        if (birb.intersects(obstacle) || birb.y > 600) {
            gameOver = true;
        }
    }

    private void findObstaclesToRecycle(Rectangle obstacle, List<Rectangle> toRemove) {
        if (obstacle.x < -150) {
            toRemove.add(obstacle);
        }
    }

    private void recycleObstacles(List<Rectangle> obstaclesToRemove) {
        for (Rectangle obstacle : obstaclesToRemove) {

            if (obstacle.y == 0) {
                addObstacles();
            }
            obstacles.remove(obstacle);
        }
    }

    private void addScore(Rectangle obstacle) {
        if (obstacle.y == 0 && (obstacle.x + 150) == 20 && !gameOver) {
            score++;
            System.out.println(score + "score");
            increaseSpeed();
            decreaseGap();
        }
    }

    public void increaseSpeed() {
        if (timer.getDelay() > 8) {
            timer.setDelay(timer.getDelay() - 1);
        }
        System.out.println(timer.getDelay());
    }

    public void decreaseGap() {
        if (this.gap > 125) {
            this.gap -= 5;
            System.out.println(this.gap);
        }
    }


    @Override
    public void keyReleased(KeyEvent e) {

        final int maxHeight = 20;

        final int kc = e.getKeyCode();

        if (kc == KeyEvent.VK_SPACE && birb.y > maxHeight) {
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
        this.gameOver = false;
        this.score = 0;
        this.birb = new Rectangle(70, 100, 60, 70);
        this.obstacles = new ArrayList<>();

        addObstacles(1500);
        setupDifficulty();
        addObstacles();
        repaint();


        this.timer.setDelay(delay);
        this.timer.start();
    }

    private void setupDifficulty() {
        if (difficulty.equals(Difficulty.HARD)) {
            gap = 200;
            delay = 15;
            background = makeImage("images/backgroundNight.jpg");
            gameOverScreen = makeImage("images/gameOverNight.jpg");
            tumbleweed = makeImage("images/tumbleweedNight.png");
            birbWingsUp = makeImage("images/birbNightWingsUp.png");
            birbWingsDown = makeImage("images/birbNightWingsDown.png");
        } else {
            gap = 275;
            delay = 30;
            background = makeImage("images/backgroundDay.jpg");
            gameOverScreen = makeImage("images/gameOverDay.jpg");
            tumbleweed = makeImage("images/tumbleweedDay.png");
            birbWingsUp = makeImage("images/birbDayWingsUp.png");
            birbWingsDown = makeImage("images/birbDayWingsDown.png");
        }
    }

    public void createPlayer(int score) {
        String input = JOptionPane.showInputDialog("Enter your name").trim();

        highScore.add(new Player(input, score, difficulty.toString()));
        highScore.sort(new ScoreComparator().reversed());
        SaveAndLoad.saveHighScore(highScore);
    }

    public void addToHighScoreList(int score) {
        if (highScore.size() == 10 && highScore.get(highScore.size() - 1).getScore() < score) {
            highScore.remove(highScore.size() - 1);
            this.createPlayer(score);
        } else if (highScore.size() < 10) {
            this.createPlayer(score);
        }
    }
}





