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

import edu.swau.softball.model.Division;
import edu.swau.softball.service.DivisionService;
import edu.swau.softball.service.SeasonService;
import edu.swau.softball.utils.Constants;
import edu.swau.softball.validation.DivisionValidator;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author J. David Mendoza <jdmendoza@swau.edu>
 */
@Controller
@RequestMapping("/admin/division")
public class DivisionController extends BaseController {
    
    @Autowired
    private DivisionService service;
    @Autowired
    private SeasonService seasonService;
    @Autowired 
    private DivisionValidator validator;
    
    @RequestMapping(method = RequestMethod.GET)
    public String index() {
        return "redirect:/admin/division/list";
    }
    
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String list(Model model,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "direction", required = false) String direction,
            @RequestParam(value = "filter", required = false) String filter) {
        log.debug("Page: {}", page);

        if (StringUtils.isBlank(sort)) {
            sort = "season";
            direction = "desc";
        }

        Map<String, Object> params = buildPagination(page, sort, direction, filter);

        Page<Division> p;
        PageRequest pageRequest = (PageRequest) params.get(Constants.PAGE_REQUEST);
        if (StringUtils.isNotBlank(filter)) {
            p = service.search(filter, pageRequest);
        } else {
            p = service.list(pageRequest);
        }

        paginate(model, p, params);

        model.addAttribute("divisions", p);

        return "/admin/division/list";
    }
    
    @RequestMapping(value = "/new", method = RequestMethod.GET)
    public String create(Model model) {
        Division division = new Division();
        model.addAttribute("division", division);
        model.addAttribute("seasons", seasonService.all());
        return "/admin/division/new";
    }

    @RequestMapping(value = "/new", method = RequestMethod.POST)
    public String create(@ModelAttribute("division") Division division, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        validator.validate(division, bindingResult);
        if (bindingResult.hasErrors()) {
            log.warn("Could not create division. {}", bindingResult.getAllErrors());
            model.addAttribute("seasons", seasonService.all());
            return "/admin/division/new";
        }

        try {
            division = service.create(division);
            return "redirect:/admin/division/show/" + division.getId();
        } catch (Exception e) {
            log.error("Could not create division.", e);
            bindingResult.reject("Could not create division. {}", e.getMessage());
            model.addAttribute("seasons", seasonService.all());
            return "/admin/division/new";
        }
    }

    @RequestMapping(value = "/show/{divisionId}", method = RequestMethod.GET)
    public String show(@PathVariable("divisionId") Integer divisionId, Model model) {
        Division division = service.get(divisionId);
        model.addAttribute("division", division);
        return "/admin/division/show";
    }

    @RequestMapping(value = "/delete/{divisionId}", method = RequestMethod.GET)
    public String delete(@PathVariable("divisionId") Integer divisionId, RedirectAttributes redirectAttributes) {
        try {
            String name = service.delete(divisionId);
            redirectAttributes.addFlashAttribute("message", "Division " + name + " deleted successfully!");
        } catch (Exception e) {
            log.error("Could not delete division " + divisionId + ".", e);
            redirectAttributes.addFlashAttribute("message", "Division " + divisionId + " could not be deleted.");
            redirectAttributes.addFlashAttribute("messageStyle", "alert-danger");
        }
        return "redirect:/admin/division/list";
    }
    
    @RequestMapping(value = "/edit/{divisionId}", method = RequestMethod.GET)
    public String update(Model model, @PathVariable("divisionId") Integer divisionId) {
        Division division = service.get(divisionId);
        model.addAttribute("division", division);
        model.addAttribute("seasons", seasonService.all());
        return "/admin/division/edit";
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public String update(@ModelAttribute("division") Division division, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        validator.validate(division, bindingResult);
        if (bindingResult.hasErrors()) {
            log.warn("Could not update division. {}", bindingResult.getAllErrors());
            model.addAttribute("seasons", seasonService.all());
            return "/admin/division/edit";
        }

        try {
            division = service.create(division);
            return "redirect:/admin/division/show/" + division.getId();
        } catch (Exception e) {
            log.error("Could not update division.", e);
            bindingResult.reject("Could not update division. {}", e.getMessage());
            model.addAttribute("seasons", seasonService.all());
            return "/admin/division/edit";
        }
    }

}
