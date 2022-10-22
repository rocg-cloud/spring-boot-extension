package com.livk.yauaa.util;

import com.livk.support.SpringContextHolder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;
import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;
import org.springframework.http.server.reactive.ServerHttpRequest;

/**
 * <p>
 * UserAgentUtils
 * </p>
 *
 * @author livk
 * @date 2022/9/30
 */
@UtilityClass
public class UserAgentUtils {

    public static final String USER_AGENT = "user-agent";

    private static final UserAgentAnalyzer ANALYZER;

    static {
        ANALYZER = SpringContextHolder.getBean(UserAgentAnalyzer.class);
    }

    public UserAgent parse(String userAgent) {
        return ANALYZER.parse(userAgent);
    }

    public UserAgent parse(HttpServletRequest request) {
        String userAgent = request.getHeader(UserAgentUtils.USER_AGENT);
        return parse(userAgent);
    }

    public UserAgent parse(ServerHttpRequest request) {
        String userAgent = request.getHeaders().getFirst(UserAgentUtils.USER_AGENT);
        return parse(userAgent);
    }
}