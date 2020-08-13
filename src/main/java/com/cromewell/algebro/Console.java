package com.cromewell.algebro;

import javafx.scene.control.TextArea;

public class Console {
    public static final int WIDTH = 800;
    public static final int Height = 420;
    private TextArea console;

    public Console(TextArea console) {
        this.console = console;
    }

    public void printLine(String s) {
        if (console.getText().equals("")) {
            console.setText(s);
        } else {
            console.setText(console.getText() + "\n" + s);
        }
        console.appendText(""); //for auto scrolling listener. Set text sets scroll to 0.
    }

    public void newLine() {
        console.setText(console.getText() + "\n");
    }

    public void clear() {
        console.setText("");
    }
}
