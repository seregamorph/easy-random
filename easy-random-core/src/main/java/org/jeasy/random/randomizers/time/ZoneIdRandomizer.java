/*
 * The MIT License
 *
 *   Copyright (c) 2020, Mahmoud Ben Hassine (mahmoud.benhassine@icloud.com)
 *
 *   Permission is hereby granted, free of charge, to any person obtaining a copy
 *   of this software and associated documentation files (the "Software"), to deal
 *   in the Software without restriction, including without limitation the rights
 *   to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *   copies of the Software, and to permit persons to whom the Software is
 *   furnished to do so, subject to the following conditions:
 *
 *   The above copyright notice and this permission notice shall be included in
 *   all copies or substantial portions of the Software.
 *
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *   AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *   LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *   THE SOFTWARE.
 */
package org.jeasy.random.randomizers.time;

import org.jeasy.random.api.Randomizer;
import org.jeasy.random.randomizers.AbstractRandomizer;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A {@link Randomizer} that generates random {@link ZoneId}.
 *
 * @author Mahmoud Ben Hassine (mahmoud.benhassine@icloud.com)
 */
public class ZoneIdRandomizer extends AbstractRandomizer<ZoneId> {

    /**
     * Create a new {@link ZoneIdRandomizer}.
     */
    public ZoneIdRandomizer() {
    }

    /**
     * Create a new {@link ZoneIdRandomizer}.
     *
     * @param seed initial seed
     */
    public ZoneIdRandomizer(long seed) {
        super(seed);
    }

    /**
     * Create a new {@link ZoneIdRandomizer}.
     *
     * @return a new {@link ZoneIdRandomizer}.
     * @deprecated in favor of the equivalent constructor
     */
    @Deprecated
    public static ZoneIdRandomizer aNewZoneIdRandomizer() {
        return new ZoneIdRandomizer();
    }

    /**
     * Create a new {@link ZoneIdRandomizer}.
     *
     * @param seed initial seed
     * @return a new {@link ZoneIdRandomizer}.
     * @deprecated in favor of the equivalent constructor
     */
    @Deprecated
    public static ZoneIdRandomizer aNewZoneIdRandomizer(final long seed) {
        return new ZoneIdRandomizer(seed);
    }

    @Override
    public ZoneId getRandomValue() {
        List<Map.Entry<String, String>> zoneIds = new ArrayList<>(ZoneId.SHORT_IDS.entrySet());
        Map.Entry<String, String> randomZoneId = zoneIds.get(random.nextInt(zoneIds.size()));
        return ZoneId.of(randomZoneId.getValue());
    }
}
