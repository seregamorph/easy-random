package org.jeasy.random;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Random;
import java.util.Set;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class RepeatedRandomTest {

    @Test
    public void validateEqualsAndHashCodeSameRandomInstance() {
        val clazz = Parent.class;

        int total = 50;
        int failed = 0;
        for (int i = 0; i < total; i++) {
            val seed = new Random().nextLong();

            val instance1 = randomInstance(clazz, seed);
            // same seed - hence same object (mostly)
            val instance2 = randomInstance(clazz, seed);
            try {
                assertEquals(instance1, instance2);
            } catch (AssertionError e) {
                //e.printStackTrace();
                failed++;
            }
        }
        System.out.println("failed=" + failed);
        // flaky failure
        Assertions.assertThat(failed)
                .isGreaterThan(0)
                .isLessThan(total);
    }

    private static Object randomInstance(Class<?> type, long seed) {
        val easyRandom = new EasyRandom(new EasyRandomParameters()
                .objectPoolSize(2)
                .seed(seed)
                // .collectionSizeRange(2, 2) fails 30%
                // fails 60%
                .collectionSizeRange(3, 3));
        return easyRandom.nextObject(type);
    }

    @Data
    public static abstract class IdResource {
        private Long id;
    }

    @Data
    public static class Parent {
        private List<Mid> mid;
    }

    @Data
    public static class Mid {
        private Set<Child> children;
    }

    @Data
    public static class Child extends IdResource {
        @ToString.Exclude
        @EqualsAndHashCode.Exclude
        private Mid mid;
    }


}
