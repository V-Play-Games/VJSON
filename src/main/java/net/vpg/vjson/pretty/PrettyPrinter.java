package net.vpg.vjson.pretty;

import java.io.PrintStream;
import java.util.function.Consumer;

public class PrettyPrinter {
    private final PrettyPrintConfig config;
    private final Consumer<String> write;
    private int indentLevel;

    public PrettyPrinter(PrettyPrintConfig config, Consumer<String> write) {
        this.config = config;
        this.write = write;
    }

    public PrettyPrinter(PrettyPrintConfig config, StringBuilder sb) {
        this(config, sb::append);
    }

    public PrettyPrinter(PrettyPrintConfig config, PrintStream stream) {
        this(config, stream::print);
    }

    public PrettyPrintConfig getConfig() {
        return config;
    }

    public void incrementIndentLevel() {
        indentLevel++;
    }

    public void decrementIndentLevel() {
        indentLevel--;
    }

    public void print(String s) {
        write.accept(s);
    }

    public void newLineAndIndent() {
        print("\n" + config.getIndent().repeat(indentLevel));
    }

    public void space() {
        print(" ");
    }
}
