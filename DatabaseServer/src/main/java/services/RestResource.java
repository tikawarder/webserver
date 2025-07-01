package services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJsonProvider;
import org.glassfish.jersey.server.ResourceConfig;

public class RestResource {
	private final JacksonJsonProvider provider;

	public RestResource(ObjectMapper mapper, JavaTimeModule module, SerializationFeature feature, JacksonJsonProvider jsonProvider ) {
		this.provider = jsonProvider;
		mapper.registerModule(module);
		mapper.disable(feature);
		provider.setMapper(mapper);
	}

	public ResourceConfig getRestProviderConfig(String packageName, Class<?> classToRegister) {
		return new ResourceConfig()
				.packages(packageName)
				.register(provider)
				.register(classToRegister);
	}
}
