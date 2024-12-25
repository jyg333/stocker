package com.stocker.backend.repository;

import com.stocker.backend.model.composite.MemberRoleId;
import com.stocker.backend.model.entity.MemberRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberRoleRepository extends JpaRepository<MemberRole, MemberRoleId> {

    @Modifying
//    @Query("DELETE FROM MemberRole mr where mr.idx = :idx")
    @Query("DELETE FROM MemberRole mr where mr.id.idx = :idx")
    void deleteAllByIdx(@Param("idx") Integer idx);

    @Query("SELECT ri.rolesName FROM RoleInfo ri JOIN MemberRole mr ON mr.rolesId=ri.rolesId WHERE mr.idx=:idx")
    List<String> findRoleNameByIdx(@Param("idx") Integer idx);

    MemberRole findByIdx(Integer idx);


}
