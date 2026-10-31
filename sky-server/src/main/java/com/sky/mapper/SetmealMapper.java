package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper
public interface SetmealMapper {

    /**
     * 根据分类id查询套餐的数量
     * @param id
     * @return
     */
    @Select("select count(id) from setmeal where category_id = #{categoryId}")
    Integer countByCategoryId(Long id);

    /**
     * 新增套餐基础信息
     * @param setmeal
     */
    @AutoFill(value = OperationType.INSERT)
    void insertSetmeal(Setmeal setmeal);

    /**
     * 套餐分页查询
     * @param setmealPageQueryDTO
     * @return
     */

    Page<SetmealVO> setmealQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 根据id查询套餐基础信息
     * @param id
     * @return
     */
    Setmeal getById(Long id);

    /**
     * 更新套餐基础信息
     * @param setmeal
     */
    @AutoFill(value = OperationType.UPDATE)
    void update(Setmeal setmeal);

    /**
     * 删除套餐基础信息
     * @param ids
     */
    void deleteSetmeal(List<Long> ids);

    /**
     * 根据ids列表中套餐状态为1的数量
     * @param ids
     * @return
     */
    int countByStatus(List<Long> ids);

    /**
     * 根据id修改套餐状态
     * @param status
     * @param id
     */
    void updateStatus(Integer status, Long id);

    /**
     * 根据分类id查询套餐列表
     * @param categoryId
     * @return
     */

    List<Setmeal> getByCategoryId(Long categoryId);

    /**
     *
     * @param id
     * @return
     */

    List<DishItemVO> getSetmealDish(Long id);
}
