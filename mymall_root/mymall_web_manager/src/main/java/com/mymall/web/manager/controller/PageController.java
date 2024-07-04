package com.mymall.web.manager.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageController {

    @RequestMapping("/{model}/{page}.html")
    public String toPage(@PathVariable String model,@PathVariable String page){
        return model + "/" + page + ".html";
    }

    @RequestMapping("/{path}.html")
    public String toHtml(@PathVariable  String path){
        return path + ".html";
    }

}
