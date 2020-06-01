package com.leyou.item.controller;

import com.leyou.item.dto.SpecGroupDTO;
import com.leyou.item.dto.SpecParamDTO;
import com.leyou.item.service.SpecService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("spec")
public class SpecController {

    @Autowired
    private SpecService specService;

    /**
     * 以分类查询规格参数组
     * @param cid
     * @return
     */
    @GetMapping("groups/of/category")
    public ResponseEntity<List<SpecGroupDTO>> querySpecGroupByCategoryId(@RequestParam("id") Long cid){
        return ResponseEntity.ok(this.specService.querySpecGroupByCategoryId(cid));
    }

    /**
     * 以规格参数组id查询对应的规格参数
     */
    @GetMapping("params")
    public ResponseEntity<List<SpecParamDTO>> querySpecParamsBySpecGroupId(
            @RequestParam(value = "gid",required = false) Long gid,
            @RequestParam(value = "cid",required = false) Long cid){
        return ResponseEntity.ok(this.specService.querySpecParamsBySpecGroupId(gid,cid));
    }
}
