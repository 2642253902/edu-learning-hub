package com.exampe;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;
import jakarta.annotation.Resource;
import org.apache.ibatis.annotations.Mapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.sql.DataSource;
import java.nio.file.Paths;
import java.sql.Types;
import java.util.Collections;

@SpringBootTest
class StudyProjectBarkendApplicationTests {

//    @Test
//        // 测试密码加密功能
//    void contextLoads() {
//        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
//        System.out.println(encoder.encode("123456"));
//    }
//
//    @Test
//        // 测试 MyBatis-Plus 代码生成器
//    void MybatisPlusCodeGenTest() {
//        FastAutoGenerator.create("jdbc:mysql://localhost:3306/study", "root", "root")
//                .globalConfig(builder -> {
//                    builder.author("26422") // 设置作者
////                            .enableSwagger() // 开启 swagger 模式
//                            .outputDir("D://Desktop"); // 指定输出目录
//                })
//                .dataSourceConfig(builder ->
//                        builder.typeConvertHandler((globalConfig, typeRegistry, metaInfo) -> {
//                            int typeCode = metaInfo.getJdbcType().TYPE_CODE;
//                            if (typeCode == Types.SMALLINT) {
//                                // 自定义类型转换
//                                return DbColumnType.INTEGER;
//                            }
//                            return typeRegistry.getColumnType(metaInfo);
//                        })
//                )
//                .packageConfig(builder ->
//                        builder.parent("com.exampe") // 设置父包名
//                                .moduleName("api") // 设置父包模块名
//                                .pathInfo(Collections.singletonMap(OutputFile.xml, "D://Desktop")) // 设置mapperXml生成路径
//                )
//                .strategyConfig(builder ->
//                        {    builder.addInclude("sys_role_route") // 设置需要生成的表名
//                                .addTablePrefix("sys_"); // 设置过滤表前缀
//                            builder.mapperBuilder()
//                                    .mapperAnnotation(Mapper.class);
//                        }
//                )
//                .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
//                .execute();
//    }

}