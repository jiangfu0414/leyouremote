package com.leyou.item.controller;

import com.leyou.common.vo.PageResult;
import com.leyou.item.dto.SkuDTO;
import com.leyou.item.dto.SpuDTO;
import com.leyou.item.dto.SpuDetailDTO;
import com.leyou.item.entity.SpuDetail;
import com.leyou.item.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    @GetMapping("spu/page")
    public ResponseEntity<PageResult<SpuDTO>> querySpuByPage(
            @RequestParam(value = "page",defaultValue = "1")Integer page,
            @RequestParam(value = "rows",defaultValue = "5")Integer rows,
            @RequestParam(value = "saleable",required = false)Boolean saleable,
            @RequestParam(value = "key",required = false)String key
    ){
        return ResponseEntity.ok(this.goodsService.querySpuByPage(page,rows,saleable,key));
    }

    @PostMapping("/goods")
    public ResponseEntity<Void> addGoods(@RequestBody SpuDTO spuDTO){
        this.goodsService.addGoods(spuDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @PutMapping("spu/saleable")
    public ResponseEntity<Void> motifysaleable(
            @RequestParam("id") Long spuId,
            @RequestParam(value = "saleable")Boolean saleable){
        this.goodsService.motifysaleable(spuId,saleable);
        return ResponseEntity.ok().build();
    }

    @GetMapping("spu/detail")
    public ResponseEntity<SpuDetailDTO> querySpuDetailBySpuId(@RequestParam("id") Long Spuid){
        return ResponseEntity.ok(this.goodsService.querySpuDetailBySpuId(Spuid));

    }

    @GetMapping("sku/of/spu")
    public ResponseEntity<List<SkuDTO>> querySkuBySpuid(@RequestParam("id") Long spuid){
        return ResponseEntity.ok(this.goodsService.querySkuBySpuid(spuid));

    }

    @PutMapping("/goods")
    public ResponseEntity<Void> updateGoods(@RequestBody SpuDTO spuDTO){
        this.goodsService.updateGoods(spuDTO);
        return ResponseEntity.ok().build();
    }
}
