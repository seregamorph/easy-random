package org.jeasy.random;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.val;
import org.junit.jupiter.api.Test;

public class RepeatedRandomTest {

    @Test
    public void validateEqualsAndHashCodeSameRandomInstance() {
        val clazz = PlanActivityGroupResource.class;

        for (int i = 0; i < 100; i++) {
            System.err.println(i);
            val seed = new Random().nextLong();

            val instance1 = randomInstance(clazz, seed);
            // same seed - hence same object (mostly)
            val instance2 = randomInstance(clazz, seed);

            assertEquals(instance1, instance2);
            //collector.checkThat("hashCode() should be the same", instance1.hashCode(), equalTo(instance2.hashCode()));
        }
    }

    protected EasyRandomParameters prepareRandomParameters(long seed) {
        val random = new Random(seed);
        return new EasyRandomParameters()
                .objectPoolSize(2)
                .seed(seed)
                .overrideDefaultInitialization(true)
                // Serializable mapping is for IdResource, should be handled via correct generic type randomization
                // https://github.com/j-easy/easy-random/issues/440
                // https://github.com/j-easy/easy-random/issues/441
                .randomize(Serializable.class, () -> (long) random.nextInt(1024))
                .randomize(Long.class, () -> (long) random.nextInt(1024))
                .randomize(Integer.class, () -> random.nextInt(1024))
                .randomize(Double.class, () -> random.nextInt(1024) / 256.0d)
                .randomize(BigDecimal.class, () -> new BigDecimal(random.nextInt(1024))
                        .divide(new BigDecimal(256), 4, RoundingMode.DOWN))
                //.randomize(Object.class, () -> random.nextInt(1024))
                .stringLengthRange(3, 5)
                .collectionSizeRange(2, 3);
    }

    private Object randomInstance(Class<?> type, long seed) {
        val easyRandom = new EasyRandom(prepareRandomParameters(seed));
        return easyRandom.nextObject(type);
    }

    @EqualsAndHashCode
    public static class PartialPayload {

    }

    @EqualsAndHashCode
    public static class PartialResource extends PartialPayload {

    }

    @Data
    public static abstract class IdResource<K extends Serializable, T extends IdResource<K, ?>>
            extends PartialResource {

        private K id;

        @SuppressWarnings("unchecked")
        public T setId(K id) {
            this.id = id;
            return (T) this;
        }
    }

    @Data
    public static class PlanActivityGroupResource extends PartialResource {

        private ProductivityActivityResource activity;

        private long plannedTime;
    }

    @Data
    public static class ProductivityActivityResource extends IdResource<Long, ProductivityActivityResource> {

        private String name;

        @EqualsAndHashCode.Exclude
        @ToString.Exclude
        private ProductivityActivityResource parentActivity;

        private Long linkedEntityId;

        @EqualsAndHashCode.Exclude
        @ToString.Exclude
        private ProductivityActivityResource originalActivity;

        private Set<ProductivityAliasResource> productivityApplications = new HashSet<>();

        private long timeUsed;

        private String color;

        private ManagerResource manager;
    }

    @Getter
    @Setter
    @EqualsAndHashCode(callSuper = true)
    public static class ManagerResource extends AvatarResource<ManagerResource> {

    }

    @Getter
    @Setter
    @EqualsAndHashCode(callSuper = true)
    public static abstract class AvatarResource<P extends AvatarResource<P>> extends IdResource<Long, P> {

    }

    @Data
    public static class ProductivityAliasResource extends IdResource<Long, ProductivityAliasResource> {

        private String name;

        private Set<ActivityProcessResource> processes = new HashSet<>();
    }

    @Getter
    @Setter
    @EqualsAndHashCode(callSuper = true)
    public static class ActivityProcessResource extends IdResource<Long, ActivityProcessResource> {

        private String name;

        private String process;

        @ToString.Exclude
        @EqualsAndHashCode.Exclude
        private ProductivityAliasResource productivityAlias;
    }


}
