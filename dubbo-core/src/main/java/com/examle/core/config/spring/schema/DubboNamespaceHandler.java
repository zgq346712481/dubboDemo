package com.examle.core.config.spring.schema;

import com.examle.core.common.Version;
import com.examle.core.config.ApplicationConfig;
import com.examle.core.config.ProtocolConfig;
import com.examle.core.config.RegistryConfig;
import com.examle.core.config.spring.ConfigCenterBean;
import com.examle.core.config.spring.ReferenceBean;
import com.examle.core.config.spring.ServiceBean;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * 1.Spring容器会默认加载classpath/META-INF下的spring.handlers和spring.schemas文件
 * 2.BeanDefinitionParser:负责将标签转换成bean定义对象BeanDefinition
 */
public class DubboNamespaceHandler extends NamespaceHandlerSupport {

    static {
        Version.checkDuplicate(DubboNamespaceHandler.class);
    }
    @Override
    public void init() {
        registerBeanDefinitionParser("application", new DubboBeanDefinitionParser(ApplicationConfig.class, true));
        registerBeanDefinitionParser("registry", new DubboBeanDefinitionParser(RegistryConfig.class, true));
        registerBeanDefinitionParser("config-center", new DubboBeanDefinitionParser(ConfigCenterBean.class, true));
        registerBeanDefinitionParser("protocol", new DubboBeanDefinitionParser(ProtocolConfig.class, true));
        registerBeanDefinitionParser("service", new DubboBeanDefinitionParser(ServiceBean.class, true));
        registerBeanDefinitionParser("reference", new DubboBeanDefinitionParser(ReferenceBean.class, false));
    }
}
