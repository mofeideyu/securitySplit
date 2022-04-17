package com.mofei.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mofei.filter.LoginFilter;
import com.mofei.service.impl.MyUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
//    @Bean
//    public UserDetailsService userDetailsService() {/**自定义内存*/
//        InMemoryUserDetailsManager inMemoryUserDetailsManager = new InMemoryUserDetailsManager();
//        inMemoryUserDetailsManager.createUser(User.withUsername("admin").password("{noop}123").roles("ADMIN").build());
//        return inMemoryUserDetailsManager;
//    }
    private MyUserDetailService myUserDetailService;
    @Autowired
    public void setMyUserDetailService(MyUserDetailService myUserDetailService) {
        this.myUserDetailService = myUserDetailService;
    }

    /**自定义AuthenticationManager，但是AuthenticationManagerBuilder是工厂内部的,本地的*/
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(myUserDetailService);
    }

    /**将AuthenticationManagerBuilder暴露出,可以给外部使用*/
    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public LoginFilter loginFilter() throws Exception {
        LoginFilter loginFilter = new LoginFilter();
        /**指定认证的URL*/
        loginFilter.setFilterProcessesUrl("/doLogin");
        loginFilter.setUsernameParameter("username");
        loginFilter.setPasswordParameter("password");
        loginFilter.setAuthenticationManager(authenticationManagerBean());
        /**认证成功处理*/
        loginFilter.setAuthenticationSuccessHandler((httpServletRequest, httpServletResponse, authentication) -> {
            Map<String, Object> map = new HashMap<>();
            map.put("code",200);
            map.put("msg","登录成功");
            map.put("data",authentication.getPrincipal());
            httpServletResponse.setStatus(HttpStatus.OK.value());
            httpServletResponse.setContentType("application/json;charset=UTF-8");
            httpServletResponse.getWriter().println(new ObjectMapper().writeValueAsString(map));
        });
        loginFilter.setAuthenticationFailureHandler((httpServletRequest, httpServletResponse, e) -> {
            Map<String, Object> map = new HashMap<>();
            map.put("code",-1);
            map.put("msg","登录失败");
            httpServletResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            httpServletResponse.setContentType("application/json;charset=UTF-8");
            httpServletResponse.getWriter().println(new ObjectMapper().writeValueAsString(map));
        });
        return loginFilter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .and()
                /**异常处理*/
                .exceptionHandling()
                /**认证异常处理*/
                .authenticationEntryPoint((httpServletRequest, httpServletResponse, e) -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("code",-1);
                    map.put("msg","请认证之后进行处理");
                    httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
                    httpServletResponse.setContentType("application/json;charset=UTF-8");
                    httpServletResponse.getWriter().println(new ObjectMapper().writeValueAsString(map));
                })
                .and()
                .logout()
//                .logoutUrl("/logout")
                .logoutRequestMatcher(new OrRequestMatcher(
                        new AntPathRequestMatcher("/logout", HttpMethod.DELETE.name()),
                        new AntPathRequestMatcher("/logout",HttpMethod.GET.name())
                ))
                .logoutSuccessHandler((httpServletRequest, httpServletResponse, authentication) -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("code",200);
                    map.put("msg","注销成功");
                    httpServletResponse.setStatus(HttpStatus.OK.value());
                    httpServletResponse.setContentType("application/json;charset=UTF-8");
                    httpServletResponse.getWriter().println(new ObjectMapper().writeValueAsString(map));
                })
                .and()
                .csrf().disable();

        /**addFilterAt(filterA,filterB):用过滤器A替换过滤器B的位置,
         * before: 放在过滤器链中哪个过滤器之前
         * after: 放在过滤器链中哪个过滤器之后
         * */
        http.addFilterAt(loginFilter(), UsernamePasswordAuthenticationFilter.class);
    }

}
