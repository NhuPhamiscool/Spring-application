package au.edu.sydney.soft3202.task3;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HexFormat;
import java.net.URI;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

import java.sql.SQLException;

@Controller
public class ShoppingController {
    private final SecureRandom randomNumberGenerator = new SecureRandom();
    private final HexFormat hexFormatter = HexFormat.of();

    Map<String, String> sessions = new HashMap<>();
    Map<String, ShoppingBasket> userBaskets = new HashMap<>();

    List<String> users = null;
    DatabaseHelper dbHelper = null;
    Map<String, Integer> initialItems = new HashMap<>();

    public ShoppingController() {
        initialItems.put("apple", 0);
        initialItems.put("orange", 0);
        initialItems.put("pear", 0);
        initialItems.put("banana", 0);
    }


    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam(value = "user", defaultValue = "") String user, Model model) {
        try {
            dbHelper = new DatabaseHelper();
            if (dbHelper.getUser("A") == null) {
                dbHelper.addUser("A");
            }
            if (!user.equals("Admin")) {
                user = dbHelper.getUser(user);
            }
        } catch (SQLException sqle) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("Unable to connect: " + sqle.getMessage()+ ".\n");
        }

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid user.\n");
        }

        byte[] sessionTokenBytes = new byte[16];
        randomNumberGenerator.nextBytes(sessionTokenBytes);
        String sessionToken = hexFormatter.formatHex(sessionTokenBytes);

        sessions.put(sessionToken, user);

        String setCookieHeaderValue = String.format("session=%s; Path=/; HttpOnly; SameSite=Strict;", sessionToken);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Set-Cookie", setCookieHeaderValue);

        return ResponseEntity.status(HttpStatus.FOUND).headers(headers).location(URI.create("/cart")).build();
    }

    @GetMapping("/cart")
    public String cart(@CookieValue(value = "session", defaultValue = "") String sessionToken, Model model) {
        if (!sessions.containsKey(sessionToken)) {
            return "unauthorized";
        }

        String user = sessions.get(sessionToken);
        if (user.equals("Admin")) {
            try {
                users = dbHelper.getUsers();
            } catch (SQLException se) {
                return "error";
            }
            model.addAttribute("users", users);
            return "users";
        } else {
            // Load this from the database instead.
            ShoppingBasket userBasket = userBaskets.get(user);
            if (userBasket == null) {
                userBasket = new ShoppingBasket();
                userBasket.items.putAll(initialItems);
                userBaskets.put(user, userBasket);
            }

            model.addAttribute("items", userBasket.getItems());
            model.addAttribute("total", userBasket.getValue());
            return "cart";
        }
    }

    @GetMapping("/newname")
    public String newName(@CookieValue(value = "session", defaultValue = "") String sessionToken) {
        if (!sessions.containsKey(sessionToken)) {
            return "unauthorized";
        }
        return "newname";
    }

    @GetMapping("/delname")
    public String delNamePage(Model model, @CookieValue(value = "session", defaultValue = "") String sessionToken) {
        if (!sessions.containsKey(sessionToken)) {
            return "unauthorized";
        }

        String user = sessions.get(sessionToken);
        ShoppingBasket userBasket = userBaskets.get(user);

        model.addAttribute("items", userBasket.values);

        return "delname";
    }

    @GetMapping("/updatename")
    public String showUpdateName(@CookieValue(value = "session", defaultValue = "") String sessionToken, Model model) {
        if (!sessions.containsKey(sessionToken)) {
            return "unauthorized";
        }

        String user = sessions.get(sessionToken);
        ShoppingBasket userBasket = userBaskets.get(user);
        model.addAttribute("items", userBasket.values);
        return "updatename";
    }

    @GetMapping("/logout")
    public String logout(@CookieValue(value = "session", defaultValue = "") String sessionToken) {
        if (!sessions.containsKey(sessionToken)) {
            return "unauthorized";
        }

        sessions.remove(sessionToken);
        return "redirect:/";
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception e, Model model) {
        String stackTrace = Arrays.stream(e.getStackTrace())
                .limit(1)
                .map(StackTraceElement::toString)
                .collect(Collectors.joining("\n"));

        String errorMessage = String.format("%s\n%s", e.getMessage(), stackTrace);
        model.addAttribute("errorMessage", errorMessage);
        return "error";
    }
}
