package io.lassomarketing.ei2.twitter.api.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;

@Data(staticConstructor = "of")
public class TwitterAudienceUsersOperation {

    @JsonProperty("operation_type")
    private final OperationType operationType;

    private final Params params;

    public enum OperationType {Update, Delete}

    @Data(staticConstructor = "of")
    public static class Params {

        @JsonProperty("effective_at")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
        private final ZonedDateTime effectiveAt;

        @JsonProperty("expires_at")
        @JsonInclude(Include.NON_NULL)
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
        private final ZonedDateTime expiresAt;

        private final List<User> users;
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @JsonInclude(Include.NON_NULL)
    public static class User {

        @JsonProperty("email")
        private final List<String> emails;

        @JsonProperty("device_id")
        private final List<String> deviceIds;

        public static User ofEmail(String email) {
            return new User(List.of(email), null);
        }

        public static User ofMadid(String madid) {
            return new User(null, List.of(madid));
        }
    }
}
