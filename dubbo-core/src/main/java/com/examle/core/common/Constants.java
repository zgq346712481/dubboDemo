package com.examle.core.common;

import java.util.regex.Pattern;

public class Constants {

    public static final String DUBBO = "dubbo";

    public static final String INTERFACE_KEY = "interface";

    public static final String EXPORT_KEY = "export";

    public static final String REFER_KEY = "refer";

    public static final String GROUP_KEY = "group";

    public static final String VERSION_KEY = "version";

    public static final String DEFAULT_KEY_PREFIX = "default.";

    public static final String DEFAULT_KEY = "default";

    public static final String METHODS_KEY = "methods";

    public static final String LOCAL_PROTOCOL = "injvm";

    public static final String CONFIG_CHECK_KEY = "config.check";

    public static final String PROXY_KEY = "proxy";

    public static final String ANYHOST_VALUE = "0.0.0.0";

    public static final String DUBBO_VERSION_KEY = "dubbo";

    public static final String RELEASE_KEY = "release";

    public static final String TIMESTAMP_KEY = "timestamp";

    public static final String PID_KEY = "pid";

    public static final Pattern REGISTRY_SPLIT_PATTERN = Pattern
            .compile("\\s*[|;]+\\s*");

    public static final String REGISTRY_KEY = "registry";

    public static final String REGISTRY_PROTOCOL = "registry";

    public static final String REGISTER_KEY = "register";

    public static final String SUBSCRIBE_KEY = "subscribe";

    public static final String SIDE_KEY = "side";

    public static final String PROVIDER_SIDE = "provider";

    public static final String SCOPE_KEY = "scope";

    public static final String SCOPE_REMOTE = "remote";

    public static final String SCOPE_LOCAL = "local";

    public static final String SCOPE_NONE = "none";

    public static final String CONSUMER_SIDE = "consumer";

    public static final String GENERIC_SERIALIZATION_NATIVE_JAVA = "nativejava";

    public static final String GENERIC_SERIALIZATION_DEFAULT = "true";

    public static final String GENERIC_SERIALIZATION_BEAN = "bean";

    public static final String DUBBO_PROPERTIES_KEY = "dubbo.properties.file";

    public static final String DEFAULT_DUBBO_PROPERTIES = "dubbo.properties";

    public static final String DUBBO_IP_TO_REGISTRY = "DUBBO_IP_TO_REGISTRY";

    public static final String REGISTER_IP_KEY = "register.ip";

    public static final String GENERIC_KEY = "generic";

    public static final String DEFAULT_REGISTRY = "dubbo";

    public static final String CLIENT_KEY = "client";

    public static final String TRANSPORTER_KEY = "transporter";

    public static final String BACKUP_KEY = "backup";

    public static final Pattern COMMA_SPLIT_PATTERN = Pattern
            .compile("\\s*[,]+\\s*");

    public static final String TIMEOUT_KEY = "timeout";

    public static final String MONITOR_KEY = "monitor";

    public static final String INTERFACES = "interfaces";

    public static final String STUB_KEY = "stub";

    public static final String LOCAL_KEY = "local";

    public static final String FUTURE_RETURNTYPE_KEY = "future_returntype";

    public static final String ASYNC_KEY = "async";

    public static final String QOS_ENABLE = "qos.enable";

    public static final String QOS_PORT = "qos.port";

    public static final String ACCEPT_FOREIGN_IP = "qos.accept.foreign.ip";

    public static final String CONSUMER_PROTOCOL = "consumer";

    public static final String ANY_VALUE = "*";
    /**
     * simple the registry for provider.
     */
    public static final String SIMPLIFIED_KEY = "simplified";

    public static final String CATEGORY_KEY = "category";

    public static final String PROVIDERS_CATEGORY = "providers";

    public static final String CONSUMERS_CATEGORY = "consumers";

    public static final String CONFIGURATORS_CATEGORY = "configurators";

    public static final String ROUTERS_CATEGORY = "routers";

    public static final String CHECK_KEY = "check";

    public static final String TAG_KEY = "dubbo.tag";

    public static final String CONFIGURATORS_SUFFIX = ".configurators";
}
