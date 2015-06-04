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
package edu.swau.softball.service.impl;

import edu.swau.softball.dao.TeamRepository;
import edu.swau.softball.dao.UserRepository;
import edu.swau.softball.model.User;
import edu.swau.softball.service.BaseService;
import edu.swau.softball.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author J. David Mendoza <jdmendoza@swau.edu>
 */
@Service
@Transactional
public class UserServiceImpl extends BaseService implements UserService {
    
    @Autowired
    private UserRepository repository;
    @Autowired
    private TeamRepository teamRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<User> search(String filter, PageRequest pageRequest) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> list(PageRequest pageRequest) {
        return repository.findAll(pageRequest);
    }

    @Override
    public User create(User user) {
        user.setTeam(teamRepository.findOne(user.getTeam().getId()));
        return repository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public User get(Long userId) {
        return repository.findOne(userId);
    }

    @Override
    public String delete(Long userId) {
        User user = repository.findOne(userId);
        repository.delete(user);
        return user.getFullName();
    }

}
