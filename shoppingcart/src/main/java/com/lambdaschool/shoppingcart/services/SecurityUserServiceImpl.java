package com.lambdaschool.shoppingcart.services;


import com.lambdaschool.shoppingcart.exceptions.ResourceNotFoundException;
import com.lambdaschool.shoppingcart.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//Tie in Spring Security User to our User Model
//Since this updates data makes transactional
@Service(value = "securityUserService")
public class SecurityUserServiceImpl implements UserDetailsService {
    //Bring in User Repository
    @Autowired
    private UserRepository userrepos;

    //Tie in Spring Security User to our User Model
    //Since this updates data makes transactional
    @Transactional
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        //Get user from our User Repository
       com.lambdaschool.shoppingcart.models.User user = userrepos.findByUsername(s.toLowerCase());

        //If user can't be found throw error
        //Stating invalid username or password so that you don't give away too much info to hackers
        if (user == null) {
            throw new ResourceNotFoundException("Invalid username or password");
        }

        //Create new Spring Security User import org.springframework.security.core.userdetails.User;
        //To be used in the Token Store
        return new User(user.getUsername(), user.getPassword(), user.getAuthority());
    }
}
