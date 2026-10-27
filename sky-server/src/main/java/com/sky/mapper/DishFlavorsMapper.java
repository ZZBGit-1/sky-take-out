package com.sky.mapper;


import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DishFlavorsMapper {
    /**
     * 批量插入菜品口味数据
     * dish_flavor 表没有公共字段，不能加 @AutoFill
     * @param flavors
     */
    void insertBatch(@Param("flavors") List<DishFlavor> flavors);

    /**
     * 批量删除菜品口味
     * @param ids
     */
    void deleteByDishIds(List<Long> ids);

    /**
     * 根据菜品id查询口味数据
     * @param id
     * @return
     */
    List<DishFlavor> getByDishId(Long id);

    /**
     * 根据菜品id删除口味数据
     * @param id
     */
    void deleteByDishId(Long id);
}
