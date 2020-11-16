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

    public enum CategoryLevel {
        DEFAULT, MANAGER, USER, TEAM, ASSIGNMENT
    }

    public enum CategoryRootType {
        PRODUCTIVE, COMMUNICATION, DISTRACTION, UNCATEGORIZED;
    }

    @Data
    public static class ProductivityActivityResource extends IdResource<Long, ProductivityActivityResource> {

        private CategoryLevel level;

        private String name;

        @EqualsAndHashCode.Exclude
        @ToString.Exclude
        private ProductivityActivityResource parentActivity;

        private Long linkedEntityId;

        @EqualsAndHashCode.Exclude
        @ToString.Exclude
        private ProductivityActivityResource originalActivity;

        private Set<ProductivityAliasResource> productivityApplications = new HashSet<>();

        private List<AliasActivityResource> allAliases = new ArrayList<>();

        private long timeUsed;

        private String color;

//        private UserResource owner;

        private CategoryRootType rootType;

        private ManagerResource manager;
    }

    public enum AvatarType {
        CANDIDATE,
        MANAGER
    }

    @Getter
    @Setter
    @EqualsAndHashCode(callSuper = true)
    public static class ManagerResource extends AvatarResource<ManagerResource> {

        private CompanyResource company;

//        private UserResource accountManager;

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

        private AvatarType type;

        public boolean isPersonal() {
            return false;
        }

        public boolean isManager() {
            return true;
        }

        public boolean isCompanyAdmin() {
            return false;
        }

        public boolean isCandidate() {
            return false;
        }
    }

    @Getter
    @Setter
    @EqualsAndHashCode(callSuper = true)
    public static class WorkflowJiraManagerResource extends IdResource<Long, WorkflowJiraManagerResource> {

        private String username;

        @EqualsAndHashCode.Exclude
        @ToString.Exclude
        private ManagerResource manager;

        @EqualsAndHashCode.Exclude
        @ToString.Exclude
        private TeamResource team;
    }

    @Getter
    @Setter
    @EqualsAndHashCode(callSuper = true)
    public static class TeamResource extends IdResource<Long, TeamResource> {

        private String name;
        private CompanyResource company;
        private ManagerResource teamOwner;
        private List<ManagerResource> watchers;
        private Date createdOn;
        private Date updatedOn;
        private Long dfcTeamId;
        private List<AgreementResource> agreements;
        private List<MetricSetupResource> metricsSetups;
        private List<ManagerResource> reportingManagers;
        private List<TeamDemandResource> demands;
        private TeamTemplateResource teamTemplate;
        private TeamCategoryResource teamCategory;
        private MarketplaceMemberStatus marketplaceStatus;
        private TeamManagerChangeType teamManagerChangeType;
        private BigDecimal weeklyCost;
        private WorkflowJiraProjectResource workflowJiraProject;
        private List<WorkflowStateMappingResource> workflowStateMapping;
        private boolean deleted;
        private CommunicationStatus communicationStatus;
    }

    public enum CommunicationStatus {
        ON, OFF, NOT_SET;
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

    public enum TeamManagerChangeType {
        ALL,
        DISABLE_FOR_DIRECT_MANAGERS,
        DISABLE_FOR_ALL_MANAGERS,
    }

    public enum MarketplaceMemberStatus {
        AVAILABLE,
        ASSIGNED,
        INACTIVE
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
        private MetricComputation metricComputation;
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

    public enum MetricComputation {
        CUSTOM_FIELD_SUM,
        ISSUE_COUNT
    }

    @Getter
    @Setter
    @EqualsAndHashCode(callSuper = true)
    public static class AgreementResource extends PartialResource {

        private String url;
        private String fileName;
        private Date createdOn;
        private Date updatedOn;
    }

    @Getter
    @Setter
    @EqualsAndHashCode(callSuper = true)
    public static class AvailableSlotResource extends IdResource<Long, AvailableSlotResource> {

        private Date startDateTime;

        private Date endDateTime;

        @EqualsAndHashCode.Exclude
        @ToString.Exclude
        private ManagerResource manager;
    }

    @Getter
    @Setter
    @EqualsAndHashCode(callSuper = true)
    public static class JobResource extends IdResource<Long, JobResource> {

        private String title;
    }

    @Getter
    @Setter
    @EqualsAndHashCode(callSuper = true)
    public static class RejectedMemberResource extends IdResource<Long, RejectedMemberResource> {

        private MarketplaceMemberResource marketplaceMember;

        @EqualsAndHashCode.Exclude
        @ToString.Exclude
        private ManagerResource manager;

        private Date rejectedOn;
    }

    @Data
    public static class MarketplaceMemberResource extends IdResource<Long, MarketplaceMemberResource> {

        private ApplicationResource application;
    }

    @Data
    public static class ApplicationResource extends IdResource<Long, ApplicationResource> {

        private CandidateResource candidate;
        private JobResource job;

    }

    @Data
    public static class CandidateResource extends AvatarResource<CandidateResource> {

        private String skypeId;

    }

    @Getter
    @Setter
    @EqualsAndHashCode(callSuper = true)
    public static class CompanyResource extends IdResource<Long, CompanyResource> {

        private String name;

        private String website;

        private LocationResource location;

        private Long dfcCompanyId;

        private boolean internal;

        private float xoPercentage;

        private Date createdOn;

        private Date updatedOn;

        private float currentBalance;

        private String bdcCustomerId;

        private CommunicationStatus communicationStatus;
    }

    @Getter
    @Setter
    @EqualsAndHashCode(callSuper = true)
    public static class LocationResource extends PartialResource {

        private static final int MAX_PHONE_LENGTH = 18;

        private static final String PHONE_LENGTH_EXCEED_ERROR_MESSAGE =
                "Phone number must not be more than " + MAX_PHONE_LENGTH + " characters long.";

        // Other details to be added in future tickets

        private CountryResource country;

        private TimeZoneResource timeZone;

        private String city;

        private String state;

        private String zip;

        private String address;

        private BigDecimal latitude;

        private BigDecimal longitude;

        private String phone;

    }

    @Getter
    @Setter
    @EqualsAndHashCode(callSuper = true)
    public static class CountryResource extends IdResource<Long, CountryResource> {

        private String name;

        private String code;

        private boolean allowed;

        private List<TimeZoneResource> timezones;

    }

    @Getter
    @Setter
    @EqualsAndHashCode(callSuper = true)
    public static class TimeZoneResource extends IdResource<Long, TimeZoneResource> {

        private String name;

        private Integer standardOffset;

        private Integer offset;

        private String hourlyOffset;

    }

    @Getter
    @Setter
    @EqualsAndHashCode(callSuper = true)
    public static abstract class AvatarResource<P extends AvatarResource<P>> extends IdResource<Long, P> {

        public static final String FIELD_PRINTABLE_NAME = "printableName";
        public static final String FIELD_USER_ID = "userId";
        public static final String FIELD_FIRST_NAME = "firstName";
        public static final String FIELD_LAST_NAME = "lastName";
        public static final String FIELD_EMAIL = "email";
        public static final String FIELD_PHOTO_URL = "photoUrl";
        public static final String FIELD_AVATAR_TYPES = "avatarTypes";
        public static final String FIELD_USER_AVATARS = "userAvatars";
        public static final String FIELD_USER_SECURITY = "userSecurity";
        public static final String FIELD_HEADLINE = "headline";
        public static final String FIELD_SUMMARY = "summary";

        // The class being public allows java.lang.Class.getMethod to return a Method with generic information even when
        // it's called from a different package. Useful in BaseResourceTest (located in a different package).

        // Other details to be added in future tickets

//        private UserResource user = new UserResource();

        private LocationResource location;

        //        @Proxy(AvatarResource.FIELD_USER)
//        public String getPrintableName() {
//            return user.getPrintableName();
//        }

//        //        @Proxy(AvatarResource.FIELD_USER)
//        public Long getUserId() {
//            return user.getId();
//        }
//
//        //        @Proxy(AvatarResource.FIELD_USER)
//        public String getFirstName() {
//            return user.getFirstName();
//        }
//
//        //        @Proxy(AvatarResource.FIELD_USER)
//        public String getLastName() {
//            return user.getLastName();
//        }
//
//        //        @Proxy(AvatarResource.FIELD_USER)
//        public String getEmail() {
//            return user.getEmail();
//        }
//
//        //        @Proxy(AvatarResource.FIELD_USER)
//        public String getPhotoUrl() {
//            return user.getPhotoUrl();
//        }
//
//        //        @Proxy(AvatarResource.FIELD_USER)
//        public List<AvatarType> getAvatarTypes() {
//            // Return a copy to avoid avatarTypes being mapped twice
//            return user.getAvatarTypes() == null ? new ArrayList<>() : new ArrayList<>(user.getAvatarTypes());
//        }
//
//        //        @Proxy({AvatarResource.FIELD_USER, UserResource.FIELD_USER_AVATARS})
//        public List<UserAvatarResource> getUserAvatars() {
//            // Return a copy to avoid userAvatars being mapped twice
//            return user.getUserAvatars() == null ? new ArrayList<>() : new ArrayList<>(user.getUserAvatars());
//        }
//
//        //        @Proxy({AvatarResource.FIELD_USER, UserResource.FIELD_USER_SECURITY})
//        public UserSecurityResource getUserSecurity() {
//            return user.getUserSecurity();
//        }
//
//        //        @Proxy(AvatarResource.FIELD_USER)
//        public String getHeadline() {
//            return user.getHeadline();
//        }
//
//        //        @Proxy(AvatarResource.FIELD_USER)
//        public String getSummary() {
//            return user.getSummary();
//        }

        public P setLocation(LocationResource location) {
            this.location = location;
            return (P) this;
        }

    }

    @Getter
    @Setter
    @EqualsAndHashCode(callSuper = true)
    public static class UserSecurityResource extends PartialResource {

        public static final String FIELD_ACCOUNT_NON_EXPIRED = "accountNonExpired";
        public static final String FIELD_ACCOUNT_NON_LOCKED = "accountNonLocked";
        public static final String FIELD_CREDENTIALS_NON_EXPIRED = "credentialsNonExpired";

        private boolean linkedInLogin;
        private boolean enabled;
        private String securityQuestion;

        public boolean isAccountNonExpired() {
            return true;
        }

        public boolean isAccountNonLocked() {
            return true;
        }

        public boolean isCredentialsNonExpired() {
            return true;
        }

    }

    @Getter
    @Setter
    @EqualsAndHashCode(callSuper = true)
    public static class UserAvatarResource extends IdResource<Long, UserAvatarResource> {

        private AvatarType type;

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

    @Data
    public static class AliasActivityResource extends IdResource<Long, AliasActivityResource> {

        @ToString.Exclude
        @EqualsAndHashCode.Exclude
        private ProductivityActivityResource activity;

        private ProductivityAliasResource alias;

        private CategorizationType type = CategorizationType.INCLUDE;
    }

    public enum CategorizationType {
        INCLUDE, EXCLUDE
    }

//    @Data
//    public static class UserResource extends IdResource<Long, UserResource> {
//
//        private String headline;
//
//        private String summary;
//
//        private String fullName;
//
//        private String printableName;
//
//        private String firstName;
//
//        private String lastName;
//
//        private String email;
//
//        private String photoUrl;
//
//        private List<AvatarType> avatarTypes;
//
//        private List<UserAvatarResource> userAvatars;
//
//        private UserSecurityResource userSecurity;
//
//        private boolean infoShared;
//
//        private LocationResource location;
//    }

}
