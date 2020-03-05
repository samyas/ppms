package com.advancedit.ppms.repositories;

import com.advancedit.ppms.models.user.User;
import com.advancedit.ppms.models.user.VerificationToken;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface VerificationTokenRepository extends MongoRepository<VerificationToken, String> {

	VerificationToken findByToken(UUID token);

	VerificationToken findByEmailAndToken(String email, UUID token);

}
