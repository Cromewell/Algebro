package com.cromewell.algebro;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.TextArea;
import javafx.util.Duration;

import java.util.ArrayList;


public class Writer implements Runnable {

    private final TextArea console;
    private final ArrayList<String> toWrite = new ArrayList<>();
    private boolean isWriting;
    private int textSpeed;
    public static final int DEFAULT_TEXT_SPEED = 5; //30

    public Writer(TextArea console) {
        this.console = console;
        isWriting = false;
        textSpeed = DEFAULT_TEXT_SPEED;
    }

    public void add(String s) {
        toWrite.add(s);
        run();
    }

    @Override
    public void run() {
        if (toWrite.size() != 0 && !isWriting) {
            String s = toWrite.get(0);
            if (s.equals("\n")) {
                console.appendText("\n");
                toWrite.remove(0);
                run();
                return;
            }
            if (s.equals("")) {
                toWrite.remove(0);
                run();
                return;
            }
            if(s.equals("$clear$")){
                console.setText("");
                toWrite.remove(0);
                run();
                return;
            }
            isWriting = true;
            final IntegerProperty i = new SimpleIntegerProperty(0);
            Timeline timeline = new Timeline();
            KeyFrame keyFrame = new KeyFrame(
                    Duration.millis(textSpeed),
                    event -> {
                        if (i.get() > s.length()) {
                            timeline.stop();
                        } else {
                            if (i.get() == 0) console.setText(console.getText() + s.charAt(0));
                            else {
                                console.setText(console.getText() + s.charAt(i.get()));
                                if (i.get() + 1 == s.length()) console.appendText("\n"); //add NL when finished
                            }
                            i.set(i.get() + 1);
                        }
                    });
            timeline.getKeyFrames().add(keyFrame);
            timeline.setCycleCount(s.length());
            timeline.setOnFinished(e -> {
                toWrite.remove(0);
                isWriting = false;
                run();
            });
            timeline.play();
        }
    }

    public boolean isWriting() {
        return isWriting;
    }

    public void setTextSpeed(int textSpeed) {
        this.textSpeed = textSpeed;
    }
}
