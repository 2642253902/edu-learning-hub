package com.exampe.auth.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouteTreeDTO {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    private String name;
    private String path;
    private String parentId;
    private Integer level;
    private String remark;
    private Integer sort;
    private List<RouteTreeDTO> children;
}
