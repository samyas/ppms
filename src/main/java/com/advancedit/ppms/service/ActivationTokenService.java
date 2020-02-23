package com.advancedit.ppms.service;

import com.advancedit.ppms.models.user.ActivationToken;
import com.advancedit.ppms.repositories.ActivationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
public class ActivationTokenService {

	@Value("${activation.token.expiration.days:3}")
	private int activationDuration = 3;

	@Autowired
	private ActivationTokenRepository activationTokenRepository;
    
    public ActivationToken generateActivationToken(long tenantId, String email){
		ActivationToken activationToken = new ActivationToken();
		activationToken.setEmail(email);
		activationToken.setTenantId(tenantId);
		activationToken.setToken(UUID.randomUUID().toString());
		activationToken.setCreationDate(new Date());
		return activationTokenRepository.insert(activationToken);
    }

	public Optional<ActivationToken> getActivationToken(String email, String token){
		List<ActivationToken> activationTokens = activationTokenRepository.findByTokenAndEmail(token, email);
		//-3  mercredi before lundi
		Date date = Date.from(LocalDateTime.now().minusDays(activationDuration).atZone(ZoneId.systemDefault()).toInstant());
		return activationTokens.stream().filter( a -> date.before(a.getCreationDate())).findFirst();
	}

	//Just for test
	public String getActivationToken(String email){
		List<ActivationToken> activationTokens = activationTokenRepository.findByEmail(email);
		Date date = Date.from(LocalDateTime.now().minusDays(activationDuration).atZone(ZoneId.systemDefault()).toInstant());
		return activationTokens.stream().filter( a -> date.before(a.getCreationDate())).findFirst()
				.map(ActivationToken::getToken).orElse(null);
	}
   
}
