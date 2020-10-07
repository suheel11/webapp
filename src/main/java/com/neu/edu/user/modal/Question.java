package com.neu.edu.user.modal;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "QUESTION_TBL")
public class Question {

    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    private String questionId;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY, value="created_timestamp")
    private String created_timestamp;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY, value="updated_timestamp")
    private String updated_timestamp;
    private String question_text;
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "ques_cats",
            joinColumns = {@JoinColumn(name = "questionId")},
            inverseJoinColumns = {@JoinColumn(name = "categoryId")})
    private Set<Category> categories=new HashSet<>();
    @OneToMany(targetEntity = Answer.class,cascade = CascadeType.ALL)
    @JoinColumn(name = "qa_fk",referencedColumnName = "questionId")
    private List<Answer> answers;
    private String userId;
}
