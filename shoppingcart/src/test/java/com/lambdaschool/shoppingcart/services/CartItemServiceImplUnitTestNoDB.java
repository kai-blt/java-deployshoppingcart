package com.lambdaschool.shoppingcart.services;

import com.lambdaschool.shoppingcart.ShoppingCart;
import com.lambdaschool.shoppingcart.models.*;
import com.lambdaschool.shoppingcart.repository.CartItemRepository;
import com.lambdaschool.shoppingcart.repository.ProductRepository;
import com.lambdaschool.shoppingcart.repository.UserRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ShoppingCart.class)
public class CartItemServiceImplUnitTestNoDB {

    @Autowired
    private CartItemService cartItemService;

    @MockBean
    private UserRepository userrepos;

    @MockBean
    private ProductRepository prodrepos;

    @MockBean
    private CartItemRepository cartitemrepos;


    private List<User> userList = new ArrayList<>();
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

        //Enable Mockito
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void addToCart() {
        //Mock up cart
        CartItem testCartItem = new CartItem(userList.get(0), productList.get(0), 1, "testcomment");


        Mockito.when(userrepos.findById(10L))
                .thenReturn(Optional.of(userList.get(0)));

        Mockito.when(prodrepos.findById(1L))
                .thenReturn(Optional.of(productList.get(0)));

        Mockito.when(cartitemrepos.findById(any(CartItemId.class)))
                .thenReturn(Optional.of(testCartItem));

        Mockito.when(cartitemrepos.save(any(CartItem.class)))
                .thenReturn(testCartItem);

        assertEquals(testCartItem, cartItemService.addToCart(10L, 1L, "testcomment"));
    }

    @Test
    public void removeFromCart() {
        //Mock up cart
        CartItem testCartItem = new CartItem(userList.get(0), productList.get(0), 2, "testcomment");


        Mockito.when(userrepos.findById(10L))
                .thenReturn(Optional.of(userList.get(0)));

        Mockito.when(prodrepos.findById(1L))
                .thenReturn(Optional.of(productList.get(0)));

        Mockito.when(cartitemrepos.findById(any(CartItemId.class)))
                .thenReturn(Optional.of(testCartItem));

        Mockito.when(cartitemrepos.save(any(CartItem.class)))
                .thenReturn(testCartItem);

        assertEquals(testCartItem, cartItemService.removeFromCart(10L, 1L, "testcomment"));
    }
}