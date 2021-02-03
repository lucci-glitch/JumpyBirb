import javax.swing.JFrame;
import static javax.swing.JFrame.EXIT_ON_CLOSE;

public class Main {
    final private static int WIDTH = 1000;
    final private static int HEIGHT = 1000;
    public static void main(String[] args) {
        JFrame main = new JFrame("Flappy Birds");

        main.setSize(WIDTH, HEIGHT);
        main.setResizable(false);
        main.setDefaultCloseOperation(EXIT_ON_CLOSE);
        main.setVisible(true);
    }
}
