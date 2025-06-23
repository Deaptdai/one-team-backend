package com.deapt.oneteambackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.deapt.oneteambackend.common.result.Result;
import com.deapt.oneteambackend.common.result.StatusCode;
import com.deapt.oneteambackend.exception.BaseException;
import com.deapt.oneteambackend.model.domin.Team;
import com.deapt.oneteambackend.model.domin.User;
import com.deapt.oneteambackend.model.dto.TeamQueryDTO;
import com.deapt.oneteambackend.model.request.TeamAddRequest;
import com.deapt.oneteambackend.model.request.TeamJoinRequest;
import com.deapt.oneteambackend.model.request.TeamQuitRequest;
import com.deapt.oneteambackend.model.request.TeamUpdateRequest;
import com.deapt.oneteambackend.model.vo.TeamUserVO;
import com.deapt.oneteambackend.service.TeamService;
import com.deapt.oneteambackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author Deapt
 * @description 队伍表示层
 * @since 2025/6/13 10:36
 */

@RestController
@RequestMapping("/team")
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000"}, allowCredentials = "true")
public class TeamController {

    @Resource
    private UserService userService;

    @Resource
    private TeamService teamService;

    @PostMapping("/add")
    public Result<Long> addTeam(@RequestBody TeamAddRequest teamAddRequest, HttpServletRequest request){
        if (teamAddRequest == null){
            throw new BaseException(StatusCode.REQUEST_IS_NULL,"请求参数为空");
        }
        User loginUser = userService.getLoginUser(request);
        Team team = new Team();
        BeanUtils.copyProperties(teamAddRequest,team);
        long teamId = teamService.addTeam(team, loginUser);
        return Result.success(teamId,StatusCode.SUCCESS);
    }

    @DeleteMapping("/delete")
    public Result<Boolean> deleteTeam(@RequestBody long id, HttpServletRequest request){
        if (id <= 0){
            throw new BaseException(StatusCode.REQUEST_IS_NULL,"请求参数为空");
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.deleteTeam(id,loginUser);
        if (!result){
            throw new BaseException(StatusCode.ERROR,"删除失败");
        }
        return Result.success(true,StatusCode.SUCCESS);
    }

    @PostMapping("/update")
    public Result<Boolean> updateTeam(@RequestBody TeamUpdateRequest teamUpdateRequest, HttpServletRequest request){
        if (teamUpdateRequest == null){
            throw new BaseException(StatusCode.REQUEST_IS_NULL,"请求参数为空");
        }

        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.updateTeam(teamUpdateRequest,loginUser);
        if (!result){
            throw new BaseException(StatusCode.ERROR,"更新失败");
        }
        return Result.success(true,StatusCode.SUCCESS);
    }

    @GetMapping("/get")
    public Result<Team> getTeamById(@Param("id") long id){
        if (id <= 0){
            throw new BaseException(StatusCode.REQUEST_IS_NULL,"请求参数为空");
        }
        Team team = teamService.getById(id);
        if (team == null){
            throw new BaseException(StatusCode.ERROR,"查询失败");
        }
        return Result.success(team,StatusCode.SUCCESS);
    }

    @GetMapping("/list")
    public Result<List<TeamUserVO>> listTeams(TeamQueryDTO teamQueryDTO, HttpServletRequest request){
        if (teamQueryDTO == null){
            throw new BaseException(StatusCode.REQUEST_IS_NULL,"请求参数为空");
        }
        User loginUser = userService.getLoginUser(request);
        List<TeamUserVO> teamUserVOList = teamService.listTeams(teamQueryDTO,loginUser);
        return Result.success(teamUserVOList,StatusCode.SUCCESS);
    }
    @GetMapping("/list/page")
    public Result<Page<Team>> listTeamsByPage(TeamQueryDTO teamQueryDTO){
        if (teamQueryDTO == null){
            throw new BaseException(StatusCode.REQUEST_IS_NULL,"请求参数为空");
        }
        Team team = new Team();
        BeanUtils.copyProperties(teamQueryDTO,team);

        Page<Team> page = new Page<>(teamQueryDTO.getPageNum(), teamQueryDTO.getPageSize());
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>(team);
        Page<Team> pageResult = teamService.page(page, queryWrapper);
        return Result.success(pageResult,StatusCode.SUCCESS);
    }

    @PostMapping("/join")
    public Result<Boolean> joinTeam(@RequestBody TeamJoinRequest teamJoinRequest, HttpServletRequest request) {
        if (teamJoinRequest == null) {
            throw new BaseException(StatusCode.REQUEST_IS_NULL, "请求参数为空");
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.joinTeam(teamJoinRequest, loginUser);
        if (!result) {
            throw new BaseException(StatusCode.ERROR, "加入队伍失败");
        }
        return Result.success(true, StatusCode.SUCCESS);
    }

    @PostMapping("/quit")
    public Result<Boolean> quitTeam(@RequestBody TeamQuitRequest teamQuitRequest, HttpServletRequest request) {
        if (teamQuitRequest == null) {
            throw new BaseException(StatusCode.REQUEST_IS_NULL, "请求参数为空");
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.quitTeam(teamQuitRequest, loginUser);
        if (!result) {
            throw new BaseException(StatusCode.ERROR, "退出队伍失败");
        }
        return Result.success(true, StatusCode.SUCCESS);
    }
}
