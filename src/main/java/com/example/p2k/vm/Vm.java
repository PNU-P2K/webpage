package com.example.p2k.vm;

import com.example.p2k.course.Course;
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


    private String vmname;

    @ManyToOne
    private User user;

    private String password;

    private String description;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    private int port;

    private String containerId;
    private String imageId;

    private String state;

    @Column(nullable = false)
    private Boolean scope;

    @Column
    private Boolean control;

    private String vmKey;

    @Column
    private String containerKey;

    @Builder
    public Vm(String vmname, User user, String password, String description, Course course, int port, String containerId, String imageId, String state, Boolean scope, Boolean control, String vmKey, String containerKey) {
        this.vmname = vmname;
        this.user = user;
        this.password = password;
        this.description = description;
        this.course = course;
        this.port = port;
        this.containerId = containerId;
        this.imageId = imageId;
        this.state = state;
        this.scope = scope;
        this.control = control;
        this.vmKey = vmKey;
        this.containerKey = containerKey;
    }

    public void updateState(String state) {
        this.state = state;
    }

    public void updateContaierId(String containerId) {
        this.containerId = containerId;
    }

    public void updateImageId(String imageId) {
        this.imageId = imageId;
    }

    public void update(Vm vm) {
        this.vmname = vm.getVmname();
    }
}
