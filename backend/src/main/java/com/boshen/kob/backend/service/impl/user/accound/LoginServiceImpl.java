package com.boshen.kob.backend.service.impl.user.accound;

import com.boshen.kob.backend.pojo.User;
import com.boshen.kob.backend.service.impl.utils.UserDetailsImpl;
import com.boshen.kob.backend.service.user.account.LoginService;
import com.boshen.kob.backend.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.HashMap;
import java.util.Map;

//这个服务的逻辑就是验证用户是否合法（使用authenticationManager.authenticate来验证，当这个东西合法的话，就会返回一个实例化对象）
//接收这个实例化对象并使用getPrincipal方法拿到我们的用户，用户的类就是之前定义的UserDetails
public class LoginServiceImpl implements LoginService {

//用来判断用户是否已经登录了（判断用户名密码是否匹配，使用controller传递过来的用户名和密码，之后使用UserDetailsService的获取User的方法将其获得，之后再authenticate中进行逻辑判断和比较）
    @Autowired
    private AuthenticationManager authenticationManager;


    @Override
    public Map<String, String> login(String username, String password) {
//        封装用户名密码(authenticate只能接收这个东西需要将其封装一下)
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(username, password);
//        需要传入用户名和密码（将controller接口传递过来的东西放进去就可以实现自动校验用户是否可以登录了）
//        这个就是一个校验的函数，如果登录失败就会自动处理，如果没有报异常就表示下面的代码肯定可以执行
//        这个验证的方法需要调用到UserDetailsService重写的getUserByname方法这就和之前的UserDetailsService扯上了关系
        Authentication authenticate = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        UserDetailsImpl loginUser = (UserDetailsImpl) authenticate.getPrincipal();
//        从这里再将user取出来
        User user = loginUser.getUser();
//之后创建一个JWTToken（这里只需传入一个UserId）
//        这里我们需要创建一个token之后发送回给前端
        String jwt = JwtUtil.createJWT(user.getId().toString());
//返回给前端的数据需要通过key-val的形式放入map中
        Map<String,String> map = new HashMap<>();
//        这里返回的信息需要和前端进行对应
        map.put("error_message","success");
        map.put("token",jwt);
        return map;
    }
}
