package com.deapt.oneteambackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.deapt.oneteambackend.model.domin.Team;
import com.deapt.oneteambackend.model.domin.User;
import com.deapt.oneteambackend.model.dto.TeamQueryDTO;
import com.deapt.oneteambackend.model.request.TeamJoinRequest;
import com.deapt.oneteambackend.model.request.TeamUpdateRequest;
import com.deapt.oneteambackend.model.vo.TeamUserVO;

import java.util.List;

/**
* @author Acer
* @description 针对表【team(队伍)】的数据库操作Service
* @createDate 2025-06-13 10:21:59
*/
public interface TeamService extends IService<Team> {
    /**
     * 创建队伍
     * @param team 队伍
     * @param loginUser 登录用户
     * @return 队伍ID
     */
    long addTeam(Team team, User loginUser);

    /**
     * 查询队伍列表
     *
     * @param teamQueryDTO 队伍查询封装类
     * @param loginUser 当前登录用户
     * @return 队伍列表
     */
    List<TeamUserVO> listTeams(TeamQueryDTO teamQueryDTO, User loginUser);

    /**
     * 更新队伍信息
     * @param teamUpdateRequest 队伍更新请求
     * @return 是否更新成功
     */
    boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser);

    /**
     * 根据ID查询队伍信息
     * @param teamJoinRequest 队伍加入请求
     * @return 队伍信息
     */
    boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser);
}
