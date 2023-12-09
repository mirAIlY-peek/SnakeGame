package hellofx;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.util.LinkedList;
import java.util.Random;


public class Game extends Application {
    private static final int tile_SIZE = 20;
    private static final int grid_Size = 30;
    private static int SPEED = 10;

    private LinkedList<Coordinate> snake;
    private Coordinate food;
    private Direction direction;
    private boolean gameOver;
    private int score;
    private int main;
    private KeyCode rememberKeyCode = KeyCode.A;

    //launch the application Sneak
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        snake = new LinkedList<>();
        direction = Direction.RIGHT;

        gameOver = false;
        score = 0; //score

        createSnake();
        spownFood();

        Canvas canvas = new Canvas(grid_Size * tile_SIZE, grid_Size * tile_SIZE);// это холст
        GraphicsContext gc = canvas.getGraphicsContext2D();

        StackPane root = new StackPane(canvas);
        Scene scene = new Scene(root, grid_Size * tile_SIZE, grid_Size * tile_SIZE);

        scene.setOnKeyPressed(event -> handleKeyPress(event.getCode()));

        new AnimationTimer() {
            long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (now - lastUpdate >= 1_000_000_000 / SPEED) {
                    if (!gameOver) {

                        updateGame();
                        render(gc);

                    }
                    lastUpdate = now;
                }
            }
        }.start();

        primaryStage.setTitle("Snake Game");
        primaryStage.setScene(scene);
        primaryStage.show();
    }




    private void createSnake() {
        snake.clear();
        snake.add(new Coordinate(grid_Size / 2, grid_Size / 2));
        snake.add(new Coordinate(grid_Size / 2 - 1, grid_Size / 2));
        snake.add(new Coordinate(grid_Size / 2 - 2, grid_Size / 2));
    }

    private void spownFood() {
        Random random = new Random();
        int x, y;
        do {
            x = random.nextInt(grid_Size);
            y = random.nextInt(grid_Size);
        } while (snake.contains(new Coordinate(x, y)));

        food = new Coordinate(x, y);
    }

    private void updateGame() {
        moveSnake();
        intersects();
        checkFood();
    }

    private void moveSnake() {
        Coordinate head = snake.getFirst();
        Coordinate newHead = new Coordinate(head.getX() + direction.getX(), head.getY() + direction.getY());
        snake.addFirst(newHead);

        if (!snake.contains(food)) {
            snake.removeLast();
        } else {
            spownFood();
            score++;
            System.out.println("Score : " + score);
            SPEED++;
        }
    }

    private void intersects() {
        Coordinate head = snake.getFirst();

        if (head.getX() < 0 || head.getX() >= grid_Size || head.getY() < 0 || head.getY() >= grid_Size
                || snake.subList(1, snake.size()).contains(head)) {
            gameOver = true;
            showGameOverAlert();
        }
    }

    private void checkFood() {
        if (snake.getFirst().equals(food)) {

            spownFood();

        }

    }

    private void handleKeyPress(KeyCode code) {

        switch (code) {
            case W:
                if(rememberKeyCode != code) {
                    direction = Direction.UP;
                    rememberKeyCode=KeyCode.S;
                }
                break;
            case S:
                if(rememberKeyCode!= code){
                    direction = Direction.DOWN;
                    rememberKeyCode=KeyCode.W;
                }
                break;
            case A:
                if(rememberKeyCode != code){
                    direction = Direction.LEFT;
                    rememberKeyCode=KeyCode.D;
                }
                break;
            case D:
                if(rememberKeyCode != code){
                    direction = Direction.RIGHT;
                    rememberKeyCode=KeyCode.A;
                }
                break;
            default:
                break;
        }
    }

    private void render(GraphicsContext draw) {
        draw.clearRect(0, 0, grid_Size * tile_SIZE, grid_Size * tile_SIZE);


        draw.setStroke(Color.BLACK);
        draw.setLineWidth(1);

        boolean isHead = true;

        for (Coordinate body : snake) {

            draw.strokeRect(body.getX() * tile_SIZE, body.getY() * tile_SIZE, tile_SIZE-2, tile_SIZE-2);

            if (isHead) {
                draw.setFill(Color.RED);  //головы змею здесь
            } else {
                draw.setFill(Color.GOLDENROD);  // зеленый цвет дя тела
            }

            isHead=false;

            draw.fillRect(body.getX() * tile_SIZE , body.getY() * tile_SIZE, tile_SIZE - 2, tile_SIZE - 2);

        }



        draw.setStroke(Color.RED);
        draw.setLineWidth(3);
        draw.strokeRect(food.getX() * tile_SIZE, food.getY() * tile_SIZE, tile_SIZE-2, tile_SIZE-2);

        draw.setFill(Color.GOLDENROD);
        draw.fillRect(food.getX() * tile_SIZE, food.getY() * tile_SIZE, tile_SIZE-2, tile_SIZE-2);

        draw.setFill(Color.BLACK);
        draw.fillText("Score : " + score, 10, 15);
    }

    private void showGameOverAlert() {
        System.out.println("GAME OVER. Your score: " + score);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText(null);
        alert.setContentText("Your score: " + score);
        alert.setOnHidden(evt -> resetGame());
        alert.show();
    }

    private void resetGame() {
        createSnake();
        spownFood();
        direction = Direction.RIGHT;
        gameOver = false;
        score = 0;
        SPEED=10;
    }

    private static class Coordinate {
        private final int x;
        private final int y;

        public Coordinate(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {

            return x;

        }

        public int getY() {

            return y;

        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Coordinate that = (Coordinate) obj;
            return x == that.x && y == that.y;
        }

        @Override
        public int hashCode() {
            return 31 * x + y;
        }
    }

    private enum Direction {
        UP(0, -1), DOWN(0, 1), LEFT(-1, 0), RIGHT(1, 0);

        private final int x;
        private final int y;

        Direction(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }
}

