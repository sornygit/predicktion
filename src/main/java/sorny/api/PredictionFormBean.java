package sorny.api;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

/**
 * Created by Magnus on 2016-12-04.
 */
public class PredictionFormBean implements Serializable {
    // todo: remove pre-done values after initial setup
    private static final LocalDate MIN_DATE = LocalDate.now().plusDays(1);
    private Long id;
    @Length(max = 255)
    private String keyWords; // = "potus, trump";
    @NotEmpty
    @Length(max = 2048)
    private String prediction;
    @Length(max = 255)
    private String braggingLink; // = "http://img4.wikia.nocookie.net/__cb20111027164037/wikiality/images/thumb/5/54/I_told_you_so.jpg/500px-I_told_you_so.jpg";
    @Length(max = 2048)
    private String braggingMessage; // = "I TOLD YOU SO LOSER!";
    @AfterTomorrow
    private String unravelDate;
    @NotEmpty
    @Length(max = 255)
    private String topic;
    private Timestamp submitted;

    public PredictionFormBean() {
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getKeyWords() {
        return keyWords;
    }

    public void setKeyWords(String keyWords) {
        this.keyWords = keyWords;
    }

    public String getPrediction() {
        return prediction;
    }

    public void setPrediction(String prediction) {
        this.prediction = prediction;
    }

    public String getBraggingLink() {
        return braggingLink;
    }

    public void setBraggingLink(String braggingLink) {
        this.braggingLink = braggingLink;
    }

    public String getBraggingMessage() {
        return braggingMessage;
    }

    public void setBraggingMessage(String braggingMessage) {
        this.braggingMessage = braggingMessage;
    }

    public String getUnravelDate() {
        return unravelDate;
    }

    public Optional<LocalDate> getUnravelDateAsDate() {
        Optional<LocalDate> result;
        if (unravelDate != null) {
            try {
                LocalDate date = LocalDate.parse(unravelDate, DateTimeFormatter.ISO_DATE);
                result = Optional.of(date);
            } catch (DateTimeParseException e) {
                result = Optional.empty();
            }
        }
        else
            result = Optional.empty();

        return result;
    }

    public void setUnravelDate(String unravelDate) {
        this.unravelDate = unravelDate;
    }

    public void setSubmitted(Timestamp submitted) {
        this.submitted = submitted;
    }

    public Timestamp getSubmitted() {
        return submitted;
    }
}
