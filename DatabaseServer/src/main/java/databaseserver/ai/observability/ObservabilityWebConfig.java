package databaseserver.ai.observability;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ObservabilityWebConfig implements WebMvcConfigurer {

    private final AiEndpointInterceptor aiEndpointInterceptor;

    public ObservabilityWebConfig(AiEndpointInterceptor aiEndpointInterceptor) {
        this.aiEndpointInterceptor = aiEndpointInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(aiEndpointInterceptor).addPathPatterns("/api/ai/**");
    }
}
