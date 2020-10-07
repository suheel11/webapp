package com.neu.edu.user.modal;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ANSWER_TBL")
public class Answer {

    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    private String answerId;
    private String questionId;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY, value="created_timestamp")
    private String created_timestamp;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY, value="updated_timestamp")
    private String updated_timestamp;
    private String userId;
    private String answer_text;
}
