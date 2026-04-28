package com.exampe.study.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.exampe.study.entity.StudyGroup;

import java.util.List;

public interface IStudyGroupService extends IService<StudyGroup> {
    List<StudyGroup> listGroups();

    boolean joinGroup(String groupId, String userId, String username);
}
