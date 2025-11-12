//package com.example.planting.controller;
//
//import com.example.planting.model.Order;
//import com.example.planting.model.User;
//import com.example.planting.repository.OrderRepository;
//import com.example.planting.repository.UserRepository;
//import com.example.planting.service.OrderService;
//import com.example.planting.service.UserService;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Controller;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//
//import java.security.Principal;
//import java.time.LocalDateTime;
//import java.util.*;
//
//@Controller
//@RequestMapping("/cart")
//public class CartController {
//
//    @Autowired
//    private OrderService orderService;
//
//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    private OrderRepository orderRepository ;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    private final ObjectMapper mapper = new ObjectMapper();
//
//    // 🛒 View Cart Page
//    @GetMapping
//    public String showCart(Model model, @AuthenticationPrincipal UserDetails userDetails) throws JsonProcessingException {
//        User user = userService.findByEmail(userDetails.getUsername());
//        Order order = orderService.getOrCreateCart(user.getId());
//
//        ObjectMapper mapper = new ObjectMapper();
//        List<Map<String, Object>> cartItems = new ArrayList<>();
//        if (order.getItems() != null) {
//            cartItems = mapper.readValue(order.getItems(), List.class);
//        }
//
//        // Extract address JSON if available
//        Map<String, String> address = null;
//        if (order.getShippingAddress() != null) {
//            address = mapper.readValue(order.getShippingAddress(), Map.class);
//        }
//
//        model.addAttribute("cartItems", cartItems);
//        model.addAttribute("total", order.getTotalAmount() != null ? order.getTotalAmount() : 0);
//        model.addAttribute("order", order);
//        model.addAttribute("address", address);  // ✅ needed in cart.html
//
//        return "cart"; // ✅ Must match `cart.html` inside templates
//    }
//
//
//    // ➕ Add Item to Cart
//    @PostMapping("/add")
//    public String addToCart(@RequestParam Long productId,
//                            @RequestParam String name,
//                            @RequestParam Double price,
//                            @AuthenticationPrincipal UserDetails userDetails) throws JsonProcessingException {
//        User user = userService.findByEmail(userDetails.getUsername());
//        Order order = orderService.getOrCreateCart(user.getId());
//
//        List<Map<String, Object>> cartItems = getCartItems(order);
//
//        boolean found = false;
//        for (Map<String, Object> item : cartItems) {
//            if (Long.parseLong(item.get("id").toString()) == productId) {
//                int qty = Integer.parseInt(item.get("quantity").toString());
//                item.put("quantity", qty + 1);
//                found = true;
//                break;
//            }
//        }
//
//        if (!found) {
//            Map<String, Object> newItem = new HashMap<>();
//            newItem.put("id", productId);
//            newItem.put("name", name);
//            newItem.put("price", price);
//            newItem.put("quantity", 1);
//            cartItems.add(newItem);
//        }
//
//        updateOrderTotals(order, cartItems);
//        return "redirect:/cart";
//    }
//
//    // 🔁 Update Quantity
//    @PostMapping("/update")
//    public String updateQuantity(@RequestParam Long id,
//                                 @RequestParam int quantity,
//                                 @AuthenticationPrincipal UserDetails userDetails) throws JsonProcessingException {
//        User user = userService.findByEmail(userDetails.getUsername());
//        Order order = orderService.getOrCreateCart(user.getId());
//
//        List<Map<String, Object>> cartItems = getCartItems(order);
//
//        for (Map<String, Object> item : cartItems) {
//            if (Long.parseLong(item.get("id").toString()) == id) {
//                item.put("quantity", quantity);
//                break;
//            }
//        }
//
//        updateOrderTotals(order, cartItems);
//        return "redirect:/cart";
//    }
//
//    // ❌ Remove Item
//    @GetMapping("/remove/{id}")
//    public String removeItem(@PathVariable Long id,
//                             @AuthenticationPrincipal UserDetails userDetails) throws JsonProcessingException {
//        User user = userService.findByEmail(userDetails.getUsername());
//        Order order = orderService.getOrCreateCart(user.getId());
//
//        List<Map<String, Object>> cartItems = getCartItems(order);
//        cartItems.removeIf(item -> Long.parseLong(item.get("id").toString()) == id);
//
//        updateOrderTotals(order, cartItems);
//        return "redirect:/cart";
//    }
//
//    // 🗑 Clear Cart
//    @GetMapping("/clear")
//    public String clearCart(@AuthenticationPrincipal UserDetails userDetails) {
//        User user = userService.findByEmail(userDetails.getUsername());
//        Order order = orderService.getOrCreateCart(user.getId());
//
//        order.setItems(null);
//        order.setTotalAmount(0.0);
//        order.setTax(0.0);
//        order.setGrandTotal(0.0);
//        order.setShippingAddress(null);
//
//        orderService.save(order);
//        return "redirect:/cart";
//    }
//
//    // 📍 Save/Update Shipping Address
//    @PostMapping("/address")
//    public String updateAddress(@RequestParam String name,
//                                @RequestParam String street,
//                                @RequestParam String city,
//                                @RequestParam String pincode,
//                                @RequestParam String phone,
//                                @AuthenticationPrincipal UserDetails userDetails) throws JsonProcessingException {
//
//        User user = userService.findByEmail(userDetails.getUsername());
//        Order order = orderService.getOrCreateCart(user.getId());
//
//        Map<String, String> address = new LinkedHashMap<>();
//        address.put("name", name);
//        address.put("street", street);
//        address.put("city", city);
//        address.put("pincode", pincode);
//        address.put("phone", phone);
//
//        order.setShippingAddress(mapper.writeValueAsString(address));
//        orderService.save(order);
//        return "redirect:/cart";
//    }
//
//    // 📦 Utility: Convert JSON string → List
//    private List<Map<String, Object>> getCartItems(Order order) throws JsonProcessingException {
//        if (order.getItems() == null) return new ArrayList<>();
//        return mapper.readValue(order.getItems(), List.class);
//    }
//
//    // 📦 Utility: Convert JSON address → Map
//    private Map<String, String> parseAddress(String json) {
//        try {
//            if (json != null && !json.isEmpty()) {
//                return mapper.readValue(json, Map.class);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return new HashMap<>();
//    }
//
//    // 💰 Utility: Update Total, Tax, Grand Total
//    private void updateOrderTotals(Order order, List<Map<String, Object>> cartItems) throws JsonProcessingException {
//        order.setItems(mapper.writeValueAsString(cartItems));
//
//        double total = cartItems.stream()
//                .mapToDouble(item -> {
//                    double price = Double.parseDouble(item.get("price").toString());
//                    int qty = Integer.parseInt(item.get("quantity").toString());
//                    return price * qty;
//                }).sum();
//
//        order.setTotalAmount(total);
//        order.setTax(total * 0.05);
//        order.setGrandTotal(total + order.getTax());
//        orderService.save(order);
//    }
//    @GetMapping("/checkout")
//    public String showCheckoutPage(Model model) {
//        // If you want to show order summary or cart details here, you can add them later.
//        return "checkout"; // Must match checkout.html in /templates
//    }
//
//    @PostMapping("/checkout")
//    @Transactional // add this if not present
//    public String processCheckout(
//            @RequestParam("paymentMethod") String paymentMethod,
//            @RequestParam(value = "cardName", required = false) String cardName,
//            @RequestParam(value = "cardNumber", required = false) String cardNumber,
//            @RequestParam(value = "expiry", required = false) String expiry,
//            @RequestParam(value = "cvv", required = false) String cvv,
//            Principal principal,
//            RedirectAttributes redirectAttributes) {
//
//        String email = principal.getName();
//        User user = userRepository.findByEmail(email);
//
//        List<Order> cartOrders = orderRepository.findByUserIdAndOrderStatus(user.getId(), "In Cart");
//
//        if (cartOrders != null && !cartOrders.isEmpty()) {
//            for (Order order : cartOrders) {
//                if ("card".equalsIgnoreCase(paymentMethod)) {
//                    order.setPaymentStatus("Paid");
//                    if (cardNumber != null && cardNumber.length() >= 4) {
//                        order.setPaymentDetails("Card: **** **** **** " + cardNumber.substring(cardNumber.length() - 4));
//                    } else {
//                        order.setPaymentDetails("Card");
//                    }
//                } else {
//                    order.setPaymentStatus("Pending");
//                    order.setPaymentDetails("Cash on Delivery");
//                }
//
//                order.setOrderStatus("Ordered");
//                order.setUpdatedAt(LocalDateTime.now());
//
//                System.out.println("✅ Saving Order ID: " + order.getId());
//                orderRepository.save(order);
//            }
//
//            redirectAttributes.addFlashAttribute("method", paymentMethod.equals("card") ? "Card" : "Cash on Delivery");
//            redirectAttributes.addFlashAttribute("message", "🎉 Order placed successfully!");
//        } else {
//            redirectAttributes.addFlashAttribute("error", "No items in cart to checkout.");
//        }
//
//        return "redirect:/cart/payment-success";
//    }
//
//    @GetMapping("/payment-success")
//    public String showPaymentSuccess(Model model, @ModelAttribute("method") String method) {
//        model.addAttribute("method", method);
//        return "payment-success";
//    }
//
//
//}
package com.example.planting.controller;

import com.example.planting.model.Order;
import com.example.planting.model.User;
import com.example.planting.repository.OrderRepository;
import com.example.planting.repository.UserRepository;
import com.example.planting.service.OrderService;
import com.example.planting.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    private final ObjectMapper mapper = new ObjectMapper();

    // 🛒 View Cart Page
    @GetMapping
    public String showCart(Model model, @AuthenticationPrincipal UserDetails userDetails) throws JsonProcessingException {
        User user = userService.findByEmail(userDetails.getUsername());
        Order order = orderService.getOrCreateCart(user.getId());

        List<Map<String, Object>> cartItems = getCartItems(order);
        Map<String, String> address = parseAddress(order.getShippingAddress());

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("total", order.getTotalAmount() != null ? order.getTotalAmount() : 0);
        model.addAttribute("order", order);
        model.addAttribute("address", address);

        return "cart";
    }

    // ➕ Add Item to Cart
    @PostMapping("/add")
    public String addToCart(@RequestParam Long productId,
                            @RequestParam String name,
                            @RequestParam Double price,
                            @AuthenticationPrincipal UserDetails userDetails) throws JsonProcessingException {
        User user = userService.findByEmail(userDetails.getUsername());
        Order order = orderService.getOrCreateCart(user.getId());

        List<Map<String, Object>> cartItems = getCartItems(order);
        boolean found = false;

        for (Map<String, Object> item : cartItems) {
            if (Long.parseLong(item.get("id").toString()) == productId) {
                int qty = Integer.parseInt(item.get("quantity").toString());
                item.put("quantity", qty + 1);
                found = true;
                break;
            }
        }

        if (!found) {
            Map<String, Object> newItem = new HashMap<>();
            newItem.put("id", productId);
            newItem.put("name", name);
            newItem.put("price", price);
            newItem.put("quantity", 1);
            cartItems.add(newItem);
        }

        updateOrderTotals(order, cartItems);
        return "redirect:/cart";
    }

    // 🔁 Update Quantity
    @PostMapping("/update")
    public String updateQuantity(@RequestParam Long id,
                                 @RequestParam int quantity,
                                 @AuthenticationPrincipal UserDetails userDetails) throws JsonProcessingException {
        User user = userService.findByEmail(userDetails.getUsername());
        Order order = orderService.getOrCreateCart(user.getId());

        List<Map<String, Object>> cartItems = getCartItems(order);
        for (Map<String, Object> item : cartItems) {
            if (Long.parseLong(item.get("id").toString()) == id) {
                item.put("quantity", quantity);
                break;
            }
        }

        updateOrderTotals(order, cartItems);
        return "redirect:/cart";
    }

    // ❌ Remove Item
    @GetMapping("/remove/{id}")
    public String removeItem(@PathVariable Long id,
                             @AuthenticationPrincipal UserDetails userDetails) throws JsonProcessingException {
        User user = userService.findByEmail(userDetails.getUsername());
        Order order = orderService.getOrCreateCart(user.getId());

        List<Map<String, Object>> cartItems = getCartItems(order);
        cartItems.removeIf(item -> Long.parseLong(item.get("id").toString()) == id);

        updateOrderTotals(order, cartItems);
        return "redirect:/cart";
    }

    // 🗑 Clear Cart
    @GetMapping("/clear")
    public String clearCart(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByEmail(userDetails.getUsername());
        Order order = orderService.getOrCreateCart(user.getId());

        order.setItems(null);
        order.setTotalAmount(0.0);
        order.setTax(0.0);
        order.setGrandTotal(0.0);
        order.setShippingAddress(null);

        orderService.save(order);
        return "redirect:/cart";
    }

    // 📍 Save/Update Shipping Address
    @PostMapping("/address")
    public String updateAddress(@RequestParam String name,
                                @RequestParam String street,
                                @RequestParam String city,
                                @RequestParam String pincode,
                                @RequestParam String phone,
                                @AuthenticationPrincipal UserDetails userDetails) throws JsonProcessingException {

        User user = userService.findByEmail(userDetails.getUsername());
        Order order = orderService.getOrCreateCart(user.getId());

        Map<String, String> address = new LinkedHashMap<>();
        address.put("name", name);
        address.put("street", street);
        address.put("city", city);
        address.put("pincode", pincode);
        address.put("phone", phone);

        order.setShippingAddress(mapper.writeValueAsString(address));
        orderService.save(order);
        return "redirect:/cart";
    }

    // 📦 Utility: Convert JSON string → List
    private List<Map<String, Object>> getCartItems(Order order) throws JsonProcessingException {
        if (order.getItems() == null) return new ArrayList<>();
        return mapper.readValue(order.getItems(), List.class);
    }

    // 📦 Utility: Convert JSON address → Map
    private Map<String, String> parseAddress(String json) {
        try {
            if (json != null && !json.isEmpty()) {
                return mapper.readValue(json, Map.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }

    // 💰 Utility: Update Total, Tax, Grand Total
    private void updateOrderTotals(Order order, List<Map<String, Object>> cartItems) throws JsonProcessingException {
        order.setItems(mapper.writeValueAsString(cartItems));

        double total = cartItems.stream()
                .mapToDouble(item -> {
                    double price = Double.parseDouble(item.get("price").toString());
                    int qty = Integer.parseInt(item.get("quantity").toString());
                    return price * qty;
                }).sum();

        order.setTotalAmount(total);
        order.setTax(total * 0.05);
        order.setGrandTotal(total + order.getTax());
        orderService.save(order);
    }

    // ✅ GET Checkout Page
    @GetMapping("/checkout")
    public String showCheckoutPage() {
        return "checkout"; // Must match checkout.html in /templates
    }

    // ✅ POST Checkout / Place Order
    @PostMapping("/checkout")
    @Transactional
    public String processCheckout(
            @RequestParam("paymentMethod") String paymentMethod,
            @RequestParam(value = "cardName", required = false) String cardName,
            @RequestParam(value = "cardNumber", required = false) String cardNumber,
            @RequestParam(value = "expiry", required = false) String expiry,
            @RequestParam(value = "cvv", required = false) String cvv,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        String email = principal.getName();
        User user = userRepository.findByEmail(email);

        List<Order> cartOrders = orderRepository.findByUserIdAndOrderStatus(user.getId(), "In Cart");

        if (cartOrders != null && !cartOrders.isEmpty()) {
            for (Order order : cartOrders) {
                if ("card".equalsIgnoreCase(paymentMethod)) {
                    order.setPaymentStatus("Paid");
                    if (cardNumber != null && cardNumber.length() >= 4) {
                        order.setPaymentDetails("Card: **** **** **** " + cardNumber.substring(cardNumber.length() - 4));
                    } else {
                        order.setPaymentDetails("Card");
                    }
                } else {
                    order.setPaymentStatus("Pending");
                    order.setPaymentDetails("Cash on Delivery");
                }

                order.setOrderStatus("Ordered");
                order.setUpdatedAt(LocalDateTime.now());
                orderRepository.save(order);
            }

            redirectAttributes.addFlashAttribute("method", paymentMethod.equals("card") ? "Card" : "Cash on Delivery");
        } else {
            redirectAttributes.addFlashAttribute("error", "No items in cart to checkout.");
            return "redirect:/cart";
        }

        return "redirect:/cart/payment-success";
    }

    // ✅ Payment Success Page
    @GetMapping("/payment-success")
    public String showPaymentSuccess(Model model, @ModelAttribute("method") String method) {
        model.addAttribute("method", method);
        return "payment-success"; // Must match payment-success.html
    }
}
