package vn.iotstar.services;

public interface IEmailService {

	void sendEmail(String to, String subject, String text);

}
