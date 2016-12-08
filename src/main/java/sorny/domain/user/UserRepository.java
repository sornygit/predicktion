package sorny.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for {@link UserEntity} instances
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    UserEntity getByUsername(String username);
}
