package vn.iotstar.services;

public interface IRecaptchaService {
    boolean verify(String token, String clientIp);
}
