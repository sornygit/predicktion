package sorny.domain.user;

import org.apache.commons.lang3.Validate;
import sorny.api.UserDetailsFormBean;
import sorny.api.error.BadPasswordException;
import sorny.api.error.IllegalEmailAddressException;
import sorny.api.error.InvalidUsernameException;
import sorny.domain.prediction.PredictionEntity;
import sorny.domain.security.PasswordHash;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * JPA entity for users
 */
@Entity
//@Cacheable(true)
//@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class UserEntity implements Serializable {
    public static final String emailAddressMatcher =
            "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?";
    public static final String standardUsernameMatcher = "^[a-zA-Z0-9_-]{3,30}$";
    public static final String passWordMatcher = "^(?=.*[0-9])(?=.*[a-z])(?=.*[!@#$%^&+=_-])(?=\\S+$).{8,}$";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Basic
    @Column(unique = true, nullable = false)
    public String username;

    @Basic
    @Column(nullable = false)
    public String password;

    @Basic
    @Column(unique = true, nullable = false)
    public String email;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
    private Set<PredictionEntity> predictionEntities;

    protected UserEntity() {
    }

    public UserEntity(String username, String password, String email) throws BadPasswordException, InvalidUsernameException, IllegalEmailAddressException {
        Validate.notEmpty(username);
        Validate.notEmpty(password);
        Validate.notEmpty(email);
        validateUsername(username, "");
        this.username = username;
        setPassword(password);
        setEmail(email);
        predictionEntities = new LinkedHashSet<>();
    }

    public long getId() {
        return id;
    }

    public Set<PredictionEntity> getPredictionEntities() {
        return predictionEntities;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) throws IllegalEmailAddressException {
        if (email == null || (!email.matches(emailAddressMatcher)))
            throw new IllegalEmailAddressException(email);
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public boolean isPassword(String password) {
        try {
            return PasswordHash.validatePassword(password, this.password);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void validateUsername(String username, String extraMessage) throws InvalidUsernameException {
        // Below regexp fetched from the web - valid email address
        if (username == null || username.isEmpty() || (!(username.matches(standardUsernameMatcher) || username.matches(emailAddressMatcher))))
            throw new InvalidUsernameException("Invalid username, must be between 3-30 chars and only consist of a-z, A-Z, 0-9, '-_@.'. " + extraMessage);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserEntity user = (UserEntity) o;

        if (id != user.id) return false;
        if (password != null ? !password.equals(user.password) : user.password != null) return false;
        if (username != null ? !username.equals(user.username) : user.username != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", number of predictions=" + predictionEntities.size() +
                '}';
    }

    public void addPrediction(PredictionEntity predictionEntity) {
        Validate.notNull(predictionEntity);
        if (!hasPredictionWithId(predictionEntity.getId()))
            predictionEntities.add(predictionEntity);
    }

    public PredictionEntity getPredictionWithId(Long id) {
        for (PredictionEntity predictionEntity : predictionEntities) {
            if (predictionEntity.getId().equals(id)) {
                return predictionEntity;
            }
        }
        return null;
    }

    public boolean hasPredictionWithId(Long id) {
        Validate.notNull(id);
        boolean hasPrediction = false;

        for (PredictionEntity predictionEntity : predictionEntities) {
            if (predictionEntity.getId().equals(id)) {
                hasPrediction = true;
                break;
            }
        }

        return hasPrediction;
    }

    public void removePrediction(PredictionEntity predictionEntity) {
        Validate.notNull(predictionEntity);
        predictionEntities.remove(predictionEntity);
    }

    public void setPassword(String password) throws BadPasswordException {
        if (!isPasswordApproved(password)) {
            String policyMessage = "Password isn't good enough - MUST be min 6 chars long. I'd however recommend 8+ chars, 1+ numbers, 1+ special characters.";
            throw new BadPasswordException(policyMessage);
        }

        try {
            this.password = PasswordHash.createHash(password);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isPasswordApproved(String password) {
        if (password == null || (!(password.length() >= 6))) {
            return false;
        } else return true;
    }

    public UserDetailsFormBean asForm() {
        final UserDetailsFormBean formBean = new UserDetailsFormBean();
        formBean.setEmail(getEmail());
        formBean.setUsername(getUsername());
        return formBean;
    }
}
