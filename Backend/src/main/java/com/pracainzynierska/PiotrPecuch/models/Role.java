package com.pracainzynierska.PiotrPecuch.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

@Entity
@Table(name = "roles")
@Getter
@Setter
public class Role implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(length=20)
    private ERole name;

    public Role(){

    }

    public Role(ERole name){
        this.name = name;
    }

    @Override
    public String getAuthority() {
        return name.name();
    }
}
