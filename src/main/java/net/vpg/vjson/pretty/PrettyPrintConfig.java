package net.vpg.vjson.pretty;

public class PrettyPrintConfig {
    private String indent = "  ";
    private boolean spaceWithinBraces = false;
    private boolean spaceWithinBrackets = false;
    private boolean spaceBeforeComma = false;
    private boolean spaceAfterComma = true;
    private boolean spaceBeforeColon = false;
    private boolean spaceAfterColon = true;
    private boolean arrayContentsOnSameLine = false;
    private boolean objectContentsOnSameLine = false;

    public String getIndent() {
        return indent;
    }

    public PrettyPrintConfig setIndent(String indent) {
        this.indent = indent;
        return this;
    }

    public boolean isSpaceWithinBraces() {
        return spaceWithinBraces;
    }

    public PrettyPrintConfig setSpaceWithinBraces(boolean spaceWithinBraces) {
        this.spaceWithinBraces = spaceWithinBraces;
        return this;
    }

    public boolean isSpaceWithinBrackets() {
        return spaceWithinBrackets;
    }

    public PrettyPrintConfig setSpaceWithinBrackets(boolean spaceWithinBrackets) {
        this.spaceWithinBrackets = spaceWithinBrackets;
        return this;
    }

    public boolean isSpaceBeforeComma() {
        return spaceBeforeComma;
    }

    public PrettyPrintConfig setSpaceBeforeComma(boolean spaceBeforeComma) {
        this.spaceBeforeComma = spaceBeforeComma;
        return this;
    }

    public boolean isSpaceAfterComma() {
        return spaceAfterComma;
    }

    public PrettyPrintConfig setSpaceAfterComma(boolean spaceAfterComma) {
        this.spaceAfterComma = spaceAfterComma;
        return this;
    }

    public boolean isSpaceBeforeColon() {
        return spaceBeforeColon;
    }

    public PrettyPrintConfig setSpaceBeforeColon(boolean spaceBeforeColon) {
        this.spaceBeforeColon = spaceBeforeColon;
        return this;
    }

    public boolean isSpaceAfterColon() {
        return spaceAfterColon;
    }

    public PrettyPrintConfig setSpaceAfterColon(boolean spaceAfterColon) {
        this.spaceAfterColon = spaceAfterColon;
        return this;
    }

    public boolean isArrayContentsOnSameLine() {
        return arrayContentsOnSameLine;
    }

    public PrettyPrintConfig setArrayContentsOnSameLine(boolean arrayContentsOnSameLine) {
        this.arrayContentsOnSameLine = arrayContentsOnSameLine;
        return this;
    }

    public boolean isObjectContentsOnSameLine() {
        return objectContentsOnSameLine;
    }

    public PrettyPrintConfig setObjectContentsOnSameLine(boolean objectContentsOnSameLine) {
        this.objectContentsOnSameLine = objectContentsOnSameLine;
        return this;
    }
}
