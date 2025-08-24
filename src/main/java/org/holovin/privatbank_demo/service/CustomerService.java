package org.holovin.privatbank_demo.service;

import lombok.RequiredArgsConstructor;
import org.holovin.privatbank_demo.entity.Customer;
import org.holovin.privatbank_demo.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    @Transactional
    public Customer createCustomer(Customer customer) {
        if (customer.getId() != null && customerRepository.existsById(customer.getId())) {
            throw new IllegalArgumentException("Customer with id " + customer.getId() + " already exists");
        }

        if (customer.getEmail() != null && customerRepository.existsByEmail(customer.getEmail())) {
            throw new IllegalArgumentException("Customer with email " + customer.getEmail() + " already exists");
        }

        return customerRepository.save(customer);
    }
}
