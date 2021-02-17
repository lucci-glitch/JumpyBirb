import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.*;
import java.util.List;

public class GameSurface extends JPanel implements ActionListener, KeyListener {

    private Timer timer;
    private Rectangle birb;
    private List<Rectangle> obstacles;
    private int speed = -1;
    private boolean gameOver = false;
    private int score;
    private List<Player> highScore = new ArrayList<>();


    // När en Gamesurface skapas med en viss storlek, skapas obstacles
    public GameSurface(final int width, final int height) {
        score = 0;

        // hur ser avataren som du styr ut, här en rektangel  från JFrame?
        // och vilken x- och y-position börjar den på (två första)
        this.birb = new Rectangle(20, width / 2 - 15, 30, 20);
        this.obstacles = new ArrayList<>();

        // lägger till 3 obstacles (3 uppe och 3 nere)
        for (int i = 0; i < 6; i++) {
            addObstacles();
        }

        // hur lång tid det tar för hela fönstret att röra sig
        // Fires one or more ActionEvents at specified intervals. Används alltså tillsammans med en ActionEvent
        // An example use is an animation object that uses a Timer as the trigger for drawing its frames.
        // this i (10, this) hänvisar till den här actionListenern
        this.timer = new Timer(10, this);
        this.timer.start();


    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        repaint(g);
    }

    // obstacles skapas med en position och en storlek
    private void addObstacles() {
        obstacles.add(new Rectangle(1000, 400, 150, 200));
        obstacles.add(new Rectangle(1000, 0, 150, 200));
    }





    private void repaint(Graphics g) {
        final Dimension d = this.getSize();

        if (gameOver) {
            calHighScore(score);
            g.setColor(new Color(0x0000FF, false));
            g.fillRect(0, 0, d.width, d.height);
            g.setColor(Color.white);
            g.setFont(new Font("Arial", Font.BOLD, 32));
            // la till score här, bara som test - problem med poängräkningen... blir alltid 12 poäng ist för 1


            int pos = 100;
            for (Player gamer : highScore) {
                g.drawString(gamer.toString(), 100, pos);
                pos += 42;
            }


            return;
        }

        // fill the background
        g.setColor(Color.blue);
        g.fillRect(0, 0, d.width, d.height);

        // fill the obstacles
        for (Rectangle obstacle : obstacles) {
            g.setColor(Color.pink);
            g.fillRect(obstacle.x, obstacle.y, obstacle.width, obstacle.height);
        }

        // draw the space ship
        g.setColor(Color.black);
        g.fillRect(birb.x, birb.y, birb.width, birb.height);
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
            if (obstacle.x + obstacle.y < -150) {
                toRemove.add(obstacle);
            }

            if (birb.intersects(obstacle) || birb.y > 600) {
                gameOver = true;
            }

            // when obstacle kommer till x=20 och ej gameOver
            // score++ - lite issues med poängräkningen...
            if ((obstacle.x + 150) == 20 && !gameOver) {
                score++;
            }

        }

        // tar bort ur listan de som simmat ur bild
        obstacles.removeAll(toRemove);

        // lägger till lika många obstacles som tagits bort
        for (int i = 0; i < toRemove.size(); i++) {
            addObstacles();
        }

        // gör att fåglen faller.
        birb.translate(0, +2);

        // samma bakgrund osv som innan
        this.repaint();

    }

    public void increaseSpeed() {
        speed--;
    }


    @Override
    public void keyReleased(KeyEvent e) {

        final int maxHeight = this.getSize().height - birb.height - 10;
        final int minHeight = 20;


        final int kc = e.getKeyCode();

        if (kc == KeyEvent.VK_SPACE && birb.y > minHeight) {
            birb.translate(0, -100);
        }


    }


    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

        final int kc = e.getKeyCode();

        if (gameOver == true && kc == KeyEvent.VK_SPACE) {
            System.out.println("klickade efter game over");
            restart();
        }
    }

    public void restart() {
        gameOver = false;
        this.birb = new Rectangle(20, 1000 / 2 - 15, 30, 20);

        // la till detta för att restart fortfarande skulle fungera med mina ändringar
        this.obstacles = new ArrayList<>();
        this.score = 0;
        for (int i = 0; i < 6; i++) {
            addObstacles();
        }
        repaint();
        this.timer.start();
    }

    public void createPlayer(int score) {

       /* Scanner scan = new Scanner(System.in);
        System.out.println("Ange ditt namn");
        String input = scan.nextLine();*/
        String input = JOptionPane.showInputDialog("Skriv ditt namn");

        highScore.add(new Player(input, score));
        highScore.sort(new ScoreComparator().reversed());
        System.out.println(highScore);
    }

    public void calHighScore(int score) {
        if (highScore.size() == 10 && highScore.get(highScore.size() - 1).getScore() < score) {
            highScore.remove(highScore.size() - 1);
            this.createPlayer(score);
            return;
        }
        this.createPlayer(score);
    }


}





