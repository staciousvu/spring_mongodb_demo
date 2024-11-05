package com.example.mongodbdemo;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final MongoTemplate mongoTemplate;
    public List<Product> findByName(String name){
        Query query=new Query(Criteria.where("name").is(name));
        return mongoTemplate.find(query,Product.class);
    }
    public List<Product> findProductsGreaterThan(double price){
        return mongoTemplate.find(new Query(Criteria.where("price").gt(price)),Product.class);
    }
    public List<Product> findProductsWithCategoryAndPriceRange(String category,double minPrice,
                                                               double maxPrice){
        Criteria criteria=new Criteria();
        criteria.and("category").is(category);
        criteria.and("price").gt(minPrice).lt(maxPrice);
        return mongoTemplate.find(new Query(criteria), Product.class);
    }
    public List<Product> findProductsWithStockLessThan(int stock){
        return mongoTemplate.find(new Query(Criteria.where("stock").lt(stock)), Product.class);
    }
    public List<Product> findProductWithNameContainKey(String key){
        Criteria criteria=new Criteria();
        criteria.and("name").regex(key,"i");
        return mongoTemplate.find(new Query(criteria), Product.class);
    }
    public List<Product> findProductsWithPriceLessThanOrStockGreaterThan(double price,int stock){
        Criteria criteria=new Criteria()
                .orOperator(
                        Criteria.where("price").lt(price),
                        Criteria.where("stock").gt(stock)
                );
        return mongoTemplate.find(new Query(criteria), Product.class);
    }
    public List<Map> calAveragePriceByCategory(){
        GroupOperation groupOperation= Aggregation.group("category")
                .avg("price").as("average_price");
        ProjectionOperation projectionOperation=Aggregation.project()
                .and("_id").as("category")
                .and("average_price").as("average_price")
                .andExclude("_id");
        SortOperation sortOperation=Aggregation.sort(
                Sort.by("average_price").descending()
        );
        Aggregation aggregation=Aggregation
                .newAggregation(groupOperation,projectionOperation,sortOperation);
        AggregationResults<Map> results=mongoTemplate
                .aggregate(aggregation, Product.class, Map.class);
        return results.getMappedResults();
    }
    public List<Map> countProductByCategory(){
        GroupOperation groupOperation=Aggregation.group("category")
                .count().as("count_product");
        ProjectionOperation projectionOperation=Aggregation.project()
                .and("_id").as("category")
                .and("count_product").as("count_product")
                .andExclude("_id");
        Aggregation aggregation=Aggregation.newAggregation(groupOperation,projectionOperation);
        AggregationResults<Map> results=mongoTemplate
                .aggregate(aggregation, Product.class, Map.class);
        return results.getMappedResults();
    }
    public List<Product> findProducts(String name, Double price, String category, String brand) {
        Criteria criteria = new Criteria();

        if (name != null) {
            criteria.and("name").is(name);
        }
        if (price != null) {
            criteria.and("price").is(price);
        }
        if (category != null) {
            criteria.and("category").is(category);
        }
        if (brand != null) {
            criteria.and("brand").is(brand);
        }

        Query query = new Query(criteria);
        return mongoTemplate.find(query, Product.class);
    }
    public Page<Product> getProducts(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Query query = new Query().with(pageable).with(Sort.by(Sort.Order.desc("price")));

        long total = mongoTemplate.count(query, Product.class);
        List<Product> products = mongoTemplate.find(query, Product.class);

        return new PageImpl<>(products, pageable, total);
    }
}
