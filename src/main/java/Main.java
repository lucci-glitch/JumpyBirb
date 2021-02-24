import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.io.File;

import static javax.swing.JFrame.EXIT_ON_CLOSE;

public class Main {
    final private static int WIDTH = 1000;
    final private static int HEIGHT = 600;

    public static void main(String[] args) {

        Enum<Difficulty> difficulty = Difficulty.EASY;

        if (args.length != 0) {

            if (args[0].equalsIgnoreCase("hard")) {
                difficulty = Difficulty.HARD;
            }
        }

        JFrame main = new JFrame("Jumpy Birb");
        GameSurface gs = new GameSurface(WIDTH, HEIGHT, difficulty);

        main.setSize(WIDTH, HEIGHT);
        main.setResizable(false);
        main.add(gs);
        main.addKeyListener(gs);
        main.setDefaultCloseOperation(EXIT_ON_CLOSE);
        main.setVisible(true);


        music();

    }

    public static void music() {
        try {
            File filePath = new File("Flappy.wav");

            if (filePath.exists()) {
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(filePath);
                Clip clip = AudioSystem.getClip();
                clip.open(audioInput);
                clip.loop(Clip.LOOP_CONTINUOUSLY);
                clip.start();
            } else {
                System.out.println("Incorrect audio file path!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
