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
package net.vpg.vjson.reader

import net.vpg.vjson.parser.ParseException
import net.vpg.vjson.reader.JSONReader.TokenType

abstract class AbstractJSONReader : JSONReader {
    protected var currentToken: Any? = null
    protected var currentTokenType: TokenType? = null

    override fun getCurrentTokenType(): TokenType? {
        checkOpen()
        return currentTokenType
    }

    override fun getNextTokenType(): TokenType? {
        checkOpen()
        return this.nextTokenType0.also { currentTokenType = it }
    }

    override fun getCurrentToken(): Any? {
        checkOpen()
        return currentToken
    }

    override fun getNextToken(): Any? {
        getNextTokenType()
        return currentToken
    }

    @get:Throws(ParseException::class)
    protected abstract val nextTokenType0: TokenType?

    protected abstract fun checkOpen()
}
