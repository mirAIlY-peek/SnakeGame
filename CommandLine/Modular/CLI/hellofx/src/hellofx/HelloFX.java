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


public class HelloFX extends Application {
    private static final int TILE_SIZE = 20;
    private static final int GRID_SIZE = 30;
    private static int SPEED = 10;

    private LinkedList<Coordinate> snake;
    private Coordinate food;
    private Direction direction;
    private boolean gameOver;
    private int score;
    private int main;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        snake = new LinkedList<>();
        direction = Direction.UP;
        gameOver = false;
        score = 0;

        createSnake();
        spawnFood();

        Canvas canvas = new Canvas(GRID_SIZE * TILE_SIZE, GRID_SIZE * TILE_SIZE);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        StackPane root = new StackPane(canvas);
        Scene scene = new Scene(root, GRID_SIZE * TILE_SIZE, GRID_SIZE * TILE_SIZE);

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
        snake.add(new Coordinate(GRID_SIZE / 2, GRID_SIZE / 2));
        snake.add(new Coordinate(GRID_SIZE / 2 - 1, GRID_SIZE / 2));
        snake.add(new Coordinate(GRID_SIZE / 2 - 2, GRID_SIZE / 2));
    }

    private void spawnFood() {
        Random random = new Random();
        int x, y;
        do {
            x = random.nextInt(GRID_SIZE);
            y = random.nextInt(GRID_SIZE);
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
            spawnFood();
            score++;
            System.out.println("Score : " + score);
            SPEED++;
        }
    }

    private void intersects() {
        Coordinate head = snake.getFirst();

        if (head.getX() < 0 || head.getX() >= GRID_SIZE || head.getY() < 0 || head.getY() >= GRID_SIZE
                || snake.subList(1, snake.size()).contains(head)) {
            gameOver = true;
            showGameOverAlert();
        }
    }

    private void checkFood() {
        if (snake.getFirst().equals(food)) {

            spawnFood();

        }

    }

    private void handleKeyPress(KeyCode code) {

        switch (code) {
            case W:
                direction = Direction.UP;
                break;
            case S:
                direction = Direction.DOWN;
                break;
            case A:
                direction = Direction.LEFT;
                break;
            case D:
                direction = Direction.RIGHT;
                break;
            default:
                break;
        }
    }

    private void render(GraphicsContext gc) {
        gc.clearRect(0, 0, GRID_SIZE * TILE_SIZE, GRID_SIZE * TILE_SIZE);

        // Draw Snake
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);

        boolean isHead = true;

        for (Coordinate segment : snake) {

            gc.strokeRect(segment.getX() * TILE_SIZE, segment.getY() * TILE_SIZE, TILE_SIZE-2, TILE_SIZE-2);


            // Рисуем прямоугольник для каждого сегмента с заливкой
            if (isHead) {
                gc.setFill(Color.RED);  // Красный цвет для головы
            } else {
                gc.setFill(Color.GOLDENROD);  // Зеленый цвет для остальных сегментов
            }

            isHead=false;

            gc.fillRect(segment.getX() * TILE_SIZE , segment.getY() * TILE_SIZE, TILE_SIZE - 2, TILE_SIZE - 2);

        }


        // Draw Food
        gc.setStroke(Color.RED);
        gc.setLineWidth(1);
        gc.strokeRect(food.getX() * TILE_SIZE, food.getY() * TILE_SIZE, TILE_SIZE-2, TILE_SIZE-2);

        gc.setFill(Color.GOLDENROD);
        gc.fillRect(food.getX() * TILE_SIZE, food.getY() * TILE_SIZE, TILE_SIZE-2, TILE_SIZE-2);

        // Draw Score
        gc.setFill(Color.BLACK);
        gc.fillText("Score : " + score, 10, 15);
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
        spawnFood();
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

