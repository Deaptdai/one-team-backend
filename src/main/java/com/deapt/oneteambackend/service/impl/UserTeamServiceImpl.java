package com.deapt.oneteambackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.deapt.oneteambackend.mapper.UserTeamMapper;
import com.deapt.oneteambackend.model.domin.UserTeam;
import com.deapt.oneteambackend.service.UserTeamService;
import org.springframework.stereotype.Service;

/**
* @author Acer
* @description 针对表【user_team(用户队伍关系)】的数据库操作Service实现
* @createDate 2025-06-13 10:24:29
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService {

}




