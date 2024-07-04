package com.mymall.common.cas.config;

import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.validation.Cas20ServiceTicketValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.authentication.CasAuthenticationProvider;
import org.springframework.security.cas.web.CasAuthenticationEntryPoint;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.cors.CorsUtils;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class CasSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private CasProperties casProperties;

    @Autowired
    private AuthenticationUserDetailsService casUserDetailService;

    /**
     * 设置AuthenticationProvider
     */
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(casAuthenticationProvider());
    }

    /**
     * security安全策略
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry config = http.authorizeRequests()
                // 放行OPTIONS请求
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers("/**/*.css").permitAll()
                .antMatchers("/**/*.js").permitAll()
                .antMatchers("/login/*").permitAll()
                .antMatchers("/seckill/*/*").permitAll()
                .antMatchers("/seckill/*/*/*").permitAll()
                .antMatchers("/seckill-index.html").permitAll()
                .antMatchers("/seckill-item.html").permitAll()
                .antMatchers("/pay/notify").permitAll()
                .antMatchers("/index").permitAll()
                .antMatchers("/search").permitAll()
                .antMatchers("/item/price").permitAll()
                .antMatchers("/user/name").permitAll()
                .antMatchers("/user/register").permitAll()
                .antMatchers("/user/sendSms").permitAll()
                .antMatchers("/user/save").permitAll()
                .antMatchers("/user/findOrder").permitAll()
                .antMatchers("/user/findOrderNew").permitAll()
                .antMatchers("/cart/buyNew").permitAll()
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                .antMatchers("/favicon.ico").permitAll();

        //剩下的所有请求都需要验证
        config.anyRequest().authenticated();

        http.logout().permitAll()//放行logout
             .and()
            .formLogin();//表单登录

        http.exceptionHandling().authenticationEntryPoint(casAuthenticationEntryPoint())
                .and()
                .addFilter(casAuthenticationFilter())
                .addFilterBefore(logoutFilter(), LogoutFilter.class)
                .addFilterBefore(singleSignOutFilter(), CasAuthenticationFilter.class);


        http.csrf().disable(); //禁用CSRF
    }

    /**
     * cas认证入口
     */
    @Bean
    public CasAuthenticationEntryPoint casAuthenticationEntryPoint() {
        CasAuthenticationEntryPoint casAuthenticationEntryPoint = new CasAuthenticationEntryPoint();
        //设置cas 服务端登录url
        casAuthenticationEntryPoint.setLoginUrl(casProperties.getCasServerLoginUrl());
        //设置cas 客户端信息
        casAuthenticationEntryPoint.setServiceProperties(serviceProperties());
        return casAuthenticationEntryPoint;
    }

    /**
     * cas客户端信息
     */
    @Bean
    public ServiceProperties serviceProperties() {
        ServiceProperties serviceProperties = new ServiceProperties();
        //设置cas客户端登录完整的url
        serviceProperties.setService(casProperties.getCasServiceUrl() + casProperties.getCasServiceLoginUrl());
        serviceProperties.setAuthenticateAllArtifacts(true);
        return serviceProperties;
    }

    /**
     * cas认证过滤器
     * casAuthenticationFilter.setFilterProcessesUrl 必须要设置完整路径 不然会无限重定向
     */
    @Bean
    public CasAuthenticationFilter casAuthenticationFilter() throws Exception {
        CasAuthenticationFilter casAuthenticationFilter = new CasAuthenticationFilter();
        casAuthenticationFilter.setAuthenticationManager(authenticationManager());
        casAuthenticationFilter.setFilterProcessesUrl(casProperties.getCasServiceUrl() + casProperties.getCasServiceLoginUrl());
        casAuthenticationFilter.setServiceProperties(serviceProperties());
        return casAuthenticationFilter;
    }

    /**
     * cas认证Provider
     */
    @Bean
    public CasAuthenticationProvider casAuthenticationProvider() {
        CasAuthenticationProvider casAuthenticationProvider = new CasAuthenticationProvider();
        //设置UserDetailService 登录成功后 会进入loadUserDetails方法中
        casAuthenticationProvider.setAuthenticationUserDetailsService(casUserDetailService);
        //设置cas 客户端信息
        casAuthenticationProvider.setServiceProperties(serviceProperties());
        //设置cas 票证验证器
        casAuthenticationProvider.setTicketValidator(cas20ServiceTicketValidator());
        //设置cas 秘钥
        casAuthenticationProvider.setKey(casProperties.getCasKey());
        return casAuthenticationProvider;
    }

    /**
     * cas 票证验证器
     */
    @Bean
    public Cas20ServiceTicketValidator cas20ServiceTicketValidator() {
        return new Cas20ServiceTicketValidator(casProperties.getCasServerUrl());
    }

    /**
     * 单点注销过滤器
     * 用于接收cas服务端的注销请求
     */
    @Bean
    public SingleSignOutFilter singleSignOutFilter() {
        SingleSignOutFilter singleSignOutFilter = new SingleSignOutFilter();
//        singleSignOutFilter.setCasServerUrlPrefix(casProperties.getCasServerUrl());
        singleSignOutFilter.setIgnoreInitConfiguration(true);
        return singleSignOutFilter;
    }

    /**
     * 单点退出过滤器
     * 用于跳转到cas服务端
     */
    @Bean
    public LogoutFilter logoutFilter() {
        LogoutFilter logoutFilter = new LogoutFilter(casProperties.getCasServerLogoutUrl(), new SecurityContextLogoutHandler());
        logoutFilter.setFilterProcessesUrl(casProperties.getCasServiceLogoutUrl());
        return logoutFilter;
    }

}
