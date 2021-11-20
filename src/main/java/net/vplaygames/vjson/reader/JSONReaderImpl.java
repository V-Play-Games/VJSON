package net.vplaygames.vjson.reader;

import net.vplaygames.vjson.parser.ParseException;

public abstract class JSONReaderImpl implements JSONReader {
    protected Object currentToken;
    protected int currentTokenType;

    protected JSONReaderImpl() {}

    @Override
    public abstract int getPosition();

    @Override
    public int getCurrentTokenType() {
        checkOpen();
        return currentTokenType;
    }

    @Override
    public int getNextTokenType() {
        checkOpen();
        return currentTokenType = getNextTokenType0();
    }

    @Override
    public Object getCurrentToken() {
        checkOpen();
        return currentToken;
    }

    public Object getNextToken() {
        getNextTokenType();
        return getCurrentToken();
    }

    public void expectNextType(int type) throws ParseException {
        thr(getNextTokenType() != type);
    }

    public void thr(boolean check) {
        if (check) thr();
    }

    public abstract void thr();

    protected abstract int getNextTokenType0() throws ParseException;

    protected abstract void checkOpen();
}
