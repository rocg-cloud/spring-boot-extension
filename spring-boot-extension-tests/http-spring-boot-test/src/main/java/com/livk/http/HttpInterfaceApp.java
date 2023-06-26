/*
 * Copyright 2021 spring-boot-extension the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.livk.http;

import com.livk.autoconfigure.http.customizer.HttpServiceProxyFactoryCustomizer;
import com.livk.commons.spring.SpringLauncher;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.EmbeddedValueResolver;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * @author livk
 */
@SpringBootApplication
public class HttpInterfaceApp {

	public static void main(String[] args) {
		SpringLauncher.run(args);
	}

	@Bean
	public HttpServiceProxyFactoryCustomizer httpServiceProxyFactoryCustomizer(ConfigurableBeanFactory beanFactory) {
		return builder -> builder.embeddedValueResolver(new EmbeddedValueResolver(beanFactory));
	}
}