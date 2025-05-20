package vn.iotstar.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import vn.iotstar.model.RecaptchaDto;
import vn.iotstar.services.IRecaptchaService;

import java.util.Collections;

@Service
public class RecaptchaServiceImpl implements IRecaptchaService {
    @Value("${recaptcha.secret}")
    private String secret;

    @Value("${recaptcha.verify.url}")
    private String verifyUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public boolean verify(String token, String clientIp) {
        UriComponentsBuilder uri = UriComponentsBuilder.fromHttpUrl(verifyUrl)
                .queryParam("secret", secret)
                .queryParam("response", token)
                .queryParam("remoteip", clientIp);

        RecaptchaDto response = restTemplate.postForObject(
                uri.toUriString(), Collections.emptyList(), RecaptchaDto.class);

        return response != null && response.isSuccess();
    }
}
