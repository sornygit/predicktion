package sorny.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * JPA repository for User entities
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    UserEntity getByUsername(String username);
}
