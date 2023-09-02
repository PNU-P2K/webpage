package com.example.p2k.vm;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VmRepository extends JpaRepository<Vm, Long> {

    @Query("select v from Vm v where v.user.id= :id")
    List<Vm> findAllByUserId(@Param("id") Long id);

    @Query("select v from Vm v where v.user.id = :userId and v.course.id = :id")
    List<Vm> findUserIdAndCourseId(@Param("userId") Long userId, @Param("id") Long id);

    Optional<Vm> findByVmKey(@Param("vmKey") String vmKey);

    @Modifying
    @Query("delete from Vm v where v.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}
