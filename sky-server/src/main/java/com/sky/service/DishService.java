package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {

    /**
     * 新增菜品
     * @param dishDTO
     */
    void addDishWithFlavors(DishDTO dishDTO);


    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 删除菜品
     * @param ids
     * @return
     */
    void deleteBatch(List<Long> ids);

    /**
     * 根据id查询菜品
     * @param id
     * @return
     */
    DishVO getByIdWithFlavor(Long id);

    /**
     * 修改菜品
     * @param dishDTO
     */
    void updateDishWithFlavors(DishDTO dishDTO);

    /**
     * 菜品启用停用状态
     * @param status
     * @param id
     */
    void startOrStopDish(Integer status, Long id);
}
