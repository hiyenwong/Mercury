package io.hiyen.mercury.utils;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import io.hiyen.mercury.utils.constants.WebType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;

import java.util.List;
import java.util.Map;

/**
 * @author Hi Yen Wong
 * @date 2023/1/17 15:26
 */

public class UrlRequestHelper<T> {

    private String host;

    private String port;

    private String contextPath;

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    private String protocol;


    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath;
    }

    public UrlRequestHelper() {
    }

    public static class RequestUtilsInstance {
        private static final UrlRequestHelper<?> INSTANCE = new UrlRequestHelper<>();
    }

    public static UrlRequestHelper getInstance() {
        return RequestUtilsInstance.INSTANCE;
    }

    public String getHostPart() {
        return this.protocol + "://" + this.host + ":" + this.port;
    }

    public String getUrl(String path) {
        if (StringUtils.isEmpty(path)) {
            return this.getHostPart() + WebType.FORWARD_SLASH + contextPath;
        } else {
            return this.getHostPart() + WebType.FORWARD_SLASH + contextPath + WebType.FORWARD_SLASH + path;
        }
    }

    public HttpHeaders getHeader(String token) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Content-Type", "application/json");
        httpHeaders.set("Authorization", token);
        return httpHeaders;
    }

    public String getParams(Map<String, T> params) {
        List<String> p = Lists.newArrayList();
        if (null != params && params.size() > 0) {
            params.entrySet().forEach(param -> {
                if (null != param.getValue()) {
                    p.add(param.getKey() + (WebType.EQUAL) + param.getValue());
                }
            });
        }
        return Joiner.on(WebType.AND).join(p);
    }

    public String getLink(String path, Map<String, T> params) {
        return new StringBuilder().append(this.getUrl(path)).append(WebType.CURSOR_HELP)
                .append(this.getParams(params)).toString();
    }
}
