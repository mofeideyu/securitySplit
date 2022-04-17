package com.mofei.dao;

import com.mofei.entity.RoleInfo;
import com.mofei.entity.UsersInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserDao {
    UsersInfo loadUserByUserName(@Param("username") String username);

    List<RoleInfo> getRolesByUID(@Param("uid") Integer userId);
}
