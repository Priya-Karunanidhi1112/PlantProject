package com.example.planting.model;

import jakarta.persistence.*;

@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String category;
    private double price;

    @Column(name = "image_type")
    private String imageType;



    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] imageData;  // Stores image as binary data

    private String label;

    public Product() {}

    public Product(String name, String category, double price, byte[] imageData, String label, String imageType) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.imageData = imageData;
        this.label = label;
        this.imageType = imageType;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public byte[] getImageData() { return imageData; }
    public void setImageData(byte[] imageData) { this.imageData = imageData; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public String getImageType() {
        return imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }
}
