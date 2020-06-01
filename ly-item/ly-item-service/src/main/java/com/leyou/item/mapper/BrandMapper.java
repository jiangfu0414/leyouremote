package com.leyou.item.mapper;


import com.leyou.item.entity.Brand;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.web.bind.annotation.RequestParam;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

//用brand泛型是因为再实体类中可以查到数据库里面去，dto没有加注解所以
//不能在泛型中写BrandDTO
public interface BrandMapper extends Mapper<Brand> {

    int insertCategoryBrand(@Param("cids") List<Long> cids, @Param("bid") Long id);

    @Delete("delete from tb_category_brand where brand_id=#{bid}")
    int deleteCategoryBrandByBrandId(@Param("bid") Long id);

    @Select("select tb.id,tb.name from tb_brand tb inner join tb_category_brand cb on tb.id =cb.brand_id where cb.category_id=#{cid}")
    List<Brand> queryBrandByCategory(@RequestParam("cid") Long cid);
}



