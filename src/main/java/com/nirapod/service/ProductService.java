package com.nirapod.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nirapod.dao.ProductDAO;
import com.nirapod.dao.TaxStructureDAO;
import com.nirapod.dto.request.ProductRequest;
import com.nirapod.model.Product;
import com.nirapod.model.TaxStructure;

@Service(value = "productService")
@Transactional
public class ProductService {

    @Autowired
    ProductDAO productDAO;

    @Autowired
    TaxStructureDAO taxStructureDAO;

    public void create(ProductRequest request) {
    TaxStructure taxStructure = taxStructureDAO.getById(request.getTaxStructureId());

    if (taxStructure == null) {
        throw new RuntimeException("Tax structure not found with id: " + request.getTaxStructureId());
    }

    Product product = new Product();
    applyRequestToProduct(product, request, taxStructure);
    productDAO.save(product);
}

	


    public List<Product> getAll() {
        return productDAO.getAll();
    }

    public Product getById(int id) {
        return productDAO.getById(id);
    }

    public void update(int id, ProductRequest request) {
	    Product existingProduct = productDAO.getById(id);
	
	    if (existingProduct == null) {
	        throw new RuntimeException("Product not found with id: " + id);
	    }
	
	    TaxStructure taxStructure = taxStructureDAO.getById(request.getTaxStructureId());
	
	    if (taxStructure == null) {
	        throw new RuntimeException("Tax structure not found with id: " + request.getTaxStructureId());
	    }
	
	    applyRequestToProduct(existingProduct, request, taxStructure);
	    productDAO.update(existingProduct);
	}

    public void delete(int id) {
        productDAO.delete(id);
    }
    
    private void applyRequestToProduct(Product product, ProductRequest request, TaxStructure taxStructure) {
        product.setProductName(request.getProductName());
        product.setHsCode(request.getHsCode());
        product.setCategory(request.getCategory());
        product.setTaxType(request.getTaxType());
        product.setTaxStructure(taxStructure);
        product.setTaxRate(request.getTaxRate());
        product.setUnit(request.getUnit());
        product.setDescription(request.getDescription());
        product.setStatus(request.getStatus());
    }

    
}