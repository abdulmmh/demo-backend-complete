package com.nirapod.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.*;

import com.nirapod.dto.ProductRequest;
import com.nirapod.model.Product;
import com.nirapod.services.ProductService;


@RestController
@RequestMapping(value = "/api/taxable-products")
@CrossOrigin(origins = "http://localhost:4200")
public class ProductController {

    @Autowired
    ProductService productService;
    
    @PostMapping
    public void create(@RequestBody ProductRequest request) {
        productService.create(request);
    }

   
    
    @GetMapping()
    public List<Product> getAll( ){
    	return productService.getAll();
    }
    
    @GetMapping("/{id}")
    public Product getById(@PathVariable int id) {
        return productService.getById(id);
    }
    
    @PutMapping("/{id}")
    public void update(@PathVariable int id, @RequestBody ProductRequest request) {
        productService.update(id, request);
    }
    
    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
    	productService.delete(id);
    }
}