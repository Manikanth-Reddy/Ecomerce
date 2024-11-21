package com.ecom.util;

public enum BucketType {
	CATEGORY(1), PRODUCT(2), PROFILE(3);

	private Integer id;

	private BucketType(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

}
