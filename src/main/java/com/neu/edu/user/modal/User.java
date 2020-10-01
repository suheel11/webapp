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
@Table(name = "USER_TBL")
public class User {

    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    //@ApiModelProperty(readOnly = true)
    //@JsonProperty(access = JsonProperty.Access.READ_ONLY)
   // @Column(name="userId", columnDefinition = "VARCHAR(255)", insertable = false, updatable = false, nullable = false)
    private String userId;
    @Column(name = "firstName")
    private String name;
    private String lastName;
    @Column(unique = true,nullable = false)
    private String email;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY, value="password")
    private String password;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY, value="accountCreated")
    private String accountCreated;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY, value="accountUpdated")
    private String accountUpdated;


    public User(User user) {
        this.name = user.getName();
        this.lastName = user.getLastName();
        this.password = user.getPassword();
        this.email = user.getEmail();
        this.userId = user.getUserId();
        this.accountCreated = user.getAccountCreated();
        this.accountUpdated = user.getAccountUpdated();
    }
}