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

        private CompanyResource company;

        private List<MarketplaceMemberResource> savedCandidates;

        private List<RejectedMemberResource> rejectedMembers;

        private List<MarketplaceMemberResource> rejectedCandidates;

        private List<JobResource> subscriptionJobs;

        private Boolean feedbackRequired;

        private Date lastFeedbackDate;

        private List<AvailableSlotResource> availableSlots;

        private String zoomHostId;

        private Boolean manualTimeNotificationsEnabled;

        private List<WorkflowJiraManagerResource> workflowJiraManagers;

    }

    @Getter
    @Setter
    @EqualsAndHashCode(callSuper = true)
    public static class WorkflowJiraManagerResource extends IdResource<Long, WorkflowJiraManagerResource> {

    }

    @Getter
    @Setter
    @EqualsAndHashCode(callSuper = true)
    public static class TeamResource extends IdResource<Long, TeamResource> {

    }

    @Getter
    @Setter
    @EqualsAndHashCode(callSuper = true)
    public static class WorkflowStateMappingResource extends PartialResource {

        private WorkflowStateResource workflowState;
        private String jiraStatusName;
        private Integer sequenceNumber;
    }

    @Getter
    @Setter
    @EqualsAndHashCode(callSuper = true)
    public static class WorkflowStateResource extends IdResource<Long, WorkflowStateResource> {

        private String name;
    }

    @Getter
    @Setter
    @EqualsAndHashCode(callSuper = true)
    public static class WorkflowJiraProjectResource extends IdResource<Long, WorkflowJiraProjectResource> {

        private Long jiraId;
        private String jiraKey;
        private String jiraName;
        private String groupName;
        private Long permissionSchemeId;
        private WorkflowJiraServerResource workflowJiraServer;
        private Date lastSyncedAt;
    }

    @Getter
    @Setter
    @EqualsAndHashCode(callSuper = true)
    public static class WorkflowJiraServerResource extends IdResource<Long, WorkflowJiraServerResource> {

        private String name;
        private String url;
        private String username;
        private byte[] password;
        private String rawPassword;
    }


    @Getter
    @Setter
    @EqualsAndHashCode(callSuper = true)
    public static class TeamCategoryResource extends IdResource<Long, TeamCategoryResource> {

        private String name;
    }

    @Getter
    @Setter
    @EqualsAndHashCode(callSuper = true)
    public static class TeamTemplateResource extends IdResource<Long, TeamTemplateResource> {

        private String name;
        private Date createdOn;
        private Date updatedOn;
        private String goal;
        private List<TeamTemplateSeatResource> templateSeats;
        private TeamCategoryResource teamCategory;
    }

    @Getter
    @Setter
    @EqualsAndHashCode(callSuper = true)
    public static class TeamTemplateSeatResource extends PartialResource {

        private JobResource job;
        private Integer count;
        private String description;
    }

    @Data
    public static class TeamDemandResource extends IdResource<Long, TeamDemandResource> {

        private JobResource job;

        @EqualsAndHashCode.Exclude
        @ToString.Exclude
        private TeamResource team;

        private String description;
    }

    @Getter
    @Setter
    @EqualsAndHashCode(callSuper = true)
    public static class MetricSetupResource extends IdResource<Long, MetricSetupResource> {

        @EqualsAndHashCode.Exclude
        @ToString.Exclude
        private TeamResource team;

        private String host;
        private String username;
        private String password;
        private byte[] encodedPassword;
        private String doneCriteria;
        private String projects;
        private boolean active;
        private Date createdOn;
        private String customQuery;
        private String advancedQuery;
        private String metricName;
        private BigDecimal metricTarget;
        private String worksheetName;
        private MetricType type;
        private String customFieldId;
        private boolean currentTeamMetric;
        private int errorCount;
        private SpreadsheetAccess spreadsheetAccess;
        private Date metricUpdatedOn;
    }

    public enum SpreadsheetAccess {
        SHARED,
        OAUTH
    }

    public enum MetricType {
        JIRA,
        ZENDESK,
        MANUAL,
        SPREADSHEET,
        SALESFORCE,
        DESK
    }

    @Getter
    @Setter
    @EqualsAndHashCode(callSuper = true)
    public static class AvailableSlotResource extends IdResource<Long, AvailableSlotResource> {
    }

    @Getter
    @Setter
    @EqualsAndHashCode(callSuper = true)
    public static class JobResource extends IdResource<Long, JobResource> {

    }

    @Getter
    @Setter
    @EqualsAndHashCode(callSuper = true)
    public static class RejectedMemberResource extends IdResource<Long, RejectedMemberResource> {

    }

    @Data
    public static class MarketplaceMemberResource extends IdResource<Long, MarketplaceMemberResource> {

    }

    @Data
    public static class ApplicationResource extends IdResource<Long, ApplicationResource> {


    }

    @Getter
    @Setter
    @EqualsAndHashCode(callSuper = true)
    public static class CompanyResource extends IdResource<Long, CompanyResource> {

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
