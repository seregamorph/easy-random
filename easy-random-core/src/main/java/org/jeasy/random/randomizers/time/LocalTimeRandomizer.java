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

import java.time.LocalTime;

/**
 * A {@link Randomizer} that generates random {@link LocalTime}.
 *
 * @author Mahmoud Ben Hassine (mahmoud.benhassine@icloud.com)
 */
public class LocalTimeRandomizer implements Randomizer<LocalTime> {

    private final HourRandomizer hourRandomizer;
    private final MinuteRandomizer minuteRandomizer;
    private final NanoSecondRandomizer nanoSecondRandomizer;

    /**
     * Create a new {@link LocalTimeRandomizer}.
     */
    public LocalTimeRandomizer() {
        hourRandomizer = new HourRandomizer();
        minuteRandomizer = new MinuteRandomizer();
        nanoSecondRandomizer = new NanoSecondRandomizer();
    }

    /**
     * Create a new {@link LocalTimeRandomizer}.
     *
     * @param seed initial seed
     */
    public LocalTimeRandomizer(final long seed) {
        hourRandomizer = new HourRandomizer(seed);
        minuteRandomizer = new MinuteRandomizer(seed);
        nanoSecondRandomizer = new NanoSecondRandomizer(seed);
    }

    /**
     * Create a new {@link LocalTimeRandomizer}.
     *
     * @return a new {@link LocalTimeRandomizer}.
     * @deprecated in favor of the equivalent constructor
     */
    @Deprecated
    public static LocalTimeRandomizer aNewLocalTimeRandomizer() {
        return new LocalTimeRandomizer();
    }

    /**
     * Create a new {@link LocalTimeRandomizer}.
     *
     * @param seed initial seed
     * @return a new {@link LocalTimeRandomizer}.
     * @deprecated in favor of the equivalent constructor
     */
    @Deprecated
    public static LocalTimeRandomizer aNewLocalTimeRandomizer(final long seed) {
        return new LocalTimeRandomizer(seed);
    }

    @Override
    public LocalTime getRandomValue() {
        int randomHour = hourRandomizer.getRandomValue();
        int randomMinute = minuteRandomizer.getRandomValue();
        int randomSecond = minuteRandomizer.getRandomValue(); // seconds are also between 0 and 59
        int randomNanoSecond = nanoSecondRandomizer.getRandomValue();
        return LocalTime.of(randomHour, randomMinute, randomSecond, randomNanoSecond);
    }
}
