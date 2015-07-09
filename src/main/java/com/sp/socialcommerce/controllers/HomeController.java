package com.sp.socialcommerce.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author <a href="mailto:szymon.przedwojski@amg.net.pl">Szymon Przedwojski</a>
 */
@Controller
@RequestMapping(value = "/")
public class HomeController {

    @RequestMapping
    public String toLogin() {
        return "redirect:login";
    }

}
