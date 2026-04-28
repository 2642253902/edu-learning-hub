-- 学习社区与评价系统建表（示例 SQL，MySQL）

-- 资源评价
CREATE TABLE IF NOT EXISTS resource_review (
  id VARCHAR(64) PRIMARY KEY,
  resource_id VARCHAR(64) NOT NULL,
  course_id VARCHAR(64),
  user_id VARCHAR(64),
  username VARCHAR(128),
  rating INT,
  content TEXT,
  likes INT DEFAULT 0,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 学习小组
CREATE TABLE IF NOT EXISTS study_group (
  id VARCHAR(64) PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  description TEXT,
  owner_id VARCHAR(64),
  owner_name VARCHAR(128),
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 小组成员
CREATE TABLE IF NOT EXISTS study_group_member (
  id VARCHAR(64) PRIMARY KEY,
  group_id VARCHAR(64) NOT NULL,
  user_id VARCHAR(64) NOT NULL,
  username VARCHAR(128),
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uk_group_user (group_id, user_id)
);

-- 小组帖子
CREATE TABLE IF NOT EXISTS group_post (
  id VARCHAR(64) PRIMARY KEY,
  group_id VARCHAR(64) NOT NULL,
  user_id VARCHAR(64),
  username VARCHAR(128),
  title VARCHAR(255),
  content TEXT,
  comment_count INT DEFAULT 0,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 帖子回复
CREATE TABLE IF NOT EXISTS post_comment (
  id VARCHAR(64) PRIMARY KEY,
  post_id VARCHAR(64) NOT NULL,
  user_id VARCHAR(64),
  username VARCHAR(128),
  content TEXT,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 可选：点赞记录（用于防止重复点赞）
CREATE TABLE IF NOT EXISTS review_like (
  id VARCHAR(64) PRIMARY KEY,
  review_id VARCHAR(64) NOT NULL,
  user_id VARCHAR(64),
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 索引
CREATE INDEX idx_review_resource ON resource_review(resource_id);
CREATE INDEX idx_group_id ON group_post(group_id);
CREATE INDEX idx_post_id ON post_comment(post_id);
CREATE INDEX idx_group_member_group ON study_group_member(group_id);

-- 社区菜单（按需执行）
-- 说明：如果 sys_routes 已存在相同 path，请先删除重复项再执行。
INSERT INTO sys_routes (id, title, path, parent_id, level, remark, sort, menu_visible)
SELECT '3000', '学习社区', '/study/community', '0', '1', '学习社区', '30', 1
WHERE NOT EXISTS (SELECT 1 FROM sys_routes WHERE path = '/study/community');

INSERT INTO sys_routes (id, title, path, parent_id, level, remark, sort, menu_visible)
SELECT '3001', '小组广场', '/study/community/GroupList', '3000', '2', '小组广场', '1', 1
WHERE NOT EXISTS (SELECT 1 FROM sys_routes WHERE path = '/study/community/GroupList');

INSERT INTO sys_routes (id, title, path, parent_id, level, remark, sort, menu_visible)
SELECT '3002', '公共讨论', '/study/community/PublicDiscussion', '3000', '2', '公共讨论', '2', 1
WHERE NOT EXISTS (SELECT 1 FROM sys_routes WHERE path = '/study/community/PublicDiscussion');

-- 把社区菜单赋权给所有角色（若你需要按角色控制，请改为指定 role_id）
INSERT INTO sys_role_route (id, role_id, route_id)
SELECT REPLACE(UUID(), '-', ''), r.id, '3000'
FROM sys_role r
WHERE NOT EXISTS (
  SELECT 1 FROM sys_role_route rr WHERE rr.role_id = r.id AND rr.route_id = '3000'
);

INSERT INTO sys_role_route (id, role_id, route_id)
SELECT REPLACE(UUID(), '-', ''), r.id, '3001'
FROM sys_role r
WHERE NOT EXISTS (
  SELECT 1 FROM sys_role_route rr WHERE rr.role_id = r.id AND rr.route_id = '3001'
);

INSERT INTO sys_role_route (id, role_id, route_id)
SELECT REPLACE(UUID(), '-', ''), r.id, '3002'
FROM sys_role r
WHERE NOT EXISTS (
  SELECT 1 FROM sys_role_route rr WHERE rr.role_id = r.id AND rr.route_id = '3002'
);
