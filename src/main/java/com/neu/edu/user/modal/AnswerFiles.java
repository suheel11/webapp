package com.neu.edu.user.modal;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="AnswerFiles")
public class AnswerFiles {
    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    private String fileId;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY,value="answerId")
    private String answerId;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY,value="userId")
    private String userId;
    private String fileName;

    private String createdDate;
    private String s3objectName;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY,value="size")
    @Column(name="size",nullable=false)
    private String size;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY,value="mime")
    @Column(name="mime",nullable=false)
    private String mime;
}