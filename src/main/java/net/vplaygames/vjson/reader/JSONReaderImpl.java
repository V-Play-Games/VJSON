package net.vplaygames.vjson.reader;

import net.vplaygames.vjson.parser.ParseException;

import java.io.IOException;

public abstract class JSONReaderImpl implements JSONReader {
    protected char[] buffer;
    protected int position;
    protected Object currentToken;
    protected int currentTokenType;
    protected StringBuilder builder;
    protected boolean closeUnderlyingResource;
    protected boolean isStringBased;
    protected int lastPos;
    protected int currentPosition;

    protected JSONReaderImpl(char[] buffer,
                             boolean closeUnderlyingResource,
                             boolean isStringBased,
                             int currentPosition) {
        this(buffer,
            new StringBuilder(),
            closeUnderlyingResource,
            isStringBased,
            -1,
            0,
            0,
            null,
            currentPosition);
    }

    protected JSONReaderImpl(char[] buffer,
                             StringBuilder builder,
                             boolean closeUnderlyingResource,
                             boolean isStringBased,
                             int currentTokenType,
                             int lastPos,
                             int position,
                             Object currentToken,
                             int currentPosition) {
        this.buffer = buffer;
        this.position = position;
        this.currentToken = currentToken;
        this.currentTokenType = currentTokenType;
        this.builder = builder;
        this.closeUnderlyingResource = closeUnderlyingResource;
        this.isStringBased = isStringBased;
        this.lastPos = lastPos;
        this.currentPosition = currentPosition;
    }

    @Override
    public int getPosition() {
        checkOpen();
        return position;
    }

    @Override
    public int getCurrentTokenType() {
        checkOpen();
        return currentTokenType;
    }

    @Override
    public int getNextTokenType() throws IOException {
        checkOpen();
        return currentTokenType = getNextTokenType0();
    }

    @Override
    public Object getCurrentToken() {
        checkOpen();
        return currentToken;
    }

    public Object getNextToken() throws IOException {
        getNextTokenType();
        return getCurrentToken();
    }

    public void expectNextType(int type) throws ParseException, IOException {
        thr(getNextTokenType() != type);
    }

    public void thr(boolean check) {
        if (check) thr();
    }

    public void thr() {
        throw new ParseException(position, buffer[currentPosition]);
    }

    protected abstract int getNextTokenType0() throws IOException, ParseException;

    protected abstract void checkOpen();
}
