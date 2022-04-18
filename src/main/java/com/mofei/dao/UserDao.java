package com.mofei.dao;

import com.mofei.entity.RoleInfo;
import com.mofei.entity.UsersInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

@Mapper
public interface UserDao {
    /**根据用户名出查询用户信息*/
    UsersInfo loadUserByUserName(@Param("username") String username);

    /**根据用户id查询用户角色*/
    List<RoleInfo> getRolesByUID(@Param("uid") Integer userId);

    /**根据用户名更新用户密码*/
    Integer updatePassword(@Param("username") String username, @Param("password") String password);
}
