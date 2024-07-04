package com.mymall.web.manager.config;

import com.mymall.web.manager.security.AuthenticationSuccessHandlerImpl;
import com.mymall.web.manager.security.UserDetailsServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private AuthenticationSuccessHandlerImpl handler;

    @Bean
    public PasswordEncoder getPwdEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    UserDetailsService customUserService(){ //注册UserDetailsService 的bean
        return new UserDetailsServiceImpl();
    }
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customUserService()); //user Details Service验证
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 表单认证
        http.formLogin()
                .loginProcessingUrl("/login") //当发现 /login 请求时认为是登录，需要执行 UserDetailsServiceImpl
                .successForwardUrl("/toMain") //此处是 post 请求
                .successHandler(handler)
                .loginPage("/login.html");

        // url 拦截
        http.authorizeRequests()
                .antMatchers("/js/**","/css/**","/img/*","/plugins/**").permitAll() //忽略静态资源
                .antMatchers("/login.html").permitAll()  //login.html 丌需要被认证
                .antMatchers("/fail.html").permitAll() //fail.html 不需要被认证
                .anyRequest().authenticated(); //所有的请求都必须被认证。必须登录后才能访问。

        //关闭 csrf 防护
        http.csrf().disable();

        //关闭Frame
        http.headers().frameOptions().disable();
    }

}
