package com.examle.core.config;

import com.examle.core.common.Constants;
import com.examle.core.common.URL;
import com.examle.core.common.bytecode.Wrapper;
import com.examle.core.common.extension.ExtensionLoader;
import com.examle.core.common.utils.StringUtils;
import com.examle.core.config.context.ConfigManager;
import com.examle.core.config.support.Parameter;
import com.examle.core.rpc.Exporter;
import com.examle.core.rpc.Protocol;
import com.examle.core.rpc.ProxyFactory;
import com.examle.core.rpc.model.ApplicationModel;
import com.examle.core.rpc.model.ProviderModel;

import java.util.*;

public class ServiceConfig<T> extends AbstractServiceConfig {

    /**
     * Whether the provider has been exported
     */
    private transient volatile boolean exported;

    //标记服务是否已未导出，如果调用了未导出的方法，则该值为true
    private transient volatile boolean unexported;

    private ProviderConfig provider;

    /**
     * The service name
     */
    private String path;


    /**
     * The reference of the interface implementation
     */
    private T ref;

    /**
     * The interface class of the exported service
     */
    private Class<?> interfaceClass;

    /**
     * The interface name of the exported service
     */
    private String interfaceName;

    private static final Protocol protocol = ExtensionLoader.getExtensionLoader(Protocol.class).getAdaptiveExtension();

    private static final ProxyFactory proxyFactory = ExtensionLoader.getExtensionLoader(ProxyFactory.class).getAdaptiveExtension();


    @Parameter(excluded = true)
    public boolean isExported() {
        return exported;
    }

    @Parameter(excluded = true)
    public boolean isUnexported() {
        return unexported;
    }

    public synchronized void export() {
        checkAndUpdateSubConfigs();
        if (provider != null) {
            if (export == null) {
                export = provider.getExport();
            }
        }
        if (export != null && !export) {
            return;
        }
        if(false){//延时发布

        }else{
            doExport();
        }

    }

    protected synchronized void doExport() {
        if (unexported) {
            throw new IllegalStateException("Already unexported!");
        }
        if (exported) {
            return;
        }
        exported = true;
        if (path == null || path.length() == 0) {
            path = interfaceName;
        }
        ProviderModel providerModel = new ProviderModel(getUniqueServiceName(), ref, interfaceClass);
        ApplicationModel.initProviderModel(getUniqueServiceName(), providerModel);
        doExportUrls();
    }

    private void doExportUrls() {
        List<URL> registryURLs = loadRegistries(true);
        for (ProtocolConfig protocolConfig : protocols) {
            doExportUrlsFor1Protocol(protocolConfig, registryURLs);
        }
    }

    private void doExportUrlsFor1Protocol(ProtocolConfig protocolConfig, List<URL> registryURLs) {
        String name = protocolConfig.getName();
        if (name == null || name.length() == 0) {
            name = Constants.DUBBO;
        }
        Map<String, String> map = new HashMap<String, String>();
        if(false){

        }else{
            String[] methods = Wrapper.getWrapper(interfaceClass).getMethodNames();
            if(methods.length == 0){
            }else{
                map.put(Constants.METHODS_KEY, StringUtils.join(new HashSet<String>(Arrays.asList(methods)), ","));
            }
        }

        String host = "192.168.1.100";
        Integer port = 20880;
        URL url = new URL(name, host, port, path, map);

        String scope = null;
        //如果配置不是远程的，导出到本地(只有在配置是远程的时候才导出到远程)
        exportLocal(url);
    }

    @Parameter(excluded = true)
    public String getUniqueServiceName() {
        StringBuilder buf = new StringBuilder();
        if (group != null && group.length() > 0) {
            buf.append(group).append("/");
        }
        buf.append(StringUtils.isNotEmpty(path) ? path : interfaceName);
        if (version != null && version.length() > 0) {
            buf.append(":").append(version);
        }
        return buf.toString();
    }

    public void checkAndUpdateSubConfigs() {
        //启动注册中心
        startConfigCenter();
        checkDefault();
    }

    private void checkDefault() {
        createProviderIfAbsent();
    }

    private void createProviderIfAbsent() {
        if (provider != null) {
            return;
        }
        setProvider (
                ConfigManager.getInstance()
                        .getDefaultProvider()
                        .orElseGet(() -> {
                            ProviderConfig providerConfig = new ProviderConfig();
                            providerConfig.refresh();
                            return providerConfig;
                        })
        );
    }

    public ProviderConfig getProvider() {
        return provider;
    }

    public void setProvider(ProviderConfig provider) {
        ConfigManager.getInstance().addProvider(provider);
        this.provider = provider;
    }

    private void exportLocal(URL url) {
        if (!Constants.LOCAL_PROTOCOL.equalsIgnoreCase(url.getProtocol())) {
//            Exporter<?> exporter = protocol.export(
//                    proxyFactory.getInvoker(ref, (Class) interfaceClass, local));
            /**
             * 将协议改为injvm，IP:127.0.0.1,端口为0
             */
            URL local = url;
            Exporter<?> exporter = protocol.export(
                    proxyFactory.getInvoker(ref, (Class) interfaceClass, local));
        }
    }

}
