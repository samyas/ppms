package com.advancedit.ppms.repositories;

import org.springframework.data.repository.CrudRepository;

import com.advancedit.ppms.models.Product;

public interface ProductRepository extends CrudRepository<Product, String> {
	
	@Override
    void delete(Product deleted);
}
