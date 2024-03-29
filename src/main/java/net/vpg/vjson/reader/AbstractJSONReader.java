package net.vpg.vjson.reader;

import net.vpg.vjson.parser.ParseException;
import net.vpg.vjson.parser.TokenType;

public abstract class AbstractJSONReader implements JSONReader {
    protected Object currentToken;
    protected TokenType currentTokenType;

    @Override
    public TokenType getCurrentTokenType() {
        checkOpen();
        return currentTokenType;
    }

    @Override
    public TokenType getNextTokenType() {
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
        return currentToken;
    }

    protected abstract TokenType getNextTokenType0() throws ParseException;

    protected abstract void checkOpen();
}
