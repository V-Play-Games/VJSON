/*
 * Copyright 2021 Vaibhav Nargwani
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
