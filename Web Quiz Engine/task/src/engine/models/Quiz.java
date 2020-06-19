package engine.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity(name = "models.Quiz")
@JsonIgnoreProperties(value = {"answer", "userId"}, allowSetters = true)
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long userId;

    @NotNull
    private String title;

    @NotNull
    private String text;

    @NotNull
    @Size(min=2)
    private String[] options;

    private Integer[] answer;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setOptions(String[] options) {
        this.options = options.clone();
    }

    public String[] getOptions() {
        return options;
    }

    public void setAnswer(Integer[] answer) {
        this.answer = answer.clone();
    }

    public Integer[] getAnswer() {
        return answer == null ? new Integer[]{} : answer;
    }
}