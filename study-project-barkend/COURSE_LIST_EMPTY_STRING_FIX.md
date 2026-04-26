# 课程列表查询空字符串问题修复

## 🐛 问题描述

### 现象
```
请求1: http://localhost:8080/study/cloudComputingCourse/list?courseTypeId=&pageNo=1&pageSize=10
结果: 返回空数据 ❌

请求2: http://localhost:8080/study/cloudComputingCourse/list?pageNo=1&pageSize=10
结果: 返回正常数据 ✅
```

### 问题分析

#### 原因
当前代码使用 `QueryWrapper<>(cloudComputingCourse)` 会将所有**非 null** 字段作为查询条件，包括**空字符串**。

#### 原始代码
```java
@GetMapping(value = "/list")
public RestBean<IPage<CloudComputingCourse>> queryPageList(
        CloudComputingCourse cloudComputingCourse, 
        @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo, 
        @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, 
        HttpServletRequest req) {
    QueryWrapper<CloudComputingCourse> queryWrapper = new QueryWrapper<>(cloudComputingCourse);
    Page<CloudComputingCourse> page = new Page<>(pageNo, pageSize);
    IPage<CloudComputingCourse> pageList = cloudComputingCourseService.page(page, queryWrapper);
    return RestBean.success(pageList);
}
```

#### 生成的 SQL
```sql
-- 当传递 courseTypeId= 时
SELECT * FROM cloud_computing_course 
WHERE course_type_id = '';  -- 空字符串条件，查不到任何数据
```

---

## ✅ 解决方案

### 修复后的代码

```java
@GetMapping(value = "/list")
public RestBean<IPage<CloudComputingCourse>> queryPageList(
        CloudComputingCourse cloudComputingCourse, 
        @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo, 
        @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, 
        HttpServletRequest req) {
    QueryWrapper<CloudComputingCourse> queryWrapper = new QueryWrapper<>();
    
    // 处理课程名称模糊查询
    String courseName = cloudComputingCourse.getCourseName();
    if (courseName != null && !courseName.isEmpty()) {
        queryWrapper.like("course_name", courseName.replace("*", ""));
    }
    
    // 处理课程分类ID精确查询（忽略空字符串）
    String courseTypeId = cloudComputingCourse.getCourseTypeId();
    if (courseTypeId != null && !courseTypeId.isEmpty()) {
        queryWrapper.eq("course_type_id", courseTypeId);
    }
    
    // 处理教师ID精确查询（忽略空字符串）
    String teacherId = cloudComputingCourse.getTeacherId();
    if (teacherId != null && !teacherId.isEmpty()) {
        queryWrapper.eq("teacher_id", teacherId);
    }
    
    // 处理课程状态精确查询（忽略 null）
    if (cloudComputingCourse.getCourseStatus() != null) {
        queryWrapper.eq("course_status", cloudComputingCourse.getCourseStatus());
    }
    
    Page<CloudComputingCourse> page = new Page<>(pageNo, pageSize);
    IPage<CloudComputingCourse> pageList = cloudComputingCourseService.page(page, queryWrapper);
    return RestBean.success(pageList);
}
```

### 修复原理

#### 1. 手动构建查询条件
不再使用 `QueryWrapper<>(entity)` 自动映射，而是手动添加每个查询条件。

#### 2. 空字符串过滤
对每个字段进行检查，只有**非 null 且非空字符串**时才添加查询条件：

```java
if (value != null && !value.isEmpty()) {
    queryWrapper.eq("column", value);
}
```

#### 3. 支持的查询方式

| 字段 | 查询类型 | 说明 |
|------|---------|------|
| courseName | 模糊查询 (LIKE) | 支持 `*` 号，自动去除 |
| courseTypeId | 精确查询 (=) | 忽略空字符串 |
| teacherId | 精确查询 (=) | 忽略空字符串 |
| courseStatus | 精确查询 (=) | 忽略 null |

---

## 📊 修复效果对比

### 场景 1: 传递空字符串
```
请求: /list?courseTypeId=&pageNo=1&pageSize=10

修复前:
SQL: WHERE course_type_id = ''
结果: 空数据 ❌

修复后:
SQL: 无条件（忽略空字符串）
结果: 全部数据 ✅
```

### 场景 2: 传递有效值
```
请求: /list?courseTypeId=abc123&pageNo=1&pageSize=10

修复前:
SQL: WHERE course_type_id = 'abc123'
结果: 正确 ✅

修复后:
SQL: WHERE course_type_id = 'abc123'
结果: 正确 ✅
```

### 场景 3: 不传递参数
```
请求: /list?pageNo=1&pageSize=10

修复前:
SQL: 无条件
结果: 全部数据 ✅

修复后:
SQL: 无条件
结果: 全部数据 ✅
```

### 场景 4: 组合查询
```
请求: /list?courseName=Java&courseTypeId=xyz&pageNo=1&pageSize=10

修复前:
SQL: WHERE course_name LIKE '%Java%' AND course_type_id = 'xyz'
结果: 正确 ✅

修复后:
SQL: WHERE course_name LIKE '%Java%' AND course_type_id = 'xyz'
结果: 正确 ✅
```

---

## 🔍 技术细节

### MyBatis-Plus QueryWrapper 行为

#### setEntity 的问题
```java
// ❌ 问题：空字符串会被当作查询条件
CloudComputingCourse course = new CloudComputingCourse();
course.setCourseTypeId("");  // 空字符串
QueryWrapper<Course> qw = new QueryWrapper<>(course);
// 生成: WHERE course_type_id = ''
```

#### 正确的做法
```java
// ✅ 正确：手动控制查询条件
QueryWrapper<Course> qw = new QueryWrapper<>();
String typeId = course.getCourseTypeId();
if (typeId != null && !typeId.isEmpty()) {
    qw.eq("course_type_id", typeId);
}
// 空字符串不会添加条件
```

### 为什么需要检查空字符串？

前端表单提交时，空输入框通常会传递空字符串 `""`，而不是 `null`：

```javascript
// 前端代码
axios.get('/list', {
  params: {
    courseTypeId: '',  // 空字符串，不是 null
    pageNo: 1,
    pageSize: 10
  }
})
```

如果不检查空字符串，会生成错误的 SQL 条件。

---

## 💡 最佳实践

### 1. 统一的处理方法

可以创建一个工具方法来简化空值判断：

```java
public class QueryHelper {
    
    /**
     * 添加等于条件（忽略 null 和空字符串）
     */
    public static <T> void addEqCondition(QueryWrapper<T> wrapper, String column, String value) {
        if (value != null && !value.isEmpty()) {
            wrapper.eq(column, value);
        }
    }
    
    /**
     * 添加模糊查询条件（忽略 null 和空字符串）
     */
    public static <T> void addLikeCondition(QueryWrapper<T> wrapper, String column, String value) {
        if (value != null && !value.isEmpty()) {
            wrapper.like(column, value.replace("*", ""));
        }
    }
}
```

**使用示例**:
```java
QueryWrapper<CloudComputingCourse> queryWrapper = new QueryWrapper<>();

QueryHelper.addLikeCondition(queryWrapper, "course_name", cloudComputingCourse.getCourseName());
QueryHelper.addEqCondition(queryWrapper, "course_type_id", cloudComputingCourse.getCourseTypeId());
QueryHelper.addEqCondition(queryWrapper, "teacher_id", cloudComputingCourse.getTeacherId());
```

### 2. 使用 LambdaQueryWrapper（类型安全）

```java
LambdaQueryWrapper<CloudComputingCourse> queryWrapper = new LambdaQueryWrapper<>();

if (StringUtils.isNotBlank(cloudComputingCourse.getCourseName())) {
    queryWrapper.like(CloudComputingCourse::getCourseName, 
                      cloudComputingCourse.getCourseName().replace("*", ""));
}

if (StringUtils.isNotBlank(cloudComputingCourse.getCourseTypeId())) {
    queryWrapper.eq(CloudComputingCourse::getCourseTypeId, 
                    cloudComputingCourse.getCourseTypeId());
}
```

**优势**:
- ✅ 类型安全，编译期检查
- ✅ 重构友好，字段改名会自动更新
- ✅ IDE 智能提示

### 3. 使用 Apache Commons StringUtils

```java
import org.apache.commons.lang3.StringUtils;

// 更简洁的判断
if (StringUtils.isNotBlank(courseTypeId)) {
    queryWrapper.eq("course_type_id", courseTypeId);
}
```

**StringUtils 方法**:
- `isNotBlank(str)` - 不为 null、不为空、不只包含空白字符
- `isNotEmpty(str)` - 不为 null、不为空
- `isEmpty(str)` - 为 null 或为空

---

## ⚠️ 注意事项

### 1. 其他 Controller 也需要类似修复

检查项目中其他使用 `QueryWrapper<>(entity)` 的地方：

```bash
# 搜索可能的类似问题
grep -r "new QueryWrapper<>" src/main/java/*/controller/
```

### 2. 前端传参规范

建议前端在传参时：
- 空值传递 `null` 或不传该参数
- 避免传递空字符串 `""`

```javascript
// ✅ 推荐
params: {
  courseTypeId: value || undefined,  // 空值时不传
  pageNo: 1,
  pageSize: 10
}

// ❌ 不推荐
params: {
  courseTypeId: '',  // 空字符串
  pageNo: 1,
  pageSize: 10
}
```

### 3. 数据库字段默认值

如果数据库字段有默认值，空字符串可能导致意外行为：

```sql
-- 假设 course_type_id 有默认值
ALTER TABLE cloud_computing_course 
MODIFY course_type_id VARCHAR(50) DEFAULT 'default_type';

-- 查询 WHERE course_type_id = '' 可能不符合预期
```

---

## 🧪 测试用例

### 测试 1: 空字符串参数
```bash
curl "http://localhost:8080/study/cloudComputingCourse/list?courseTypeId=&pageNo=1&pageSize=10"
# 期望: 返回所有数据（忽略空字符串条件）
```

### 测试 2: 有效参数
```bash
curl "http://localhost:8080/study/cloudComputingCourse/list?courseTypeId=abc123&pageNo=1&pageSize=10"
# 期望: 返回 course_type_id = 'abc123' 的数据
```

### 测试 3: 模糊查询
```bash
curl "http://localhost:8080/study/cloudComputingCourse/list?courseName=Java&pageNo=1&pageSize=10"
# 期望: 返回 course_name LIKE '%Java%' 的数据
```

### 测试 4: 组合查询
```bash
curl "http://localhost:8080/study/cloudComputingCourse/list?courseName=Java&courseTypeId=xyz&teacherId=t001&pageNo=1&pageSize=10"
# 期望: 返回满足所有条件的数据
```

### 测试 5: 无参数
```bash
curl "http://localhost:8080/study/cloudComputingCourse/list?pageNo=1&pageSize=10"
# 期望: 返回所有数据
```

---

## 📝 总结

### 问题根源
- `QueryWrapper<>(entity)` 会将空字符串作为查询条件
- 前端表单空输入框通常传递空字符串而非 null

### 解决方案
- 手动构建查询条件
- 对每个字段进行 null 和空字符串检查
- 只在有有效值时添加查询条件

### 关键代码
```java
String value = entity.getField();
if (value != null && !value.isEmpty()) {
    queryWrapper.eq("column", value);
}
```

### 影响范围
- ✅ 修复了课程列表查询的空字符串问题
- ⚠️ 建议检查其他类似的查询接口

---

**修复时间**: 2026-04-27  
**修复人员**: AI Assistant  
**状态**: ✅ 已完成并测试
