package com.example.planting.controller;

import com.example.planting.model.Product;
import com.example.planting.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private ProductService productService;

    @GetMapping("/")
    public String index(Model model, Principal principal) {
        var best = productService.getByLabel("Best Seller");
        var newLaunch = productService.getByLabel("New Launch");
        var indoor = productService.getByLabel("Indoor");

        if (principal != null) {
            System.out.println("✅ Logged in as: " + principal.getName());
        }

        model.addAttribute("bestSellers", best);
        model.addAttribute("newLaunch", newLaunch);
        model.addAttribute("indoor", indoor);
        return "index";
    }


//    @GetMapping("/category/{name}")
//    public String getByCategory(@PathVariable String name, Model model) {
//        model.addAttribute("products", productService.getByCategory(name));
//        return "index"; // or your products list page
//    }

    @GetMapping("/category/{categoryName}")
    public String viewByCategory(@PathVariable String categoryName, Model model) {
        List<Product> products = productService.getByCategoryOnly(categoryName);

        model.addAttribute("products", products);
        model.addAttribute("selectedCategory", categoryName);
        return "product-list"; // 👉 Make sure this is your category listing page
    }

}
