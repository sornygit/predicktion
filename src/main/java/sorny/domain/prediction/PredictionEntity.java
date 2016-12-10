package sorny.domain.prediction;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import sorny.api.PredictionFormBean;
import sorny.application.LinkType;
import sorny.domain.crypto.CryptoResult;
import sorny.domain.crypto.CryptoTool;
import sorny.domain.user.UserEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;

/**
 * JPA Entity for predictions
 */
@Entity
public class PredictionEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false)
    private String topic;

    @ElementCollection
    private List<String> keyWords = new ArrayList<>();

    @Column(length = 2048, nullable = true)
    private byte[] encryptedPrediction;

    @Column(length = 2048, nullable = true)
    private String prediction;

    @Column(nullable = false)
    private String cryptoKey;

    @Basic(optional = false)
    private CameTrueStatus cameTrueStatus = CameTrueStatus.SECRET;

    @Column(length = 2048, nullable = true)
    private String cameTrueComment;

    @Column(length = 255, nullable = true)
    private String braggingLink;

    @Column(length = 2048)
    private String braggingMessage;

    @Column(nullable = true)
    private LocalDate unravelDate;

    @Column(nullable = false)
    private Timestamp submitted;

    @Column(nullable = true)
    private Timestamp unravelled;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name="userid", nullable = false)
    private UserEntity user;

    // Needed by JPA
    protected PredictionEntity() {
    }

    public PredictionEntity(String topic, List<String> keyWords, byte[] encryptedPrediction, String braggingLink, String braggingMessage, UserEntity user, String cryptoKey) {
        this.topic = topic;
        this.keyWords = keyWords;
        this.encryptedPrediction = encryptedPrediction;
        this.braggingLink = braggingLink;
        this.braggingMessage = braggingMessage;
        this.user = user;
        this.cryptoKey = cryptoKey;
        user.addPrediction(this);
        submitted = new Timestamp(System.currentTimeMillis());
    }

    public byte[] getEncryptedPrediction() {
        return encryptedPrediction;
    }

    public void clearEncryptedPrediction() {
        this.encryptedPrediction = null;
    }

    public Timestamp getUnravelled() {
        return unravelled;
    }

    public void setUnravelled(Timestamp unravelled) {
        this.unravelled = unravelled;
    }

    public Timestamp getSubmitted() {
        return submitted;
    }

    public String getCryptoKey() {
        return cryptoKey;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Long getId() {
        return id;
    }

    public List<String> getKeyWords() {
        return keyWords;
    }

    public void addKeyWord(String keyWord) {
        Validate.notEmpty(keyWord);
        if (!keyWords.contains(keyWord)) {
            keyWords.add(keyWord);
        }
    }

    public void setKeyWords(List<String> keyWords) {
        Validate.notNull(keyWords);
        Validate.notEmpty(keyWords);
        this.keyWords = keyWords;
    }

    public UserEntity getUser() {
        return user;
    }

    public LocalDate getUnravelDate() {
        return unravelDate;
    }

    public void setUnravelDate(LocalDate unravelDate) {
        this.unravelDate = unravelDate;
    }

    public String getPrediction() {
        return prediction;
    }

    public void setPrediction(String key, byte[] encryptedPrediction) {
        Validate.notEmpty(key);
        Validate.notNull(encryptedPrediction);
        Validate.isTrue(Arrays.equals(encryptedPrediction, this.encryptedPrediction));
        String result = CryptoTool.decrypt(new CryptoResult(key, encryptedPrediction));
        this.prediction = result;
    }

    public CameTrueStatus getCameTrueStatus() {
        return cameTrueStatus;
    }

    public void setCameTrueStatus(CameTrueStatus cameTrueStatus) {
        Validate.notNull(cameTrueStatus);
        this.cameTrueStatus = cameTrueStatus;
    }

    public String getBraggingLink() {
        if (LinkType.isYouTubeLink(braggingLink))
            return braggingLink.replaceFirst("watch\\?v=", "embed/");
        else
            return braggingLink;
    }

    public void setBraggingLink(String braggingLink) {
        Validate.notEmpty(braggingLink);
        if (LinkType.isYouTubeLink(braggingLink))
            this.braggingLink = braggingLink.replaceFirst("watch\\?v=", "embed/");
        else
            this.braggingLink = braggingLink;
    }

    public String getBraggingMessage() {
        return braggingMessage;
    }

    public void setBraggingMessage(String braggingMessage) {
        Validate.notEmpty(braggingMessage);
        this.braggingMessage = braggingMessage;
    }

    @Override
    public String toString() {
        return "Prediction{" +
                "id=" + id +
                ", topic=" + topic +
                ", prediction=" + prediction +
                ", keyWords=" + keyWords +
                ", cameTrueStatus=" + cameTrueStatus +
                ", unravelDate=" + unravelDate +
                ", submitted=" + submitted +
                ", unravelled=" + unravelled +
                '}';
    }

    public PredictionFormBean asForm() {
        PredictionFormBean formBean = new PredictionFormBean();
        formBean.setId(getId());
        formBean.setTopic(getTopic());
        formBean.setBraggingLink(getBraggingLink());
        formBean.setBraggingMessage(getBraggingMessage());
        formBean.setKeyWords(StringUtils.join(getKeyWords(), ", "));
        formBean.setPrediction(getPrediction());
        if (getUnravelDate() != null)
            formBean.setUnravelDate(getUnravelDate().format(DateTimeFormatter.BASIC_ISO_DATE));
        formBean.setSubmitted(getSubmitted());
        return formBean;
    }

    public String getServerTimeZone() {
        return TimeZone.getDefault().toZoneId().getId();
    }
}
