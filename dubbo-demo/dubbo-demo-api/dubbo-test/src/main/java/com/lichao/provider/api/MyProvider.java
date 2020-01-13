package com.lichao.provider.api;

import com.lichao.provider.impl.HashServiceImpl;
import com.lichao.provider.policy.BaseConfig;
import com.lichao.provider.policy.LargeConfig;
import com.lichao.provider.policy.MediumConfig;
import com.lichao.provider.policy.SmallConfig;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ProtocolConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.ServiceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author guohaoice@gmail.com
 */
public class MyProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(MyProvider.class);

    private static ApplicationConfig application = new ApplicationConfig();

    private static RegistryConfig registry = new RegistryConfig();

    private static ProtocolConfig protocol = new ProtocolConfig();




    public static void main(String[] args) throws InterruptedException {
        // 虚拟机参数
        String env = System.getProperty("quota");
        LOGGER.info("env is set:{}", env);
        if (StringUtils.isEmpty(env)) {
            env = "small";
            LOGGER.info("[PROVIDER-SERVICE] No specific args found, use [DEFAULT] to run demo provider");
        }
        BaseConfig config;
        switch (env) {
            case "small":
                config = new SmallConfig();
                break;
            case "medium":
                config = new MediumConfig();
                break;
            case "large":
                config = new LargeConfig();
                break;
            default:
                throw new IllegalStateException();
        }

        // 当前应用配置
        application.setName("service-provider");
        application.setQosEnable(false);

        // 连接注册中心配置
        registry.setAddress("N/A");

        // 服务提供者协议配置
        protocol.setName("dubbo");
        protocol.setPort(config.getPort());
        protocol.setThreads(config.getMaxThreadCount());
        protocol.setHost("0.0.0.0");

        // 注意：ServiceConfig为重对象，内部封装了与注册中心的连接，以及开启服务端口
        exportHashService(config.getConfigs());

        try {
            exportCallbackServiceIfNeed();
        } catch (Exception e) {
            LOGGER.error("exportCallbackServiceIfNeed failed", e);
        }

        Thread.currentThread().join();
    }




    private static void exportHashService(List<ThrashConfig> configs) {
        // 服务提供者暴露服务配置
        ServiceConfig<HashInterface> service =
                new ServiceConfig<>();
        service.setApplication(application);
        service.setRegistry(registry);
        service.setProtocol(protocol);
        service.setInterface(HashInterface.class);
        service.setRef(new HashServiceImpl(System.getProperty("salt"), configs));

        // 暴露及注册服务
        service.export();
    }




    private static void exportCallbackServiceIfNeed() {
//        Set<String> supportedExtensions =
//                ExtensionLoader.getExtensionLoader(CallbackService.class).getSupportedExtensions();
//        if (supportedExtensions != null && supportedExtensions.size() == 1) {
//            CallbackService callbackService =
//                    ExtensionLoader.getExtensionLoader(CallbackService.class)
//                            .getExtension(supportedExtensions.iterator().next());
//            ServiceConfig<CallbackService> callbackServiceServiceConfig = new ServiceConfig<>();
//            callbackServiceServiceConfig.setApplication(application);
//            callbackServiceServiceConfig.setRegistry(registry);
//            callbackServiceServiceConfig.setProtocol(protocol);
//            callbackServiceServiceConfig.setInterface(CallbackService.class);
//            callbackServiceServiceConfig.setRef(callbackService);
//            callbackServiceServiceConfig.setCallbacks(1000);
//            callbackServiceServiceConfig.setConnections(1);
//            MethodConfig methodConfig = new MethodConfig();
//            ArgumentConfig argumentConfig = new ArgumentConfig();
//            argumentConfig.setCallback(true);
//            argumentConfig.setIndex(1);
//            methodConfig.setArguments(Collections.singletonList(argumentConfig));
//            methodConfig.setName("addListener");
//            callbackServiceServiceConfig.setMethods(Collections.singletonList(methodConfig));
//            callbackServiceServiceConfig.export();
//        }
    }
}
