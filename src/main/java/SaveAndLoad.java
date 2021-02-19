import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class SaveAndLoad {

    static Path file = FileSystems.getDefault().getPath("highScore.txt");


    public static List<Player> loadHighScore() {
        String player;
        List<Player> scoresAndName = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file.toFile()))) {
            while ((player = br.readLine()) != null) {

                String[] score = player.split("-");
                scoresAndName.add(new Player(score[0], Integer.parseInt(score[1])));
            }

        } catch (FileNotFoundException e) {
            System.out.println(e);
        } catch (IOException ex) {
            System.out.println(ex);
        }

        return scoresAndName;
    }


    public static void saveHighScore(List<Player> highScore) {

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file.toFile()))) {

            for (Player gamer : highScore) {
                bw.write(gamer.toString() + System.lineSeparator());
            }


        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}