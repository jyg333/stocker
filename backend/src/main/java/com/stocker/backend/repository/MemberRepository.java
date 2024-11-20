package com.stocker.backend.repository;


import com.stocker.backend.model.dto.MemberDetailDto;
import com.stocker.backend.model.dto.MemberDto;
import com.stocker.backend.model.dto.MemberSimpleDto;
import com.stocker.backend.model.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    @Query(value = "SELECT m.id, m.name, GROUP_CONCAT(DISTINCT r.roles_name ORDER BY r.roles_name) AS roles " +
            "FROM member m " +
            "JOIN member_roles mr ON m.idx = mr.idx " +
            "JOIN roles_info r ON mr.roles_id = r.roles_id " +
            "WHERE m.id IN ( SELECT m2.id FROM member m2 JOIN member_roles mr2 ON m2.idx = mr2.idx JOIN roles_info r2 ON mr2.roles_id = r2.roles_id WHERE r2.roles_name = :roleName ) " +
            "GROUP BY m.id " +
            "ORDER BY m.id", nativeQuery = true)
    List<Object[]> findMemberListDto(@Param("roleName") String roleName);

    @Query("SELECT new com.stocker.backend.model.dto.MemberDto(m.idx, m.id, m.position, m.activation, m.name) FROM Member m")
    List<MemberDto> findMemberDto();

    // 사용자 본인 정보조회
    @Query("SELECT new com.stocker.backend.model.dto.MemberDto(m.idx, m.id, m.position,  m.activation, m.name) FROM Member m WHERE m.id=:id")
    MemberDto findByIdDto(@Param("id") String id);

    @Query("SELECT m.idx FROM Member m WHERE m.id = :id")
    Integer findIdxById(@Param("id") String id);

    @Query("SELECT m.password FROM Member m WHERE m.id = :id")
    String findPasswordById(@Param("id") String id);

    Member findById(String id);

    //Id 기반으로 권한을 가져오는 쿼리
    @Query("SELECT ri.rolesName FROM RoleInfo ri JOIN MemberRole mr On ri.rolesId=mr.id.rolesId JOIN Member m on mr.id.idx=m.idx WHERE m.id=:id")
    ArrayList<String> findRoleNameById(@Param("id") String id);

    @Query("SELECT new com.stocker.backend.model.dto.MemberSimpleDto(m.idx,m.id, m.name, pl.updatedDt, ll.joinIp, ll.joinDt) FROM Member m JOIN PasswordLog pl ON m.id = pl.id JOIN LoginLog ll ON ll.id = m.id  WHERE m.id = :id")
    MemberSimpleDto findMemberDetailsById(@Param("id") String id);

    //UserDetail
    @Query("SELECT new com.stocker.backend.model.dto.MemberDetailDto(m.idx, m.id, m.name,m.activation, pl.updatedDt, ll.failCount, ll.failDt,ll.joinIp, ll.joinDt, ll.createDt, ll.createdBy, ll.updatedDt, ll.updatedBy) from Member m JOIN LoginLog ll on ll.id = m.id JOIN PasswordLog pl on pl.id = m.id WHERE m.id = :id")
    MemberDetailDto findDetailById(@Param("id") String id);

    // fail count 가 5회 인경우

    @Modifying
    @Query("UPDATE Member m SET m.activation=false WHERE m.id=:id")
    void updateActivation(@Param("id")String id);


    @Query(value = "SELECT m.id, m.position, m.activation, " +
        "ll.fail_count, " +
        "GROUP_CONCAT(DISTINCT ri.roles_name ORDER BY ri.roles_name SEPARATOR ', ') AS roles, " +
        "FROM member m " +
        "JOIN login_log ll ON m.id = ll.id " +
        "JOIN tdb.member_roles mr ON m.idx = mr.idx " +
        "JOIN roles_info ri ON ri.roles_id = mr.roles_id " +
        "GROUP BY m.id, m.idx order by m.idx LIMIT :limit OFFSET :offset",
        nativeQuery = true)
    List<Object[]> findMemberListDto(@Param("limit")Integer limit, @Param("offset") Integer offset);




}
