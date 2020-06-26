/** 
* Copyright (c) Orange. All Rights Reserved.
* 
* This source code is licensed under the MIT license found in the 
* LICENSE file in the root directory of this source tree. 
*/

package com.orange.lo.sample.lo2disstream.flexible_engine;


import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.orange.lo.sample.lo2disstream.flexible_engine.FEDeviceClient;

@Component
public class DisClientCache {

	private Map<String, FEDeviceClient> map = new ConcurrentHashMap<>();
	
	public FEDeviceClient add(String deviceId, FEDeviceClient client) {
		return map.put(deviceId, client);
	}
	
	public FEDeviceClient get(String deviceId) {
		return map.get(deviceId);
	}
	
	public boolean remove(String deviceId) {

		FEDeviceClient client = map.remove(deviceId);
		return true;
	}
	
	public Set<String> getDeviceIds() {
		return map.keySet();
	}
}
