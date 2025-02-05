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
package net.vpg.vjson.parser

class ParseException : RuntimeException {
    val type: Int
    val position: Int
    val token: String?

    constructor(position: Int, token: String?) : super("Unexpected token $token at position $position") {
        this.position = position
        this.type = UNEXPECTED_TOKEN
        this.token = token
    }

    constructor(position: Int, cause: Throwable?) : super("Unexpected exception at position $position", cause) {
        this.position = position
        this.type = UNEXPECTED_EXCEPTION
        this.token = null
    }

    companion object {
        const val UNEXPECTED_TOKEN = 0
        const val UNEXPECTED_EXCEPTION = 1
    }
}
