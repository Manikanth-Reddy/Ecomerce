package com.ecom.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {

	public boolean saveFile(MultipartFile file, Integer bucketType);
}
