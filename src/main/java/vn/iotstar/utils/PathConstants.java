package vn.iotstar.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class PathConstants {
	@Value("${app.upload.directory}")
	private String uploadDirectory;
	
	public static String UPLOAD_DIRECTORY;
	
	@PostConstruct
	public void init()
	{
		UPLOAD_DIRECTORY = uploadDirectory;
	}
}