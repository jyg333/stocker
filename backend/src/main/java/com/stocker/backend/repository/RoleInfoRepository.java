package com.stocker.backend.repository;

import com.stocker.backend.model.entity.RoleInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface RoleInfoRepository extends JpaRepository<RoleInfo, Integer> {

    @Query("SELECT ri.rolesName FROM RoleInfo ri LEFT JOIN MemberRole mr on mr.rolesId=ri.rolesId LEFT JOIN Member m on m.idx= mr.idx WHERE m.id= :id")
    List<String> findAllWithMemberRolesByMemberId(@Param("id") String id);
    @Query("SELECT ri.rolesId FROM RoleInfo ri WHERE ri.rolesName IN :roleNames")
    List<Integer> findRoleIdsByRoleNames(@Param("roleNames") List<String> roleNames);

    RoleInfo findByRolesId(Integer rolesId);

}
