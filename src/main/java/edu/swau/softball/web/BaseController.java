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
package edu.swau.softball.web;

import edu.swau.softball.utils.Constants;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.ui.Model;

/**
 *
 * @author J. David Mendoza <jdmendoza@swau.edu>
 */
public abstract class BaseController {

    protected final transient Logger log = LoggerFactory.getLogger(getClass());

    protected Map<String, Object> buildPagination(Integer page, String sort, String direction, String filter) {
        Map<String, Object> params = new HashMap<>();
        String oposite;
        if (page == null) {
            page = 0;
        }
        if (StringUtils.isBlank(sort)) {
            sort = "lastUpdated";
            direction = "desc";
        }
        if (StringUtils.isBlank(direction)) {
            direction = "asc";
        }
        Sort s;
        switch (direction) {
            case "desc":
                s = new Sort(Sort.Direction.DESC, sort);
                oposite = "asc";
                break;
            default:
                s = new Sort(Sort.Direction.ASC, sort);
                oposite = "desc";
        }
        params.put(Constants.PAGE, page);
        params.put(Constants.SORT, sort);
        log.debug("{} : {}", direction, oposite);
        params.put(Constants.DIRECTION, direction);
        params.put(Constants.OPOSITE_DIRECTION, oposite);
        params.put(Constants.FILTER, filter);
        PageRequest pageRequest = new PageRequest(page, 20, s);
        params.put(Constants.PAGE_REQUEST, pageRequest);

        return params;
    }
    
    protected void paginate(Model model, Page page, Map<String, Object> params) {
        Integer pageNumber = (Integer) params.get(Constants.PAGE);
        String sort = (String) params.get(Constants.SORT);
        String direction = (String) params.get(Constants.DIRECTION);
        String oposite = (String) params.get(Constants.OPOSITE_DIRECTION);
        String filter = (String) params.get(Constants.FILTER);
        if (pageNumber == null) {
            pageNumber = 0;
        }
        List<Integer> pages = new ArrayList<>();

        int current = page.getNumber() + 1;
        int begin = Math.max(0, current - 6);
        int end = Math.min(begin + 11, page.getTotalPages());

        if (begin > 0) {
            pages.add(0);
            if (begin > 1) {
                pages.add(-1);
            }
        }
        for (int i = begin; i < end; i++) {
            pages.add(i);
        }
        if (end < page.getTotalPages()) {
            pages.add(-1);
            pages.add(page.getTotalPages() - 1);
        }
        model.addAttribute(Constants.SORT, sort);
        log.debug("{} : {}", direction, oposite);
        model.addAttribute(Constants.DIRECTION, direction);
        model.addAttribute(Constants.OPOSITE_DIRECTION, oposite);
        model.addAttribute(Constants.CURRENT_PAGE, pageNumber);
        model.addAttribute(Constants.TOTAL_PAGES, page.getTotalPages() - 1);
        model.addAttribute(Constants.PAGES, pages);
        model.addAttribute(Constants.FILTER, filter);
    }
}
