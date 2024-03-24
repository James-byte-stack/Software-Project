package com.example.springboot.service.impl;

import com.example.springboot.entity.Car;
import com.example.springboot.mapper.CarMapper;
import com.example.springboot.service.ICarService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Jing
 * @since 2024-03-09
 */
@Service
public class CarServiceImpl extends ServiceImpl<CarMapper, Car> implements ICarService {

}
