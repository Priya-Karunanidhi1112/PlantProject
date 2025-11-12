package com.example.planting.controller;

import com.example.planting.model.Product;
import com.example.planting.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ProductService productService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("products", productService.getAll());
        return "dashboard";
    }

    @PostMapping("/add")
    public String addProduct(@ModelAttribute Product product,
                             @RequestParam("imageFile") MultipartFile file,
                             Model model) throws IOException {
        if (!file.isEmpty()) {
            product.setImageData(file.getBytes());
        }
        productService.save(product);
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/edit/{id}")
    public String editProductForm(@PathVariable Long id, Model model) {
        model.addAttribute("product", productService.getById(id));
        model.addAttribute("products", productService.getAll());
        return "dashboard"; // reuse same page
    }

    @PostMapping("/update")
    public String updateProduct(@ModelAttribute Product product,
                                @RequestParam("imageFile") MultipartFile file) throws IOException {
        if (!file.isEmpty()) {
            product.setImageData(file.getBytes());
        } else {
            Product old = productService.getById(product.getId());
            product.setImageData(old.getImageData());
        }
        productService.save(product);
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        productService.deleteById(id);
        return "redirect:/admin/dashboard";
    }
}
