package com.ecommerce.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long productId;
    @NotBlank
    @Size(min = 3,message = "Product name must have atleast 3 characters!")
    private String productName;
    @NotBlank
    @Size(min = 6, message = "product name must have atleast 6 characters!")
    private String description;
    private String image;
    private Integer quantity;
    private double price; //100
    private double discount; // 25%
    private double specialPrice; //75

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;



}
