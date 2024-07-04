package com.mymall.web.manager.security;

import com.mymall.contract.system.AdminService;
import com.mymall.contract.system.ResourceService;
import com.mymall.pojo.system.Admin;

import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.*;

public class UserDetailsServiceImpl implements UserDetailsService {

    @DubboReference
    private AdminService adminService;

    @DubboReference
    private ResourceService resourceService;

    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        //查询管理员
        Map map= new HashMap();
        map.put("loginName",s);
        map.put("status","1");
        List<Admin> list = adminService.findList(map);
        if(list.size()==0){
            return null;
        }

        //构建权限列表
        List<GrantedAuthority> grantedAuths = new ArrayList<GrantedAuthority>();
        List<String> resKeyList = resourceService.findResKeyByLoginName(s);
        for(String resKey :resKeyList ){
            grantedAuths.add(new SimpleGrantedAuthority(resKey));
        }

        return new User(s,list.get(0).getPassword(), grantedAuths);
    }

}
