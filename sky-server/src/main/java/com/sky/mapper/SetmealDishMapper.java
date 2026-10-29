package com.sky.mapper;


import com.sky.annotation.AutoFill;
import com.sky.entity.SetmealDish;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SetmealDishMapper {
    List<Long> getSetmealDishIds(List<Long> dishIds);

    /**
     * 批量插入套餐菜品信息
     * @param setmealDishes
     */

    void insertSetmealDishes(List<SetmealDish> setmealDishes);

    /**
     * 根据套餐id查询套餐菜品信息
     * @param id
     * @return
     */
    List<SetmealDish> getSetmealDishes(Long id);

    /**
     * 根据套餐id删除套餐菜品信息
     * @param id
     */
    void deleteSetmealDishes(Long id);

    /**
     * 根据ids删除套餐菜品信息
     * @param ids
     */
    void deleteSetmealDishesByIds(List<Long> ids);

    /**
     * 根据套餐id和状态查询套餐菜品信息
     * @param id
     * @return
     */
    Integer countByStatus(Long id);
}
