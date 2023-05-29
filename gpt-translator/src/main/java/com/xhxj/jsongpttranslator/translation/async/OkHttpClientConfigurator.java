package com.xhxj.jsongpttranslator.translation.async;

import com.unfbx.chatgpt.OpenAiClient;
import com.unfbx.chatgpt.function.KeyRandomStrategy;
import com.unfbx.chatgpt.interceptor.OpenAILogger;
import com.unfbx.chatgpt.interceptor.OpenAiResponseInterceptor;
import com.xhxj.jsongpttranslator.framework.chatgptconfig.ChatgptConfig;
import com.xhxj.jsongpttranslator.service.openaiproperties.OpenaiPropertiesService;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;

/**
 * @author:luoshuzhong
 * @create: 2023-05-29 14:49
 * @Description: 设置okhttpClient
 */
@Component
public class OkHttpClientConfigurator {

    @Autowired
    private ChatgptConfig chatgptConfig;

    @Autowired
    private OpenaiPropertiesService openaiPropertiesService;


    private OkHttpClient okHttpClient;


    public void setOkHttpClient() {
        Proxy proxy = null;
        //自定义代理
        if (StringUtils.isNotBlank(chatgptConfig.getProxyUlr())) {
            Proxy.Type proxyType = Proxy.Type.valueOf(chatgptConfig.getProxyType());
            proxy = new Proxy(proxyType, new InetSocketAddress(chatgptConfig.getProxyUlr(), chatgptConfig.getProxyPort()));
        }

        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new OpenAILogger());
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)//自定义日志输出
                .addInterceptor(new OpenAiResponseInterceptor())//自定义返回值拦截
                .connectTimeout(30, TimeUnit.SECONDS)//自定义超时时间
                .writeTimeout(360, TimeUnit.SECONDS)//自定义超时时间
                .readTimeout(360, TimeUnit.SECONDS);//自定义超时时间

        if (proxy != null) {
            builder.proxy(proxy);
        }
        //构建客户端
        this.okHttpClient = builder.build();
    }

    public OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }

    public OpenAiClient getOpenAiClient() {
        //构建请求信息
        OkHttpClient client = okHttpClient;


        OpenAiClient.Builder openAiClientBuilder = OpenAiClient.builder()
                .apiKey(openaiPropertiesService.getOpenaiKey())
                .keyStrategy(new KeyRandomStrategy())
                .okHttpClient(client);

        //如果有代理地址就设置
        if (StringUtils.isNotBlank(chatgptConfig.getApiHost())) {
            openAiClientBuilder.apiHost(chatgptConfig.getApiHost());
        }

        return openAiClientBuilder.build();
    }
}
