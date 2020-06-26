package com.orange.lo.sample.lo2disstream.flexible_engine;

import com.huaweicloud.dis.DIS; 

public class FEDeviceClient {
	private String DisDeviceId;
	private	String DisDeviceName;
	private String DisStreamName;
	private DIS Disclientstandard;
	
	
	public FEDeviceClient (String DisDeviceId, String DisDeviceName, String DisStreamName, DIS Disclientstandard) {
		this.DisDeviceId = DisDeviceId;
		this.DisDeviceName = DisDeviceName;
		this.DisStreamName = DisStreamName;
		this.Disclientstandard = Disclientstandard;
	}
	
	public String getDisDeviceName() {
		return this.DisDeviceName;
	}
	
	public String getDisStreamName() {
		return this.DisStreamName;
	}
	
	public String setDisStreamName(String DisStreamName) {
		this.DisStreamName = DisStreamName;
		return this.DisStreamName;
	}
	
	public DIS getDisclientstandard() {
		return this.Disclientstandard;
	}
	
}
