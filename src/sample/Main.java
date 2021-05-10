package sample;

// importing packages
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class Main extends Application {
    // variable declaration
    //speed,color,width,height,location for the food,a boolean(gameOver) and random variable
    static int speed = 5;
    static int foodcolor = 0;
    static int width = 20;
    static int height = 20;
    static int foodX = 0;
    static int foodY = 0;
    static int corners = 30;
    static List<Corner> snake = new ArrayList<>();
    static Dir direction = Dir.LEFT;
    static boolean gameOver = false;
    static Random rand = new Random();

    //The directions for the controls
    public enum Dir {
        LEFT, RIGHT, UP, DOWN
    }

    //The corners of the box in which the snake is contained
    public static class Corner {
        int x;
        int y;

        public Corner(int x, int y) {
            this.x = x;
            this.y = y;
        }

    }

    public void start(Stage primaryStage) {
        try {
            newFood();
            //VBox and Canvas as background and GraphicsContext is used to represent snake
            VBox root = new VBox();
            Canvas c = new Canvas(width * corners, height * corners);
            GraphicsContext gc = c.getGraphicsContext2D();
            root.getChildren().add(c);

            new AnimationTimer() {
                long lastTick = 0;

                public void handle(long now) {
                    if (lastTick == 0) {
                        lastTick = now;
                        tick(gc);
                        return;
                    }

                    if (now - lastTick > 1000000000 / speed) {
                        lastTick = now;
                        tick(gc);
                    }
                }

            }.start();

            Scene scene = new Scene(root, width * corners, height * corners);

            // Keyboard control set to W,A,S,D
            scene.addEventFilter(KeyEvent.KEY_PRESSED, key -> {
                if (key.getCode() == KeyCode.W) {
                    direction = Dir.UP;
                }
                if (key.getCode() == KeyCode.A) {
                    direction = Dir.LEFT;
                }
                if (key.getCode() == KeyCode.S) {
                    direction = Dir.DOWN;
                }
                if (key.getCode() == KeyCode.D) {
                    direction = Dir.RIGHT;
                }

            });

            // adding start snake parts
            // setting snake parts to 3 parts
            snake.add(new Corner(width / 2, height / 2));
            snake.add(new Corner(width / 2, height / 2));
            snake.add(new Corner(width / 2, height / 2));

            primaryStage.setScene(scene);
            primaryStage.setTitle("SNAKE GAME!");
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // tick
    public static void tick(GraphicsContext gc) {
        if (gameOver) {
            gc.setFill(Color.RED);
            gc.setFont(new Font("", 50));
            gc.fillText("GAME OVER", 150, 300);
            return;
        }

        for (int i = snake.size() - 1; i >= 1; i--) {
            snake.get(i).x = snake.get(i - 1).x;
            snake.get(i).y = snake.get(i - 1).y;
        }

        //Choose direction of Snake
        switch (direction) {
            case UP:
                snake.get(0).y--;
                if (snake.get(0).y < 0) {
                    gameOver = true; // GameOver if snake touches a border
                }
                break;
            case DOWN:
                snake.get(0).y++;
                if (snake.get(0).y > height) {
                    gameOver = true; // GameOver if snake touches a border
                }
                break;
            case LEFT:
                snake.get(0).x--;
                if (snake.get(0).x < 0) {
                    gameOver = true; // GameOver if snake touches a border
                }
                break;
            case RIGHT:
                snake.get(0).x++;
                if (snake.get(0).x > width) {
                    gameOver = true; // GameOver if snake touches a border
                }
                break;

        }

        // eat food and the snake grows
        if (foodX == snake.get(0).x && foodY == snake.get(0).y) {
            snake.add(new Corner(-1, -1));
            newFood();
        }

        // self destroy if snake hits the snake
        for (int i = 1; i < snake.size(); i++) {
            if (snake.get(0).x == snake.get(i).x && snake.get(0).y == snake.get(i).y) {
                gameOver = true;
            }
        }

        // fill background
        gc.setFill(Color.GREENYELLOW);
        gc.fillRect(0, 0, width * corners, height * corners);

        // score
        gc.setFill(Color.BLACK);
        gc.setFont(new Font("", 30));
        gc.fillText("Score: " + (speed - 6), 10, 30);

        gc.setFill(Color.BLACK);
        gc.setFont(new Font("", 20));
        gc.fillText("DIRECTIONS:\n W for UP \n A for LEFT \n S for DOWN \n D for RIGHT ", 450, 30);

        // random foodcolor
        Color cc = Color.WHITE;

        switch (foodcolor) {
            case 0:
                cc = Color.PURPLE;
                break;
            case 1:
                cc = Color.LIGHTBLUE;
                break;
            case 2:
                cc = Color.YELLOW;
                break;
            case 3:
                cc = Color.PINK;
                break;
            case 4:
                cc = Color.ORANGE;
                break;
        }
        gc.setFill(cc);
        gc.fillOval(foodX * corners, foodY * corners, corners, corners);

        // snake
        for (Corner c : snake) {
            gc.setFill(Color.BLUEVIOLET);
            gc.fillRect(c.x * corners, c.y * corners, corners - 1, corners - 1);
            gc.setFill(Color.DARKBLUE);
            gc.fillRect(c.x * corners, c.y * corners, corners - 2, corners - 2);

        }

    }

    // food
    public static void newFood() {
        //place a new food on random location on the canvas
        start: while (true) {
            foodX = rand.nextInt(width);
            foodY = rand.nextInt(height);

            for (Corner c : snake) {
                if (c.x == foodX && c.y == foodY) {
                    continue start;
                }
            }
            //Choosing a new color and increasing speed
            foodcolor = rand.nextInt(5);
            speed++;
            break;

        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}