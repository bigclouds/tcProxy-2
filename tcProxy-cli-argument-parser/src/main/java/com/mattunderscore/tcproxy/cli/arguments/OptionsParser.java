/* Copyright © 2014 Matthew Champion
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

package com.mattunderscore.tcproxy.cli.arguments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author matt on 23/08/14.
 */
public final class OptionsParser {
    private final Map<String, Option<?>> flagMap = new HashMap<>();

    public OptionsParser(Option<?>... options) {
        for (final Option<?> option : options) {
            final String shortFlag = option.getShortFlag();
            if (shortFlag != null) {
                if (flagMap.put(shortFlag, option) != null) {
                    throw new IllegalStateException("Duplicate flag " + shortFlag);
                }
            }
            final String longFlag = option.getLongFlag();
            if (longFlag != null) {
                if (flagMap.put(longFlag, option) != null) {
                    throw new IllegalStateException("Duplicate flag " + longFlag);
                }
            }
        }
    }

    public List<Setting<?>> parse(String[] args) {
        final List<Setting<?>> settings = new ArrayList<>();
        for (int i = 0; i < args.length; i++) {
            final Option<Object> option = (Option<Object>)flagMap.get(args[i]);
            final SettingParser<Object> parser = option.getParser();
            if (parser != null) {
                try {
                    final Object value = parser.parse(args[i + 1]);
                    settings.add(Setting.create(option, value));
                    i++;
                } catch (NotParsableException e) {
                    settings.add(Setting.create(option));
                }
            }
            else {
                settings.add(Setting.create(option));
            }
        }
        return settings;
    }
}
