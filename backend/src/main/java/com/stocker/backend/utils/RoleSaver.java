package com.stocker.backend.utils;


import com.stocker.backend.model.entity.MemberRole;
import com.stocker.backend.repository.MemberRoleRepository;
import com.stocker.backend.repository.RoleInfoRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RoleSaver {

    public void saveRole(RoleInfoRepository roleInfoRepository, MemberRoleRepository memberRoleRepository,List<Integer> roleIds,Integer idx){

        List<MemberRole> memberRoles = new ArrayList<>();
        for(Integer roleId : roleIds){

            MemberRole memberRole = new MemberRole();
            memberRole.setIdx(idx);
            memberRole.setRolesId(roleId);

            memberRole.setRolesInfo(roleInfoRepository.findById(roleId).orElse(null));
            memberRoles.add(memberRole);

        }
        memberRoleRepository.saveAll(memberRoles);
    }
}
