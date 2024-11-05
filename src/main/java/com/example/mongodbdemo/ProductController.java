package com.example.mongodbdemo;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final MongoTemplate mongoTemplate;
    private final ProductRepository productRepository;
    private final ProductService productService;
    @GetMapping
    public List<Product> getAll(){
        return productRepository.findAll();
    }
    @PostMapping
    public Product createProduct(@RequestBody Product product){
        return productRepository.save(product);
    }
    @GetMapping("/{id}")
    public Product getProduct(@PathVariable String id){
        return productRepository.findById(id).orElseThrow();
    }
    @GetMapping("/price-less-than/{price}")
    public List<Product> findProductWithPriceLessThan(@PathVariable double price){
        Criteria criteria=new Criteria();
        criteria.and("price").lt(price);
        Query query=new Query(criteria);
        return mongoTemplate.find(query,Product.class);
    }
    @GetMapping("/avg")
    public List<Map> avg(){
        return productService.calAveragePriceByCategory();
    }
    @GetMapping("/count")
    public List<Map> count(){
        return productService.countProductByCategory();
    }
}
