package vn.iotstar.services;

public interface IOtpService {

	boolean validateOtp(String email, String otp);

	String generateOtp(String email);

}
