/** 
* Copyright (c) Orange. All Rights Reserved.
* 
* This source code is licensed under the MIT license found in the 
* LICENSE file in the root directory of this source tree. 
*/

package com.orange.lo.sample.lo2disstream.flexible_engine;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;


import com.orange.lo.sample.lo2disstream.flexible_engine.FlexibleEngineProperties;

@Configuration
public class FlexibleEngineConfiguration {
	
	private FlexibleEngineProperties flexibleengineProperties;
	
	public FlexibleEngineConfiguration(FlexibleEngineProperties flexibleengineProperties) {
		this.flexibleengineProperties = flexibleengineProperties;
	}
	
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
	
}
