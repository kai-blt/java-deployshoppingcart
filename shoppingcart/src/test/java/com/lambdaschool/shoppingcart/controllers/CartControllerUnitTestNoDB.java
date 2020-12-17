package com.lambdaschool.shoppingcart.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lambdaschool.shoppingcart.ShoppingCart;
import com.lambdaschool.shoppingcart.models.*;
import com.lambdaschool.shoppingcart.repository.CartItemRepository;
import com.lambdaschool.shoppingcart.repository.ProductRepository;
import com.lambdaschool.shoppingcart.repository.UserRepository;
import com.lambdaschool.shoppingcart.services.CartItemService;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = ShoppingCart.class)
@AutoConfigureMockMvc
@WithMockUser(username = "admin",
        roles = {"USER", "ADMIN"})
public class CartControllerUnitTestNoDB {

    //Autowire Application context
    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    //Bring in the service needed to test
    @MockBean
    private CartItemService cartItemService;

    //Mock up repos
    @MockBean
    private UserRepository userrepos;

    @MockBean
    private ProductRepository prodrepos;

    @MockBean
    private CartItemRepository cartitemrepos;

    //Mock List of Users in Database
    List<User> userList = new ArrayList<>();
    List<Product> productList = new ArrayList<>();

    @Before
    public void setUp() throws Exception {
        //Mock up a role
        Role testRole = new Role("admin");
        testRole.setRoleid(1);

        //Mock up a customer
        User testUser = new User("testusername", "testpassword", "email@email.com", "none");
        testUser.setUserid(10);

        //Mock up UserRoles
        UserRoles testUserRole = new UserRoles(testUser, testRole);

        //Add role to user
        testUser.getRoles().add(testUserRole);

        //Mock up a Products
        Product testProduct = new Product();
        testProduct.setProductid(1);
        testProduct.setName("Test Product Name");
        testProduct.setPrice(10.00);
        testProduct.setDescription("Test description");

        Product testProduct2 = new Product();
        testProduct2.setProductid(2);
        testProduct2.setName("Test Product 2 Name");
        testProduct2.setPrice(20.00);
        testProduct2.setDescription("Test 2 description");

        //Mock up cart
        CartItem testCartItem = new CartItem(testUser, testProduct, 1, "testcomment");

        //Add a cart to user
        testUser.getCarts().add(testCartItem);

        //Add user to fake DB
        userList.add(testUser);
        productList.add(testProduct2);


        RestAssuredMockMvc.webAppContextSetup(webApplicationContext);

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        //Enable Mockito
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void listCartItemsByUserId() {
    }

    @Test
    public void addToCart() throws Exception {
        //Mock up API url
        String apiUrl = "/carts/add/product/2";

        CartItem blankCart = new CartItem();
        blankCart.setUser(userList.get(0));
        blankCart.setQuantity(1);
        blankCart.setProduct(productList.get(0));
        blankCart.setComments("test comment");

        //Mock the service call
        Mockito.when(userrepos.findByUsername(any(String.class))).thenReturn(userList.get(0));

        Mockito.when(cartItemService.addToCart(any(Long.class), any(Long.class), any(String.class)))
                .thenReturn(blankCart);

        //Make request builder
        RequestBuilder rb = MockMvcRequestBuilders.put(apiUrl).accept(MediaType.APPLICATION_JSON);

        //Perform request
        MvcResult r =  mockMvc.perform(rb).andReturn();

        String testresult = r.getResponse().getContentAsString();

        //Convert Java to JSON
        ObjectMapper mapper = new ObjectMapper();
        String expectedResult = mapper.writeValueAsString(blankCart);

        assertEquals(expectedResult, testresult);
    }

    @Test
    public void removeFromCart() throws Exception{
        //Mock up API url
        String apiUrl = "/carts/remove/user/10/product/2";

        //Mock up cart
        CartItem testCartItem = new CartItem(userList.get(0), productList.get(0), 1, "testcomment");

        //Convert Java to JSON
        ObjectMapper mapper = new ObjectMapper();
        String testCartItemAsString = mapper.writeValueAsString(testCartItem);


        //Mock the service call
        Mockito.when(cartItemService.removeFromCart(userList.get(0).getUserid(), productList.get(0).getProductid(), "testcomment"))
                .thenReturn(testCartItem);


        //Make request builder
        RequestBuilder rb = MockMvcRequestBuilders.delete(apiUrl).accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(testCartItemAsString);

        //Perform request
        mockMvc.perform(rb).andExpect(status().isOk());
    }
}