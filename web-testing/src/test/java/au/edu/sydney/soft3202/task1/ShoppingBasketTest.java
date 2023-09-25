package au.edu.sydney.soft3202.task1;

import au.edu.sydney.soft3202.task1.ShoppingServiceApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.*;

import static org.junit.jupiter.api.Assertions.*;


public class ShoppingBasketTest {
    private ApplicationContext testObject;

    @BeforeEach // Fresh server each time
    public void serverStart() {
        testObject = SpringApplication.run(ShoppingServiceApplication.class); // Literally just run our application.
    }

    @AfterEach // Need to stop the server or else port will remain in use next test
    public void serverStop() {
        SpringApplication.exit(testObject);
    }

    @Test
    public void loginPostTest() {
        try {
            HttpRequest req = HttpRequest.newBuilder(new URI("http://localhost:8080/login?user=A"))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpClient client = HttpClient.newBuilder().build();

            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            
            assertEquals(302, resp.statusCode());
            
        } catch (URISyntaxException | IOException e) {
            fail();
        } catch (InterruptedException e) {
            fail();
        }
    }

    @Test
    public void loginInvalidPostTest() {
        try {
            HttpRequest req = HttpRequest.newBuilder(new URI("http://localhost:8080/login?user=M"))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpClient client = HttpClient.newBuilder().build();

            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
            
            
            assertTrue(resp.body().toString().contains("Invalid user.\n"));
        } catch (URISyntaxException | IOException e) {
            fail();
        } catch (InterruptedException e) {
            fail();
        }
    }

    @Test
    public void cartGetTest() {
        try {
            HttpRequest req = HttpRequest.newBuilder(new URI("http://localhost:8080/login?user=A"))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpClient client = HttpClient.newBuilder().build();

            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());

   
            String cookie = resp.headers().firstValue("Set-Cookie").toString();
            cookie = cookie.substring(0, cookie.indexOf(";"));
            String cookieName = cookie.substring(0, cookie.indexOf("="));
            String cookieValue = cookie.substring(cookie.indexOf("=") + 1, cookie.length());
    
            HttpRequest req2 = HttpRequest.newBuilder(new URI("http://localhost:8080/cart?session=" + cookieValue))
                    .GET()
                    .build();
            

            HttpClient client2 = HttpClient.newBuilder().build();

            HttpResponse<String> resp2 = client2.send(req2, HttpResponse.BodyHandlers.ofString());

            // Some sort of assertions etc.etc.
            assertNotNull(resp.body());
            
        } catch (URISyntaxException | IOException e) {
            fail();
        } catch (InterruptedException e) {
            fail();
        }
    }

    @Test
    public void cartAddCountPostTest() {
        try {
            HttpRequest req2 = HttpRequest.newBuilder(new URI("http://localhost:8080/login?user=A"))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpClient client2 = HttpClient.newBuilder().build();

            client2.send(req2, HttpResponse.BodyHandlers.ofString());

            HttpRequest req = HttpRequest.newBuilder(new URI("http://localhost:8080/cart?count=2,3,,"))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpClient client = HttpClient.newBuilder().build();

            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());

            // Some sort of assertions etc.etc.
            assertNotNull(resp.body());
            
        } catch (URISyntaxException | IOException e) {
            fail();
        } catch (InterruptedException e) {
            fail();
        }
    }

    @Test
    public void newnameGetTest() {
        try {
            HttpRequest req2 = HttpRequest.newBuilder(new URI("http://localhost:8080/login?user=A"))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpClient client2 = HttpClient.newBuilder().build();

            client2.send(req2, HttpResponse.BodyHandlers.ofString());

            HttpRequest req = HttpRequest.newBuilder(new URI("http://localhost:8080/newname"))
                    .GET()
                    .build();

            HttpClient client = HttpClient.newBuilder().build();

            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());

            // Some sort of assertions etc.etc.
            assertNotNull(resp.body());
            
        } catch (URISyntaxException | IOException e) {
            fail();
        } catch (InterruptedException e) {
            fail();
        }
    }

    @Test
    public void newnameAddPostTest() {
        try {
            HttpRequest req2 = HttpRequest.newBuilder(new URI("http://localhost:8080/login?user=A"))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpClient client2 = HttpClient.newBuilder().build();

            client2.send(req2, HttpResponse.BodyHandlers.ofString());

            HttpRequest req = HttpRequest.newBuilder(new URI("http://localhost:8080/newname?newitemname=strawberry&newitemcost=2.3"))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpClient client = HttpClient.newBuilder().build();

            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());

            // Some sort of assertions etc.etc.
           
            assertEquals(302, resp.statusCode());
        } catch (URISyntaxException | IOException e) {
            fail();
        } catch (InterruptedException e) {
            fail();
        }
    }

    @Test
    public void newnamePostAlreadyPresentNameTest() {
        try {
            HttpRequest req2 = HttpRequest.newBuilder(new URI("http://localhost:8080/login?user=A"))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpClient client2 = HttpClient.newBuilder().build();

            client2.send(req2, HttpResponse.BodyHandlers.ofString());

            HttpRequest req = HttpRequest.newBuilder(new URI("http://localhost:8080/newname?newitemname=apple&newitemcost=2.3"))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpClient client = HttpClient.newBuilder().build();

            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());

            // Some sort of assertions etc.etc.
            // 412: precondition-failed
            assertEquals(412, resp.statusCode());
        } catch (URISyntaxException | IOException e) {
            fail();
        } catch (InterruptedException e) {
            fail();
        }
    }

    @Test
    public void newnamePostInvalidCostTest() {
        try {
            HttpRequest req2 = HttpRequest.newBuilder(new URI("http://localhost:8080/login?user=A"))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpClient client2 = HttpClient.newBuilder().build();

            client2.send(req2, HttpResponse.BodyHandlers.ofString());

            HttpRequest req = HttpRequest.newBuilder(new URI("http://localhost:8080/newname?newitemname=strawberry&newitemcost=-2.3"))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpClient client = HttpClient.newBuilder().build();

            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());

            // Some sort of assertions etc.etc.
           
            assertEquals(412, resp.statusCode());
        } catch (URISyntaxException | IOException e) {
            fail();
        } catch (InterruptedException e) {
            fail();
        }
    }


    @Test
    public void logoutGetTest() {
        try {
            
            HttpRequest req = HttpRequest.newBuilder(new URI("http://localhost:8080/logout"))
                    .GET()
                    .build();

            HttpClient client = HttpClient.newBuilder().build();

            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());

            // Some sort of assertions etc.etc.
            assertNotNull(resp.body());
            
        } catch (URISyntaxException | IOException e) {
            fail();
        } catch (InterruptedException e) {
            fail();
        }
    }

    @Test
    public void delnameGetTest() {
        try {
            HttpRequest req2 = HttpRequest.newBuilder(new URI("http://localhost:8080/login?user=A"))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpClient client2 = HttpClient.newBuilder().build();

            client2.send(req2, HttpResponse.BodyHandlers.ofString());

            HttpRequest req = HttpRequest.newBuilder(new URI("http://localhost:8080/delname"))
                    .GET()
                    .build();

            HttpClient client = HttpClient.newBuilder().build();

            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());

            // Some sort of assertions etc.etc.
            assertNotNull(resp.body());
            
        } catch (URISyntaxException | IOException e) {
            fail();
        } catch (InterruptedException e) {
            fail();
        }
    }

    @Test
    public void delNamePostTest() {
        try {
            HttpRequest req2 = HttpRequest.newBuilder(new URI("http://localhost:8080/login?user=A"))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpClient client2 = HttpClient.newBuilder().build();

            client2.send(req2, HttpResponse.BodyHandlers.ofString());

            HttpRequest req = HttpRequest.newBuilder(new URI("http://localhost:8080/delname?removeItem=false,true,false,false,false"))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpClient client = HttpClient.newBuilder().build();

            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());

            // Some sort of assertions etc.etc.
           
            assertEquals(302, resp.statusCode());
        } catch (URISyntaxException | IOException e) {
            fail();
        } catch (InterruptedException e) {
            fail();
        }
    }


    @Test
    public void updateNameGetTest() {
        try {
            HttpRequest req2 = HttpRequest.newBuilder(new URI("http://localhost:8080/login?user=A"))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpClient client2 = HttpClient.newBuilder().build();

            client2.send(req2, HttpResponse.BodyHandlers.ofString());

            HttpRequest req = HttpRequest.newBuilder(new URI("http://localhost:8080/updatename"))
                    .GET()
                    .build();

            HttpClient client = HttpClient.newBuilder().build();

            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());

            // Some sort of assertions etc.etc.
            assertNotNull(resp.body());
           
        } catch (URISyntaxException | IOException e) {
            fail();
        } catch (InterruptedException e) {
            fail();
        }
    }

    @Test
    public void updateNamePost1Test() {
        try {

            HttpRequest req2 = HttpRequest.newBuilder(new URI("http://localhost:8080/login?user=A"))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpClient client2 = HttpClient.newBuilder().build();

            client2.send(req2, HttpResponse.BodyHandlers.ofString());

            HttpRequest req = HttpRequest.newBuilder(new URI("http://localhost:8080/updatename?newname=apple&newvalue=2.3"))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpClient client = HttpClient.newBuilder().build();

            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());

            // Some sort of assertions etc.etc.
           
            assertEquals(302, resp.statusCode());
        } catch (URISyntaxException | IOException e) {
            fail();
        } catch (InterruptedException e) {
            fail();
        }
    }

    @Test
    public void updateNamePost2Test() {
        try {
            HttpRequest req2 = HttpRequest.newBuilder(new URI("http://localhost:8080/login?user=A"))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpClient client2 = HttpClient.newBuilder().build();

            client2.send(req2, HttpResponse.BodyHandlers.ofString());

            HttpRequest req = HttpRequest.newBuilder(new URI("http://localhost:8080/updatename?newname=,,apple&newvalue=1.3,,2.3"))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpClient client = HttpClient.newBuilder().build();

            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());

            // Some sort of assertions etc.etc.
           
            assertEquals(302, resp.statusCode());
        } catch (URISyntaxException | IOException e) {
            fail();
        } catch (InterruptedException e) {
            fail();
        }
    }

    @Test
    public void updateNamePost3Test() {
        try {
            HttpRequest req2 = HttpRequest.newBuilder(new URI("http://localhost:8080/login?user=A"))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpClient client2 = HttpClient.newBuilder().build();

            client2.send(req2, HttpResponse.BodyHandlers.ofString());

            HttpRequest req = HttpRequest.newBuilder(new URI("http://localhost:8080/updatename?newname=,,apple&newvalue=,1.3"))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpClient client = HttpClient.newBuilder().build();

            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());

            // Some sort of assertions etc.etc.
            
            assertEquals(302, resp.statusCode());
        } catch (URISyntaxException | IOException e) {
            fail();
        } catch (InterruptedException e) {
            fail();
        }
    }

    @Test
    public void updateNamePost4Test() {
        try {
            HttpRequest req2 = HttpRequest.newBuilder(new URI("http://localhost:8080/login?user=A"))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpClient client2 = HttpClient.newBuilder().build();

            client2.send(req2, HttpResponse.BodyHandlers.ofString());

            HttpRequest req = HttpRequest.newBuilder(new URI("http://localhost:8080/updatename?newname=,apple&newvalue=,,1.3"))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpClient client = HttpClient.newBuilder().build();

            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());

            // Some sort of assertions etc.etc.
            
            assertEquals(302, resp.statusCode());
        } catch (URISyntaxException | IOException e) {
            fail();
        } catch (InterruptedException e) {
            fail();
        }
    }

    

    @Test
    public void counterGetTest() {
        try {
            HttpRequest req2 = HttpRequest.newBuilder(new URI("http://localhost:8080/login?user=A"))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpClient client2 = HttpClient.newBuilder().build();

            client2.send(req2, HttpResponse.BodyHandlers.ofString());

            HttpRequest req = HttpRequest.newBuilder(new URI("http://localhost:8080/counter"))
                    .GET()
                    .build();

            HttpClient client = HttpClient.newBuilder().build();

            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());

            // Some sort of assertions etc.etc.
            assertNotNull(resp.body());
            
        } catch (URISyntaxException | IOException e) {
            fail();
        } catch (InterruptedException e) {
            fail();
        }
    }

    @Test
    public void greetingGetTest() {
        try {
            HttpRequest req2 = HttpRequest.newBuilder(new URI("http://localhost:8080/login?user=A"))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpClient client2 = HttpClient.newBuilder().build();

            client2.send(req2, HttpResponse.BodyHandlers.ofString());

            HttpRequest req = HttpRequest.newBuilder(new URI("http://localhost:8080/greeting"))
                    .GET()
                    .build();

            HttpClient client = HttpClient.newBuilder().build();

            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());

            // Some sort of assertions etc.etc.
            assertNotNull(resp.body());
            
        } catch (URISyntaxException | IOException e) {
            fail();
        } catch (InterruptedException e) {
            fail();
        }
    }

    @Test
    public void costGetTest() {
        try {
            HttpRequest req2 = HttpRequest.newBuilder(new URI("http://localhost:8080/login?user=A"))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpClient client2 = HttpClient.newBuilder().build();

            client2.send(req2, HttpResponse.BodyHandlers.ofString());

            HttpRequest req = HttpRequest.newBuilder(new URI("http://localhost:8080/cost"))
                    .GET()
                    .build();

            HttpClient client = HttpClient.newBuilder().build();

            HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());

            // Some sort of assertions etc.etc.
            assertNotNull(resp.body());
           
        } catch (URISyntaxException | IOException e) {
            fail();
        } catch (InterruptedException e) {
            fail();
        }
    }
}

