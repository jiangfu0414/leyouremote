package com.leyou.item.controller;

import com.leyou.item.dto.CategoryDTO;
import com.leyou.item.entity.Category;
import com.leyou.item.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    CategoryService categoryService;

    @GetMapping("/of/parent")
    public ResponseEntity<List<CategoryDTO>> queryCategoryByPid(@RequestParam("pid") Long pid){
        return ResponseEntity.ok(this.categoryService.queryCategoryByPid(pid));
    }

    /**
     * 根据品牌id查询分类的信息
     * @param brandid
     * @return
     */
    @GetMapping("of/brand/{id}")
    public ResponseEntity<List<CategoryDTO>> queryCategoryByBrandId(@PathVariable("id") Long brandid){
        return ResponseEntity.ok(this.categoryService.queryCategoryByBrandId(brandid));
    }
}
