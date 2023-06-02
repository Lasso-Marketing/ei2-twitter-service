package io.lassomarketing.ei2.snapchat.jpa.repository;

import io.lassomarketing.ei2.snapchat.jpa.model.Audience;
import io.lassomarketing.ei2.snapchat.jpa.model.AudienceId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AudienceRepository extends JpaRepository<Audience, AudienceId> {
}
