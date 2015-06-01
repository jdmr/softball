/*
 * The MIT License
 *
 * Copyright 2015 Southwestern Adventist University.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package edu.swau.softball.utils;

import edu.swau.softball.dao.RoleRepository;
import edu.swau.softball.dao.UserRepository;
import edu.swau.softball.model.Role;
import edu.swau.softball.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 *
 * @author J. David Mendoza <jdmendoza@swau.edu>
 */
@Component
public class Bootstrap implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger log = LoggerFactory.getLogger(Bootstrap.class);
    
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("Initializing Softball App.");

        Role role = roleRepository.findOne("ROLE_ADMIN");
        if (role == null) {
            role = new Role();
            role.setAuthority("ROLE_ADMIN");
            roleRepository.save(role);
        }
        
        User user = userRepository.findByUsernameIgnoreCase("jdmr");
        if (user == null) {
            user = new User();
            user.setUsername("jdmr");
            user.setPassword(passwordEncoder.encode("changeme"));
            user.setEmail("jdmr@swau.edu");
            user.setFirstName("David");
            user.setLastName("Mendoza");
            user.getRoles().add(role);
            userRepository.save(user);
        }

        log.info("Softball App is UP!");
    }

}
