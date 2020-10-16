package com.advancedit.ppms.repositories;

import com.advancedit.ppms.models.user.User;

import java.util.List;

public interface UserCustomRepository {

   List<User> findByEmails(long tenantId, List<String> emails);
}
