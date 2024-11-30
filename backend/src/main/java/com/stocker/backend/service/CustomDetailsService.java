package com.stocker.backend.service;

import com.stocker.backend.model.CustomUserDetails;
import com.stocker.backend.model.entity.Member;
import com.stocker.backend.repository.MemberRepository;
import com.stocker.backend.repository.RoleInfoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/*Spring Security가 로그인시 UserDetailService 인터페이스를 구현해야한다
* loadUserByUsername method를 구현하도록 강제하는 인터페이스로 사용자 명으로 security User 객체를 조회하여 반환하는 메서드*/
@RequiredArgsConstructor
@Service
public class CustomDetailsService implements UserDetailsService {

//    private static final Logger logger = LogManager.getLogger(CustomDetailsService.class);

    private final MemberRepository memberRepository;
    private final RoleInfoRepository roleInfoRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Member member = memberRepository.findById(username);

        if (member == null) {
            throw new UsernameNotFoundException("사용자가 입력한 아이디에 해당하는 사용자를 찾을 수 없습니다.");
        }

        //2024-08-08 해당 유저의 권한 값을 DB에서 불러오는 기능 추가
        List<String> roles = memberRepository.findRoleNameById(member.getId());

        Collection<SimpleGrantedAuthority> grant = new ArrayList<>();
        roles.forEach(data -> grant.add(new SimpleGrantedAuthority(data)));

        CustomUserDetails customUserDetails = new CustomUserDetails();

        customUserDetails.setUsername(member.getId());
        customUserDetails.setPassword(member.getPassword());
        customUserDetails.setEnabled(true);
        customUserDetails.setAccountNonExpired(true);
        customUserDetails.setAccountNonLocked(true);
        customUserDetails.setCredentialsNonExpired(true);
        customUserDetails.setAuthorities(grant);
        return customUserDetails;
    }
}




