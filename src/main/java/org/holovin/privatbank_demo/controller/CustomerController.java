package org.holovin.privatbank_demo.controller;

import lombok.RequiredArgsConstructor;
import org.holovin.privatbank_demo.entity.Customer;
import org.holovin.privatbank_demo.service.CustomerService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    public Customer create(@RequestBody Customer customer) {
        return customerService.createCustomer(customer);
    }
}
