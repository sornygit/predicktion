package sorny.domain;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import sorny.api.PredictionFormBean;
import sorny.api.error.*;
import sorny.domain.crypto.CryptoResult;
import sorny.domain.crypto.CryptoTool;
import sorny.domain.prediction.CameTrueStatus;
import sorny.domain.prediction.PredictionEntity;
import sorny.domain.prediction.PredictionRepository;
import sorny.domain.user.UserEntity;
import sorny.domain.user.UserRepository;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Main application service
 */
@Service
@Transactional
public class MainApplicationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MainApplicationService.class);
    private final String message = "User information is already taken by another user.";

    @Autowired
    UserRepository userRepository;

    @Autowired
    PredictionRepository predictionRepository;

    @PersistenceContext
    EntityManager entityManager;

    public MainApplicationService() {
        LOGGER.debug("Starting " + MainApplicationService.class.getSimpleName() + " ...");
    }

    @PostConstruct
    protected void init() {
        UserEntity admin = userRepository.getByUsername("admin");
        if (admin == null) {
            try {
                // The password below needs to be changed to something non-trivial before production,
                // and will crash app startup unless you set it!
                admin = new UserEntity("admin", "admin!", "sornydude@gmail.com");
            } catch (UserException e) {
                throw new RuntimeException("Couldn't create administrator user due to the following problem: " + e.getMessage());
            }

            admin = userRepository.save(admin);
            LOGGER.debug("Created [admin] user as it didn't exist.");
        }

        LOGGER.debug("Started.");
    }

    public UserEntity signUp(String username, String password, String email) throws UserAlreadyExistsException, BadPasswordException, InvalidUsernameException, IllegalEmailAddressException {
        Validate.notNull(username);
        UserEntity user = userRepository.getByUsername(username);

        if (user != null)
            throw new UserAlreadyExistsException(message);

        UserEntity saved;
        try {
            saved = userRepository.save(new UserEntity(username, password, email));
        } catch (Exception e) {
            throw new UserAlreadyExistsException(message);
        }
        LOGGER.info("User signed up: " + saved.getUsername());

        return saved;
    }

    public UserEntity getByUsername(String username) {
        Validate.notNull(username);
        UserEntity user = userRepository.getByUsername(username);
        if (user == null) throw new IllegalArgumentException("No user with username " + username);
        return user;
    }

    public UserEntity persist(UserEntity user) throws UserAlreadyExistsException {
        Validate.notNull(user);
        UserEntity u = entityManager.merge(user);
        try {
            u = userRepository.save(u);
        } catch (Throwable e) {
            throw new UserAlreadyExistsException(message);
        }

        if (LOGGER.isDebugEnabled())
            LOGGER.debug("User saved: " + u.getUsername());
        return u;
    }

    private PredictionEntity persist(PredictionEntity predictionEntity) {
        Validate.notNull(predictionEntity);
        PredictionEntity entity = predictionRepository.save(predictionEntity);
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Prediction saved: " + entity);
        return entity;
    }

    public UserEntity getCurrentlyLoggedInUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        UserEntity user = userRepository.getByUsername(username);

        if (user == null) throw new IllegalStateException("Logged in user doesn't exist in database. HAX!");

        return user;
    }

    public PredictionFormBean savePredictionForm(PredictionFormBean formBean) {
        UserEntity user = getCurrentlyLoggedInUser();
        PredictionEntity predictionEntity;
        final Long id = formBean.getId();
        final List<String> keyWordsList = CollectionUtils.arrayToList(StringUtils.split(formBean.getKeyWords(), ", "));
        if (id != null) {
            predictionEntity = user.getPredictionWithId(id);
            if (predictionEntity == null)
                throw new IllegalArgumentException("No prediction with id " + id);

            predictionEntity.setTopic(formBean.getTopic());
            final Optional<LocalDate> unravelDate = formBean.getUnravelDateAsDate();
            if (unravelDate.isPresent())
                predictionEntity.setUnravelDate(unravelDate.get());
            predictionEntity.setKeyWords(keyWordsList);
            predictionEntity.setBraggingMessage(formBean.getBraggingMessage());
            predictionEntity.setBraggingLink(formBean.getBraggingLink());
            user = userRepository.save(user);
        } else {
            final String prediction = formBean.getPrediction();
            if (StringUtils.isEmpty(prediction))
                throw new IllegalArgumentException("No content in prediction");

            final CryptoResult cryptoResult = CryptoTool.encrypt(prediction);
            try {
                predictionEntity = new PredictionEntity(formBean.getTopic(), keyWordsList,
                        cryptoResult.encryptedText, formBean.getBraggingLink(), formBean.getBraggingMessage(), user,
                        cryptoResult.aesKey);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            if (formBean.getUnravelDateAsDate().isPresent()) {
                final LocalDate unravelDate = formBean.getUnravelDateAsDate().get();
                if (!unravelDate.isAfter(LocalDate.now().plusDays(1))) {
                    throw new IllegalArgumentException("Date needs to be after tomorrow, or no proper prediction!");
                }
                predictionEntity.setUnravelDate(unravelDate);
            }
            predictionEntity = persist(predictionEntity);
        }

        final PredictionFormBean result = predictionEntity.asForm();
        return result;
    }

    public PredictionEntity unravelPrediction(Long id, CameTrueStatus cameTrueStatus) {
        UserEntity user = getCurrentlyLoggedInUser();
        PredictionEntity predictionEntity = user.getPredictionWithId(id);
        if (predictionEntity != null) {
            if (predictionEntity.getCameTrueStatus() != CameTrueStatus.SECRET)
                throw new IllegalStateException("Prediction has already been unravelled, can't do it again!");

            predictionEntity.setPrediction(predictionEntity.getCryptoKey(), predictionEntity.getEncryptedPrediction());
            predictionEntity.setCameTrueStatus(cameTrueStatus);
            predictionEntity.setUnravelled(new Timestamp(System.currentTimeMillis()));
            predictionEntity = persist(predictionEntity);
        } else {
            throw new IllegalArgumentException("User " + user.username + " does not have a prediction with id " + id);
        }
        return predictionEntity;
    }

    public boolean deletePrediction(Long id) {
        UserEntity user = getCurrentlyLoggedInUser();
        PredictionEntity predictionEntity = user.getPredictionWithId(id);
        if (predictionEntity != null) {
            user.removePrediction(predictionEntity);
            user = userRepository.save(user);
            predictionRepository.delete(id);
            return true;
        } else {
            return false;
        }
    }
}
