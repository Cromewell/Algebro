package com.cromewell.algebro;

import javafx.scene.control.TextArea;

public class Console {
    public static final int WIDTH = 800;
    public static final int Height = 440;
    private final TextArea console;
    private final Writer writer;

    public Console(TextArea console) {
        this.console = console;
        writer = new Writer(console);
        new Thread(writer).start();
    }

    public void printLine(String s) {
        writer.add(s);
    }

    public void newLine() {
        writer.add("\n");
    }

    public void clear() {
        writer.add("$clear$");
    }

    public Writer getWriter() {
        return writer;
    }
}
