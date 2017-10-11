package com.TestJsp.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import java.io.IOException;

/**
 * Created by RXLiuli on 2017/8/31.
 * 中文编码格式过滤器
 * filterName: 过滤器名字
 * urlPatterns: 过滤器范围
 */
@WebFilter(filterName = "EncodeFilter", urlPatterns = {"/*"},
        initParams = {@WebInitParam(name = "encode", value = "UTF-8")})
public class EncodeFilter implements Filter {
    private String encode = null;

    /**
     * 初始化编码格式(在项目启动时加载一次)
     *
     * @param filterConfig 过滤器初始化实例
     * @throws ServletException 可能抛出的 Servlet 异常
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("中文编码格式过滤器初始化");
        if (encode == null) encode = filterConfig.getInitParameter("encode");
    }

    /**
     * 对页面设置字符集
     *
     * @param servletRequest  请求对象
     * @param servletResponse 响应问题
     * @param filterChain     过滤链
     * @throws IOException      IO 异常
     * @throws ServletException Servlet 异常
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        System.out.println("中文编码格式过滤器过滤了");
        if (servletRequest.getCharacterEncoding() == null) {
            servletRequest.setCharacterEncoding(encode);
            servletResponse.setCharacterEncoding(encode);
            servletResponse.setContentType("text/html; charset=" + encode);
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    /**
     * 销毁编码格式(在项目服务关闭时)
     */
    @Override
    public void destroy() {
        System.out.println("中文编码格式过滤器销毁了");
        encode = null;
    }
}
