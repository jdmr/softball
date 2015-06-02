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
import edu.swau.softball.service.TeamService;
import edu.swau.softball.service.DivisionService;
import edu.swau.softball.utils.Constants;
import edu.swau.softball.validation.TeamValidator;
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
@RequestMapping("/admin/team")
public class TeamController extends BaseController {
    
    @Autowired
    private TeamService service;
    @Autowired
    private DivisionService divisionService;
    @Autowired 
    private TeamValidator validator;
    
    @RequestMapping(method = RequestMethod.GET)
    public String index() {
        return "redirect:/admin/team/list";
    }
    
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String list(Model model,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "direction", required = false) String direction,
            @RequestParam(value = "filter", required = false) String filter) {
        log.debug("Page: {}", page);

        if (StringUtils.isBlank(sort)) {
            sort = "division";
            direction = "desc";
        }

        Map<String, Object> params = buildPagination(page, sort, direction, filter);

        Page<Team> p;
        PageRequest pageRequest = (PageRequest) params.get(Constants.PAGE_REQUEST);
        if (StringUtils.isNotBlank(filter)) {
            p = service.search(filter, pageRequest);
        } else {
            p = service.list(pageRequest);
        }

        paginate(model, p, params);

        model.addAttribute("teams", p);

        return "/admin/team/list";
    }
    
    @RequestMapping(value = "/new", method = RequestMethod.GET)
    public String create(Model model) {
        Team team = new Team();
        model.addAttribute("team", team);
        model.addAttribute("divisions", divisionService.all());
        return "/admin/team/new";
    }

    @RequestMapping(value = "/new", method = RequestMethod.POST)
    public String create(@ModelAttribute("team") Team team, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        validator.validate(team, bindingResult);
        if (bindingResult.hasErrors()) {
            log.warn("Could not create team. {}", bindingResult.getAllErrors());
            model.addAttribute("divisions", divisionService.all());
            return "/admin/team/new";
        }

        try {
            team = service.create(team);
            return "redirect:/admin/team/show/" + team.getId();
        } catch (Exception e) {
            log.error("Could not create team.", e);
            bindingResult.reject("Could not create team. {}", e.getMessage());
            model.addAttribute("divisions", divisionService.all());
            return "/admin/team/new";
        }
    }

    @RequestMapping(value = "/show/{teamId}", method = RequestMethod.GET)
    public String show(@PathVariable("teamId") Integer teamId, Model model) {
        Team team = service.get(teamId);
        model.addAttribute("team", team);
        return "/admin/team/show";
    }

    @RequestMapping(value = "/delete/{teamId}", method = RequestMethod.GET)
    public String delete(@PathVariable("teamId") Integer teamId, RedirectAttributes redirectAttributes) {
        try {
            String name = service.delete(teamId);
            redirectAttributes.addFlashAttribute("message", "Team " + name + " deleted successfully!");
        } catch (Exception e) {
            log.error("Could not delete team " + teamId + ".", e);
            redirectAttributes.addFlashAttribute("message", "Team " + teamId + " could not be deleted.");
            redirectAttributes.addFlashAttribute("messageStyle", "alert-danger");
        }
        return "redirect:/admin/team/list";
    }
    
    @RequestMapping(value = "/edit/{teamId}", method = RequestMethod.GET)
    public String update(Model model, @PathVariable("teamId") Integer teamId) {
        Team team = service.get(teamId);
        model.addAttribute("team", team);
        model.addAttribute("divisions", divisionService.all());
        return "/admin/team/edit";
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public String update(@ModelAttribute("team") Team team, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        validator.validate(team, bindingResult);
        if (bindingResult.hasErrors()) {
            log.warn("Could not update team. {}", bindingResult.getAllErrors());
            model.addAttribute("divisions", divisionService.all());
            return "/admin/team/edit";
        }

        try {
            team = service.create(team);
            return "redirect:/admin/team/show/" + team.getId();
        } catch (Exception e) {
            log.error("Could not update team.", e);
            bindingResult.reject("Could not update team. {}", e.getMessage());
            model.addAttribute("divisions", divisionService.all());
            return "/admin/team/edit";
        }
    }

}
