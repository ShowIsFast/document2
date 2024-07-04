package com.mymall.common.cas.config;

import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CasUserDetailService implements AuthenticationUserDetailsService<CasAssertionAuthenticationToken> {

    private static Logger log = LoggerFactory.getLogger(CasUserDetailService.class);

    @Override
    public UserDetails loadUserDetails(CasAssertionAuthenticationToken casAssertionAuthenticationToken) throws UsernameNotFoundException {
        log.info("登陆用户名: " + casAssertionAuthenticationToken.getName());
        //由于登录交于cas服务端管理，如果进入这里代表登录成功，可不做任何操作，也可以根据自身系统业务开发相关功能代码
        return new User(casAssertionAuthenticationToken.getName(), "", AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER"));
    }

}