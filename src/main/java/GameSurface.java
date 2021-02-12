import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class GameSurface extends JPanel implements ActionListener, KeyListener {

    private Timer timer;
    private Rectangle birb;
    private Rectangle obstacle;
    private Rectangle obstacle2;
    private int speed = -1;
    private boolean gameOver = false;

    // När en Gamesurface skapas med en viss storlek, skapas aliens
    public GameSurface(final int width, final int height) {


        // hur ser avataren som du styr ut, här en rektangel  från JFrame?
        // och vilken x- och y-position börjar den på (två första)
        this.birb = new Rectangle(20, width / 2 - 15, 30, 20);
        this.obstacle = new Rectangle(1000, 400, 150, 200);
        this.obstacle2 = new Rectangle(1000, 0, 150, 200);

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


    private void repaint(Graphics g) {
        final Dimension d = this.getSize();

        if (gameOver) {
            g.setColor(new Color(255, false));
            g.fillRect(0, 0, d.width, d.height);
            g.setColor(Color.white);
            g.setFont(new Font("Arial", Font.BOLD, 48));
            g.drawString("Game over!", 375, 300);
            return;
        }

        // fill the background
        g.setColor(Color.blue);
        g.fillRect(0, 0, d.width, d.height);

        // fill the obstacle
        g.setColor(Color.red);
        g.fillRect(obstacle.x, obstacle.y, obstacle.width, obstacle.height);

        // fill the obstacle
        g.setColor(Color.green);
        g.fillRect(obstacle2.x, obstacle2.y, obstacle2.width, obstacle2.height);


        // draw the space ship
        g.setColor(Color.black);
        g.fillRect(birb.x, birb.y, birb.width, birb.height);
    }


    @Override
    public void actionPerformed(ActionEvent e) {

        if (gameOver) {
            timer.stop();
            return;
        }

        // this will trigger on the timer event
        // if the game is not over yet it will
        // update the positions of all aliens
        // and check for collision with the space ship


        obstacle.translate(speed, 0);
        obstacle2.translate(speed, 0);


        // gör att fåglen faller.
        birb.translate(0, +2);
        // System.out.println(birb.getLocation());

        if (birb.intersects(obstacle) || (birb.intersects(obstacle2)) || birb.y > 600) {
            gameOver = true;
        }

        // samma bakgrund osv som innan
        this.repaint();

    }

    public void increaseSpeed() {
        speed--;
    }

    @Override
    public void keyReleased(KeyEvent e) {

        final int maxHeight = this.getSize().height-birb.height-10;
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
       this.obstacle = new Rectangle(1000, 400, 150, 200);
       this.obstacle2 = new Rectangle(1000, 0, 150, 200);
       repaint();
       this.timer.start();
    }

}
