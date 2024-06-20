package com.nick.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/query")
public class QueryController {
    @RequestMapping("/dataset")
    public String queryDataSet(@RequestParam("dataSet") String dataSet) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Finished: " + dataSet);
        return "dataSet: " + dataSet;
    }

    @RequestMapping("/datatype")
    public String queryDataType() {
        return "datatype";
    }
}
