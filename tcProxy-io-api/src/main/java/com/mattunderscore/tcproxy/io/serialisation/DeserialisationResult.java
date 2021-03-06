/* Copyright © 2016 Matthew Champion
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
 * Neither the name of mattunderscore.com nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL MATTHEW CHAMPION BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */

package com.mattunderscore.tcproxy.io.serialisation;

/**
 * @author Matt Champion on 13/01/16
 */
public final class DeserialisationResult<T> implements Deserialiser.Result<T> {
    private final T result;
    private final int bytesProcessed;
    private final boolean moreData;

    private DeserialisationResult(T result, int bytesProcessed, boolean moreData) {
        this.result = result;
        this.bytesProcessed = bytesProcessed;
        this.moreData = moreData;
    }

    @Override
    public boolean needsMoreData() {
        return false;
    }

    @Override
    public boolean hasMoreData() {
        return moreData;
    }

    @Override
    public boolean notDeserialisable() {
        return false;
    }

    @Override
    public boolean hasResult() {
        return true;
    }

    @Override
    public T result() {
        return result;
    }

    @Override
    public int bytesProcessed() {
        return bytesProcessed;
    }

    /**
     * Create result.
     * @param result The result
     * @param moreData If there is more data in the buffer
     * @param <T> The type of result
     * @return The result
     */
    public static final <T> Deserialiser.Result<T> create(T result, int bytesProcessed, boolean moreData) {
        return new DeserialisationResult<>(result, bytesProcessed, moreData);
    }
}
