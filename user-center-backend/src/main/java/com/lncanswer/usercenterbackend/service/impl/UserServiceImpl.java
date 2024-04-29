package com.lncanswer.usercenterbackend.service.impl;
import java.util.ArrayList;
import java.util.Date;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lncanswer.usercenterbackend.common.ErrorCode;
import com.lncanswer.usercenterbackend.constant.UserConstant;
import com.lncanswer.usercenterbackend.exception.BusinessException;
import com.lncanswer.usercenterbackend.model.domain.User;
import com.lncanswer.usercenterbackend.service.UserService;
import com.lncanswer.usercenterbackend.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.lncanswer.usercenterbackend.constant.UserConstant.ADMIN_ROLE;

/**
* @author JohnChen
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2024-04-25 13:01:41
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    /**
     * 盐值加密 -- 混淆密码
     */
    private static final String SALT = "sin";

    /**
     * 用户登录状态键值
     */
    private static final String USER_LOGIN_STATE = "userLoginState";
    @Resource
    private UserMapper userMapper;

    /**
     *
     * @param userAccount 用户账户
     * @param password    用户密码
     * @param checkPassword 用户校验密码（二次输入的密码）
     * @return 新用户id
     */
    @Override
    public long registerUser(String userAccount, String password, String checkPassword,String planetCode) {
        //1、校验是否为空 利用commons lang包下的StringUtils
        if (StringUtils.isAnyBlank(userAccount,password,checkPassword)){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }

        if (userAccount.length() < 4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号长度小于4");
        }

        //账户不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号不能包含特殊字符");
        }


        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserAccount,userAccount);
        User user = userMapper.selectOne(queryWrapper);
        if (user != null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号已经存在");
        }

        if (password.length() < 8 || checkPassword.length() < 8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码长度小于八位");
        }

        if (!password.equals(checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"两次输入的密码不一致");
        }

//        if (planetCode.length() > 5){
//            throw new BusinessException(ErrorCode.PARAMS_ERROR,"编号长度大于5");
//        }

        //查询编号是否重复
//        LambdaQueryWrapper<User> queryWrapper1 = new LambdaQueryWrapper<User>();
//        queryWrapper1.eq(User::getPlanetCode,planetCode);
//        long count = this.count(queryWrapper1);
//        if (count >0){
//            throw new BusinessException(ErrorCode.PARAMS_ERROR,"编号重复");
//        }

        // 对密码加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes());
        log.info("加密之后的密码：{}",encryptPassword);

        //创建用户存储到数据库
        User newUser = new User();
        String username = RandomUtil.randomString(8);
        newUser.setUsername(username);
        newUser.setUserAccount(userAccount);
        newUser.setPlanetCode(planetCode);
        //使用默认头像
        newUser.setAvatarUrl("https://picx.zhimg.com/80/v2-b1bc73ed804303e2952bcdcda631f2f0_1440w.webp?source=2c26e567");
        newUser.setUserPassword(encryptPassword);

        boolean saveResult = this.save(newUser);
        if (!saveResult){
            throw new RuntimeException("保存用户失败");
        }
        return newUser.getId();
    }

    /**
     *
     * @param userAccount 用户账户
     * @param password    用户密码
     * @param checkCode   校验码(可扩展)
     * @param request   servletRequest
     * @return 返回用户脱敏之后的信息
     */
    @Override
    public User login(String userAccount, String password, String checkCode, HttpServletRequest request) {
        //1、判断是否为空
        if (StringUtils.isAnyBlank(userAccount,password)){
            throw new  BusinessException(ErrorCode.PARAMS_ERROR,"输入的账号密码不能为空");
        }

        if (userAccount.length() < 4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号长度小于4");
        }

        //2、账户不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号包含特殊字符");
        }
        //判断用户是否注册
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserAccount,userAccount);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号未注册");
        }
        if (password.length() < 8 ){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号密码小于8");
        }

        //3、校验密码是否和数据库中存储的密文密码一致
        String enterPassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes());
        String userPassword = user.getUserPassword();
        if (!enterPassword.equals(userPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"输入的密码有误");
        }
        log.info("enterPassword = {}, userPassword = {}",enterPassword,userPassword);

        //4、用户数据脱敏 返回用户信息
        User safetyUser = safetyUser(user);


        //5、记录用户登录状态
        request.getSession().setAttribute(USER_LOGIN_STATE,safetyUser);
        return safetyUser;
    }

    /**
     * 查询用户列表
     * @param username 用户名称
     * @return 返回用户列表
     */
    @Override
    public List<User> queryUserList(String username,HttpServletRequest request) {
        //仅管理员可查询
        if (!isAdmin(request)){
            throw new BusinessException(ErrorCode.NO_AUTH,"仅管理员可查询");
        }
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        if (StringUtils.isNotBlank(username)){
            lambdaQueryWrapper.like(User::getUsername,username);
        }

        List<User> list = this.list(lambdaQueryWrapper);
        if (list == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"查询用户不存在");
        }
        return list.stream().map(this::safetyUser).collect(Collectors.toList());
    }

    /**
     * 删除用户
     * @param id   用户id
     * @param request http请求
     * @return true or false
     */
    @Override
    public boolean deleteById(long id, HttpServletRequest request) {
        //判断当前用户是否为管理员
        if (!isAdmin(request)){
            throw new BusinessException(ErrorCode.NO_AUTH,"仅管理员可查看");
        }

        if ( id <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户id小于0");
        }
        return this.removeById(id);
    }

    /**
     * 获取当前登录用户信息
     * @param request httpRequest请求
     * @return 返回用户信息
     */
    @Override
    public User getCurrentUser(HttpServletRequest request) {
        Object userOjb = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userOjb;
        if (user == null){
            return null;
        }
        //获取当前登录的用户状态之后，再去数据库查询一次 是为了保证用户的信息是及时的
        Long userId = user.getId();
        User newUser = this.getById(userId);
        return safetyUser(newUser);
    }

    /**
     * 用户注销
     * @param request httpRequest请求对象
     * @return
     */
    @Override
    public void userLogout(HttpServletRequest request) {
        request.getSession().removeAttribute(USER_LOGIN_STATE);
    }

    /**
     * 是否为管理员
     * @param request
     * @return true or false
     */
    private boolean isAdmin(HttpServletRequest request){
        User user = (User)request.getSession().getAttribute(USER_LOGIN_STATE);
        if (user == null || user.getUserRole() != ADMIN_ROLE){
            return false;
        }
        return true;
    }

    /**
     * 用户脱敏
     * @param user 用户信息
     * @return 安全用户信息
     */
    private User safetyUser(User user){
        if (user == null){
            return null;
        }
        User safetyUser = new User();
        safetyUser.setId(user.getId());
        safetyUser.setUsername(user.getUsername());
        safetyUser.setUserAccount(user.getUserAccount());
        safetyUser.setAvatarUrl(user.getAvatarUrl());
        safetyUser.setGender(0);
        safetyUser.setPlanetCode(user.getPlanetCode());
        safetyUser.setPhone(user.getPhone());
        safetyUser.setEmail(user.getEmail());
        safetyUser.setUserStatus(user.getUserStatus());
        safetyUser.setCreateTime(user.getCreateTime());
        safetyUser.setUserRole(user.getUserRole());
        return safetyUser;
    }
}



