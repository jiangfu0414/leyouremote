package com.leyou.item.service;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.common.utils.BeanHelper;
import com.leyou.item.dto.CategoryDTO;
import com.leyou.item.entity.Category;
import com.leyou.item.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    public List<CategoryDTO> queryCategoryByPid(Long pid) {
        Category record = new Category();
        record.setParentId(pid);

        //根据pid查询对应的list集合
        List<Category> categories = this.categoryMapper.select(record);
        if (CollectionUtils.isEmpty(categories)){
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FOUND);
        }
        return BeanHelper.copyWithCollection(categories,CategoryDTO.class);
    }

    public List<CategoryDTO> queryCategoryByBrandId(Long brandid) {

        List<Category> categories = this.categoryMapper.selectCategoryByBrandId(brandid);
        if (CollectionUtils.isEmpty(categories)){
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        return BeanHelper.copyWithCollection(categories,CategoryDTO.class);
    }


    public List<CategoryDTO> queryCategoryByIds(List<Long> categoryIds) {

        List<Category> categories=this.categoryMapper.selectByIdList(categoryIds);

        if (CollectionUtils.isEmpty(categories)){
            new LyException(ExceptionEnum.DATA_NOT_FOUND);
        }
        return BeanHelper.copyWithCollection(categories,CategoryDTO.class);
    }
}
