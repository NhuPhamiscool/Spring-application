package au.edu.sydney.soft3202.task1;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ModelAttribute;
// import javax.servlet.http.HttpServletRequest;

import java.net.URI;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import java.io.IOException;
// import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
// import java.net.http;
// import java.net.http.HttpResponse;
import java.net.*;

@Controller
public class ShoppingController {
    private final SecureRandom randomNumberGenerator = new SecureRandom();
    private final HexFormat hexFormatter = HexFormat.of();

    private final AtomicLong counter = new AtomicLong();
    ShoppingBasket shoppingBasket;

    Map<String, String> sessions = new HashMap<>();

    String[] users = {"A", "B", "C", "D", "E"};


    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam(value = "user", defaultValue = "") String user) {
        
        // We are just checking the username, in the real world you would also check their password here
        // or authenticate the user some other way.
        if (!Arrays.asList(users).contains(user)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid user.\n");
        }

        // // Generate the session token.
        byte[] sessionTokenBytes = new byte[16];
        randomNumberGenerator.nextBytes(sessionTokenBytes);
        String sessionToken = hexFormatter.formatHex(sessionTokenBytes);

        shoppingBasket = new ShoppingBasket();
        // // Store the association of the session token with the user.
        sessions.put(sessionToken, user);

        // // Create HTTP headers including the instruction for the browser to store the session token in a cookie.
        String setCookieHeaderValue = String.format("session=%s; Path=/; HttpOnly; SameSite=Strict;", sessionToken);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Set-Cookie", setCookieHeaderValue);

        // Redirect to the cart page, with the session-cookie-setting headers.
        return ResponseEntity.status(HttpStatus.FOUND).headers(headers).location(URI.create("/cart")).build();
    }

    @GetMapping(value = "/cart")
    public String cart(@CookieValue(value = "session", defaultValue = "") String sessionToken, Model model) {
        if (!sessions.containsKey(sessionToken)) {
            return "invalid";
            
        }
        
        model.addAttribute("items", shoppingBasket.getItems());
       
        return "cartSubmit";
       

    }


    
    @PostMapping(value = "/cart")
    public String save(@RequestParam(value = "count") String count, Model model) {
        
        String[] lineBroken = count.split(",");
       
        for (int i = 0; i < lineBroken.length; i++) {

            if (!lineBroken[i].equals("")) {
                
                int countCast = Integer.parseInt(lineBroken[i]);
                
                String itemName = shoppingBasket.getItemName(i);
                
                shoppingBasket.addItem(itemName, countCast);

            } 
        }
        
        model.addAttribute("items", shoppingBasket.getItems());
        
        return "cart";
    }
    

    @GetMapping("/newname")
    public String addNewItemAndCost() {
        
        return "addNewItem";
    }

    @PostMapping("/newname")
    public ResponseEntity<String> addNewItemAndCost(@RequestParam(value = "newitemname") String newItemName, 
    @RequestParam(value = "newitemcost") double cost, Model model) {
        if (shoppingBasket.items.containsKey(newItemName)) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body("Item " + newItemName + " is already present.\n");
        }
        if (cost < 0.0) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body("Invalid item cost.\n");
        }
        shoppingBasket.addNewItem(newItemName, cost);
        shoppingBasket.modifyNameListToItemHashMap();

        
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("/cart")).build();
    }

    @GetMapping("/logout")
    public String logOut(@CookieValue(value = "session", defaultValue = "") String sessionToken) {
        sessions.remove(sessionToken);
        return "logout";
    }


    @GetMapping("/updatename")
    public String updateName(Model model) {
        model.addAttribute("items", shoppingBasket.getItems());
        return "updateItem";
    }

    @PostMapping("/updatename")
    public ResponseEntity<String> updateItemAndCost(@RequestParam(value = "newname") String newItemName, 
    @RequestParam(value = "newvalue") String newValue, Model model) {
        
        
        String[] nameBroken = newItemName.split(",");
        String[] valueBroken = newValue.split(",");

        
        List<String> newNameList = new ArrayList<>();
        
        int chosenLength;
        
        if (valueBroken.length > nameBroken.length) {
            
            List<String> arrlist = new ArrayList<String>(Arrays.asList(nameBroken));
            int counter = arrlist.size();
        
            while (arrlist.size() != valueBroken.length) {
                arrlist.add(counter, "");
                counter+=1;
            }
            nameBroken = arrlist.toArray(nameBroken);
            
        } else if (valueBroken.length < nameBroken.length){
            List<String> arrlist = new ArrayList<String>(Arrays.asList(valueBroken));
            int counter = arrlist.size();
        
            while (nameBroken.length != arrlist.size()) {
                arrlist.add(counter, "");
                counter+=1;
            }
            valueBroken = arrlist.toArray(valueBroken);
           
        }

        for (int i = 0; i < nameBroken.length; i++) {
            
            if (!nameBroken[i].equals("") && !valueBroken[i].equals("")) {
            
                
                String itemName = shoppingBasket.getItemName(i);
            
                Double castedValue = Double.valueOf(valueBroken[i]);
           

                shoppingBasket.updateItemNameAndCostInValuesHashMap(itemName, nameBroken[i], castedValue);

            } else if (!nameBroken[i].equals("") && valueBroken[i].equals("")) {
                String itemName = shoppingBasket.getItemName(i);
                
                shoppingBasket.updateItemNameAndCostInValuesHashMap(itemName, nameBroken[i], 0.0);

            } else if (nameBroken[i].equals("") && !valueBroken[i].equals("")) {
                String itemName = shoppingBasket.getItemName(i);
                Double castedValue = Double.valueOf(valueBroken[i]);
                shoppingBasket.updateItemNameAndCostInValuesHashMap(itemName, itemName, castedValue);
            }
            
        }
        shoppingBasket.modifyNameListToItemHashMap();

        
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("/cart")).build();
    }

    @GetMapping("/delname")
    public String addNewItemAndCost(Model model) {
        model.addAttribute("items", shoppingBasket.getAllItemCost());
        return "deleteItem";

    }

    @PostMapping("/delname")
    public ResponseEntity<String> deleteItem(@RequestParam(value = "removeItem") String removeItem, Model model) {
       
        String[] lineBroken = removeItem.split(",");
        
        int count = 0;
        for (int i = 0; i < lineBroken.length - 1; ) {

            if (lineBroken[i].equals("false") && lineBroken[i+1].equals("true")) {
            
                String itemName = shoppingBasket.getItemName(count);
                
                shoppingBasket.deleteItem(itemName);
                i += 2;
                count += 1;
                

            } else {
                i += 1;
                count += 1;
            }
            
        }
        shoppingBasket.modifyNameListToItemHashMap();
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("/cart")).build();
    }

    @GetMapping("/counter")
    public ResponseEntity<String> counter() {
        counter.incrementAndGet();
        return ResponseEntity.status(HttpStatus.OK).body("[" + counter + "]");
    }

    @GetMapping("/cost")
    public ResponseEntity<String> cost() {
        return ResponseEntity.status(HttpStatus.OK).body(
            shoppingBasket.getValue() == null ? "0" : shoppingBasket.getValue().toString()
        );
    }

    @GetMapping("/greeting")
    public String greeting(
        @RequestParam(name="name", required=false, defaultValue="World") String name,
        Model model
    ) {
        model.addAttribute("name", name);
        return "greeting";
    }

}
