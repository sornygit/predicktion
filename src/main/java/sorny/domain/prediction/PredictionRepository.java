package sorny.domain.prediction;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data JPA repository for {@link PredictionEntity} instances
 */
public interface PredictionRepository extends JpaRepository<PredictionEntity, Long> {
}
