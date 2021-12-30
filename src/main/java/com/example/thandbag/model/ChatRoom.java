package com.example.thandbag.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ChatRoom {

    @Id
    private String id;

    @Column (nullable = false)
    private Long pubUserId;

    @Column (nullable = false)
    private Long subUserId;
}
