package io.lassomarketing.ei2.twitter.jpa.repository;

import io.lassomarketing.ei2.twitter.jpa.model.AudienceUploadStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AudienceUploadStatisticsRepository extends JpaRepository<AudienceUploadStatistics, String> {
}
