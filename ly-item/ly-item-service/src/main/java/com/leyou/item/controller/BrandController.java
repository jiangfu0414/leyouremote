package com.leyou.item.controller;

import com.leyou.common.vo.PageResult;
import com.leyou.item.dto.BrandDTO;
import com.leyou.item.service.BrandServiece;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/brand")
public class BrandController {

    @Autowired
    BrandServiece brandServiece;

    //查询品牌
    @GetMapping("/page")
    public ResponseEntity<PageResult<BrandDTO>> pageQuery(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows,
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestParam(value = "desc", required = false) Boolean desc,
            @RequestParam(value = "key", required = false) String key
    ) {
        return ResponseEntity.ok(this.brandServiece.pageQuery(page, rows, sortBy, desc, key));
    }

    //新增品牌
    @PostMapping
    public ResponseEntity<Void> addBrand(
            BrandDTO brandDTO,  //这里不能用requestbody，根据brandDTO的属性自动生成requestparms，标注required为false
            @RequestParam("cids")List<Long>cids){

        this.brandServiece.addBrand(brandDTO,cids);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    //修改品牌
    @PutMapping
    public ResponseEntity<Void> updataBrand(
            BrandDTO brandDTO,  //这里不能用requestbody，根据brandDTO的属性自动生成requestparms，标注required为false
            @RequestParam("cids")List<Long>cids){

        this.brandServiece.updataBrand(brandDTO,cids);
        return ResponseEntity.ok().build();
    }


    /**
     * 根据分类id查询品牌
     * @param cid
     * @return
     */
    @GetMapping("of/category")
    public ResponseEntity<List<BrandDTO>> queryBrandByCategory(@RequestParam("id") Long cid){
        return ResponseEntity.ok(this.brandServiece.queryBrandByCategory(cid));

    }
}

