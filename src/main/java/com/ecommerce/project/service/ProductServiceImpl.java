package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.payload.ProductResponse;
import com.ecommerce.project.repositories.CategoryRepository;
import com.ecommerce.project.repositories.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService{

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FileService fileService;

    @Value("${project.image}")
    private String path;

    @Override
    public ProductDTO addProduct(Long categoryId, ProductDTO productDTO){
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        boolean isProductPresent = false;
        List<Product> products = category.getProducts();

        for(Product value: products){
            if(value.getProductName().equals(productDTO.getProductName())){
                isProductPresent = true;
                break;
            }
        }
        if(!isProductPresent){
            Product product = modelMapper.map(productDTO, Product.class);

            product.setCategory(category);
            product.setImage("default.png");
            double specialPrice = product.getPrice() -  ((product.getDiscount() * 0.01) * product.getPrice());
            product.setSpecialPrice(specialPrice);

            Product savedProduct = productRepository.save(product);
            ProductDTO savedProductDTO = modelMapper.map(savedProduct, ProductDTO.class);
            return savedProductDTO;

        }else{
            throw new APIException("Product already exists!!");

        }
    }

    @Override
    public ProductResponse getAllProducts() {
        //if product size is 0 or not
        List<Product> products = productRepository.findAll();
        List<ProductDTO> productDTOS = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .collect(Collectors.toList());

        if(products.isEmpty()){
            throw new APIException("No Products Exist in the repository yet!!");
        }

        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        return productResponse;

    }

    @Override
    public ProductResponse searchByCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));

        List<Product> products = productRepository.findByCategoryOrderByPriceAsc(category);
        List<ProductDTO> productDTOS = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .collect(Collectors.toList());
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);

        return productResponse;
    }

    @Override
    public ProductResponse searchProductByKeyword(String keyword){
        List<Product> products = productRepository.findByProductNameLikeIgnoreCase('%' + keyword + '%');
        List<ProductDTO> productDTOS = products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .collect(Collectors.toList());

        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        return productResponse;

    }

    @Override
    public ProductDTO updateProduct(Long productId, ProductDTO productDTO) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        Product savedProductFromDB = optionalProduct
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        Product product = modelMapper.map(productDTO, Product.class);

        savedProductFromDB.setProductName(product.getProductName());
        savedProductFromDB.setDescription(product.getDescription());
        savedProductFromDB.setQuantity(product.getQuantity());
        savedProductFromDB.setDiscount(product.getDiscount());
        savedProductFromDB.setPrice(product.getPrice());

        double specialPrice = product.getPrice() - ((product.getDiscount() * 0.01) * product.getPrice());
        savedProductFromDB.setSpecialPrice(specialPrice);

        Product savedProduct = productRepository.save(savedProductFromDB);
        ProductDTO savedProductDTO = modelMapper.map(savedProduct, ProductDTO.class);
        return savedProductDTO;
    }

    @Override
    public ProductDTO deleteProduct(Long productId) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        Product savedProductFromDB = optionalProduct
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));
        productRepository.delete(savedProductFromDB);
        ProductDTO deletedProductDTO = modelMapper.map(savedProductFromDB, ProductDTO.class);
        return deletedProductDTO;
    }

    @Override
    public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {

        //Get the product from the db
        Optional<Product> optionalProduct = productRepository.findById(productId);
        Product savedProductFromDB = optionalProduct
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));


        //upload image to server (uploading into the /image dir)
        //Get the file name of uploaded image

        String fileName = fileService.uploadImage(path, image);

        //updating the new file name to the product
        savedProductFromDB.setImage(fileName);
        
        //save the updated product
        Product updatedProduct = productRepository.save(savedProductFromDB);

        //return DTO after mapping product to DTO
        ProductDTO updatedProductDTO = modelMapper.map(updatedProduct, ProductDTO.class);
        return updatedProductDTO;
    }



}












