package com.advancedit.ppms.service;

import com.advancedit.ppms.exceptions.PPMSException;
import com.advancedit.ppms.models.user.VerificationToken;
import com.advancedit.ppms.repositories.UserRepository;
import com.advancedit.ppms.repositories.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static com.advancedit.ppms.utils.GeneralUtils.decode;


@Service
public class VerificationTokenService {

	@Value("${email.token.expiration.days:5}")
	private int expirationDuration = 5;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private VerificationTokenRepository verificationTokenRepository;

	@Autowired
	private PersonService personService;





	public VerificationToken generateValidationEmailToken(long tenantId, String email){
		VerificationToken verificationToken = new VerificationToken();
		verificationToken.setToken(UUID.randomUUID());
		verificationToken.setCreationDate(new Date());
		verificationToken.setEmail(email);
		verificationToken.setTenantId(tenantId);
		return verificationTokenRepository.save(verificationToken);
	}


	public VerificationToken validateToken(String emailToken) {

		String decode = decode(emailToken);
		String[] split = decode.split(":");
		String email = split[0];
		String token = split[1];
		VerificationToken verificationToken = verificationTokenRepository.findByEmailAndToken(email, UUID.fromString(token));
		if (verificationToken == null){
			throw new PPMSException("Token is invalid");
		}
		Date date = Date.from(LocalDateTime.now().minusDays(expirationDuration).atZone(ZoneId.systemDefault()).toInstant());
		if (verificationToken.getCreationDate().before(date)){
			throw new PPMSException("Token is expired");
		}

		return verificationToken;
	}


}


