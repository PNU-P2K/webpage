package com.example.p2k.vm;

import com.example.p2k.user.BaseTimeEntity;
import com.example.p2k.user.User;
import jakarta.persistence.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name="vm_tb",
        indexes = {
                @Index(name = "vm_user_id_idx", columnList = "user_id")
        })
public class Vm extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String vmname;

    @ManyToOne
    private User user;

    private String password;

    private int cousrdid;

    private int port;

    private String containerId;

    private String state;

    @Column(nullable = false)
    private Boolean scope;

    @Column(nullable = false)
    private Boolean control;

    private String vmKey;

    @Column
    private String containerKey;

    @Builder
    public Vm(String vmname, User user, String password, int courseId, int port, String containerId, String state, Boolean scope, Boolean control, String vmKey, String containerKey) {
        this.vmname = vmname;
        this.user = user;
        this.password = password;
        this.cousrdid = courseId;
        this.port = port;
        this.containerId = containerId;
        this.state = state;
        this.scope = scope;
        this.control = control;
        this.vmKey = vmKey;
        this.containerKey = containerKey;
    }

    public void update(String state) {
        this.state = state;
    }
}
