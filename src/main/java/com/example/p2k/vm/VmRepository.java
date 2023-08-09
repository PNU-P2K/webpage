package com.example.p2k.vm;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VmRepository extends JpaRepository<Vm, Long> {

    @Query("select v from Vm v where v.user.id= :id")
    List<Vm> findAllByUserId(Long id);
}
