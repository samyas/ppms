package com.advancedit.ppms.repositories;

import com.advancedit.ppms.models.user.ActivationToken;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ActivationTokenRepository extends MongoRepository<ActivationToken, String> {

	List<ActivationToken> findByTokenAndEmail(String token, String email);

	List<ActivationToken> findByEmail(String token);

}
