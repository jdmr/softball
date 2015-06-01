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

import edu.swau.softball.model.Season;
import edu.swau.softball.service.SeasonService;
import edu.swau.softball.utils.Constants;
import edu.swau.softball.validation.SeasonValidator;
import java.util.Calendar;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
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
@RequestMapping("/admin/season")
public class SeasonController extends BaseController {

    @Autowired
    private SeasonService service;
    @Autowired
    private SeasonValidator validator;

    @RequestMapping(method = RequestMethod.GET)
    public String index() {
        return "redirect:/admin/season/list";
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String list(Model model,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "direction", required = false) String direction,
            @RequestParam(value = "filter", required = false) String filter) {
        log.debug("Page: {}", page);

        if (StringUtils.isBlank(sort)) {
            sort = "id";
            direction = "desc";
        }

        Map<String, Object> params = buildPagination(page, sort, direction, filter);

        Page<Season> p;
        PageRequest pageRequest = (PageRequest) params.get(Constants.PAGE_REQUEST);
        if (StringUtils.isNotBlank(filter)) {
            p = service.search(filter, pageRequest);
        } else {
            p = service.list(pageRequest);
        }

        paginate(model, p, params);

        model.addAttribute("seasons", p);

        return "/admin/season/list";
    }

    @RequestMapping(value = "/new", method = RequestMethod.GET)
    public String create(Model model) {
        Calendar cal = Calendar.getInstance();
        Season season = new Season();
        season.setId(cal.get(Calendar.YEAR));
        model.addAttribute("season", season);
        return "/admin/season/new";
    }

    @RequestMapping(value = "/new", method = RequestMethod.POST)
    public String create(@ModelAttribute("season") Season season, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        validator.validate(season, bindingResult);
        if (bindingResult.hasErrors()) {
            log.warn("Could not create season. {}", bindingResult.getAllErrors());
            return "/admin/season/new";
        }

        try {
            service.create(season);
            return "redirect:/admin/season/show/" + season.getId();
        } catch (Exception e) {
            log.error("Could not create season.", e);
            bindingResult.reject("Could not create season. {}", e.getMessage());
            return "/admin/season/new";
        }
    }

    @RequestMapping(value = "/show/{seasonId}", method = RequestMethod.GET)
    public String show(@PathVariable("seasonId") Integer seasonId, Model model) {
        Season season = service.get(seasonId);
        model.addAttribute("season", season);
        return "/admin/season/show";
    }

    @RequestMapping(value = "/delete/{seasonId}", method = RequestMethod.GET)
    public String delete(@PathVariable("seasonId") Integer seasonId, RedirectAttributes redirectAttributes) {
        try {
            service.delete(seasonId);
            redirectAttributes.addFlashAttribute("message", "Season " + seasonId + " deleted successfully!");
        } catch (Exception e) {
            log.error("Could not delete season " + seasonId + ".", e);
            redirectAttributes.addFlashAttribute("message", "Season " + seasonId + " could not be deleted.");
            redirectAttributes.addFlashAttribute("messageStyle", "alert-danger");
        }
        return "redirect:/admin/season/list";
    }
}
