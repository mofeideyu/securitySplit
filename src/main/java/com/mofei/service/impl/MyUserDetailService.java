package com.mofei.service.impl;

import com.mofei.dao.UserDao;
import com.mofei.entity.UsersInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
public class MyUserDetailService implements UserDetailsService {
    private final UserDao userDao;
    @Autowired
    public MyUserDetailService(UserDao userDao) {
        this.userDao = userDao;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UsersInfo usersInfo = userDao.loadUserByUserName(username);
        if (ObjectUtils.isEmpty(usersInfo)) {
            throw new UsernameNotFoundException("用户名不存在");
        }
        usersInfo.setRoles(userDao.getRolesByUID(usersInfo.getId()));
        return usersInfo;
    }
}
