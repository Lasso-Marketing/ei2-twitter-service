package io.lassomarketing.ei2.snapchat.jpa.repository;

import io.lassomarketing.ei2.snapchat.jpa.model.AudienceUploadStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AudienceUploadStatisticsRepository extends JpaRepository<AudienceUploadStatistics, String> {
}
