package com.advancedit.ppms.repositories;

import com.advancedit.ppms.models.files.ModuleFile;
import com.advancedit.ppms.service.beans.AttachType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModuleFileRepository extends MongoRepository<ModuleFile, String> {

	ModuleFile findByTenantIdAndUrlAndType(long tenantId, String url, AttachType type);

	List<ModuleFile> findByTenantIdAndModuleId(long tenantId, String moduleId);

	void deleteByTenantIdAndModuleIdAndKey(long tenantId, String moduleId, String key);
}
