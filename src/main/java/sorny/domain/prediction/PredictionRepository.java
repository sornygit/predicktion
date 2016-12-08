package sorny.domain.prediction;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Magnus on 2016-12-04.
 */
public interface PredictionRepository extends JpaRepository<PredictionEntity, Long> {
}
