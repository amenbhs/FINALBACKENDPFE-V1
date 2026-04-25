package com.example.PfeBack.controller;
import com.example.PfeBack.models.Product;
import com.example.PfeBack.Services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductController {
    @Autowired
    private ProductService productService;
    
    @Autowired
    private RestTemplate restTemplate;
    
    private static final String EU_AGRI_API_BASE = "https://agridata.ec.europa.eu/api";

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable String id) {
        return productService.getProductById(id);
    }

    @PostMapping
    public Product createProduct(@RequestBody Product product) {
        return productService.createProduct(product);
    }

    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable String id, @RequestBody Product product) {
        return productService.updateProduct(id, product);
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
    }

    // European Agricultural Data API Proxy Endpoints
    @GetMapping("/agri-data/{category}")
    public ResponseEntity<Object> getAgriData(@PathVariable String category) {
        try {
            String url = EU_AGRI_API_BASE + "/" + category;
            Object response = restTemplate.getForObject(url, Object.class);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> fallbackData = generateFallbackData(category);
            return ResponseEntity.ok(fallbackData);
        }
    }

    @GetMapping("/agri-data/fruit-vegetable/{endpoint}")
    public ResponseEntity<Object> getFruitVegetableData(@PathVariable String endpoint) {
        try {
            String url = EU_AGRI_API_BASE + "/fruitAndVegetable/" + endpoint;
            Object response = restTemplate.getForObject(url, Object.class);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> fallbackData = generateFallbackData("fruit-vegetable-" + endpoint);
            return ResponseEntity.ok(fallbackData);
        }
    }

    @GetMapping("/agri-data/fruit-vegetable/prices/{subEndpoint}")
    public ResponseEntity<Object> getFruitVegetablePricesData(@PathVariable String subEndpoint) {
        try {
            String url = EU_AGRI_API_BASE + "/fruitAndVegetable/pricesSupplyChain/" + subEndpoint;
            Object response = restTemplate.getForObject(url, Object.class);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> fallbackData = generateFallbackData("prices-" + subEndpoint);
            return ResponseEntity.ok(fallbackData);
        }
    }

    private Map<String, String> generateFallbackData(String category) {
        Map<String, String> fallbackData = new HashMap<>();
        fallbackData.put("status", "fallback");
        fallbackData.put("message", "EU Agricultural API is not accessible. Using mock data.");
        fallbackData.put("category", category);
        fallbackData.put("note", "This is sample data for development purposes");
        
        // Add some sample data based on category
        switch(category) {
            case "beef":
                fallbackData.put("price", "4.50 EUR/kg");
                fallbackData.put("trend", "stable");
                break;
            case "milk-and-dairy":
                fallbackData.put("price", "0.35 EUR/L");
                fallbackData.put("trend", "increasing");
                break;
            case "cereals":
                fallbackData.put("price", "200 EUR/ton");
                fallbackData.put("trend", "decreasing");
                break;
            default:
                fallbackData.put("price", "N/A");
                fallbackData.put("trend", "stable");
        }
        
        return fallbackData;
    }
}
