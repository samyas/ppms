package com.advancedit.ppms.repositories;

import com.advancedit.ppms.models.organisation.Organisation;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrganisationRepository extends MongoRepository<Organisation, String>, OrganisationCustomRepository {


    Organisation findByTenantId(long tenantId);


	@Override
    void delete(Organisation deleted);
}
