package com.deapt.oneteambackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.deapt.oneteambackend.common.result.StatusCode;
import com.deapt.oneteambackend.exception.BaseException;
import com.deapt.oneteambackend.mapper.TeamMapper;
import com.deapt.oneteambackend.model.domin.Team;
import com.deapt.oneteambackend.model.domin.User;
import com.deapt.oneteambackend.model.domin.UserTeam;
import com.deapt.oneteambackend.model.dto.TeamQueryDTO;
import com.deapt.oneteambackend.model.enums.TeamStatusEnum;
import com.deapt.oneteambackend.model.request.TeamJoinRequest;
import com.deapt.oneteambackend.model.request.TeamQuitRequest;
import com.deapt.oneteambackend.model.request.TeamUpdateRequest;
import com.deapt.oneteambackend.model.vo.TeamUserVO;
import com.deapt.oneteambackend.model.vo.UserVO;
import com.deapt.oneteambackend.service.TeamService;
import com.deapt.oneteambackend.service.UserService;
import com.deapt.oneteambackend.service.UserTeamService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;

/**
* @author Acer
* @description 针对表【team(队伍)】的数据库操作Service实现
* @createDate 2025-06-13 10:21:59
*/
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService {
    @Resource
    private UserTeamService userTeamService;
    @Resource
    private UserService userService;
    @Override
    @Transactional(rollbackFor = Exception.class)
    public long addTeam(Team team, User loginUser) {
        //1.请求参数是否为空
        if (team == null){
            throw new BaseException(StatusCode.PARAMETER_ERROR,"请求参数为空");
        }
        //2.用户是否登录，未登录不允许创建
        if (loginUser == null){
            throw new BaseException(StatusCode.PARAMETER_ERROR,"用户未登录");
        }
        final Long userId = loginUser.getId();

        //3.校验信息
        //队伍人数>1且<=20
        int maxNum = Optional.ofNullable(team.getMaxNum()).orElse(0) ;
        if (maxNum < 1 || maxNum > 20){
            throw new BaseException(StatusCode.PARAMETER_ERROR,"队伍人数不符合要求");
        }
        //队伍标题长度<=20
        String name = team.getName();
        if (StringUtils.isBlank(name) || name.length() > 20){
            throw new BaseException(StatusCode.PARAMETER_ERROR,"队伍标题过长");
        }
        //描述<=512
        String description = team.getDescription();
        if (StringUtils.isNotBlank(description) && description.length() > 512){
            throw new BaseException(StatusCode.PARAMETER_ERROR,"描述过长");
        }
        //status是否公开(int) 不传默认为0(公开)
        Integer status = Optional.ofNullable(team.getStatus()).orElse(0);
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
        if (statusEnum == null){
            throw new BaseException(StatusCode.PARAMETER_ERROR,"队伍状态不满足要求");
        }
        //如果status是加密状态,一定要有密码,且密码<=32
        String password = team.getPassword();
        if (TeamStatusEnum.SECRET.equals(statusEnum)){
            if (StringUtils.isBlank(password) || password.length() > 32)
                throw new BaseException(StatusCode.PARAMETER_ERROR,"密码设置有误");
        }
        //超时时间>当前时间
        Date expireTime = team.getExpireTime();
        if (new Date().after(expireTime)){
            throw new BaseException(StatusCode.PARAMETER_ERROR,"过期时间已过");
        }
        //校验用户最多创建5个队伍
        //todo 有bug，用户可能同时直接创建超过5个队伍
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId",userId);
        long hasTeamNum = this.count(queryWrapper);
        if (hasTeamNum >= 5){
            throw new BaseException(StatusCode.PARAMETER_ERROR,"用户最多创建5个队伍 ");
        }
        //4.插入队伍信息到队伍表
        team.setId(null);//当插入数据时，如果主键字段(id)的值为 null 或未指定，数据库会自动生成一个唯一的自增值作为主键。
        team.setUserId(userId);
        boolean result = this.save(team);
        Long teamId = team.getId();
        if (!result ||teamId == null){
            throw new BaseException(StatusCode.ERROR,"创建队伍失败");
        }
        //5.插入用户→队伍关系到关系表
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());
        result = userTeamService.save(userTeam);
        if (!result){
            throw new BaseException(StatusCode.ERROR,"创建队伍失败");
        }
        return teamId;
    }

    @Override
    public List<TeamUserVO> listTeams(TeamQueryDTO teamQueryDTO, User loginUser) {
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        //组合查询条件
        if (teamQueryDTO != null){
            Long id = teamQueryDTO.getId();
            if (id != null && id > 0) {
                queryWrapper.eq("id", id);
            }
            String searchText = teamQueryDTO.getSearchText();
            if (StringUtils.isNotBlank(searchText)) {
                queryWrapper.and(wrapper -> wrapper.like("name", searchText)
                        .or().like("description", searchText));
            }
            String name = teamQueryDTO.getName();
            if (StringUtils.isNotBlank(name)) {
                queryWrapper.like("name", name);
            }
            String description = teamQueryDTO.getDescription();
            if (StringUtils.isNotBlank(description)) {
                queryWrapper.like("description", description);
            }
            Integer maxNum = teamQueryDTO.getMaxNum();
            if (maxNum != null && maxNum > 0) {
                queryWrapper.eq("maxNum", maxNum);
            }
            Long userId = teamQueryDTO.getUserId();
            if (userId != null && userId > 0) {
                queryWrapper.eq("userId", userId);
            }
            //根据状态来查询
            Integer status = teamQueryDTO.getStatus();
            boolean notAdmin = userService.notAdmin(loginUser);
            TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
            if (statusEnum == null) {
                statusEnum = TeamStatusEnum.PUBLIC; // 默认公开状态
            }
            if (notAdmin && !statusEnum.equals(TeamStatusEnum.PUBLIC)){
                throw new BaseException(StatusCode.USER_NO_AUTH, "非管理员用户只能查询公开队伍");
            }
            queryWrapper.eq("status", statusEnum.getValue());
        }
        // 不展示已过期的队伍
        //expireTime > now() or expireTime is null
        queryWrapper.and(
                wrapper -> wrapper.gt("expireTime", new Date())
                        .or().isNull("expireTime")
        );

        List<Team> teamList = this.list(queryWrapper);
        if (CollectionUtils.isEmpty(teamList)){
            return new ArrayList<>();
        }

        List<TeamUserVO> teamUserVOList = new ArrayList<>();

        //关联查询创建人用户信息
        //方法1.写sql:
        //查询队伍和创建人信息
        //SELECT * FROM team t LEFT JOIN user u ON t.userId = u.id
        //查询队伍和已加入队伍成员信息
        //SELECT * FROM team t LEFT JOIN user_team ut ON t.id = ut.teamId LEFT JOIN user u ON ut.userId = u.id

        //方法2.直接使用Mybatis-Plus的关联查询
        for (Team team : teamList) {
            Long userId = team.getUserId();
            if (userId == null) {
                continue; // 如果没有创建人信息，跳过
            }
            User user = userService.getById(userId);
            TeamUserVO teamUserVO = new TeamUserVO();
            BeanUtils.copyProperties(team, teamUserVO);
            //脱敏用户
            if (user != null) {
                UserVO userVO = new UserVO();
                BeanUtils.copyProperties(user, userVO);
                teamUserVO.setCreateUser(userVO);
            }
            teamUserVOList.add(teamUserVO);
        }
        return teamUserVOList;
    }

    @Override
    public boolean updateTeam(TeamUpdateRequest teamUpdateRequest,User loginUser) {
        if (teamUpdateRequest == null) {
            throw new BaseException(StatusCode.REQUEST_IS_NULL, "请求参数为空");
        }
        Long id = teamUpdateRequest.getId();
        Team oldTeam = getTeamById(id);
        //只有管理员和创建人可以更新队伍信息
        if (userService.notAdmin(loginUser) && !oldTeam.getUserId().equals(loginUser.getId())) {
            throw new BaseException(StatusCode.USER_NO_AUTH, "非管理员用户无权更新队伍信息");
        }

        //校验更新信息
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(teamUpdateRequest.getStatus());
        if (statusEnum.equals(TeamStatusEnum.SECRET)) {
            if (StringUtils.isBlank(teamUpdateRequest.getPassword()) || teamUpdateRequest.getPassword().length() > 32) {
                throw new BaseException(StatusCode.PARAMETER_ERROR, "密码设置有误");
            }
        }else if (statusEnum.equals(TeamStatusEnum.PUBLIC)) {
            //公开状态不需要密码
            teamUpdateRequest.setPassword(null);
        } else if (statusEnum.equals(TeamStatusEnum.PRIVATE)) {
            //私有状态不需要密码
            teamUpdateRequest.setPassword(null);
        } else {
            throw new BaseException(StatusCode.PARAMETER_ERROR, "队伍状态不满足要求");
        }

        Team updateTeam = new Team();
        BeanUtils.copyProperties(teamUpdateRequest, updateTeam);
        return this.updateById(updateTeam);
    }

    @Override
    public boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser) {
        if (teamJoinRequest == null) {
            throw new BaseException(StatusCode.REQUEST_IS_NULL, "请求参数为空");
        }

        //队伍必须存在
        Long teamId = teamJoinRequest.getTeamId();
        Team team = getTeamById(teamId);
        //只能加入未过期的队伍
        Date expireTime = team.getExpireTime();
        if (expireTime != null && team.getExpireTime().before(new Date())) {
            throw new BaseException(StatusCode.ERROR, "队伍已过期，无法加入");
        }

        Integer status = team.getStatus();
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
        if (TeamStatusEnum.SECRET.equals(statusEnum)) {
            //加密队伍需要密码
            String password = teamJoinRequest.getPassword();
            if (StringUtils.isBlank(password) || !password.equals(team.getPassword())) {
                throw new BaseException(StatusCode.PARAMETER_ERROR, "密码错误，无法加入加密队伍");
            }
        }

        if (TeamStatusEnum.PRIVATE.equals(statusEnum)) {
            //私有队伍不允许加入
            throw new BaseException(StatusCode.PARAMETER_ERROR, "私有队伍不允许加入");
        }

        //该用户已加入的队伍数量
        Long userId = loginUser.getId();
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        long hasTeamNum = userTeamService.count(queryWrapper);
        if (hasTeamNum >= 5) {
            throw new BaseException(StatusCode.PARAMETER_ERROR, "用户最多加入5个队伍");
        }
        //不能重复加入已经加入的队伍
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        queryWrapper.eq("teamId", teamId);
        long hasUserJoinTeam = userTeamService.count(queryWrapper);
        if (hasUserJoinTeam > 0) {
            throw new BaseException(StatusCode.PARAMETER_ERROR, "用户不能重复加入同一个队伍");
        }

        //已经加入队伍的人数
        long joinedCount = countTeamUserByTeamId(teamId);
        //如果队伍人数已满
        Integer maxNum = team.getMaxNum();
        if (joinedCount >= maxNum) {
            throw new BaseException(StatusCode.PARAMETER_ERROR, "队伍人数已满，无法加入");
        }

        //插入用户→队伍关系到关系表
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());
        return userTeamService.save(userTeam);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean quitTeam(TeamQuitRequest teamQuitRequest, User loginUser) {
        if (teamQuitRequest == null) {
            throw new BaseException(StatusCode.REQUEST_IS_NULL, "请求参数为空");
        }

        Long teamId = teamQuitRequest.getTeamId();
        Team team = getTeamById(teamId);

        long userId = loginUser.getId();
        UserTeam userTeam = new UserTeam();
        userTeam.setTeamId(teamId);
        userTeam.setUserId(userId);
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>(userTeam);
        long count = userTeamService.count(queryWrapper);

        if (count == 0) {
            throw new BaseException(StatusCode.PARAMETER_ERROR, "用户未加入该队伍，无法退出");
        }

        long TeamHasJoinNum = countTeamUserByTeamId(teamId);
        //队伍只剩一人直接解散
        if (TeamHasJoinNum == 1){
            //删除队伍
            this.removeById(teamId);
        } else {
            //队伍至少还剩两人，判断是否为队长
            //如果是队长
            if (team.getUserId().equals(userId)) {
                //把队伍转移给最早加入的用户
                //查询已加入队伍的
                QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
                userTeamQueryWrapper.eq("teamId", teamId)
                        .orderByAsc("joinTime")
                        .last("LIMIT 2"); // 获取最早加入的用户
                List<UserTeam> userTeamList = userTeamService.list(userTeamQueryWrapper);

                if (CollectionUtils.isEmpty(userTeamList) || userTeamList.size() < 2) {
                    throw new BaseException(StatusCode.ERROR, "队伍成员不足，无法转移队长");
                }

                UserTeam nextUserTeam = userTeamList.get(1);
                Long nextTeamLeaderId = nextUserTeam.getUserId();

                Team updateTeam = new Team();
                updateTeam.setId(teamId);
                updateTeam.setUserId(nextTeamLeaderId);

                boolean result = this.updateById(updateTeam);
                if (!result) {
                    throw new BaseException(StatusCode.ERROR, "转移队长失败");
                }

            }
        }
        //删除用户→队伍关系
        return userTeamService.remove(queryWrapper);
    }

    /**
     * 根据ID获取队伍信息
     * @param teamId 队伍ID
     * @return 队伍信息
     */
    private Team getTeamById(Long teamId) {
        if (teamId == null || teamId <= 0) {
            throw new BaseException(StatusCode.PARAMETER_ERROR, "队伍ID不合法");
        }

        Team team = this.getById(teamId);
        if (team == null) {
            throw new BaseException(StatusCode.ERROR, "队伍不存在");
        }
        return team;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTeam(long id, User loginUser) {
        //校验队伍是否存在
        Team team = getTeamById(id);
        //校验登录用户是否为队长
        if (!Objects.equals(team.getUserId(), loginUser.getId())){
            throw new BaseException(StatusCode.USER_NO_AUTH, "无权限");
        }
        //移除所有加入队伍的关联信息
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        long teamId = team.getId();
        queryWrapper.eq("teamId", teamId);
        boolean result = userTeamService.remove(queryWrapper);
        if (!result) {
            throw new BaseException(StatusCode.ERROR, "移除队伍成员失败");
        }
        //移除队伍
        return this.removeById(teamId);
    }

    /**
     * 统计队伍成员数量
     * @param teamId 队伍ID
     * @return 成员数量
     */
    private long countTeamUserByTeamId(Long teamId) {
        if (teamId == null || teamId <= 0) {
            throw new BaseException(StatusCode.PARAMETER_ERROR, "队伍ID不合法");
        }
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teamId", teamId);
        return userTeamService.count(queryWrapper);
    }
}




