package com.infy.eonservice.repository;

import com.infy.eonservice.domain.Customer;
import org.springframework.data.couchbase.core.query.Query;
import org.springframework.stereotype.Repository;


/**
 * Spring Data Couchbase repository for the Customer entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CustomerRepository extends N1qlCouchbaseRepository<Customer, String> {

}
