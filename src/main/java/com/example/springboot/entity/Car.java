package com.example.springboot.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import cn.hutool.core.annotation.Alias;

/**
 * <p>
 * 
 * </p>
 *
 * @author Jing
 * @since 2024-03-09
 */
@Getter
@Setter
@ApiModel(value = "Car对象", description = "")
public class Car implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("用户名")
    @Alias("用户名")
    private String name;

    @ApiModelProperty("车辆名")
    @Alias("车辆名")
    private String carName;

    @ApiModelProperty("车牌号")
    @Alias("车牌号")
    private String carNumber;

    @ApiModelProperty("车辆地点")
    @Alias("车辆地点")
    private String location;

    @ApiModelProperty("创建时间")
    @Alias("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @ApiModelProperty("隶属车队名称")
    @Alias("隶属车队名称")
    private String carlistname;


}
