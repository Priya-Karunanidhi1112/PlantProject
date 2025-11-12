package com.example.planting.controller;

import com.example.planting.model.Product;
import com.example.planting.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
public class ProductController {

    @Autowired
    private ProductService productService;

    // ✅ Show add product form
    @GetMapping("/admin/add-product")
    public String showAddProductForm(Model model) {
        model.addAttribute("product", new Product());
        return "add-product";
    }

    // ✅ Handle product submission
    @PostMapping("/admin/add-product")
    public String saveProduct(@ModelAttribute Product product,
                              @RequestParam("imageFile") MultipartFile file,
                              Model model) {
        try {
            if (!file.isEmpty()) {
                product.setImageData(file.getBytes());

                // 👇 Ensure this is set before save
                String contentType = file.getContentType();
                System.out.println("⏺ File Content Type: " + contentType);
                product.setImageType(contentType);  // This must not be null
            }

            productService.save(product); // 🔁 Must come AFTER setImageType
            model.addAttribute("success", "Product added successfully!");
            model.addAttribute("product", new Product());
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("error", "Image upload failed.");
        }
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/admin/update-product")
    public String updateProduct(@ModelAttribute Product product,
                                @RequestParam("imageFile") MultipartFile file,
                                Model model) {
        try {
            Product existing = productService.getById(product.getId());

            if (!file.isEmpty()) {
                // New image uploaded
                product.setImageData(file.getBytes());
                product.setImageType(file.getContentType());
            } else {
                // Keep old image if no new image
                product.setImageData(existing.getImageData());
                product.setImageType(existing.getImageType());
            }

            productService.save(product);
            model.addAttribute("success", "Product updated successfully!");
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("error", "Image upload failed.");
        }

        model.addAttribute("product", product);
        return "redirect:/admin/dashboard"; // or redirect to list
    }



    // ✅ Serve product image from DB
    @GetMapping("/product/image/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> getProductImage(@PathVariable Long id) {
        Product product = productService.getById(id);
        if (product == null || product.getImageData() == null || product.getImageType() == null) {
            return ResponseEntity.notFound().build();
        }

        MediaType mediaType = MediaType.parseMediaType(product.getImageType());
        return ResponseEntity.ok().contentType(mediaType).body(product.getImageData());
    }



    // (Optional) ✅ List all products
    @GetMapping("/products")
    public String viewAllProducts(Model model) {
        List<Product> products = productService.getAll();
        model.addAttribute("products", products);
        return "product-list";
    }
}
