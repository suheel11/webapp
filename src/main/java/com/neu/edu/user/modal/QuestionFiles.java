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
@Table(name="QuestionFiles")
public class QuestionFiles {
    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    private String fileId;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY,value="questionId")
    private String questionId;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY,value="userId")
    private String userId;

    private String fileName;
    private String createdDate;
    private String s3objectName;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY,value="size")
    private String size;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY,value="mime")
    private String mime;
}