import java.util.Objects;

public class Player {

    private final String name;
    private final int score;
    private final String difficulty;


    public Player(String name, int score, String difficulty) {
        this.name = name;
        this.score = score;
        this.difficulty = difficulty.toString();
    }

    @Override
    public String toString() {
        return name + " - " + score + " - " + difficulty;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return score == player.score && Objects.equals(name, player.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, score);
    }


    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }
}
