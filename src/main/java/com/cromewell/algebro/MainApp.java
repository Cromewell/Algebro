package com.cromewell.algebro;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;

public class MainApp extends Application {

    static Player ply = new Player();
    static final List<Integer> EXISTING_LEVELS = Arrays.asList(1, 2);
    private Console console;
    private TextField input;
    private boolean continueWriting = true;
    private boolean waitingForInput = false;
    private int linesToSkip = 0;
    private int readLines = 0;
    private String currentLvl;
    private String lastInputText;
    private Stage mainStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        mainStage = stage;
        stage.setTitle("Algebro");
        try {
            InputStream fileStream = MainApp.class.getClassLoader().getResourceAsStream("imgs/pi.png");
            if (fileStream == null) throw new IOException();
            stage.getIcons().add(new Image(fileStream));
        } catch (IOException e) {
            System.out.println("Couldn't load window icon.");
        }

        BorderPane root = new BorderPane();
        TextArea output = new TextArea();
        root.setCenter(output);
        output.setDisable(true);
        output.setOpacity(1);

        //enable auto scrolling
        output.textProperty().addListener((observableValue, s, t1) -> output.setScrollTop(Double.MAX_VALUE));

        //setting the text area bg color to black and text color to ice blue
        output.setStyle("-fx-background-color: #2E2E2E; -fx-control-inner-background: #2E2E2E; -fx-text-fill: #00FFFF;");

        //load font
        InputStream fontStream = MainApp.class.getClassLoader().getResourceAsStream("fonts/OldLondon.ttf");
        Font oldLondonFont = Font.loadFont(fontStream, 26);

        output.setFont(oldLondonFont);

        console = new Console(output);

        BorderPane box = new BorderPane();
        input = new TextField();

        Button ok = new Button("OK");
        ok.setOnAction(e -> {
            if (waitingForInput) {
                lastInputText = input.getText();
                waitingForInput = false;
                continueWriting = true;
                input.setText("");
            }
            if (continueWriting) {
                continueWriting = false;
                startLevel(currentLvl, linesToSkip);
            }
        });

        //allow user to click enter instead of using the ok button
        input.setOnKeyPressed(
                keyEvent -> {
                    if (keyEvent.getCode().getName().equals("Enter")) {
                        ok.fire();
                    }
                }
        );

        box.setCenter(input);
        box.setRight(ok);
        root.setBottom(box);

        stage.setScene(new Scene(root, Console.WIDTH, Console.Height));
        stage.show();

        //start game
        startIntro();
    }

    //skip for skipping lines
    private void startLevel(String lvl, int skip) {
        BufferedReader reader;
        try {
            InputStream levelStream = MainApp.class.getClassLoader().getResourceAsStream("levels/" + lvl + ".txt");
            if (levelStream == null) throw new IOException();
            reader = new BufferedReader(new InputStreamReader(levelStream));

            if (reader.ready()) { //skip already displayed lines
                for (int i = 0; i < skip; i++) {
                    reader.readLine();
                }
            }
            while (reader.ready()) {
                String line = reader.readLine();
                String toDisplay = handleInput(line, lvl);
                if (toDisplay.equals("$break$")) break;
                console.printLine(toDisplay);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        readLines = 0;
    }

    private String handleInput(String line, String lvl) {
        if (line.contains(Keywords.PLAYER_NAME)) {
            line = line.replaceAll("\\$plyName\\$", ply.getName());
        }
        if (line.equals(Keywords.WAIT_FOR_OK)) {
            console.printLine("Drücke OK um fortzufahren...");
            console.newLine();
            continueWriting = true;
            linesToSkip += readLines + 1;
            return "$break$";
        }

        if (line.equals(Keywords.CLEAR_SCREEN)) {
            console.clear();
            line = "";
        }

        if (line.equals(Keywords.SHOW_SOLUTION)) {
            try (InputStream is = MainApp.class.getClassLoader().getResourceAsStream("solutions/sol_" + lvl + ".pdf")) {
                Path tempOutput = Files.createTempFile("temp_sol", ".pdf");
                tempOutput.toFile().deleteOnExit();
                if (is == null) throw new IOException();
                Files.copy(is, tempOutput, StandardCopyOption.REPLACE_EXISTING);
                Desktop.getDesktop().open(tempOutput.toFile());
            } catch (IOException ex) {
                System.out.println("Error while creating temporary solution file.");
            }
            line = "";
        }
        if (line.equals(Keywords.GET_INPUT)) {
            console.printLine("Erwarte Eingabe...");
            continueWriting = false;
            waitingForInput = true;
            linesToSkip += readLines + 1;
            return "$break$";
        }

        if (line.equals(Keywords.RENAME_PLAYER)) {
            ply.setName(lastInputText);
            line = "";
        }

        if (line.equals(Keywords.START_LEVEL)) {
            line = "";
            int chosenLvl;
            try {
                chosenLvl = Integer.parseInt(lastInputText);
                if (EXISTING_LEVELS.contains(chosenLvl)) {
                    console.printLine("------------------");
                    console.printLine("Lade Level " + chosenLvl + "...");
                    console.printLine("------------------");
                    //simulate loading for 1.5 seconds
                    Task<Void> sleeper = new Task<>() {
                        @Override
                        protected Void call() {
                            try {
                                Thread.sleep(1500);
                            } catch (InterruptedException ignored) {
                            }
                            return null;
                        }
                    };
                    sleeper.setOnSucceeded(event -> {
                        console.clear();
                        linesToSkip = 0;
                        currentLvl = "lvl" + lastInputText;
                        startLevel("lvl" + lastInputText, linesToSkip);
                    });
                    new Thread(sleeper).start();
                } else {
                    throw new IllegalArgumentException();
                }
            } catch (IllegalArgumentException ex) {
                console.clear();
                console.printLine("Level \"" + lastInputText + "\" konnte nicht gefunden werden.");
                linesToSkip += readLines - 1;
                continueWriting = true;
                startLevel(currentLvl, linesToSkip);
            }
        }

        if (line.equals(Keywords.SHOW_TASK)) {
            line = "";
            Stage taskWindow = new Stage();
            taskWindow.initModality(Modality.WINDOW_MODAL);
            StackPane root = new StackPane();
            InputStream fileStream = MainApp.class.getClassLoader().getResourceAsStream("imgs/" + lvl + "_task.png");
            try {
                if (fileStream == null) throw new IOException();
                ImageView taskView = new ImageView(new Image(fileStream));
                root.getChildren().add(taskView);
                taskWindow.setScene(new Scene(root, 840, 174));
                taskWindow.initOwner(mainStage);
                taskWindow.show();

            } catch (IOException e) {
                System.out.println("Couldn't load imgs/" + lvl + "_task.png");
            }
        }

        if (line.equals(Keywords.ASK_CORRECTNESS)) {
            boolean isCorrect;
            String in;

            try {
                in = input.getText();
                if (in.equals("J") || in.equals("j") || in.equals("N") || in.equals("n")) {
                    isCorrect = in.equals("J") || in.equals("j");
                } else {
                    throw new IllegalArgumentException();
                }
            } catch (IllegalArgumentException e) {
                console.clear();
                console.printLine("J(a) oder N(ein)?!");
                linesToSkip += readLines;
                continueWriting = true;
                return "$break$";

            }

            line = "";
            if (!lvl.equals("intro")) {
                console.clear();
                exitLevel(Integer.parseInt(lvl.substring(3)), isCorrect);
            }
        }
        readLines++;
        return line;
    }

    private void exitLevel(int lvl, boolean isCorrect) {
        if (isCorrect) {
            console.printLine("Die Tat ist vollbracht. Glückwunsch ist geboten!");
            console.printLine("Du erhälst " + lvl * 10 + " XP.");
            ply.receiveXP(lvl * 10);
            ply.setLastProblemSolved("Level " + lvl);
            console.printLine("------------------");
            console.printLine("Raetsel gelöst: " + ply.getProblemsSolved());
            console.printLine("------------------");
        } else {
            console.clear();
            console.printLine("Zu schade...Ein Leben muss also dahin schwinden. Hahaha!");
        }
    }

    private void startIntro() {
        currentLvl = "intro";
        startLevel(currentLvl, 0);
    }
}
