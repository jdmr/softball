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

import edu.swau.softball.model.Team;
import edu.swau.softball.model.User;
import edu.swau.softball.service.TeamService;
import edu.swau.softball.service.UserService;
import edu.swau.softball.utils.Constants;
import edu.swau.softball.validation.CoachValidator;
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
@RequestMapping("/admin/coach")
public class CoachController extends BaseController {
    
    @Autowired
    private UserService service;
    @Autowired
    private TeamService teamService;
    @Autowired 
    private CoachValidator validator;
    
    @RequestMapping(method = RequestMethod.GET)
    public String index() {
        return "redirect:/admin/coach/list";
    }
    
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String list(Model model,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "direction", required = false) String direction,
            @RequestParam(value = "filter", required = false) String filter) {
        log.debug("Page: {}", page);

        if (StringUtils.isBlank(sort)) {
            sort = "lastName";
            direction = "desc";
        }

        Map<String, Object> params = buildPagination(page, sort, direction, filter);

        Page<User> p;
        PageRequest pageRequest = (PageRequest) params.get(Constants.PAGE_REQUEST);
        if (StringUtils.isNotBlank(filter)) {
            p = service.search(filter, pageRequest);
        } else {
            p = service.list(pageRequest);
        }

        paginate(model, p, params);

        model.addAttribute("users", p);

        return "/admin/coach/list";
    }
    
    @RequestMapping(value = "/new", method = RequestMethod.GET)
    public String create(Model model) {
        User coach = new User();
        model.addAttribute("coach", coach);
        model.addAttribute("teams", teamService.all());
        return "/admin/coach/new";
    }

    @RequestMapping(value = "/new", method = RequestMethod.POST)
    public String create(@ModelAttribute("coach") User coach, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        validator.validate(coach, bindingResult);
        if (bindingResult.hasErrors()) {
            log.warn("Could not add coach. {}", bindingResult.getAllErrors());
            model.addAttribute("coach", teamService.all());
            return "/admin/coach/new";
        }

        try {
            coach = service.create(coach);
            return "redirect:/admin/coach/show/" + coach.getId();
        } catch (Exception e) {
            log.error("Could not add coach.", e);
            bindingResult.reject("Could not add coach. {}", e.getMessage());
            model.addAttribute("teams", teamService.all());
            return "/admin/coach/new";
        }
    }

    @RequestMapping(value = "/show/{coachId}", method = RequestMethod.GET)
    public String show(@PathVariable("coachId") Long coachId, Model model) {
        User coach = service.get(coachId);
        model.addAttribute("coach", coach);
        return "/admin/coach/show";
    }

    @RequestMapping(value = "/delete/{coachId}", method = RequestMethod.GET)
    public String delete(@PathVariable("coachId") Long coachId, RedirectAttributes redirectAttributes) {
        try {
            String name = service.delete(coachId);
            redirectAttributes.addFlashAttribute("message", "Coach " + name + " deleted successfully!");
        } catch (Exception e) {
            log.error("Could not delete coach " + coachId + ".", e);
            redirectAttributes.addFlashAttribute("message", "Coach " + coachId + " could not be deleted.");
            redirectAttributes.addFlashAttribute("messageStyle", "alert-danger");
        }
        return "redirect:/admin/coach/list";
    }
    
    @RequestMapping(value = "/edit/{coachId}", method = RequestMethod.GET)
    public String update(Model model, @PathVariable("coachId") Long coachId) {
        User coach = service.get(coachId);
        model.addAttribute("coach", coach);
        model.addAttribute("teams", teamService.all());
        return "/admin/coach/edit";
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public String update(@ModelAttribute("coach") User coach, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        validator.validate(coach, bindingResult);
        if (bindingResult.hasErrors()) {
            log.warn("Could not update coach. {}", bindingResult.getAllErrors());
            model.addAttribute("teams", teamService.all());
            return "/admin/coach/edit";
        }

        try {
            coach = service.create(coach);
            return "redirect:/admin/coach/show/" + coach.getId();
        } catch (Exception e) {
            log.error("Could not update coach.", e);
            bindingResult.reject("Could not update team. {}", e.getMessage());
            model.addAttribute("teams", teamService.all());
            return "/admin/coach/edit";
        }
    }

}
