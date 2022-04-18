package com.mofei.service.impl;

import com.mofei.dao.UserDao;
import com.mofei.entity.UsersInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
public class MyUserDetailService implements UserDetailsService, UserDetailsPasswordService {
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

    /**默认使用DelegatingPasswordEncoder 默认使用相当最安全的密码加密方式Bcrypt*/
    @Override
    public UserDetails updatePassword(UserDetails user, String newPassword) {
        Integer result = userDao.updatePassword(user.getUsername(), newPassword);
        if (result == 1) {
            ((UsersInfo)user).setPassword(newPassword);
        }
        return user;
    }
}
