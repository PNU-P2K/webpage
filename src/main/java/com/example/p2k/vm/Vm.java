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

    private int cousrdid;

    @Column(nullable = false)
    private Boolean scope;
    @Column(nullable = false)
    private Boolean control;

    @Column(nullable = false)
    private String vmkey;

    @Builder
    public Vm(User user, int cousrdid, Boolean scope, Boolean control, String vmkey) {
        this.user = user;
        this.cousrdid = cousrdid;
        this.scope = scope;
        this.control = control;
        this.vmkey = vmkey;
    }
}
