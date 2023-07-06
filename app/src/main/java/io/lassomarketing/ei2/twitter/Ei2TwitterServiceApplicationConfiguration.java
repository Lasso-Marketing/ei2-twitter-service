package io.lassomarketing.ei2.twitter;

import com.google.api.gax.core.CredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.Clock;

@Configuration
public class Ei2TwitterServiceApplicationConfiguration {

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }

    static CredentialsProvider credentialsProvider() {
        return GoogleCredentials::getApplicationDefault;
    }

    /*
     * Uses google-auth-library-java which supports Workload Identity Federation
     */
    @Bean
    @Primary
    CredentialsProvider googleCredentials() {
        return credentialsProvider();
    }
}
