package com.meg.listshop.lmt.api.controller;

import com.meg.listshop.lmt.api.model.TagDrilldownResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * Created by margaretmartin on 13/05/2017.
 */

@RestController
@RequestMapping("/taginfo")
@CrossOrigin
public interface TagInfoRestControllerApi {


    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    ResponseEntity<List<TagDrilldownResource>> retrieveTagList(@RequestParam(value = "tag_type", required = false) String tag_type);

    @RequestMapping(method = RequestMethod.GET, value = "/changes", produces = "application/json")
    ResponseEntity<List<TagDrilldownResource>> retrieveTagListRefresh(
            @RequestParam(value = "after", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date after
    );

}
