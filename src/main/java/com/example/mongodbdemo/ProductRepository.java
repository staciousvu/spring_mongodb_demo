package com.example.mongodbdemo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ProductRepository extends MongoRepository<Product,String> {
    @Query("{ 'name': ?0 }")
    List<Product> findByName(String name);

    @Query("{ 'price': { $gt: ?0 } }")
    List<Product> findByPriceGreaterThan(double price);

    @Query("{ 'category': ?0, 'price': { $gte: ?1, $lte: ?2 } }")
    List<Product> findByCategoryAndPriceBetween(String category, double minPrice, double maxPrice);

    @Query("{ 'stock': { $lt: ?0 } }")
    List<Product> findByStockLessThan(int stock);

    @Query("{ 'name': { $regex: ?0, $options: 'i' } }")
    List<Product> findByNameRegex(String key);

    @Query("{ $or: [ { 'price': { $lt: ?0 } }, { 'stock': { $gt: ?1 } } ] }")
    List<Product> findByPriceLessThanOrStockGreaterThan(double price, int stock);

    @Query(value = "{ '$group': { '_id': '$category', 'average_price': { '$avg': '$price' } } }")
    List<Map> calAveragePriceByCategory();

    @Query(value = "{ '$group': { '_id': '$category', 'count_product': { '$sum': 1 } } }")
    List<Map> countProductByCategory();

    @Query(value = "{ ?0: ?1, ?2: ?3, ?4: ?5, ?6: ?7 }")
    List<Product> findProducts(
            @Param("name") String name,
            @Param("price") Double price,
            @Param("category") String category,
            @Param("brand") String brand);

    @Query("{}")
    Page<Product> findAll(Pageable pageable);
}
